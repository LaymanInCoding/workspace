package com.witmoon.xmb.activity.me.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.base.BaseRecyclerViewFragmentV2;
import com.witmoon.xmb.model.ListEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代金券界面
 * Created by zhyh on 2015/6/22.
 */
public class CashCouponFragment extends BaseRecyclerViewFragmentV2 {

    private boolean isTriggerItemClick;
    private String mOrderMoney;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (null != bundle && bundle.containsKey("type")) {
            isTriggerItemClick = true;
            mOrderMoney = bundle.getString("money");
        }
    }

    @Override
    protected BaseRecyclerAdapter getListAdapter() {
        CashCouponAdapter adapter = new CashCouponAdapter();
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected void requestData() {
        if (isTriggerItemClick) {
            UserApi.enabledCashCoupon(mCurrentPage, mOrderMoney, getDefaultListener());
            return;
        }
        UserApi.cashCoupon(mCurrentPage, getDefaultListener());
    }

    @Override
    protected ListEntity parseResponse(JSONObject responseObj) throws Exception {
        JSONArray couponArray = responseObj.getJSONArray("data");
        final List<Map<String, String>> dataList = new ArrayList<>();
        for (int i = 0; i < couponArray.length(); i++) {
            JSONObject couponObj = couponArray.getJSONObject(i);
            Map<String, String> tmap = new HashMap<>();
            tmap.put("id", couponObj.getString("bonus_id"));
            tmap.put("title", couponObj.getString("type_name"));
            tmap.put("min_goods_amount", couponObj.getString("min_goods_amount"));
            tmap.put("startTime", couponObj.getString("use_start_date"));
            tmap.put("endTime", couponObj.getString("use_end_date"));
            tmap.put("type_money",couponObj.getString("type_money"));
            dataList.add(tmap);
        }
        return new ListEntity() {
            @Override
            public List<?> getList() {
                return dataList;
            }

            @Override
            public boolean hasMoreData() {
                return false;
            }
        };
    }

    @Override
    protected void onItemClick(View view, int position) {
        if (isTriggerItemClick) {
            HashMap<String, String> tmap = (HashMap<String, String>) mAdapter.getData().get
                    (position);
            Intent intent = new Intent();
            intent.putExtra("data", tmap);
            getActivity().setResult(Activity.RESULT_OK, intent);
            getActivity().finish();
        }
    }

    static class CashCouponAdapter extends BaseRecyclerAdapter {

        @Override
        protected View onCreateItemView(ViewGroup parent, int viewType) {
            return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cash_coupon,
                    parent, false);
        }

        @Override
        protected ViewHolder onCreateItemViewHolder(View view, int viewType) {
            return new CashCouponHolder(viewType, view);
        }

        @Override
        protected void onBindItemViewHolder(ViewHolder holder, int position) {
            CashCouponHolder ccHolder = (CashCouponHolder) holder;
            Map<String, String> data = (Map<String, String>) _data.get(position);
            ccHolder.mTitleText.setText(data.get("title"));
            ccHolder.mUsageConditionText.setText(String.format("使用范围：满%s元可用", data.get
                    ("min_goods_amount")));

            Long startTime = Long.valueOf(data.get("startTime")) * 1000;
            Long endTime = Long.valueOf(data.get("endTime")) * 1000;

            ccHolder.mUsefulLifeText.setText("有效期：" + DateFormat.format("yyyy.MM.dd", startTime)
                    + "~" + DateFormat.format("yyyy.MM.dd", endTime));

//            if (data.containsKey("flag")) {
//                itemView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver
//                        .OnPreDrawListener() {
//                    @Override
//                    public boolean onPreDraw() {
//                        ViewGroup.LayoutParams layoutParams = seal.getLayoutParams();
//                        layoutParams.height = itemView.getHeight();
//                        seal.setLayoutParams(layoutParams);
//                        seal.setVisibility(View.VISIBLE);
//                        return true;
//                    }
//                });
//            }
        }
    }

    static class CashCouponHolder extends BaseRecyclerAdapter.ViewHolder {

        TextView mTitleText;
        TextView mUsageConditionText;
        TextView mUsefulLifeText;
        ImageView seal;

        public CashCouponHolder(int viewType, View v) {
            super(viewType, v);
            mTitleText = (TextView) v.findViewById(R.id.title);
            mUsageConditionText = (TextView) v.findViewById(R.id.usage_condition);
            mUsefulLifeText = (TextView) v.findViewById(R.id.useful_life);
            seal = (ImageView) v.findViewById(R.id.seal);
        }
    }
}
