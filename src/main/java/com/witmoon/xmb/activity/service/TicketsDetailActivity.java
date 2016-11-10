package com.witmoon.xmb.activity.service;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.adapter.OrderAdapter;
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

public class TicketsDetailActivity extends BaseActivity {

    private ArrayList<Ticket> tickets = new ArrayList<>();
    private TicketAdapter adapter;
    private EmptyLayout emptyLayout;
    private LinearLayout headerView;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected String getActionBarTitle() {
        return "麻包券";
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
        headerView = (LinearLayout)getLayoutInflater().inflate(R.layout.item_header_ticket_shop, mRootView, false);
        stringAdapter.addHeaderView(headerView);
        mRootView.setAdapter(stringAdapter);

        ServiceApi.view_ticket(getIntent().getIntExtra("order_id", 0), listener);
    }

    private void setRecycleHeader(JSONObject shopObject) throws JSONException {
        ImageView shop_logo = (ImageView) headerView.findViewById(R.id.shop_logo);
        TextView shop_name = (TextView) headerView.findViewById(R.id.shop_name);
        TextView shop_address = (TextView) headerView.findViewById(R.id.shop_address);

        Netroid.displayBabyImage(shopObject.getString("shop_logo"),shop_logo);
        shop_name.setText(shopObject.getString("shop_name"));
        shop_address.setText(shopObject.getString("shop_address"));
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
