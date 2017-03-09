package com.witmoon.xmb.activity.goods;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.MySwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.adapter.CommodityListAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.model.Goods;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TLog;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmblibrary.observablescrollview.ObservableRecyclerView;
import com.witmoon.xmblibrary.recyclerview.itemdecoration.grid.GridDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 商品搜索结果列表Activity
 * Created by zhyh on 2015-07-11.
 */
public class SearchResultListActivity extends BaseActivity {

    protected static final int STATE_NONE = 0;
    protected static final int STATE_REFRESH = 1;
    protected static final int STATE_LOAD_MORE = 2;
    protected int mState = STATE_NONE;

    private int price_checked = 2;

    private MySwipeRefreshLayout mRefreshLayout;
    private ObservableRecyclerView mRecyclerView;
    protected EmptyLayout mErrorLayout;
    protected BaseRecyclerAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    // 列表类型, 品牌商品或者分类商品, 默认为分类商品列表
    private String mKeywords;
    private String mOrderType = "salenum";   //分类 类型 (销量、新品、价格、有货)
    private View view1, view2, view3, view4;
    private boolean isMoreData;
    private int mCurrentPage;
    private String asc_or_desc = "";
    private TextView mSalesText, mNewcomeText, mStockText, mPriceText;
    private ImageView arrow_right;
    private RelativeLayout price_text_layout;
    private AppContext mAppContext;

    public static void start(Context context, String keywords) {
        Intent intent = new Intent(context, SearchResultListActivity.class);
        intent.putExtra("TYPE_VALUE", keywords);
        context.startActivity(intent);
    }

    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItem = mLayoutManager.findLastVisibleItemPosition();
            int totalItemCount = mLayoutManager.getItemCount();
            if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {
                if (mState == STATE_NONE && mAdapter != null && mAdapter.getDataSize() > 0) {
                    loadMore();
                }
            }
        }
    };

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_goods_list;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.master_shopping_cart);
        Intent intent = getIntent();
        mKeywords = intent.getStringExtra("TYPE_VALUE");
        mAppContext = AppContext.instance();
        AQuery aQuery = new AQuery(this);
        mSalesText = aQuery.id(R.id.default_text).clicked(this).getTextView();
        mNewcomeText = aQuery.id(R.id.newcome_text).clicked(this).getTextView();
        mStockText = aQuery.id(R.id.is_exist_text).clicked(this).getTextView();
        mPriceText = aQuery.id(R.id.price_text).getTextView();
        price_text_layout = (RelativeLayout) aQuery.id(R.id.price_text_layout).clicked(this).getView();
        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view3 = findViewById(R.id.view3);
        view4 = findViewById(R.id.view4);
        arrow_right = aQuery.id(R.id.right_arrow).getImageView();
        mErrorLayout = (EmptyLayout) aQuery.id(R.id.error_layout).getView();
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                refresh();
            }
        });
        mRefreshLayout = (MySwipeRefreshLayout) aQuery.id(R.id.refresh_layout).getView();
        mRefreshLayout.setColorSchemeColors(R.color.main_gray, R.color.main_green, R.color
                .main_purple);
        mRefreshLayout.setOnRefreshListener(new MySwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
        mRecyclerView = (ObservableRecyclerView) aQuery.id(R.id.recycle_view).getView();
        mRecyclerView.addOnScrollListener(mScrollListener);
        mLayoutManager = new GridLayoutManager(this, 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mAdapter.getItemViewType(position)) {
                    case BaseRecyclerAdapter.TYPE_FOOTER:
                        return 2;
                    default:
                        return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridDividerItemDecoration(getResources().getDrawable
                (R.drawable.divider_x2), getResources().getDrawable(R.drawable.divider_x2), 2));
        mRecyclerView.setHasFixedSize(true);
        if (mAdapter != null) {
            mRecyclerView.setAdapter(mAdapter);
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            // 创建Adapter
            mAdapter = new CommodityListAdapter(this);
            mRecyclerView.setAdapter(mAdapter);
            mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
//            refresh();

        }
        refresh();
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        //toolbar.setBackgroundColor(getResources().getColor(R.color.master_shopping_cart));
    }

    @Override
    protected String getActionBarTitle() {
        return "搜索结果";
    }

    // 刷新数据
    private void refresh() {
        mCurrentPage = 1;
        mState = STATE_REFRESH;
        requestData();
    }

    // 请求数据
    private void requestData() {
        GoodsApi.search(mKeywords, mCurrentPage, mOrderType, asc_or_desc,
                mGoodsListCallback);
    }


    // 商品列表回调
    Listener<JSONObject> mGoodsListCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            try {
                List<Goods> goodsList = parseResponse(response);
                execOnLoadDataSuccess(goodsList);
                execOnLoadDataFinish();
                Log.d("mState", mState + "");
                mAppContext.getXmbDB().search_insret(mKeywords);
            } catch (JSONException e) {
                e.printStackTrace();
                execOnLoadDataError(e.getMessage());
            }
        }

        @Override
        public void onError(NetroidError error) {
            execOnLoadDataError(error.getMessage());
            execOnLoadDataFinish();
        }
    };

    // 解析网络响应
    private List<Goods> parseResponse(JSONObject response) throws JSONException {
        if (response.getJSONArray("data").length() < 20) {
            isMoreData = false;
        } else {
            isMoreData = true;
        }
        List<Goods> goodsList = new ArrayList<>();
        JSONArray goodsArray = response.getJSONArray("data");
        for (int i = 0; i < goodsArray.length(); i++) {
            Goods goods = Goods.parse(goodsArray.getJSONObject(i));
            goodsList.add(goods);
        }

        return goodsList;
    }

    // 加载更多
    private void loadMore() {
        if (mState == STATE_NONE) {
            if (mAdapter.getState() == BaseRecyclerAdapter.STATE_LOAD_MORE) {
                TLog.log("GOODS_BY_CATE", "加载更多数据.");
                mCurrentPage++;
                mState = STATE_LOAD_MORE;
                requestData();
            }
        }
    }

    // -------------------------------------------
    protected void execOnLoadDataError(String error) {
        if (mCurrentPage == 1) {
            if (mAdapter.getDataSize() == 0) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            } else {
                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                String message = error;
                if (TextUtils.isEmpty(error)) {
                    if (TDevice.hasInternet()) {
                        message = getString(R.string.tip_load_data_error);
                    } else {
                        message = getString(R.string.tip_network_error);
                    }
                }
                AppContext.showToastShort(message);
            }
        } else {
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mAdapter.setState(BaseRecyclerAdapter.STATE_NETWORK_ERROR);
        }
    }

    protected void execOnLoadDataSuccess(List<?> data) {
        if (mState == STATE_REFRESH) {
            mAdapter.clear();
            mRecyclerView.scrollVerticallyTo(0);
        }
        mAdapter.addData(data);
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

        if (data.size() == 0 && mState == STATE_REFRESH) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
            return;
        }
        if (!isMoreData) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE);
            return;
        }
        mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_MORE);
    }

    protected void execOnLoadDataFinish() {
        mRefreshLayout.setRefreshing(false);
        mState = STATE_NONE;
    }

    // --------------------------------------------------------------

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.default_text:
                if (!mOrderType.equals("salenum")) {
                    price_checked = 2;
                    view1.setVisibility(View.VISIBLE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                    view4.setVisibility(View.GONE);
                    mSalesText.setTextColor(Color.parseColor("#c86a66"));
                    mNewcomeText.setTextColor(Color.parseColor("#333333"));
                    mStockText.setTextColor(Color.parseColor("#333333"));
                    mPriceText.setTextColor(Color.parseColor("#333333"));
                    arrow_right.setImageResource(R.mipmap.price_uncheck);
                    mOrderType = "salenum";
                    asc_or_desc = "";
                    refresh();
                }
                break;
            case R.id.newcome_text:
                if (!mOrderType.equals("new")) {
                    mSalesText.setTextColor(Color.parseColor("#333333"));
                    mNewcomeText.setTextColor(Color.parseColor("#c86a66"));
                    mStockText.setTextColor(Color.parseColor("#333333"));
                    mPriceText.setTextColor(Color.parseColor("#333333"));
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.VISIBLE);
                    view3.setVisibility(View.GONE);
                    view4.setVisibility(View.GONE);
                    arrow_right.setImageResource(R.mipmap.price_uncheck);
                    mOrderType = "new";
                    price_checked = 2;
                    asc_or_desc = "";
                    refresh();
                }
                break;
            case R.id.is_exist_text:
                if (!mOrderType.equals("stock")) {
                    mSalesText.setTextColor(Color.parseColor("#333333"));
                    mNewcomeText.setTextColor(Color.parseColor("#333333"));
                    mStockText.setTextColor(Color.parseColor("#c86a66"));
                    mPriceText.setTextColor(Color.parseColor("#333333"));
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.VISIBLE);
                    view4.setVisibility(View.GONE);
                    arrow_right.setImageResource(R.mipmap.price_uncheck);
                    mOrderType = "stock";
                    price_checked = 2;
                    asc_or_desc = "";
                    refresh();
                }
                break;
            case R.id.price_text_layout:
                mSalesText.setTextColor(Color.parseColor("#333333"));
                mNewcomeText.setTextColor(Color.parseColor("#333333"));
                mStockText.setTextColor(Color.parseColor("#333333"));
                mPriceText.setTextColor(Color.parseColor("#c86a66"));
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.GONE);
                view3.setVisibility(View.GONE);
                view4.setVisibility(View.VISIBLE);
                arrow_right.setImageResource(R.mipmap.price_uncheck);
                mOrderType = "price";
                if (price_checked == 1) {
                    arrow_right.setImageResource(R.mipmap.price_check_twice);
                    price_checked = 2;
                    asc_or_desc = "desc";
                    refresh();//降序
                    break;
                }
                if (price_checked == 2) {
                    arrow_right.setImageResource(R.mipmap.price_check_once);
                    price_checked = 1;
                    asc_or_desc = "asc";
                    refresh(); //升序
                    break;
                }
                break;
        }
    }
}
