package com.witmoon.xmb.activity.mbq.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mbq.adapter.PostAdapter;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.circle.CirclePost;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

public class CircleActivity extends BaseActivity {

    private ImageView circleLogoImageView,circleJoinImageView,postImageView;
    private TextView circleTitleTextView,circleDescTextView;
    private PostAdapter adapter;
    private EmptyLayout emptyLayout;
    private ArrayList<CirclePost> mDatas = new ArrayList<>();
    private int page = 1;
    private int circle_id;
    private Boolean circle_is_join;
    private String circle_logo,circle_name,circle_post_cnt;

    private BroadcastReceiver refreshCurrentActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mDatas.clear();
            page = 1;
            setRecRequest(page);
        }
    };

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(refreshCurrentActivity);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_circle;
    }

    @Override
    protected String getActionBarTitle() {
        return "全部帖子";
    }

    @Override
    protected void  initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        initView();
        circle_id = getIntent().getIntExtra("circle_id", 0);
        circle_logo = getIntent().getStringExtra("circle_log");
        circle_name = getIntent().getStringExtra("circle_name");
        circle_is_join = getIntent().getBooleanExtra("circle_is_join",false);
        fillView();
        initRecycleView();
        IntentFilter refreshActivity = new IntentFilter(Const.INTENT_ACTION_REFRESH_CIRCLE);
        registerReceiver(refreshCurrentActivity, refreshActivity);
    }

    //初始化界面上元素
    private void initView(){
        circleLogoImageView = (ImageView)findViewById(R.id.circle_img);
        circleTitleTextView = (TextView)findViewById(R.id.circle_name);
        circleDescTextView = (TextView)findViewById(R.id.circle_desc);
        circleJoinImageView = (ImageView)findViewById(R.id.circle_join);
        postImageView = (ImageView)findViewById(R.id.toolbar_right_img);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        circleJoinImageView.setOnClickListener(this);
        postImageView.setOnClickListener(this);
    }

    //初始化 CirclePost recycleview
    private void initRecycleView(){
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new PostAdapter(mDatas,this);
        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(CircleActivity.this,PostDetailActivity.class);
                intent.putExtra("post_id",mDatas.get(position).getPost_id());
                startActivity(intent);
            }
        });
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        mRootView.setAdapter(stringAdapter);
        setRecRequest(page);
    }

    public void setRecRequest(int page1){
        CircleApi.circle_post_list(circle_id, page, new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.has("circle_detail")) {
                        fillView(response.getJSONObject("circle_detail"));
                    }
                    JSONArray responseData = response.getJSONArray("data");
                    for (int i = 0; i < responseData.length(); i++) {
                        CirclePost post = CirclePost.parse(responseData.getJSONObject(i));
                        mDatas.add(post);
                    }

                    if (responseData.length() < 20) {
                        removeFooterView();
                    } else {
                        createLoadMoreView();
                        resetStatus();
                    }
                    if (responseData.length() == 0 && page == 1) {
                        emptyLayout.setErrorType(EmptyLayout.NODATA);
                    } else {
                        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    }
                    page += 1;
                    stringAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }
        });
    }

    private void fillView(JSONObject jsonObject){
        try {
            circle_logo = jsonObject.getString("circle_logo");
            circle_name = jsonObject.getString("circle_name");
            circle_post_cnt = jsonObject.getString("circle_post_cnt") + "个话题";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        fillView();
    }

    private void fillView(){
        Netroid.displayBabyImage(circle_logo,circleLogoImageView);
        circleTitleTextView.setText(circle_name);
        circleDescTextView.setText(circle_post_cnt);
        if(XmbUtils.check_is_join(CircleActivity.this,circle_id)){
            circleJoinImageView.setImageResource(R.mipmap.mbq_minus);
            circle_is_join = true;
        }else{
            circleJoinImageView.setImageResource(R.mipmap.mbq_add);
            circle_is_join = false;
        }
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.toolbar_right_img:
                if(!circle_is_join){
                    XmbUtils.showMessageConfirm(this, "加入话题所在圈子才能发帖哦~", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            join_circle();
                        }
                    });
                    return;
                }
                Intent intent = new Intent(CircleActivity.this,PostActivity_new.class);
                intent.putExtra("circle_id",circle_id + "");
                startActivity(intent);
                break;
            case R.id.circle_join:
                join_circle();
                break;
        }
    }

    private void join_circle(){
        XmbUtils.joinCircle(CircleActivity.this,circle_id,new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getInt("status") == 1) {
                        Intent intent = new Intent(Const.INTENT_ACTION_REFRESH_MY_MBQ);
                        if(XmbUtils.popupWindow != null && XmbUtils.popupWindow.isShowing()) {
                            XmbUtils.popupWindow.dismiss();
                        }
                        XmbUtils.showMessage(CircleActivity.this, response.getString("info"));
                        if(!circle_is_join) {
                            circle_is_join = true;
                            circleJoinImageView.setImageResource(R.mipmap.mbq_minus);
                        }else{
                            circle_is_join = false;
                            circleJoinImageView.setImageResource(R.mipmap.mbq_add);
                        }
                        sendBroadcast(intent);
                    } else {
                        XmbUtils.showMessage(CircleActivity.this, response.getString("info"));
                    }
                } catch (JSONException e) {
                    XmbUtils.showMessage(CircleActivity.this, "网络异常，请稍后重试！");
                }
            }
        });
    }

}
