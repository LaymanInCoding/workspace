package com.witmoon.xmb.activity.me.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.UmengStatic;
import com.witmoon.xmb.activity.me.OrderType;
import com.witmoon.xmb.activity.me.adapter.OrderAdapter;
import com.witmoon.xmb.activity.shoppingcart.OrderSubmitSuccessActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.base.BaseRecyclerViewFragmentV2;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.ListEntity;
import com.witmoon.xmb.model.Order;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单Fragment
 * Created by zhyh on 2015/6/22.
 */
public class OrderFragment extends BaseRecyclerViewFragmentV2 {
    public static final String TYPE_KEY = "all";
    private String mOrderType;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();
        }
    };

    public static OrderFragment newInstance(String type) {

        OrderFragment orderFragment = new OrderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TYPE_KEY, type);
        orderFragment.setArguments(bundle);
        return orderFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrderType = getArguments().getString(TYPE_KEY);
        getContext().registerReceiver(mReceiver, new IntentFilter(Const.INTENT_REFRESH_GOODS_ORDER));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(mReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected BaseRecyclerAdapter getListAdapter() {
        final OrderAdapter orderAdapter = new OrderAdapter(getActivity());
        orderAdapter.setOnItemButtonClickListener(new OrderAdapter.OnItemButtonClickListener() {
            @Override
            public void onItemButtonClick(Order order) {
                if (OrderType.getType(order.getOrderType()) == OrderType.TYPE_FINISHED) {
                    UmengStatic.registStat(getActivity(), "9");

                    Bundle bundle = new Bundle();
                    bundle.putSerializable("ORDER", order);
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.GOODS_EVALUATE, bundle);
                } else if (OrderType.getType(order.getOrderType()) == OrderType
                        .TYPE_WAITING_FOR_PAYMENT) {
                    UmengStatic.registStat(getActivity(), "MyOrder11");

                    JSONObject orderInfo = new JSONObject();
                    try {
                        orderInfo.put("order_sn", order.getSerialNo());
                        orderInfo.put("order_id", order.getId());
                        orderInfo.put("desc", order.getDescription());
                        orderInfo.put("subject", order.getSubject());
                        orderInfo.put("order_amount", order.getOrder_amount());
                        orderInfo.put("is_cross_border", order.getIsCrossBorder());
                        OrderSubmitSuccessActivity.startActivity(getActivity(), orderInfo
                                .toString());
                    } catch (JSONException e) {
                    }
                } else if (OrderType.getType(order.getOrderType()) == OrderType
                        .TYPE_WAITING_FOR_RECEIVING) {
                    UmengStatic.registStat(getActivity(), "MyOrder12");

                    UserApi.affirm_received(order.getId(), new Listener<JSONObject>() {
                        @Override
                        public void onPreExecute() {
                            super.onPreExecute();
                            showWaitDialog();
                        }

                        @Override
                        public void onSuccess(JSONObject response) {
                            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
                            if (twoTuple.first) {
                                try {
                                    AppContext.showToast(response.getString("error_desc"));
                                    return;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("initType", OrderType
                                    .TYPE_FINISHED);
                            UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ORDER, bundle);
                            getActivity().finish();
                        }

                        @Override
                        public void onError(NetroidError error) {
                            super.onError(error);
                            hideWaitDialog();
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            hideWaitDialog();
                        }
                    });
                }
            }
        });
        return orderAdapter;
    }

    @Override
    protected void requestData() {
        UserApi.orderList(mOrderType, mCurrentPage, getDefaultListener());
    }

    @Override
    protected ListEntity parseResponse(final JSONObject json) {
        JSONArray orderArray = null;
        Logger.json(json.toString());
        try {
            orderArray = json.getJSONArray("data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final List<Order> orders = new ArrayList<>();
        for (int i = 0; i < orderArray.length(); i++) {
            Order order = null;
            try {
//                Log.e("Order_content",orderArray.getJSONObject(i).toString());
                order = Order.parse(orderArray.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            orders.add(order);
        }
        return new ListEntity() {
            @Override
            public List<?> getList() {
                return orders;
            }

            @Override
            public boolean hasMoreData() {
                try {
                    return json.getJSONObject("paginated").getInt("more") != 0;
                } catch (JSONException e) {
                    return false;
                }
            }
        };
    }

}