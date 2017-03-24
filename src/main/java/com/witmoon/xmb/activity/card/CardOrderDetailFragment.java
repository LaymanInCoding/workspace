package com.witmoon.xmb.activity.card;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.MabaoCardApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.XmbUtils;
import com.witmoon.xmblibrary.linearlistview.LinearListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by ming on 2017/3/21.
 */
public class CardOrderDetailFragment extends BaseFragment {
    private View view;
    private String order_sn;
    private LinearListView mGoodsListView;
    private LinearListView mCardInfoListView;
    private TextView mOrderSnText;
    private TextView mOrderTimeText;
    private TextView mOrderFreightText;
    private TextView mOrderAmountText;
    private TextView mOrderMbcardText;
    private TextView mOrderPayText;

    private EmptyLayout mEmptyLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        order_sn = getArguments().getString("order_sn");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_card_detail, container, false);
            mEmptyLayout = (EmptyLayout) view.findViewById(R.id.empty_layout);
            mGoodsListView = (LinearListView) view.findViewById(R.id.goods_list);
            mCardInfoListView = (LinearListView) view.findViewById(R.id.card_info_list);
            mOrderSnText = (TextView) view.findViewById(R.id.order_id);
            mOrderTimeText = (TextView) view.findViewById(R.id.add_time);
            mOrderFreightText = (TextView) view.findViewById(R.id.order_freight);
            mOrderAmountText = (TextView) view.findViewById(R.id.order_amount);
            mOrderMbcardText = (TextView) view.findViewById(R.id.order_mbcard);
            mOrderPayText = (TextView) view.findViewById(R.id.order_should_pay);
            setRecRequest(1);
        }
        return view;
    }

    @Override
    public void setRecRequest(int currentPage) {
        MabaoCardApi.order_detail(order_sn, mListener);
    }

    private Listener<JSONObject> mListener = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                XmbUtils.showMessage(getContext(), tt.second);
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                return;
            }
            try {
                JSONObject dataObj = response.getJSONObject("data");
                List<Map<String, Object>> cardData = parseCardList(dataObj);
                List<Map<String, Object>> cardInfoData = parseVirtualList(dataObj);
                Logger.d(dataObj.getJSONArray("virtual").length());

                CardInfoAdapter orderInfoListAdapter = new CardInfoAdapter
                        (getContext(), cardInfoData);
                CardOrderListAdapter orderListAdapter = new CardOrderListAdapter
                        (getContext(), cardData);
                mGoodsListView.setLinearAdapter(orderListAdapter);
                mCardInfoListView.setLinearAdapter(orderInfoListAdapter);
                if (dataObj.getJSONArray("virtual").length() == 0) {
                    mCardInfoListView.setVisibility(View.GONE);
                } else {
                    mCardInfoListView.setVisibility(View.VISIBLE);
                }
                mOrderTimeText = (TextView) view.findViewById(R.id.add_time);
                mOrderFreightText = (TextView) view.findViewById(R.id.order_freight);
                mOrderAmountText = (TextView) view.findViewById(R.id.order_amount);
                mOrderMbcardText = (TextView) view.findViewById(R.id.order_mbcard);
                mOrderPayText = (TextView) view.findViewById(R.id.order_should_pay);
                mOrderSnText.setText(dataObj.getString("order_sn"));
                mOrderTimeText.setText(dataObj.getString("add_time"));
                mOrderFreightText.setText(dataObj.getString("shipping_fee"));
                mOrderAmountText.setText(dataObj.getString("card_amount"));
                mOrderMbcardText.setText(dataObj.getString("mabao_card_amount"));
                mOrderPayText.setText(dataObj.getString("order_amount"));
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(NetroidError error) {
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }
    };

    private List<Map<String, Object>> parseVirtualList(JSONObject goodsObject)
            throws JSONException {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Map<String, String>> tmpList = new ArrayList<>();
        Map<String, Object> tmpMap = new HashMap<>();
        JSONArray cardInfoArray = goodsObject.getJSONArray("virtual");
        for (int i = 0; i < cardInfoArray.length(); i++) {
            JSONObject infos = cardInfoArray.getJSONObject(i);
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("card_name", infos.getString("card_name"));
            dataMap.put("card_no", infos.getString("card_no"));
            dataMap.put("card_pass", infos.getString("card_pass"));
            tmpList.add(dataMap);
        }
        tmpMap.put("virtual", tmpList);
        dataList.add(tmpMap);
        return dataList;
    }

    private List<Map<String, Object>> parseCardList(JSONObject goodsObject)
            throws JSONException {
        List<Map<String, Object>> dataList = new ArrayList<>();
        List<Map<String, String>> tmpList = new ArrayList<>();
        Map<String, Object> tmpMap = new HashMap<>();
        JSONArray goodsArray = goodsObject.getJSONArray("card_list");
        for (int i = 0; i < goodsArray.length(); i++) {
            JSONObject goods = goodsArray.getJSONObject(i);
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("card_img", goods.getString("card_img"));
            dataMap.put("card_name", goods.getString("card_name"));
            dataMap.put("card_money", goods.getString("card_money"));
            dataMap.put("card_number", goods.getInt("card_number") + "");
            tmpList.add(dataMap);
        }
        tmpMap.put("cards", tmpList);
        dataList.add(tmpMap);
        return dataList;
    }
}
