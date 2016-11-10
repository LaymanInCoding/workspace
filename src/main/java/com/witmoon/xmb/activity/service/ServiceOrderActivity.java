package com.witmoon.xmb.activity.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.adapter.OrderAdapter;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.api.ServiceApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.service.Order;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

public class ServiceOrderActivity extends BaseActivity implements View.OnClickListener {

    private EmptyLayout emptyLayout;
    private OrderAdapter adapter;
    private int page = 1;
    private ArrayList<Order> orderList = new ArrayList<>();
    private String type = "";
    private TextView await_pay,await_use,await_comment,after_sales;
    private int show_wait = 1;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.content_service_order;
    }

    @Override
    protected String getActionBarTitle() {
        return "我的服务";
    }

    BroadcastReceiver deleteCommentOrder = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int index = intent.getIntExtra("index",0);
            orderList.remove(index);
            stringAdapter.notifyDataSetChanged();
            if(orderList.size() == 0){
                emptyLayout.setErrorType(EmptyLayout.NODATA);
            }
        }
    };

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        IntentFilter refresh = new IntentFilter(Const.INTENT_ACTION_REFRESH_SERVICE_ORDER);
        registerReceiver(deleteCommentOrder, refresh);
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(this);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new OrderAdapter(orderList, this);
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        mRootView.setAdapter(stringAdapter);
        String type2 = getIntent().getStringExtra("otype");
        await_pay = (TextView) findViewById(R.id.await_pay);
        await_use = (TextView) findViewById(R.id.await_use);
        await_comment = (TextView) findViewById(R.id.await_comment);
        after_sales = (TextView) findViewById(R.id.after_sales);

        await_pay.setOnClickListener(this);
        await_use.setOnClickListener(this);
        await_comment.setOnClickListener(this);
        after_sales.setOnClickListener(this);

        if(type2 !=null && type2.equals(ServiceApi.AWAIT_USE)){
            await_use.callOnClick();
        }else {
            await_pay.callOnClick();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(deleteCommentOrder);
    }

    public void setRecRequest(int current_page) {
        ServiceApi.my_order(type, page, listener);
    }

    private Listener<JSONObject> listener = new Listener<JSONObject>() {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if(page == 1 && show_wait == 1){
                emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }
            show_wait = 1;
        }

        @Override
        public void onSuccess(JSONObject response) {
            Log.e("data",response.toString());
            try {
                JSONArray orderJsonArray = response.getJSONArray("data");
                if(page == 1){
                    orderList.clear();
                }

                for(int i = 0; i < orderJsonArray.length(); i++){
                    Order order = Order.parse(orderJsonArray.getJSONObject(i));
                    orderList.add(order);
                }

                if(page == 1 && orderJsonArray.length() == 0){
                    emptyLayout.setErrorType(EmptyLayout.NODATA);
                    mRootView.setVisibility(View.GONE);
                }else{
                    mRootView.setVisibility(View.VISIBLE);
                    emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                }
                if(orderJsonArray.length() < 20){
                    removeFooterView();
                }else{
                    createLoadMoreView();
                    resetStatus();
                }
                stringAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.await_pay:
                if (!type.equals(ServiceApi.AWAIT_PAY)) {
                    page = 1;
                    orderList.clear();
                    await_pay.setTextColor(Color.parseColor("#729481"));
                    Drawable drawable1= getResources().getDrawable(R.mipmap.fywu5);
                    drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                    await_pay.setCompoundDrawables(null, drawable1, null, null);
                    await_use.setTextColor(Color.parseColor("#606060"));
                    Drawable drawable2= getResources().getDrawable(R.mipmap.fuwu3);
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                    await_use.setCompoundDrawables(null, drawable2, null, null);
                    await_comment.setTextColor(Color.parseColor("#606060"));
                    Drawable drawable3= getResources().getDrawable(R.mipmap.wufu1);
                    drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
                    await_comment.setCompoundDrawables(null, drawable3, null, null);
                    after_sales.setTextColor(Color.parseColor("#606060"));
                    Drawable drawable4= getResources().getDrawable(R.mipmap.fuwu4);
                    drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight());
                    after_sales.setCompoundDrawables(null, drawable4, null, null);
                    type = ServiceApi.AWAIT_PAY;
                    setRecRequest(1);
                }
                break;
            case R.id.await_use:
                if (!type.equals(ServiceApi.AWAIT_USE)) {
                    page = 1;
                    orderList.clear();
                    await_pay.setTextColor(Color.parseColor("#606060"));
                    Drawable drawable1= getResources().getDrawable(R.mipmap.wufu2);
                    drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                    await_pay.setCompoundDrawables(null, drawable1, null, null);
                    await_use.setTextColor(Color.parseColor("#729481"));
                    Drawable drawable2= getResources().getDrawable(R.mipmap.fuwu8);
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                    await_use.setCompoundDrawables(null, drawable2, null, null);
                    await_comment.setTextColor(Color.parseColor("#606060"));
                    Drawable drawable3= getResources().getDrawable(R.mipmap.wufu1);
                    drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
                    await_comment.setCompoundDrawables(null, drawable3, null, null);
                    after_sales.setTextColor(Color.parseColor("#606060"));
                    Drawable drawable4= getResources().getDrawable(R.mipmap.fuwu4);
                    drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight());
                    after_sales.setCompoundDrawables(null, drawable4, null, null);
                    type = ServiceApi.AWAIT_USE;
                    setRecRequest(1);
                }
                break;
            case R.id.await_comment:
                if (!type.equals(ServiceApi.AWAIT_COMMENT)) {
                    page = 1;
                    orderList.clear();
                    await_pay.setTextColor(Color.parseColor("#606060"));
                    Drawable drawable1= getResources().getDrawable(R.mipmap.wufu2);
                    drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                    await_pay.setCompoundDrawables(null, drawable1, null, null);
                    await_use.setTextColor(Color.parseColor("#606060"));
                    Drawable drawable2= getResources().getDrawable(R.mipmap.fuwu3);
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                    await_use.setCompoundDrawables(null, drawable2, null, null);
                    await_comment.setTextColor(Color.parseColor("#729481"));
                    Drawable drawable3= getResources().getDrawable(R.mipmap.fuwu6);
                    drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
                    await_comment.setCompoundDrawables(null, drawable3, null, null);
                    after_sales.setTextColor(Color.parseColor("#606060"));
                    Drawable drawable4= getResources().getDrawable(R.mipmap.fuwu4);
                    drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight());
                    after_sales.setCompoundDrawables(null, drawable4, null, null);
                    type = ServiceApi.AWAIT_COMMENT;
                    setRecRequest(1);
                }
                break;
            case R.id.after_sales:
                if (!type.equals(ServiceApi.AFTER_SALES)) {
                    page = 1;
                    orderList.clear();
                    await_pay.setTextColor(Color.parseColor("#606060"));
                    Drawable drawable1= getResources().getDrawable(R.mipmap.wufu2);
                    drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());
                    await_pay.setCompoundDrawables(null, drawable1, null, null);
                    await_use.setTextColor(Color.parseColor("#606060"));
                    Drawable drawable2= getResources().getDrawable(R.mipmap.fuwu3);
                    drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());
                    await_use.setCompoundDrawables(null, drawable2, null, null);
                    await_comment.setTextColor(Color.parseColor("#606060"));
                    Drawable drawable3= getResources().getDrawable(R.mipmap.wufu1);
                    drawable3.setBounds(0, 0, drawable3.getMinimumWidth(), drawable3.getMinimumHeight());
                    await_comment.setCompoundDrawables(null, drawable3, null, null);
                    after_sales.setTextColor(Color.parseColor("#729481"));
                    Drawable drawable4= getResources().getDrawable(R.mipmap.fuwu7);
                    drawable4.setBounds(0, 0, drawable4.getMinimumWidth(), drawable4.getMinimumHeight());
                    after_sales.setCompoundDrawables(null, drawable4, null, null);
                    type = ServiceApi.AFTER_SALES;
                    setRecRequest(1);
                }
                break;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        page = 1;
        orderList.clear();
        show_wait = 0;
        setRecRequest(1);
    }
}
