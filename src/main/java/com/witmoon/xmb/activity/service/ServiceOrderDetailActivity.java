package com.witmoon.xmb.activity.service;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.adapter.TicketAdapter;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.ServiceApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.service.Ticket;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

public class ServiceOrderDetailActivity extends BaseActivity {

    private EmptyLayout emptyLayout;
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private TicketAdapter adapter;
    private LinearLayout headerView,bottomView;
    private int product_id;
    private int order_id;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_service_order_detail;
    }

    @Override
    protected String getActionBarTitle() {
        return "订单详情";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        layoutManager = new LinearLayoutManager(this);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new TicketAdapter(tickets, this);
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        headerView = (LinearLayout)getLayoutInflater().inflate(R.layout.item_header_ticket_product, mRootView, false);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceOrderDetailActivity.this,ProductDetailActivity.class);
                intent.putExtra("product_id",product_id);
                startActivity(intent);
            }
        });
        bottomView = (LinearLayout)getLayoutInflater().inflate(R.layout.item_bottom_ticket, mRootView, false);
        stringAdapter.addHeaderView(headerView);
        stringAdapter.addFooterView(bottomView);
        mRootView.setAdapter(stringAdapter);
        order_id = getIntent().getIntExtra("order_id", 0);
        ServiceApi.order_detail(order_id, listener);
    }

    private void setRecycleHeader(JSONObject object) {
        ImageView product_img = (ImageView) headerView.findViewById(R.id.product_img);
        TextView product_name = (TextView) headerView.findViewById(R.id.product_name);
        TextView product_shop_price = (TextView) headerView.findViewById(R.id.product_shop_price);

        try {
            Netroid.displayBabyImage(object.getString("product_img"), product_img);
            product_name.setText(object.getString("product_name"));
            product_shop_price.setText("¥ "+object.getString("product_shop_price"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setRecycleFooter(JSONObject object1,JSONObject object2,int return_status) {
        TextView shop_name = (TextView) bottomView.findViewById(R.id.shop_name);
        TextView shop_address = (TextView) bottomView.findViewById(R.id.shop_address);
        TextView shop_phone = (TextView) bottomView.findViewById(R.id.shop_phone);

        try {
            shop_name.setText(object1.getString("shop_name"));
            shop_address.setText(object1.getString("shop_address"));
            shop_phone.setText(object1.getString("shop_phone"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LinearLayout return_layout = (LinearLayout) bottomView.findViewById(R.id.return_container);
        Button return_btn = (Button) bottomView.findViewById(R.id.return_btn);
        if(return_status == 1){
            return_layout.setVisibility(View.VISIBLE);
        }else{
            return_layout.setVisibility(View.GONE);
        }

        return_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServiceOrderDetailActivity.this,ServiceReturnOrderActivity.class);
                intent.putExtra("order_id",order_id);
                startActivity(intent);
                finish();
            }
        });

        TextView product_sn = (TextView) bottomView.findViewById(R.id.product_sn);
        TextView order_phone = (TextView) bottomView.findViewById(R.id.order_mobile);
        TextView pay_time = (TextView) bottomView.findViewById(R.id.pay_time);
        TextView product_num = (TextView) bottomView.findViewById(R.id.product_num);
        TextView order_amount = (TextView) bottomView.findViewById(R.id.order_amount);
        TextView surplus = (TextView) bottomView.findViewById(R.id.surplus);
        try {
            product_id = object2.getInt("product_id");
            product_sn.setText("订单号："+object2.getString("product_sn"));
            order_phone.setText("购买手机号："+object2.getString("mobile_phone"));
            pay_time.setText("付款时间："+object2.getString("pay_time"));
            product_num.setText("数量："+object2.getString("product_number"));
            surplus.setText("麻包卡金额："+object2.getString("surplus") + "元");
            order_amount.setText("总价："+object2.getString("order_amount")+"元");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Listener<JSONObject> listener = new Listener<JSONObject>() {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {

            try {
                if(response.getInt("status") == 0){
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                    return;
                }
                setRecycleHeader(response.getJSONObject("product_info"));
                setRecycleFooter(response.getJSONObject("shop_info"),response.getJSONObject("order_info"),response.getInt("return_status"));
                JSONArray jsonArray = response.getJSONArray("ticket_info");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    tickets.add(Ticket.parse(jsonObject));
                }
                stringAdapter.notifyDataSetChanged();
            } catch (JSONException e) {

            }
            mRootView.setVisibility(View.VISIBLE);
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
    };



}
