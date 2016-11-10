package com.witmoon.xmb.activity.service;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.adapter.UserCommentAdapter;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.ServiceApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.service.UserComment;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

public class UserInfoActivity extends BaseActivity {

    private ArrayList<UserComment> commentArrayList = new ArrayList<>();
    private UserCommentAdapter adapter;
    private EmptyLayout emptyLayout;
    private int user_id;
    private String header_img;
    private String user_name;
    private RelativeLayout headerView;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_service_comment;
    }

    @Override
    protected String getActionBarTitle() {
        return "";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        user_id = getIntent().getIntExtra("user_id", 0);
        user_name = getIntent().getStringExtra("user_name");
        header_img = getIntent().getStringExtra("header_img");
        layoutManager = new LinearLayoutManager(this);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);

        headerView = (RelativeLayout)getLayoutInflater().inflate(R.layout.header_service_user, mRootView, false);
        Netroid.displayBabyImage(header_img, (ImageView) headerView.findViewById(R.id.header_img));
        ((TextView)headerView.findViewById(R.id.user_name)).setText(user_name);
        adapter = new UserCommentAdapter(commentArrayList, this);

        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        stringAdapter.addHeaderView(headerView);
        mRootView.setAdapter(stringAdapter);
        setRecRequest(1);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecRequest(1);
            }
        });
    }

    public void setRecRequest(int current_page){
        ServiceApi.userComments(user_id, current_page, shop_comments_listener);
    }

    private Listener<JSONObject> shop_comments_listener = new Listener<JSONObject>() {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            try {
                JSONArray jsonArray = response.getJSONArray("data");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    commentArrayList.add(UserComment.parse(jsonObject));
                }
                if(jsonArray.length() < 20){
                    removeFooterView();
                }else{
                    createLoadMoreView();
                    resetStatus();
                }
                stringAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
            }
            mRootView.setVisibility(View.VISIBLE);
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
    };

}
