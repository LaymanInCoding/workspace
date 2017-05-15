package com.witmoon.xmb.activity.mbq.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mbq.adapter.MessageAdapter;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

public class MessageActivity extends BaseActivity {

    private ArrayList<JSONObject> mDatas = new ArrayList<>();
    private MessageAdapter adapter;
    private int page = 1;
    private EmptyLayout emptyLayout;
    private View emptyView;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_mbq_message;
    }

    @Override
    protected String getActionBarTitle() {
        return "消息";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        initRecycleView();
    }

    //初始化 CirclePost recycleview
    private void initRecycleView() {
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(mDatas, this);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        mRootView.setAdapter(stringAdapter);
        emptyView = findViewById(R.id.xiaoren);
        setRecRequest(page);
    }

    public void setRecRequest(int page0) {
        CircleApi.get_notification(page, new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Logger.json(response.toString());
                try {
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        mDatas.add(data.getJSONObject(i));
                    }
                    stringAdapter.notifyDataSetChanged();
                    emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    if (data.length() == 0) {
                        emptyView.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
