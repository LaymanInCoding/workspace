package com.witmoon.xmb.activity.specialoffer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.SearchActivity;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.mabao.adapter.AddordableAdapter;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.ui.BoderScrollView;
import com.witmoon.xmb.ui.MyGridView;
import com.witmoon.xmb.ui.widget.CountDownTextView;
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
 * 品牌特卖场Activity
 * Created by zhyh on 2015/5/21.
 */
public class MarketPlaceActivity extends BaseActivity implements AddordableAdapter.OnItemClickListener {
    private SimpleDraweeView mMarketLogoImage;
    private CountDownTextView mCountDownTextView;
    private View view1, view2, view3, view4;
    private TextView default_text, salesnum_text, new_text, stock_text;
    private RelativeLayout stock_text_layout;
    private ImageView right_arrow;
    private int page = 1;
    private String typr = "default";
    private ArrayList<Map<String, String>> mDatas;
    private AddordableAdapter adapter;
    private boolean is_start = false;
    private int price_checked = 2;
    //设置专场
    private String mMarketId;   // 特卖场ID
    private View headerView;
    private Boolean has_footer = false;
    private EmptyLayout emptyLayout;

    private ImageView searchImg;

    public static void start(Context context, String marketId) {
        Intent intent = new Intent(context, MarketPlaceActivity.class);
        intent.putExtra("M_ID", marketId);
        context.startActivity(intent);
    }

    public static void start(Context context, String marketId, String title) {
        Intent intent = new Intent(context, MarketPlaceActivity.class);
        intent.putExtra("M_ID", marketId);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_market_place;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.master_me);
        mDatas = new ArrayList<>();
        showWaitDialog();
        mMarketId = getIntent().getStringExtra("M_ID");
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        searchImg = (ImageView) findViewById(R.id.toolbar_share);
        searchImg.setImageResource(R.mipmap.search_imags);
        searchImg.setVisibility(View.VISIBLE);
        searchImg.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        default_text = (TextView) findViewById(R.id.default_text);
        salesnum_text = (TextView) findViewById(R.id.salesnum_text);
        new_text = (TextView) findViewById(R.id.new_text);
        stock_text = (TextView) findViewById(R.id.stock_text);
        stock_text_layout = (RelativeLayout) findViewById(R.id.stock_text_layout);
        right_arrow = (ImageView) findViewById(R.id.right_arrow);
        view1.setOnClickListener(this);
        view2.setOnClickListener(this);
        view3.setOnClickListener(this);
        view4.setOnClickListener(this);
        default_text.setOnClickListener(this);
        salesnum_text.setOnClickListener(this);
        new_text.setOnClickListener(this);
        stock_text_layout.setOnClickListener(this);
        adapter = new AddordableAdapter(mDatas, this, "0");
        adapter.setOnItemClickListener(this);
        mRootView = (RecyclerView) findViewById(R.id.goods_gridView);
        layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        headerView = LayoutInflater
                .from(this)
                .inflate(R.layout.header_market_place, mRootView, false);
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
        init();
        mCountDownTextView = (CountDownTextView) headerView.findViewById(R.id.count_down_text);
        mMarketLogoImage = (SimpleDraweeView) headerView.findViewById(R.id.market_place_logo);
        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) mMarketLogoImage.getLayoutParams();
        linearParams.width = MainActivity.screen_width;
        linearParams.height = MainActivity.screen_width * 350 / 750;
        mMarketLogoImage.setLayoutParams(linearParams);
    }

    public void setRecRequest(int page0) {
        FriendshipApi.marketplace(mMarketId, page + "", typr, listener);
    }

    public void setRecRequestPrice(int page0, String asc_or_desc) {
        FriendshipApi.marketplace_price(mMarketId, page + "", asc_or_desc, typr, listener);
    }

    private void init() {
        adapter.setOnItemClickListener(this);
    }

    Listener<JSONObject> listener = new Listener<JSONObject>() {

        @Override
        public void onError(NetroidError error) {
            super.onError(error);
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onSuccess(JSONObject response) {
            try {
                JSONArray goods_list;
                if (!is_start) {
                    long time = Long.parseLong(response.getString("end_time"));
                    mCountDownTextView.setTime(time * 1000);
                    Netroid.displayImage(response.getString("banner"), mMarketLogoImage);
                    is_start = true;
                }

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
                    mRootView.scrollToPosition(0);
                    has_footer = false;
                } else {
                    if (page == 1) {
                        has_footer = true;
                        mRootView.scrollToPosition(0);
                    }
                    createLoadMoreView();
                    resetStatus();
                }
                if (page == 1) {
                    emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                }
                page += 1;
                stringAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
            }
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.default_text:
                if (!typr.equals("default")) {
                    page = 1;
                    mDatas.clear();
                    adapter.notifyDataSetChanged();
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                    view4.setVisibility(View.GONE);
                    default_text.setTextColor(Color.parseColor("#c86a66"));
                    salesnum_text.setTextColor(Color.parseColor("#333333"));
                    new_text.setTextColor(Color.parseColor("#333333"));
                    stock_text.setTextColor(Color.parseColor("#333333"));
                    right_arrow.setImageResource(R.mipmap.price_uncheck);
                    typr = "default";
                    price_checked = 2;
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);

                    setRecRequest(1);
                }
                break;
            case R.id.salesnum_text:

                if (!typr.equals("salesnum")) {
                    page = 1;
                    mDatas.clear();
                    adapter.notifyDataSetChanged();
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    view3.setVisibility(View.GONE);
                    view4.setVisibility(View.GONE);
                    default_text.setTextColor(Color.parseColor("#333333"));
                    salesnum_text.setTextColor(Color.parseColor("#c86a66"));
                    new_text.setTextColor(Color.parseColor("#333333"));
                    stock_text.setTextColor(Color.parseColor("#333333"));
                    right_arrow.setImageResource(R.mipmap.price_uncheck);
                    typr = "salesnum";
                    price_checked = 2;
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);

                    setRecRequest(1);
                }
                break;
            case R.id.new_text:

                if (!typr.equals("new")) {
                    page = 1;
                    mDatas.clear();
                    adapter.notifyDataSetChanged();
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.VISIBLE);
                    view4.setVisibility(View.GONE);
                    default_text.setTextColor(Color.parseColor("#333333"));
                    salesnum_text.setTextColor(Color.parseColor("#333333"));
                    new_text.setTextColor(Color.parseColor("#c86a66"));
                    stock_text.setTextColor(Color.parseColor("#333333"));
                    right_arrow.setImageResource(R.mipmap.price_uncheck);
                    typr = "new";
                    price_checked = 2;
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    setRecRequest(1);
                }
                break;
            case R.id.stock_text_layout:
                page = 1;
                mDatas.clear();
                adapter.notifyDataSetChanged();
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.GONE);
                view4.setVisibility(View.VISIBLE);
                default_text.setTextColor(Color.parseColor("#333333"));
                salesnum_text.setTextColor(Color.parseColor("#333333"));
                new_text.setTextColor(Color.parseColor("#333333"));
                stock_text.setTextColor(Color.parseColor("#c86a66"));
                typr = "price";
                if (price_checked == 1) {
                    right_arrow.setImageResource(R.mipmap.price_check_twice);
                    price_checked = 2;
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    setRecRequestPrice(1, "desc");//降序
                    break;
                }
                if (price_checked == 2) {
                    right_arrow.setImageResource(R.mipmap.price_check_once);
                    price_checked = 1;
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    setRecRequestPrice(1, "asc");//升序
                    break;
                }
                break;
        }
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        //toolbar.setBackgroundColor(getResources().getColor(R.color.master_me));
    }

    @Override
    protected String getActionBarTitle() {
        return getIntent().getStringExtra("title") == null ? "特卖场" : getIntent().getStringExtra("title");
    }

    @Override
    public void onItemButtonClick(Map<String, String> map) {
        CommodityDetailActivity.start(MarketPlaceActivity.this, map.get("goods_id"));
    }
}
