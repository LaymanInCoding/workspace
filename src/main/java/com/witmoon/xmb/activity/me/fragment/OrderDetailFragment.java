package com.witmoon.xmb.activity.me.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.OrderType;
import com.witmoon.xmb.activity.shoppingcart.adapter.OrderConfirmAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmblibrary.linearlistview.LinearListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单详情Fragment
 * Created by zhyh on 2015/7/29.
 */
public class OrderDetailFragment extends BaseFragment {

    public static final String KEY_ORDER_TYPE = "ORDER_TYPE";
    public static final String KEY_ORDER_SN ="ORDER_SN";
    public static final String KEY_ORDER_ID ="ORDER_ID";



    private String mOrderType;
    private String mOrderSn;
    private String mOrderId;

    private TextView mOrderTypeNameText;
    private TextView mOrderSerialNoText;
    private TextView mOrderTimeText;
    private TextView mReceiverText;
    private TextView mReceiverPhoneText;
    private TextView mReceiverAddressText;
    private View express_btn;
    private EmptyLayout emptyLayout;

    private TextView mTotalPriceText;
    private TextView mTotalPaymentText;
    private TextView mFreightChargeText;
    private TextView mCardFee;

    private TextView mBonusText_1;    // 专场优惠
    private TextView mBonusText_2;    // 代金券优惠
    private TextView mBonusText_3;    // 红包优惠
    private TextView mBonusText_4;    // 麻豆优惠

    private LinearListView mGoodsListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mOrderId = arguments.getString(KEY_ORDER_ID);
            mOrderType = arguments.getString(KEY_ORDER_TYPE);
            mOrderSn = arguments.getString(KEY_ORDER_SN);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);

        mOrderTypeNameText = (TextView) view.findViewById(R.id.title);
        mOrderSerialNoText = (TextView) view.findViewById(R.id.serial_no);
        mOrderTimeText = (TextView) view.findViewById(R.id.time);
        mReceiverText = (TextView) view.findViewById(R.id.name);
        mReceiverPhoneText = (TextView) view.findViewById(R.id.phone);
        mReceiverAddressText = (TextView) view.findViewById(R.id.address);
        express_btn = view.findViewById(R.id.express_btn);

        mTotalPaymentText = (TextView) view.findViewById(R.id.total_payment);
        mTotalPriceText = (TextView) view.findViewById(R.id.total_price);
        mFreightChargeText = (TextView) view.findViewById(R.id.freight_charge);
        mCardFee = (TextView) view.findViewById(R.id.card_fee);
        mBonusText_1 = (TextView) view.findViewById(R.id.favorable_charge_1);
        mBonusText_2 = (TextView) view.findViewById(R.id.favorable_charge_2);
        mBonusText_3 = (TextView) view.findViewById(R.id.favorable_charge_3);
        mBonusText_4 = (TextView) view.findViewById(R.id.favorable_charge_4);

        mGoodsListView = (LinearListView) view.findViewById(R.id.goods_list);
        emptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        UserApi.orderDetail(mOrderSn, new Listener<JSONObject>() {
            @Override
            public void onError(NetroidError error) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onPreExecute() {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }

            @Override
            public void onSuccess(JSONObject response) {
                TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
                if (!tt.first) {
                    AppContext.showToast(tt.second);
                    return;
                }
                try {
                    JSONObject dataObj = response.getJSONObject("data");
                    Logger.json(dataObj.toString());
                    List<Map<String, String>> goodsList = parseGoodsList(dataObj.getJSONArray
                            ("goods_list"));
                    OrderConfirmAdapter adapter = new OrderConfirmAdapter(getContext(), goodsList);// getActivity => getContext
                    mGoodsListView.setLinearAdapter(adapter);
                    parseDataObj(dataObj);
                    emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                } catch (JSONException e) {
                   e.printStackTrace();
                }
            }
        });
    }

    // 解析商品列表
    private List<Map<String, String>> parseGoodsList(JSONArray goodsArray) throws JSONException {

        List<Map<String, String>> dataList = new ArrayList<>();
        for (int i = 0; i < goodsArray.length(); i++) {
            JSONObject goods = goodsArray.getJSONObject(i);
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("id", goods.getString("goods_id"));
            dataMap.put("title", goods.getString("name"));
            dataMap.put("url", goods.getString("img"));
            dataMap.put("price_formatted", goods.getString("goods_price_formatted"));
            dataMap.put("count", goods.getString("goods_number"));
            try{
                dataMap.put("goods_attr", goods.getString("goods_attr"));
            }catch (Exception e){

            }
            dataList.add(dataMap);
        }
        return dataList;
    }

    private void parseDataObj(final JSONObject dataObj) throws JSONException {
        mOrderTypeNameText.setText("订单状态："+getResources().getString(OrderType.getType(mOrderType).getTitle()));
        mOrderSerialNoText.setText("订单号：" + dataObj.getString("order_sn"));
        mOrderTimeText.setText("下单时间：" + dataObj.getString("add_time_formatted"));
        mReceiverText.setText("收货人："+dataObj.getString("consignee"));
        mReceiverPhoneText.setText(dataObj.getString("mobile"));
        mReceiverAddressText.setText(dataObj.getString("province")+dataObj.getString("city")+dataObj.getString("district")+dataObj.getString("address"));
        mTotalPriceText.setText(dataObj.getString("goods_amount_formatted"));
        mTotalPaymentText.setText(dataObj.getString("order_amount_formatted"));
        mBonusText_1.setText(dataObj.getString("discount_formatted"));
        mBonusText_2.setText(dataObj.getString("coupus_formatted"));
        mBonusText_3.setText(dataObj.getString("bonus_formatted"));
        mBonusText_4.setText(dataObj.getString("bean_fee"));
        mFreightChargeText.setText(dataObj.getString("shipping_fee_formatted"));
        mCardFee.setText(dataObj.getString("surplus_formatted"));
        if(mOrderType.equals("shipped") || mOrderType.equals("finished")){
            express_btn.setVisibility(View.VISIBLE);
            express_btn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Bundle argument = new Bundle();

                    try {
                        argument.putString("type", dataObj.getString("shipping_name"));
                        argument.putString("invoice", dataObj.getString("invoice_no"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    UIHelper.showSimpleBack(getContext(), SimpleBackPage.LOGISTICS, argument);
                }
            });
        }else{
            express_btn.setVisibility(View.GONE);
        }
    }
}
