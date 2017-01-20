package com.witmoon.xmb.activity.shopping;

import android.app.usage.ConfigurationStats;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.TransitionRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.UmengStatic;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.mbq.adapter.MyCircleAdapter;
import com.witmoon.xmb.activity.shopping.adapter.CatAdapter;
import com.witmoon.xmb.activity.shopping.adapter.FeatureAdapter;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.ShoppingApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by de on 2016/9/19.
 */
public class MabaoFeatureFragment extends BaseFragment {
    private View view;
    private View headerView;
    private ArrayList<Map<String, Object>> mDatas = new ArrayList<>();
    private ArrayList<Map<String, String>> categoryList = new ArrayList<>();
    private EmptyLayout mEmptyLayout;
    private FeatureAdapter adapter;
    private RecyclerView brandRecyclerView;
    private CatAdapter cat_adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_mb_feature, container, false);
            mRootView = (RecyclerView) view.findViewById(R.id.recycler_view);
            headerView = inflater.inflate(R.layout.header_feature_fragment, container, false);
            setFont();
            cat_adapter = new CatAdapter(categoryList, getActivity());
            cat_adapter.setOnItemClickListener(new CatAdapter.OnItemClickListener() {
                @Override
                public void onItemnClick(Map<String, String> map) {
                    UmengStatic.registStat(getActivity(),"MaBaoFeatures0");

                    Bundle bundle = new Bundle();
                    bundle.putString("cat_id", map.get("id"));
                    bundle.putString("cat_type", map.get("type"));
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.FEATURE_BRAND, bundle);
                }
            });
            brandRecyclerView = (RecyclerView) headerView.findViewById(R.id.feature_brand_view);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this.getContext(), 2);
            gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            brandRecyclerView.setHasFixedSize(true);
            brandRecyclerView.setLayoutManager(gridLayoutManager);
            brandRecyclerView.setAdapter(cat_adapter);
            mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
            mEmptyLayout.setOnClickListener(v -> requestData());


            layoutManager = new LinearLayoutManager(this.getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRootView.setHasFixedSize(true);
            mRootView.setLayoutManager(layoutManager);

            adapter = new FeatureAdapter(mDatas, this.getContext());
            stringAdapter = new HeaderViewRecyclerAdapter(adapter);
            stringAdapter.addHeaderView(headerView);
            mRootView.setAdapter(stringAdapter);

            IntentFilter logOut = new IntentFilter(Const.INTENT_ACTION_LOGOUT);
            this.getActivity().registerReceiver(login_out, logOut);

            IntentFilter refreshFeature = new IntentFilter(Const.INTENT_ACTION_REFRESH_FEATURE);
            this.getActivity().registerReceiver(refreh_feature_data, refreshFeature);
            requestData();

        }

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    private void setFont() {
        AssetManager manager = getActivity().getAssets();//获取AssetManager
        Typeface tf = Typeface.createFromAsset(manager, "fonts/font.otf");//根据路径得到Typeface
        TextView jxsp = (TextView) headerView.findViewById(R.id.jxsp);
        jxsp.setTypeface(tf);
    }


    private BroadcastReceiver login_out = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestData();
        }
    };
    private BroadcastReceiver refreh_feature_data = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestData();
        }
    };

    private void requestData() {
        ShoppingApi.get_feature(new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                resetData();
                try {
                    JSONArray top_brand = response.getJSONArray("feature");
                    JSONArray bot_goods = response.getJSONArray("hot_goods");
                    initCat(top_brand);
                    initHandpickGood(bot_goods);
                    mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

                } catch (JSONException e) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }

            @Override
            public void onError(NetroidError error) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);

            }

            @Override
            public void onPreExecute() {
                super.onPreExecute();
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }
        });
    }

    public void initCat(JSONArray categoryJsonArray) {
        for (int i = 0; i < categoryJsonArray.length(); i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            try {
                hashMap.put("id", categoryJsonArray.getJSONObject(i).getString("id"));
                hashMap.put("url", categoryJsonArray.getJSONObject(i).getString("img"));
                hashMap.put("type", categoryJsonArray.getJSONObject(i).getString("type"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            categoryList.add(hashMap);
        }
        cat_adapter.notifyDataSetChanged();
    }

    public void initHandpickGood(JSONArray hand_pick_good) {
        for (int i = 0; i < hand_pick_good.length(); i++) {
            Map<String, Object> map = new HashMap<>();
            try {
                JSONObject jsonObject = hand_pick_good.getJSONObject(i);
                map.put("goods_id", jsonObject.getString("goods_id"));
                map.put("market_price", jsonObject.getString("market_price"));
                map.put("goods_name", jsonObject.getString("goods_name"));
                map.put("goods_img", jsonObject.getString("goods_thumb"));
                map.put("goods_price", jsonObject.getString("goods_price"));
            } catch (JSONException e) {

            }
            mDatas.add(map);
        }
        adapter.notifyDataSetChanged();
    }

    private void resetData() {
        mDatas.clear();
    }
}
