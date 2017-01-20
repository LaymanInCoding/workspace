package com.witmoon.xmb.activity.service;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.adapter.CommentAdapter;
import com.witmoon.xmb.api.ServiceApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.service.Comment;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

public class ServiceCommentActivity extends BaseActivity {

    private ArrayList<Comment> commentArrayList = new ArrayList<>();
    private CommentAdapter adapter;
    private EmptyLayout emptyLayout;
    private int shop_id;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_service_comment;
    }

    @Override
    protected String getActionBarTitle() {
        return "用户评价";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        shop_id = getIntent().getIntExtra("shop_id", 0);

        layoutManager = new LinearLayoutManager(this);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);

        adapter = new CommentAdapter(commentArrayList, this);

        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
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
        ServiceApi.shopComments(shop_id,current_page,shop_comments_listener);
    }

    private Listener<JSONObject> shop_comments_listener = new Listener<JSONObject>() {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            try {
                JSONArray jsonArray = response.getJSONArray("data");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    commentArrayList.add(Comment.parse(jsonObject));
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
