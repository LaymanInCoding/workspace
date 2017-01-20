package com.witmoon.xmb.activity.service;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.adapter.ShopAdapterNew;
import com.witmoon.xmb.activity.service.adapter.SubAdapter;
import com.witmoon.xmb.activity.service.adapter.SubServiceHeaderAdapter;
import com.witmoon.xmb.api.ServiceApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by de on 2017/1/4.
 */
public class Sub_SubServiceFragment extends BaseFragment {

    private ArrayList<JSONObject> mArrayList = new ArrayList<>();
    private SubAdapter adapter;
    private EmptyLayout emptyLayout;
    private int page = 1;
    private String cat_id;
    private String cat_name;
    private View rootView;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configToolbar();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cat_id = getArguments().getString("cat_id");
        cat_name = getArguments().getString("cat_name");
        Logger.e(cat_id);
    }

    private void configToolbar() {
        Toolbar toolbar = ((BaseActivity) getActivity()).getToolBar();
        AQuery aQuery = new AQuery(getActivity(), toolbar);
        aQuery.id(R.id.top_toolbar).visible();
        aQuery.id(R.id.toolbar_right_img).gone();
        aQuery.id(R.id.toolbar_logo_img).gone();
        aQuery.id(R.id.toolbar_right_img1).gone();
        aQuery.id(R.id.toolbar_right_img2).gone();
        aQuery.id(R.id.toolbar_title_text).text(cat_name).visible();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_service_index, container, false);

            mRootView = (RecyclerView) rootView.findViewById(R.id.recycle_view);
            layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRootView.setHasFixedSize(true);
            mRootView.setLayoutManager(layoutManager);
            adapter = new SubAdapter(mArrayList, getActivity());
            stringAdapter = new HeaderViewRecyclerAdapter(adapter);
            mRootView.setAdapter(stringAdapter);
            setRecRequest(1);
            emptyLayout = (EmptyLayout) rootView.findViewById(R.id.error_layout);
            emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRecRequest(1);
                }
            });
        }
        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        return rootView;
    }


    public void setRecRequest(int current_page) {
        page = current_page;
        ServiceApi.subSubShopList(page, cat_id, shop_list_listener);
    }

    private Listener<JSONObject> shop_list_listener = new Listener<JSONObject>() {

        @Override
        public void onError(NetroidError error) {
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (page == 1) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }
        }

        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            try {
                JSONArray jsonArray = response.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    mArrayList.add(jsonObject);
                }

                if (page == 1 && jsonArray.length() == 0) {
                    emptyLayout.setErrorType(EmptyLayout.NODATA);
                    return;
                }

                if (jsonArray.length() < 20) {
                    createMsgHeaderView();
                } else {
                    createLoadMoreView();
                    resetStatus();
                }
                page += 1;
                stringAdapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            mRootView.setVisibility(View.VISIBLE);
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
    };

}
