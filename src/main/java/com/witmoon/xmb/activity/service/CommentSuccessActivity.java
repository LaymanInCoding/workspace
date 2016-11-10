package com.witmoon.xmb.activity.service;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.adapter.ShopAdapter;
import com.witmoon.xmb.activity.service.adapter.ShopDetailAdapter;
import com.witmoon.xmb.api.ServiceApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.service.Shop;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

public class CommentSuccessActivity extends BaseActivity {

    private ArrayList<Shop> shopArrayList = new ArrayList<>();
    private ShopAdapter adapter;
    private EmptyLayout emptyLayout;
    private int page = 1;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_comment_success;
    }

    @Override
    protected String getActionBarTitle() {
        return "评价成功";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(this);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new ShopAdapter(shopArrayList, this);

        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        LinearLayout headerView = (LinearLayout) getLayoutInflater().inflate(R.layout.item_header_comment_success, mRootView, false);
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
        page = current_page;
        ServiceApi.shopList(page, shop_list_listener);
    }

    private Listener<JSONObject> shop_list_listener = new Listener<JSONObject>() {
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
                    jsonObject.put("index",i + shopArrayList.size());
                    shopArrayList.add(Shop.parse(jsonObject));
                }
                if(jsonArray.length() < 20){
                    removeFooterView();
                }else{
                    createLoadMoreView();
                    resetStatus();
                }
                page += 1;
                stringAdapter.notifyDataSetChanged();
            } catch (JSONException e) {

            }
            mRootView.setVisibility(View.VISIBLE);
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
    };

}
