package com.witmoon.xmb.activity.babycenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.baby.DynamicPost;
import com.witmoon.xmb.activity.babycenter.Adapter.DiaryAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.api.MengbaoApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.RecordDetails;
import com.witmoon.xmb.util.HttpUtility;
import com.witmoon.xmb.util.SDCardUtils;
import com.witmoon.xmb.util.SharedPreferencesUtil;
import com.witmoon.xmb.util.WeakAsyncTask;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;
import library.BitmapUtil;
import library.CropHandler;
import library.CropHelper;
import library.CropParams;

public class BabyRecordFragment extends BaseFragment implements  DialogInterface.OnClickListener, CropHandler {
    private View view;
    private LinearLayout headView;
    private ArrayList<RecordDetails> mapArrayList = new ArrayList<>();
    private DiaryAdapter diaryAdapter;
    private int maxPager;
    private int mPageNo = 1;
    private BabyRecordFragment _this = this;
    private ImageView currentImageView = null;
    private int currentImageIndex = 0;
    private File mAvatar;
    private ImageView img1,img2,img3,img4,img5;

    private BroadcastReceiver refresh_diary_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String act = intent.getStringExtra("act");
            int index = intent.getIntExtra("index",1);
            if(act != null && act.equals("del")){
                XmbUtils.showMessage(getActivity(), "删除成功");
                RecordDetails recordDetails = mapArrayList.get(index);
                if(recordDetails.getIs_first() == 1 && index != mapArrayList.size() - 1){
                    RecordDetails recordDetails2 =  mapArrayList.get(index + 1);
                    if(recordDetails2.getIs_first() == 0){
                        recordDetails2.setIs_first(1);
                        mapArrayList.remove(index+1);
                        mapArrayList.add(index + 1, recordDetails2);
                    }
                }
                mapArrayList.remove(index);
                diaryAdapter.notifyDataSetChanged();
                return;
            }
            XmbUtils.showMessage(getActivity(), "发表成功");
            mapArrayList.clear();
            mPageNo = 1;
            setRecRequest(1);
        }
    };
    private CropParams mCropParams;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_baby_record, container, false);
        headView = (LinearLayout) inflater.inflate(R.layout.header_baby_record, container, false);
        mRootView = (RecyclerView) view.findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(this.getContext());

        mCropParams = new CropParams(getActivity());
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        diaryAdapter = new DiaryAdapter(mapArrayList,getActivity());
        stringAdapter = new HeaderViewRecyclerAdapter(diaryAdapter);
        stringAdapter.addHeaderView(headView);
        mRootView.setAdapter(stringAdapter);
        IntentFilter loginFilter = new IntentFilter(Const.INTENT_ACTION_BABY);
        getActivity().registerReceiver(refresh_diary_receiver, loginFilter);
        initView();
        setRecRequest(1);
        return view;
    }

    public void setRecRequest(int currentPage){
        HomeApi.diaarylisener(currentPage, new Listener<JSONObject>() {
            @Override
            public void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            public void onError(NetroidError error) {
                super.onError(error);
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONObject jsonObject = response.getJSONObject("data");
                    maxPager = jsonObject.getInt("max_page");
                    if (maxPager == 1) {
                        removeFooterView();
                    }
                    if (mPageNo < maxPager) {
                        createLoadMoreView();
                        resetStatus();
                    }

                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mapArrayList.add(RecordDetails.parse(jsonArray.getJSONObject(i), 0));
                    }
                    mPageNo++;
                    diaryAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initView(){
        int margin = (int)getActivity().getResources().getDimension(R.dimen.dimen_20_dip);
        int width = MainActivity.screen_width - (int)getActivity().getResources().getDimension(R.dimen.dimen_40_dip);
        img1 = (ImageView) headView.findViewById(R.id.img1);
        img2 = (ImageView) headView.findViewById(R.id.img2);
        img3 = (ImageView) headView.findViewById(R.id.img3);
        img4 = (ImageView) headView.findViewById(R.id.img4);
        img5 = (ImageView) headView.findViewById(R.id.img5);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(width * 208 / 710,width * 208 / 710);
        layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(width * 208 / 710,width * 208 / 710);
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        layoutParams2.addRule(RelativeLayout.BELOW, R.id.img1);
        layoutParams2.setMargins(0, margin, 0, 0);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(width * 254 / 710,width * 436 / 710);
        layoutParams3.addRule(RelativeLayout.CENTER_IN_PARENT);
        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(width * 208 / 710,width * 208 / 710);
        layoutParams4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(width * 208 / 710,width * 208 / 710);
        layoutParams5.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams5.addRule(RelativeLayout.BELOW, R.id.img4);
        layoutParams5.setMargins(0, margin, 0, 0);
        img1.setLayoutParams(layoutParams1);
        img2.setLayoutParams(layoutParams2);
        img3.setLayoutParams(layoutParams3);
        img4.setLayoutParams(layoutParams4);
        img5.setLayoutParams(layoutParams5);
        AssetManager mgr = getActivity().getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/font.otf");
        TextView picWallTextView = (TextView) headView.findViewById(R.id.pic_wall);
        TextView picWallHintTextView = (TextView) headView.findViewById(R.id.pic_wall_hint);
        TextView diaryWallTextView = (TextView) headView.findViewById(R.id.diary_wall);
        TextView diary_day_zh = (TextView) headView.findViewById(R.id.diary_day_zh);
        TextView diary_day_en = (TextView) headView.findViewById(R.id.diary_day_en);
        TextView diary_day_hint = (TextView) headView.findViewById(R.id.diary_day_hint);
        picWallTextView.setTypeface(tf);
        picWallHintTextView.setTypeface(tf);
        diaryWallTextView.setTypeface(tf);
        diary_day_zh.setTypeface(tf);
        diary_day_en.setTypeface(tf);
        diary_day_hint.setTypeface(tf);

        View add_diary = headView.findViewById(R.id.add_diary);
        add_diary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DynamicPost.class);
                startActivity(intent);
            }
        });

        img1.setOnClickListener(new ImgClickListener(1));
        img2.setOnClickListener(new ImgClickListener(2));
        img3.setOnClickListener(new ImgClickListener(3));
        img4.setOnClickListener(new ImgClickListener(4));
        img5.setOnClickListener(new ImgClickListener(5));

        setPicWall();
    }

    private class ImgClickListener implements View.OnClickListener{
        private int current_index;
        public ImgClickListener(int t){
            current_index = t;
        }

        @Override
        public void onClick(View v) {
            currentImageIndex = current_index;
            currentImageView = (ImageView)v;
            XmbUtils.AvatarDialogFragment avatarDialogFragment = new XmbUtils.AvatarDialogFragment();
            avatarDialogFragment.setOnClickListener(_this);
            avatarDialogFragment.show(getActivity().getFragmentManager(), "AvatarDialog");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CropHelper.handleResult(this, requestCode, resultCode, data);
    }

    @Override
    public CropParams getCropParams() {
        return mCropParams;
    }

    private void setPicWall(){
        if (SharedPreferencesUtil.contains(getActivity(),"pic_wall")){
            try {
                setImageWallAction(new JSONObject(SharedPreferencesUtil.get(getActivity(),"pic_wall","").toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        MengbaoApi.getPicWall(new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                SharedPreferencesUtil.put(getActivity(),"pic_wall",response.toString());
                setImageWallAction(response);
            }
        });
    }

    private void setImageWallAction(JSONObject response){
        try {
            JSONObject dataJsonObject = response.getJSONObject("data");
            JSONObject img1JsonObject = dataJsonObject.getJSONObject("pic1");
            JSONObject img2JsonObject = dataJsonObject.getJSONObject("pic2");
            JSONObject img3JsonObject = dataJsonObject.getJSONObject("pic3");
            JSONObject img4JsonObject = dataJsonObject.getJSONObject("pic4");
            JSONObject img5JsonObject = dataJsonObject.getJSONObject("pic5");
            if (!img1JsonObject.getString("photo_thumb").equals("")){
                ImageLoader.getInstance().displayImage(img1JsonObject.getString("photo_thumb"), img1, AppContext.options_disk);
            }
            if (!img2JsonObject.getString("photo_thumb").equals("")){
                ImageLoader.getInstance().displayImage(img2JsonObject.getString("photo_thumb"), img2, AppContext.options_disk);
            }
            if (!img3JsonObject.getString("photo_thumb").equals("")){
                ImageLoader.getInstance().displayImage(img3JsonObject.getString("photo_thumb"), img3, AppContext.options_disk);
            }
            if (!img4JsonObject.getString("photo_thumb").equals("")){
                ImageLoader.getInstance().displayImage(img4JsonObject.getString("photo_thumb"), img4, AppContext.options_disk);
            }
            if (!img5JsonObject.getString("photo_thumb").equals("")){
                ImageLoader.getInstance().displayImage(img5JsonObject.getString("photo_thumb"), img5, AppContext.options_disk);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPhotoCropped(Uri uri) {
        if (!mCropParams.compress)
            saveAvatarBitmap(BitmapUtil.decodeUriAsBitmap(getActivity(), uri));
    }

    @Override
    public void onCompressed(Uri uri) {
        saveAvatarBitmap(BitmapUtil.decodeUriAsBitmap(getActivity(), uri));
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onFailed(String message) {
    }

    @Override
    public void handleIntent(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onDestroy() {
        CropHelper.clearCacheDir();
        super.onDestroy();
        getActivity().unregisterReceiver(refresh_diary_receiver);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mCropParams.refreshUri();
        if (which == 0) {   // 从相册中选择图片
            if (currentImageIndex == 3){
                mCropParams.aspectX = 254;
                mCropParams.aspectY = 436;
            }else{
                mCropParams.aspectX = 1;
                mCropParams.aspectY = 1;
            }
            mCropParams.enable = true;
            mCropParams.compress = false;
            Intent intent = CropHelper.buildGalleryIntent(mCropParams);
            startActivityForResult(intent, CropHelper.REQUEST_CROP);
        } else {
            if (!SDCardUtils.isSDCardEnable()) {
                XmbUtils.showMessage(getActivity(),"SD卡不可用.");
                return;
            }
            if (currentImageIndex == 3){
                mCropParams.aspectX = 254;
                mCropParams.aspectY = 436;
            }else{
                mCropParams.aspectX = 1;
                mCropParams.aspectY = 1;
            }
            mCropParams.enable = true;
            mCropParams.compress = false;
            Intent intent = CropHelper.buildCameraIntent(mCropParams);
            startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
        }
    }

    private void saveAvatarBitmap(Bitmap avatar) {
        currentImageView.setImageBitmap(avatar);
        mAvatar = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "avatar_diary"+currentImageIndex+".png");
        OutputStream os = null;
        try {
            os = new FileOutputStream(mAvatar);
            avatar.compress(Bitmap.CompressFormat.PNG, 100, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                    new ModifyAsyncTask(BabyRecordFragment.this).execute();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private class ModifyAsyncTask extends WeakAsyncTask<Void, Void, String, BabyRecordFragment> {

        public ModifyAsyncTask(BabyRecordFragment babyRecordFragment) {
            super(babyRecordFragment);
        }

        @Override
        protected String doInBackground(BabyRecordFragment babyRecordFragment,
                                        Void... params) {
            Map<String, File> fm = null;
            if (mAvatar != null) {
                fm = new HashMap<>();
                fm.put("pic_wall_img", mAvatar);
            }
            Map<String, String> pm = new HashMap<>();
            pm.put("img_index", currentImageIndex+"");
            try {
                String response = HttpUtility.post("http://api.xiaomabao.com/athena/set_pic_wall",
                        null, ApiHelper.getParamMap(pm), fm);
                JSONObject respObj = new JSONObject(response);
                if (respObj.getString("status").equals("1")){
                    return null;
                }else{
                    return "设置失败";
                }
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(BabyRecordFragment babyRecordFragment, String result) {
            if (result != null) {
                XmbUtils.showMessage(getActivity(),result);
                return;
            }
            XmbUtils.showMessage(getActivity(), "设置成功");
        }
    }
}
