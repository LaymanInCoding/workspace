package com.witmoon.xmb.activity.service;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.adapter.ReturnTicketAdapter;
import com.witmoon.xmb.api.ServiceApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.service.ReturnTicket;
import com.witmoon.xmb.model.service.Ticket;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

import java.util.ArrayList;
import java.util.HashMap;

public class ServiceReturnOrderActivity extends BaseActivity{

    private EmptyLayout emptyLayout;
    private int order_id;
    private ArrayList<ReturnTicket> tickets = new ArrayList<>();
    private LinearLayout headerView,bottomView;
    private ReturnTicketAdapter adapter;
    private String order_sn;
    private double per_price;
    private TextView returning_money;
    private ArrayList<String> ticket_ids = new ArrayList<>();
    private Button submitBtn;
    private int[] reason_textViews = {R.id.reason1,R.id.reason2,R.id.reason3,R.id.reason4,R.id.reason5,R.id.reason6,R.id.reason7,R.id.reason8,R.id.reason9,R.id.reason10};
    private ArrayList<String> check_reasons = new ArrayList<>();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_service_order_detail;
    }

    @Override
    protected String getActionBarTitle() {
        return "申请退款";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        order_id = getIntent().getIntExtra("order_id",0);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(this);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        headerView = (LinearLayout)getLayoutInflater().inflate(R.layout.item_service_return_header, mRootView, false);
        bottomView = (LinearLayout)getLayoutInflater().inflate(R.layout.item_service_return_bottom, mRootView, false);
        submitBtn = (Button) bottomView.findViewById(R.id.submitButton);
        returning_money = (TextView) bottomView.findViewById(R.id.returning_money);
        adapter = new ReturnTicketAdapter(tickets, this);
        for (int i = 0; i < reason_textViews.length;i++){
            final int index = i;
            check_reasons.add(i,"uncheck");
            bottomView.findViewById(reason_textViews[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView tv = (TextView) v;
                    Drawable drawable= getResources().getDrawable(R.mipmap.flight_butn_check_select);
                    if(check_reasons.get(index).equals("uncheck")){
                        check_reasons.set(index,"check");
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        tv.setCompoundDrawables(null,null,drawable,null);
                    }else{
                        check_reasons.set(index,"uncheck");
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        tv.setCompoundDrawables(null, null, null, null);
                    }
                }
            });
        }
        adapter.setOnSelectBtnClickListener(new ReturnTicketAdapter.OnSelectBtnClickListener() {
            @Override
            public void changeStatus(int position, int ticket_sel_status) {
                if (tickets.get(position).getTicket_status() == 0) {
                    ticket_ids.add(tickets.get(position).getTicket_id() + "");
                    tickets.get(position).setTicket_status(1);
                } else {
                    ticket_ids.remove(tickets.get(position).getTicket_id() + "");
                    tickets.get(position).setTicket_status(0);
                }
                stringAdapter.notifyDataSetChanged();
                returning_money.setText("现金：" + ticket_ids.size() * per_price + "元");
            }
        });
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,String> pm = new HashMap<>();
                String ticket_str = "";
                for(int i = 0;i < ticket_ids.size(); i++){
                    if(i != 0){
                        ticket_str +=  "|";
                    }
                    ticket_str += ticket_ids.get(i);
                }
                pm.put("order_id",order_id + "");
                pm.put("ticket_ids",ticket_str);
                String reason = "";
                for (int i = 0;i < check_reasons.size(); i++){
                    if(check_reasons.get(i).equals("check")){
                        TextView textView = (TextView) bottomView.findViewById(reason_textViews[i]);
                        reason += textView.getText() + "|";
                    }
                }
                if(reason.length() == 0){
                    CommonUtil.show(ServiceReturnOrderActivity.this, "请选择退款原因", 1000);
                    return;
                }

                submitBtn.setClickable(false);
                pm.put("reason",reason.substring(0,reason.length()-1));

                ServiceApi.submitReturnOrder(pm, callback2);
            }
        });
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        stringAdapter.addHeaderView(headerView);
        stringAdapter.addFooterView(bottomView);
        mRootView.setAdapter(stringAdapter);

        ServiceApi.return_order_detail(order_id, listener);
    }

    // 商品详情回调
    private Listener<JSONObject> callback2 = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            try {
                Boolean status = response.getInt("status") == 1 ? true : false;
                if(status){
                    Intent intent = new Intent(ServiceReturnOrderActivity.this,ReturnSubmitSuccessActivity.class);
                    intent.putExtra("returning_money",ticket_ids.size() * per_price+"");
                    intent.putExtra("current_date",response.getJSONObject("data").getString("current_date"));
                    startActivity(intent);
                    finish();
                }else{
                    mRootView.setVisibility(View.GONE);
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            } catch (JSONException e) {
                mRootView.setVisibility(View.GONE);
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
        }

        @Override
        public void onError(NetroidError error) {
            mRootView.setVisibility(View.GONE);
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }
    };

    private void setRecycleHeader(){
        TextView product_sn_view = (TextView) headerView.findViewById(R.id.product_sn);
        product_sn_view.setText(order_sn);
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

                JSONObject responseDataObject = response.getJSONObject("data");
                order_sn = responseDataObject.getString("product_sn");
                per_price = responseDataObject.getDouble("product_shop_price");
                setRecycleHeader();
                JSONArray jsonArray = responseDataObject.getJSONArray("tickets");
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    tickets.add(ReturnTicket.parse(jsonObject));
                }
                stringAdapter.notifyDataSetChanged();
            } catch (JSONException e) {

            }
            mRootView.setVisibility(View.VISIBLE);
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
    };
}
