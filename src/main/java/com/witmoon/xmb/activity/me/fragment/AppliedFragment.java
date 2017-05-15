package com.witmoon.xmb.activity.me.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.Out_ServiceActivity;
import com.witmoon.xmb.activity.me.adapter.Out_PriceAdapter;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.Out_;
import com.witmoon.xmb.model.RefreshEvent;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.rx.RxBus;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by de on 2015/12/3.
 */
public class AppliedFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private Out_PriceAdapter mOut_PriceAdapter;
    private EmptyLayout mEmptyLayout;
    private ArrayList<Out_> data = new ArrayList<>();
    private int page = 1;
    private boolean has_footer = false;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEvent();
    }

    private void initEvent() {
        Subscription subscription = RxBus.getDefault().toObservable(RefreshEvent.class)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<RefreshEvent>() {
                    @Override
                    public void call(RefreshEvent event) {
                        Logger.d(event.getIsRefresh());
                        data.clear();
                        setRecRequest(1);
                    }
                });
        addSubscribe(subscription);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.applied_layout, container, false);
        mRootView = (RecyclerView) view.findViewById(R.id.my_recycler);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.rgb(47, 223, 189));
        initAdapter();
        stringAdapter = new HeaderViewRecyclerAdapter(mOut_PriceAdapter);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setLayoutManager(layoutManager);
        mRootView.setHasFixedSize(true);
        mRootView.setAdapter(stringAdapter);
        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.empty_layout);
        mEmptyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecRequest(1);
            }
        });
        setRecRequest(1);
        return view;
    }

    @Override
    public void setRecRequest(int currentPage) {
        page = currentPage;
        UserApi.applyList(page + "", mListener);

    }

    private Listener<JSONObject> mListener = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            if (page == 1) {
                data.clear();
            }
            Log.e("response", response.toString());
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            JSONArray orderArray = null;
            try {
                orderArray = response.getJSONArray("data");
                if (page == 1 && orderArray.length() == 0) {
                    mEmptyLayout.setErrorType(EmptyLayout.NODATA);
                } else {
                    for (int i = 0; i < orderArray.length(); i++) {
                        Out_ out = Out_.parse(orderArray.getJSONObject(i));
                        data.add(out);
                    }
                    if (orderArray.length() < 20) {
                        if (page != 1) {
                            removeFooterView();
                        }
                        mRootView.scrollToPosition(0);
                        has_footer = false;
                    } else {
                        has_footer = true;
                        createLoadMoreView();
                        resetStatus();
                        if (page == 1) {
                            mRootView.scrollToPosition(0);
                        }
                    }
                    mOut_PriceAdapter.notifyDataSetChanged();
                    mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    page += 1;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void initAdapter() {
        mOut_PriceAdapter = new Out_PriceAdapter(getContext(), data);
        mOut_PriceAdapter.setOnItemButtonClickListener(new Out_PriceAdapter.OnItemButtonClickListener() {
            @Override
            public void onItemButtonClick(Out_ order, int position) {
                order.setPosion(position);
                order.setIs_(1);
                if (order.getRefund_status().equals("1")) {
                    Intent mIntent = new Intent(getActivity(), Out_ServiceActivity.class);
                    mIntent.putExtra("order", order);
                    startActivity(mIntent);
                    return;
                }
                Bundle mb = new Bundle();
                mb.putSerializable("order", order);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.JINDU, mb);
            }
        });
        mOut_PriceAdapter.setOnItemTypeClickListener(new Out_PriceAdapter.OnItemTypeClickListener() {
            @Override
            public void OnItemTypeClick(Out_ order, int position) {
                order.setPosion(position);
                order.setIs_(1);
                if (order.getRefund_status().equals("2")) {
                    Bundle mb = new Bundle();
                    mb.putSerializable("order", order);
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.JINDU, mb);
                } else if (order.getRefund_status().equals("3")) {
                    Intent mIntent = new Intent(getActivity(), Out_ServiceActivity.class);
                    mIntent.putExtra("order", order);
                    startActivity(mIntent);
                } else if (order.getRefund_status().equals("5")) {
                    Intent mIntent = new Intent(getActivity(), Fill_info_Fragent.class);
                    mIntent.putExtra("order", order);
                    startActivity(mIntent);
                }
            }
        });
        mOut_PriceAdapter.setmOnItemAllClickListener(new Out_PriceAdapter.OnItemAllClickListener() {
            @Override
            public void OnItemAllClickListener(Out_ order) {
                Bundle argument = new Bundle();
                argument.putString(OrderDetailFragment.KEY_ORDER_TYPE, "");
                argument.putString(OrderDetailFragment.KEY_ORDER_SN, order.getOrder_sn());
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ORDER_DETAIL, argument);
            }
        });
    }

    @Override
    public void onRefresh() {
        mOut_PriceAdapter.notifyDataSetChanged();
        page = 1;
        setRecRequest(page);
    }
}
