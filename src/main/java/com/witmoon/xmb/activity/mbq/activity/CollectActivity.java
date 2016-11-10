package com.witmoon.xmb.activity.mbq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mbq.adapter.CollectPostAdapter;
import com.witmoon.xmb.activity.mbq.adapter.PostAdapter;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.circle.CirclePost;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

public class CollectActivity extends BaseActivity {

    private CollectPostAdapter adapter;
    private ArrayList<CirclePost> mDatas = new ArrayList<>();
    private int page = 1;
    private EmptyLayout emptyLayout;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_mbq_collect;
    }

    @Override
    protected String getActionBarTitle() {
        return "我的收藏";
    }

    @Override
    protected void  initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new CollectPostAdapter(mDatas,this);
        emptyLayout = (EmptyLayout)findViewById(R.id.error_layout);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecRequest(page);
            }
        });
        adapter.setOnItemClickListener(new CollectPostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(CollectActivity.this, PostDetailActivity.class);
                intent.putExtra("post_id", mDatas.get(position).getPost_id());
                startActivity(intent);
            }
        });
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        mRootView.setAdapter(stringAdapter);
        setRecRequest(page);
    }

    public void setRecRequest(int page0){
        CircleApi.get_my_collect(page, new Listener<JSONObject>() {

            @Override
            public void onError(NetroidError error) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mDatas.add(CirclePost.parse(jsonArray.getJSONObject(i)));
                    }
                    mRootView.setVisibility(View.VISIBLE);
                    if(jsonArray.length() < 20){
                        removeFooterView();
                    }else{
                        createLoadMoreView();
                        resetStatus();
                    }
                    page += 1;
                    adapter.notifyDataSetChanged();
                    if (mDatas.size() == 0) {
                        emptyLayout.setErrorType(EmptyLayout.NODATA);
                    } else {
                        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    }
                } catch (JSONException e) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }
        });
    }
}
