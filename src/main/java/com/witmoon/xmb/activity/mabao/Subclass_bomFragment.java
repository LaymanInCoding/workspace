package com.witmoon.xmb.activity.mabao;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.mabao.adapter.AddordableAdapter;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.BoderScrollView;
import com.witmoon.xmb.ui.MyGridView;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.easydone.swiperefreshendless.EndlessRecyclerOnScrollListener;
import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by de on 2016/3/18
 */
public class Subclass_bomFragment extends BaseFragment implements View.OnClickListener,AddordableAdapter.OnItemClickListener {
    private View view1, view2, view3, view4;
    private TextView default_text, salesnum_text, new_text, stock_text;
    private int page = 1;
    private String type = "default";
    private String cat_id;
    private ArrayList<Map<String, String>> mDatas;
    private AddordableAdapter adapter;
    private String is_type;
    private String is_name;
    private boolean is_go;
    private Boolean has_footer = false;
    private EmptyLayout emptyLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cat_id = getArguments().getString("cat_id");
        is_type = getArguments().getString("is_type");
        is_name = getArguments().getString("cat_name");
        configToolbar();
    }
    // 配置Toolbar
    private void configToolbar() {
        Toolbar toolbar = ((BaseActivity) getActivity()).getToolBar();
        toolbar.setBackgroundColor(getResources().getColor(R.color.main_kin));
        AQuery aQuery = new AQuery(getActivity(), toolbar);
        aQuery.id(R.id.top_toolbar).visible();
        aQuery.id(R.id.toolbar_right_img2).gone();
        aQuery.id(R.id.toolbar_right_img1).gone();
        aQuery.id(R.id.toolbar_logo_img).gone();
        aQuery.id(R.id.toolbar_left_img).visible();
        if (is_type.equals("0")) {
            aQuery.id(R.id.toolbar_title_text).visible().text(is_name);
        } else {
            aQuery.id(R.id.toolbar_title_text).visible().text(is_name);
        }
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mDatas = new ArrayList<>();
        adapter = new AddordableAdapter(mDatas, getActivity(), "1");
        View view = inflater.inflate(R.layout.fragment_subclass_bom, container, false);
        view1 = view.findViewById(R.id.view1);
        view2 = view.findViewById(R.id.view2);
        view3 = view.findViewById(R.id.view3);
        view4 = view.findViewById(R.id.view4);
        default_text = (TextView) view.findViewById(R.id.default_text);
        salesnum_text = (TextView) view.findViewById(R.id.salesnum_text);
        new_text = (TextView) view.findViewById(R.id.new_text);
        stock_text = (TextView) view.findViewById(R.id.stock_text);
        view1.setOnClickListener(this);
        view2.setOnClickListener(this);
        view3.setOnClickListener(this);
        view4.setOnClickListener(this);
        default_text.setOnClickListener(this);
        salesnum_text.setOnClickListener(this);
        new_text.setOnClickListener(this);
        stock_text.setOnClickListener(this);
        adapter = new AddordableAdapter(mDatas, this.getContext(),"0");
        adapter.setOnItemClickListener(this);
        mRootView = (RecyclerView) view.findViewById(R.id.goods_gridView);
        layoutManager = new GridLayoutManager(this.getContext(), 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        emptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        emptyLayout.setOnLayoutClickListener(v -> setRecRequest(1));
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        ((GridLayoutManager)layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return ((stringAdapter.getItemCount() - 1 == position && has_footer)) ? ((GridLayoutManager) layoutManager).getSpanCount() : 1;
            }
        });
        mRootView.setAdapter(stringAdapter);
        setRecRequest(1);
        return view;
    }

    public void setRecRequest(int page0){
        FriendshipApi.child_category_index(cat_id, page + "", type, is_type, listener);
    }

    Listener<JSONObject> listener = new Listener<JSONObject>() {

        @Override
        public void onError(NetroidError error) {
            super.onError(error);
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            is_go  = false;
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onSuccess(JSONObject response) {
            JSONArray goods_list = null;
            try {
                goods_list = response.getJSONArray("goods_list");
                for (int i = 0; i < goods_list.length(); i++) {
                    Map<String, String> map = new HashMap<>();
                    JSONObject goods_listJson = goods_list.getJSONObject(i);
                    map.put("goods_name", goods_listJson.getString("goods_name"));
                    map.put("goods_price", goods_listJson.getString("goods_price"));
                    map.put("goods_thumb", goods_listJson.getString("goods_thumb"));
                    map.put("goods_id", goods_listJson.getString("goods_id"));
                    mDatas.add(map);
                }

                if (goods_list.length() < 20) {
                    if (page != 1) {
                        removeFooterView();
                    }
                    has_footer = false;
                } else {
                    has_footer = true;
                    createLoadMoreView();
                    resetStatus();
                    if(page == 1) {
                        mRootView.scrollToPosition(0);
                    }
                }
                adapter.notifyDataSetChanged();
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                page += 1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.default_text:
                if (!type.equals("default")) {
                    page = 1;
                    mDatas.clear();
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                    view4.setVisibility(View.GONE);
                    default_text.setTextColor(Color.parseColor("#c86a66"));
                    salesnum_text.setTextColor(Color.parseColor("#333333"));
                    new_text.setTextColor(Color.parseColor("#333333"));
                    stock_text.setTextColor(Color.parseColor("#333333"));
                    type = "default";
                    setRecRequest(1);
                }
                break;
            case R.id.salesnum_text:
                if (!type.equals("salesnum")) {
                    page = 1;
                    mDatas.clear();
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    view3.setVisibility(View.GONE);
                    view4.setVisibility(View.GONE);
                    default_text.setTextColor(Color.parseColor("#333333"));
                    salesnum_text.setTextColor(Color.parseColor("#c86a66"));
                    new_text.setTextColor(Color.parseColor("#333333"));
                    stock_text.setTextColor(Color.parseColor("#333333"));
                    type = "salesnum";
                    setRecRequest(1);
                }
                break;
            case R.id.new_text:
                if (!type.equals("new")) {
                    page = 1;
                    mDatas.clear();
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.VISIBLE);
                    view4.setVisibility(View.GONE);
                    default_text.setTextColor(Color.parseColor("#333333"));
                    salesnum_text.setTextColor(Color.parseColor("#333333"));
                    new_text.setTextColor(Color.parseColor("#c86a66"));
                    stock_text.setTextColor(Color.parseColor("#333333"));
                    type = "new";
                    setRecRequest(1);
                }
                break;
            case R.id.stock_text:
                if (!type.equals("stock")) {
                    page = 1;
                    mDatas.clear();
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                    view4.setVisibility(View.VISIBLE);
                    default_text.setTextColor(Color.parseColor("#333333"));
                    salesnum_text.setTextColor(Color.parseColor("#333333"));
                    new_text.setTextColor(Color.parseColor("#333333"));
                    stock_text.setTextColor(Color.parseColor("#c86a66"));
                    type = "stock";
                    setRecRequest(1);
                }
                break;
        }
    }

    @Override
    public void onItemButtonClick(Map<String, String> map) {
        CommodityDetailActivity.start(getActivity(), map.get("goods_id"));
    }
}
