package com.witmoon.xmb.activity.mabao;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.SearchActivity;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.mabao.adapter.AddordableAdapter;
import com.witmoon.xmb.activity.mabao.adapter.SubClassAdapter;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.MyGridView;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.StringUtils;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmblibrary.percent.PercentLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by de on 2016/3/18.
 */
public class SubclassFragment extends BaseFragment implements View.OnClickListener, AddordableAdapter.OnItemClickListener {
    private MyGridView goods_gridView_top;
    private SubClassAdapter sb_adapter;
    private AddordableAdapter adapter;
    private ArrayList<Map<String, String>> mDatas;
    private ArrayList<Map<String, String>> list;

    private String cat_id;
    private int page = 1;
    private View headerView;
    private Boolean has_footer = false;
    private EmptyLayout emptyLayout;

    private ImageView backImg;
    private ImageView searchImg;
    private TextView titleText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cat_id = getArguments().getString("cat_id");
        ((BaseActivity) getActivity()).hideToolbar();
    }

    // 配置Toolbar
//    private void configToolbar() {
//        Log.e("getActivity",getActivity().toString());
//        Toolbar toolbar = ((BaseActivity) getActivity()).getToolBar();
//        toolbar.setBackgroundColor(getResources().getColor(R.color.main_kin));
//        AQuery aQuery = new AQuery(getActivity(), toolbar);
//        aQuery.id(R.id.top_toolbar).visible();
//        aQuery.id(R.id.toolbar_right_img2).gone();
//        aQuery.id(R.id.toolbar_right_img1).visible();
//        aQuery.id(R.id.toolbar_logo_img).gone();
//        aQuery.id(R.id.toolbar_left_img).visible();
//        aQuery.id(R.id.toolbar_title_text).visible().text(getArguments().getString("cat_name"));
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subclass, container, false);
        showWaitDialog();
        mDatas = new ArrayList<>();
        backImg = (ImageView) view.findViewById(R.id.toolbar_left_img);
        backImg.setOnClickListener(v -> getActivity().onBackPressed());
        searchImg = (ImageView) view.findViewById(R.id.toolbar_right_img);
        searchImg.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchActivity.class)));
        titleText = (TextView) view.findViewById(R.id.toolbar_title_text);
        titleText.setText(getArguments().getString("cat_name"));
        emptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        emptyLayout.setOnLayoutClickListener(v -> setRecRequest(1));
        adapter = new AddordableAdapter(mDatas, getContext(), "1");
        adapter.setOnItemClickListener(this);
        mRootView = (RecyclerView) view.findViewById(R.id.goods_gridView);
        layoutManager = new GridLayoutManager(this.getContext(), 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        headerView = LayoutInflater
                .from(this.getContext())
                .inflate(R.layout.header_category_fragment, mRootView, false);
        setFont();
        goods_gridView_top = (MyGridView) headerView.findViewById(R.id.goods_gridView_top);

        adapter = new AddordableAdapter(mDatas, this.getContext(), "0");
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        stringAdapter.addHeaderView(headerView);
        ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return ((stringAdapter.getItemCount() - 1 == position && has_footer) || position == 0) ? ((GridLayoutManager) layoutManager).getSpanCount() : 1;
            }
        });
        mRootView.setAdapter(stringAdapter);
        setRecRequest(1);
//        configToolbar();
        return view;
    }

    private void setFont() {
        AssetManager mgr = getActivity().getAssets();//得到AssetManager
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
        TextView mbjx = (TextView) headerView.findViewById(R.id.mbjx);
        mbjx.setTypeface(tf);
    }

    public void setRecRequest(int current_page) {
        if (page == 1) {
            FriendshipApi.child_category_index(cat_id, listener);
        } else {
            FriendshipApi.child_category_index(cat_id, page + "", listener);
        }
    }

    Listener<JSONObject> listener = new Listener<JSONObject>() {


        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onSuccess(JSONObject response) {
            try {
                if (response.has("category")) {
                    list = new ArrayList<>();
                    JSONArray category = response.getJSONArray("category");
                    for (int i = 0; i < category.length(); i++) {
                        Map<String, String> map = new HashMap<>();
                        JSONObject categoryJson = category.getJSONObject(i);
                        map.put("cat_id", categoryJson.getString("cat_id"));
                        map.put("icon", categoryJson.getString("icon"));
                        map.put("cat_name", categoryJson.getString("cat_name"));
                        if (!StringUtils.isEmpty(map.get("cat_id")))
                            list.add(map);
                    }
                    sb_adapter = new SubClassAdapter(list, getContext());
                    goods_gridView_top.setAdapter(sb_adapter);
                }
                JSONArray goods_list = response.getJSONArray("goods_list");
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
                    if (page == 1) {
                        has_footer = true;
                    }
                    createLoadMoreView();
                    resetStatus();
                }
                page += 1;
                stringAdapter.notifyDataSetChanged();
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(NetroidError error) {
            super.onError(error);
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }
    };

    @Override
    public void onItemButtonClick(Map<String, String> map) {
        CommodityDetailActivity.start(getContext(), map.get("goods_id"));//getActivity => getContext;
    }
}
