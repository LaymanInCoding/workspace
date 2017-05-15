package com.witmoon.xmb.activity.me.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.MySwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.card.CardOrderSubmitSuccessActivity;
import com.witmoon.xmb.activity.me.adapter.CardOrderAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.ElecCardBean;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by ming on 2017/3/22.
 */
public class CardOrderFragment extends BaseFragment implements MySwipeRefreshLayout.OnRefreshListener, CardOrderAdapter.OnPayTextClickListener {
    private View view;
    private String order_status;
    private ArrayList<ElecCardBean> data = new ArrayList<>();
    private int mCurrentPage = 1;
    private EmptyLayout mEmptyLayout;
    private MySwipeRefreshLayout mSwipeRefreshLayout;
    private CardOrderAdapter mAdapter;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onRefresh();
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_card_order, container, false);
            mRootView = (RecyclerView) view.findViewById(R.id.my_recycler);
            mSwipeRefreshLayout = (MySwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
            mSwipeRefreshLayout.setOnRefreshListener(this);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.main_green, R.color.main_gray, R.color
                    .main_black, R.color.main_purple);
            mAdapter = new CardOrderAdapter(getContext(), data);
            mAdapter.setOnPayTextClickListener(this);
            stringAdapter = new HeaderViewRecyclerAdapter(mAdapter);
            layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRootView.setLayoutManager(layoutManager);
            mRootView.setHasFixedSize(true);
            mRootView.setAdapter(stringAdapter);
            mEmptyLayout = (EmptyLayout) view.findViewById(R.id.empty_layout);
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRecRequest(1);
                }
            });
            setRecRequest(1);
            getContext().registerReceiver(mReceiver, new IntentFilter(Const.INTENT_REFRESH_CARD_ORDER));
        }
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        order_status = getArguments().getString("order_status");
    }

    @Override
    public void setRecRequest(int currentPage) {
        mCurrentPage = currentPage;
        UserApi.cardOrderList(order_status, mCurrentPage, listener);
    }

    private Listener<JSONObject> listener = new Listener<JSONObject>() {
        @Override
        public void onFinish() {
            super.onFinish();
            mSwipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                XmbUtils.showMessage(getContext(), tt.second);
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                return;
            }
            if (mCurrentPage == 1) {
                data.clear();
            }
            Logger.json(response.toString());

            JSONArray orderArray = null;
            try {
                orderArray = response.getJSONArray("data");
                if (mCurrentPage == 1 && orderArray.length() == 0) {
                    mEmptyLayout.setErrorType(EmptyLayout.NODATA);
                    return;
                }
                for (int i = 0; i < orderArray.length(); i++) {
                    ElecCardBean bean = ElecCardBean.parse(orderArray.getJSONObject(i));
                    data.add(bean);
                }
                if (orderArray.length() < 20) {
                    if (mCurrentPage != 1) {
                        removeFooterView();
                    }
                    mRootView.scrollToPosition(0);
                } else {
                    createLoadMoreView();
                    resetStatus();
                    if (mCurrentPage == 1) {
                        mRootView.scrollToPosition(0);
                    }
                }
                mAdapter.notifyDataSetChanged();
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                mCurrentPage += 1;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onRefresh() {
        mAdapter.notifyDataSetChanged();
        mCurrentPage = 1;
        setRecRequest(mCurrentPage);
    }

    @Override
    public void onPayTextClick(int position) {
        ElecCardBean bean = data.get(position);
        JSONObject orderInfo = new JSONObject();
        try {
            String[] strings = bean.getOrder_amount().split("¥");
            orderInfo.put("order_sn", bean.getOrder_sn());
            orderInfo.put("order_amount", strings[1]);
            orderInfo.put("desc", "小麻包商城");
            orderInfo.put("subject", "小麻包商城");
            CardOrderSubmitSuccessActivity.startActivity(getActivity(), orderInfo
                    .toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetailTextClick(int position) {
        String order_sn = data.get(position).getOrder_sn();
        Bundle bundle = new Bundle();
        bundle.putString("order_sn", order_sn);
        UIHelper.showSimpleBack(getContext(), SimpleBackPage.CardOrderDetail, bundle);
    }

    @Override
    public void onDestroy() {
        Logger.d("unregister");
        super.onDestroy();
        getContext().unregisterReceiver(mReceiver);
    }
}
