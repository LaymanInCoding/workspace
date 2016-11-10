package com.witmoon.xmb.activity.specialoffer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.PreviewImage;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.ImageBDInfo;
import com.witmoon.xmb.model.ImageInfo;
import com.witmoon.xmb.model.RecordDetails;
import com.witmoon.xmb.ui.popupwindow.Popup;
import com.witmoon.xmb.ui.popupwindow.PopupDialog;
import com.witmoon.xmb.ui.popupwindow.PopupUtils;
import com.witmoon.xmb.ui.widget.SortTextView;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by de on 2016/1/25
 */
public class Baby_DetailsActivity extends BaseActivity implements View.OnClickListener {
    private ImageView src_img;
    private TextView name;
    private TextView mData,location;
    private SortTextView mContent;
    private LinearLayout layout1,layout2;
    private PopupDialog popupDialog;
    private RecordDetails recordDetails;
    private ImageView title_img;
    private ImageView imgview[] = new ImageView[6];
    private int index;
    private int ImagaId[] = {R.id.img_0, R.id.img_1, R.id.img_2, R.id.img_3, R.id.img_4, R.id.img_5};

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_baby_details;
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        //设置标题栏
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_shopping_cart));
        //设置通知栏
        setTitleColor_(R.color.master_shopping_cart);
    }

    @Override
    protected String getActionBarTitle() {
        return "记录详情";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        super.initialize(savedInstanceState);
        recordDetails = (RecordDetails) getIntent().getSerializableExtra("ZKD");
        index = getIntent().getIntExtra("index",1);
        src_img = (ImageView) findViewById(R.id.src_image);
        name = (TextView) findViewById(R.id.name);
        mData = (TextView) findViewById(R.id.data);
        location = (TextView)findViewById(R.id.location);
        mContent = (SortTextView) findViewById(R.id.content);
        layout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        layout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        title_img = (ImageView) findViewById(R.id.toolbar_right_img);
        title_img.setVisibility(View.VISIBLE);
        location.setText(recordDetails.getPosition());
        if (!AppContext.getLoginInfo().getAvatar().equals("")){
            ImageLoader.getInstance().displayImage(AppContext.getLoginInfo().getAvatar(), src_img, AppContext.options_disk);
        }else{
            src_img.setImageResource(R.mipmap.touxiang);
        }
        title_img.setImageResource(R.mipmap.delete_baby);
        int margin = (int)getResources().getDimension(R.dimen.dimen_60_dip);
        int margin10 = (int)getResources().getDimension(R.dimen.dimen_10_dip);
        int per_w = (MainActivity.screen_width - margin) / 3;
        for (int i = 0; i < 6; i++) {
            imgview[i] = (ImageView) findViewById(ImagaId[i]);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(per_w, per_w);
            int top = 0;
            int left = 0;
            if(i % 3 != 0){
                left = margin10;
            }
            if(i >= 3){
                top = margin10;
            }
            layoutParams.setMargins(left,top,0,0);
            imgview[i].setLayoutParams(layoutParams);
        }
        for (int i = 0; i < recordDetails.getPhoto().size(); i++) {
            imgview[i].setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage( recordDetails.getPhoto().get(i).url, imgview[i], AppContext.options_disk);
            imgview[i].setOnClickListener(new GridOnclick(0, i));
        }
        init();
    }

    class GridOnclick implements View.OnClickListener {

        private int index;
        private int row;

        public GridOnclick(int index, int row) {
            this.index = index;
            this.row = row;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Baby_DetailsActivity.this, PreviewImage.class);
            ArrayList<ImageInfo> info = recordDetails.getPhoto();
            intent.putExtra("data", (Serializable) info);
            intent.putExtra("bdinfo", new ImageBDInfo());
            intent.putExtra("index", row);
            startActivity(intent);
        }
    }

    private void init() {
        mData.setText(recordDetails.getYear()+"/"+recordDetails.getMonth() + "/" + recordDetails.getDay() + "");
        name.setText(AppContext.getLoginInfo().getName());
        title_img.setOnClickListener(this);
        mContent.setText(recordDetails.getContent());
    }

    private void showDownUpPopupDialog() {
        Popup popup = new Popup();
        popup.setvWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popup.setvHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popup.setClickable(true);
//        popup.setAnimFadeInOut(R.style.popupAnimation);
        popup.setContentView(R.layout.view_pup_baby);
        //设置触摸其他位置时关闭窗口
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int height = view.findViewById(R.id.flMaskLayer).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        PopupUtils.dismissPopupDialog();
                    }
                }
                return true;
            }
        };
        popup.setTouchListener(listener);
        popupDialog = PopupUtils.createPopupDialog(this, popup);
        popupDialog.showAtLocation(this.findViewById(R.id.src_image), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, popup.getxPos(), popup.getyPos());
        final View view = popupDialog.getContentView();
        //背景透明度设置
        view.findViewById(R.id.flMaskLayer).setAlpha(0.75f);
        final View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除
                if (v.getId() == R.id.delete) {
                    HomeApi.diarydel(recordDetails.getId(), babydelete);
                    PopupUtils.dismissPopupDialog();
                }
                //取消
                else if (v.getId() == R.id.tvCancel) {
                    PopupUtils.dismissPopupDialog();
                }
            }
        };
        view.findViewById(R.id.tvCancel).

                setOnClickListener(l);

        view.findViewById(R.id.delete).

                setOnClickListener(l);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.toolbar_right_img:
                showDownUpPopupDialog();
                break;
        }
    }

    private Listener<JSONObject> babydelete = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            showWaitDialog();
        }

        @Override
        public void onError(NetroidError error) {
            super.onError(error);
        }

        @Override
        public void onSuccess(JSONObject response) {
            try {
                if (response.getString("status").equals("1")) {
                    Intent intent = new Intent(Const.INTENT_ACTION_BABY);
                    intent.putExtra("act","del");
                    intent.putExtra("index",index);
                    sendBroadcast(intent);
                    finish();
                    return;
                }
                XmbUtils.showMessage(Baby_DetailsActivity.this, "删除失败");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFinish() {
            super.onFinish();
            hideWaitDialog();
        }
    };
}
