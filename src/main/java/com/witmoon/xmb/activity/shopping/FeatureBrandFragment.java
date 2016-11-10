package com.witmoon.xmb.activity.shopping;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.util.AQUtility;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mabao.adapter.AddordableAdapter;
import com.witmoon.xmb.activity.mabao.adapter.BrandGoodsAdapter;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.ShoppingApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by de on 2016/9/20.
 */
public class FeatureBrandFragment extends BaseFragment {

    private View view;
    private View headerView;
    private ImageView brand_logo;
    private TextView brand_desc;
    private TextView brand_name;
    private String id;
    private String type;
    private int page = 1;
    private boolean has_footer = false;
    private ArrayList<Map<String, String>> mDatas;
    private BrandGoodsAdapter adapter;
    private EmptyLayout mEmptyLayout;
    private Map<String, Object> map = new HashMap<>();
    private AQuery aQuery;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        id = getArguments().getString("cat_id");
        type = getArguments().getString("cat_type");
        mDatas = new ArrayList<>();
        super.onCreate(savedInstanceState);
    }

    private void configToolbar() {
        Toolbar toolbar = ((BaseActivity) getActivity()).getToolBar();
        toolbar.setBackgroundColor(getResources().getColor(R.color.main_kin));
        aQuery = new AQuery(getActivity(), toolbar);
        aQuery.id(R.id.toolbar_title_text).invisible();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_feature_brand, container, false);
            headerView = inflater.inflate(R.layout.header_brand_good, container, false);
            mRootView = (RecyclerView) view.findViewById(R.id.brand_goods);
            mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
            mEmptyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRecRequest(1);
                }
            });
            adapter = new BrandGoodsAdapter(mDatas, this.getContext());
            layoutManager = new GridLayoutManager(this.getContext(), 2);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRootView.setHasFixedSize(true);
            mRootView.setLayoutManager(layoutManager);
            stringAdapter = new HeaderViewRecyclerAdapter(adapter);
            stringAdapter.addHeaderView(headerView);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return ((stringAdapter.getItemCount() - 1 == position && has_footer) || position == 0) ? ((GridLayoutManager) layoutManager).getSpanCount() : 1;
                }
            });
            mRootView.setAdapter(stringAdapter);
            IntentFilter refreshBrandGood = new IntentFilter(Const.INTENT_ACTION_REFRESH_FEATURE);
            this.getActivity().registerReceiver(refreh_brand_data, refreshBrandGood);
            configToolbar();
            setRecRequest(1);
        }
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    private BroadcastReceiver refreh_brand_data = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setRecRequest(1);
        }
    };

    public void setRecRequest(int current_page) {
        if (page == 1) {
            ShoppingApi.get_brand_feature(id, type, " ", listener);
        } else {
            ShoppingApi.get_brand_feature(id, type, page + "", listener);
        }

    }

    Listener listener = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            try {
                JSONObject brand_info = response.getJSONObject("brand");
                JSONArray brand_goods = response.getJSONArray("goods_list");
                Log.e("GOODS_LIST", brand_goods.toString());
                initBrandInfo(brand_info);
                initBrandGoods(brand_goods);
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
        }
    };

    public void initBrandInfo(JSONObject brand_info) {
        try {
            brand_logo = (ImageView) headerView.findViewById(R.id.brand_logo);
            brand_desc = (TextView) headerView.findViewById(R.id.brand_desc);
            brand_name = (TextView) headerView.findViewById(R.id.brand_name);
            brand_name.setText((String) brand_info.get("brand_name"));
            brand_desc.setText((String) brand_info.get("brand_desc"));
            Netroid.displayAdImage(brand_info.get("brand_logo").toString(), brand_logo);
            aQuery.id(R.id.toolbar_title_text).visible().text((CharSequence) brand_info.get(("brand_name")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void initBrandGoods(JSONArray brand_goods) {
        try {
            for (int i = 0; i < brand_goods.length(); i++) {
                Map<String, String> map = new HashMap<>();
                JSONObject jsonObject = brand_goods.getJSONObject(i);
                map.put("goods_id", jsonObject.getString("goods_id"));
                map.put("market_price", jsonObject.getString("market_price"));
                map.put("goods_name", jsonObject.getString("goods_name"));
                map.put("goods_img", jsonObject.getString("goods_thumb"));
                map.put("goods_price", jsonObject.getString("goods_price"));
                mDatas.add(map);
            }
            if (brand_goods.length() < 20) {
                if (page != 1) {
                    removeFooterView();
                }
                has_footer = false;
            } else {
                if (page == 1) {
                    has_footer = true;
                }
                createLoadMoreView();
                resetStatus();
            }
            page += 1;
            stringAdapter.notifyDataSetChanged();
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }
    private void resetData() {
        mDatas.clear();
    }

}
