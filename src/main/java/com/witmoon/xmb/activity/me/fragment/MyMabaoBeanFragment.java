package com.witmoon.xmb.activity.me.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.adapter.MabaoBeanAdapter;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.easydone.swiperefreshendless.EndlessRecyclerOnScrollListener;
import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by de on 2016/12/19.
 */
public class MyMabaoBeanFragment extends BaseFragment {

    @BindView(R.id.tv_bean_num)
    TextView mTvBeanNum;
    @BindView(R.id.bean_detail_reclv)
    RecyclerView mBeanDetailReclv;
    @BindView(R.id.no_bean_container)
    View mNoBeanContainer;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;

    private int page = 1;
    private HeaderViewRecyclerAdapter beanHeaderAdapter;
    private LinearLayoutManager layoutManager;
    private EndlessRecyclerOnScrollListener beanListener;
    private BroadcastReceiver receiver;
    private IntentFilter filter;
    private MabaoBeanAdapter mAdapter;
    private ArrayList<JSONObject> mInfoList = new ArrayList<>();
    private int sum_num;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_bean, container, false);
        ButterKnife.bind(this, view);
        initView();
        registBroad();
        setRecRequest(page);
        return view;
    }

    private void registBroad() {
        filter = new IntentFilter(Const.INTENT_ACTION_REFRESH_BEAN);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Logger.t("Refresh").e("refresh");
                mEmptyLayout.setVisibility(View.VISIBLE);
                page = 1;
                mInfoList.clear();
                setRecRequest(page);
            }
        };
        getActivity().registerReceiver(receiver, filter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Logger.t("unregist").e("unregist");
        getActivity().unregisterReceiver(receiver);
    }

    private void initView() {
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mAdapter = new MabaoBeanAdapter(getActivity(), mInfoList);
        beanHeaderAdapter = new HeaderViewRecyclerAdapter(mAdapter);
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBeanDetailReclv.setHasFixedSize(true);
        mBeanDetailReclv.setLayoutManager(layoutManager);
        mBeanDetailReclv.setAdapter(beanHeaderAdapter);
        LinearLayout headerLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.header_bean_list, mBeanDetailReclv, false);
        beanHeaderAdapter.addHeaderView(headerLayout);

    }

    @Override
    public void setRecRequest(int currentPage) {
        UserApi.getUserBeanInfo(currentPage, new Listener<JSONObject>() {

            @Override
            public void onSuccess(JSONObject response) {
                Logger.json(response.toString());
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                try {
                    sum_num = response.getInt("number");
                    JSONArray jsonArray = response.getJSONArray("records");
                    mBeanDetailReclv.setVisibility(View.VISIBLE);
                    mNoBeanContainer.setVisibility(View.GONE);
                    if (jsonArray.length() < 10) {
                        mBeanDetailReclv.removeOnScrollListener(beanListener);
                        beanHeaderAdapter.removeFooterView();
                        beanListener = null;
                    } else {
                        beanListener = new EndlessRecyclerOnScrollListener(layoutManager) {
                            @Override
                            public void onLoadMore(int currentPage) {
                                Logger.t("PAGE").e(currentPage + "");
                                page += 1;
                                setRecRequest(page);
                            }
                        };
                        beanHeaderAdapter.removeFooterView();
                        beanHeaderAdapter.addFooterView(LayoutInflater.from(getActivity()).inflate(R.layout.view_load_more, mBeanDetailReclv, false));
                        mBeanDetailReclv.removeOnScrollListener(beanListener);
                        mBeanDetailReclv.addOnScrollListener(beanListener);
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        mInfoList.add(jsonObject);
                    }
                    if (mInfoList.size() == 0) {
                        mBeanDetailReclv.setVisibility(View.GONE);
                        mNoBeanContainer.setVisibility(View.VISIBLE);
                    }
                    mTvBeanNum.setText("" + sum_num);
                    beanHeaderAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @OnClick({R.id.bean_help_tv, R.id.send_bean_container, R.id.empty_layout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bean_help_tv:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BeanHelp);
                break;
            case R.id.send_bean_container:
                Bundle bundle = new Bundle();
                bundle.putString("bean_num", sum_num + "");
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BeanSend, bundle);
                break;
            case R.id.empty_layout:
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                setRecRequest(1);
                break;

        }
    }

}
