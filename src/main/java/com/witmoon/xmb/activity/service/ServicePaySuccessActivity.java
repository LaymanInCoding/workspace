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
import com.witmoon.xmb.activity.service.adapter.PaySuccessTicketAdapter;
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

public class ServicePaySuccessActivity extends BaseActivity {

    private EmptyLayout emptyLayout;
    private ArrayList<Ticket> tickets = new ArrayList<>();
    private LinearLayout headerView,bottomView;


    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_service_pay_success;
    }

    @Override
    protected String getActionBarTitle() {
        return "支付成功";
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
        PaySuccessTicketAdapter adapter = new PaySuccessTicketAdapter(tickets, this);
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        headerView = (LinearLayout)getLayoutInflater().inflate(R.layout.item_header_ticket_shop2, mRootView, false);
        bottomView = (LinearLayout)getLayoutInflater().inflate(R.layout.item_service_pay_succ_bottom, mRootView, false);
        Button detail_btn = (Button) bottomView.findViewById(R.id.detailButton);
        detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServicePaySuccessActivity.this,ServiceOrderDetailActivity.class);
                intent.putExtra("order_id",getIntent().getIntExtra("order_id",0));
                startActivity(intent);
                finish();
            }
        });
        stringAdapter.addHeaderView(headerView);
        stringAdapter.addFooterView(bottomView);
        mRootView.setAdapter(stringAdapter);
        ServiceApi.view_ticket(getIntent().getIntExtra("order_id",0), listener);

        findViewById(R.id.finish_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ServicePaySuccessActivity.this,ServiceOrderActivity.class);
                intent.putExtra("otype",ServiceApi.AWAIT_USE);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setRecycleHeader(JSONObject shopObject) throws JSONException {
        ImageView shop_logo = (ImageView) headerView.findViewById(R.id.shop_logo);
        TextView shop_name = (TextView) headerView.findViewById(R.id.shop_name);

        Netroid.displayBabyImage(shopObject.getString("shop_logo"), shop_logo);
        shop_name.setText(shopObject.getString("shop_name"));
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
                setRecycleHeader(response.getJSONObject("shop_info"));
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
