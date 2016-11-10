package com.witmoon.xmb.activity.goods;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.MySwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.adapter.CommodityListAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.model.Goods;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.ui.widget.SortTextView;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TLog;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmblibrary.observablescrollview.ObservableRecyclerView;
import com.witmoon.xmblibrary.recyclerview.ItemClickSupport;
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

    private MySwipeRefreshLayout mRefreshLayout;
    private ObservableRecyclerView mRecyclerView;
    protected EmptyLayout mErrorLayout;
    protected BaseRecyclerAdapter mAdapter;
    private GridLayoutManager mLayoutManager;

    // 列表类型, 品牌商品或者分类商品, 默认为分类商品列表
    private String mKeywords;

    private boolean isMoreData;
    private int mCurrentPage;
    private String mSortColumn;     // 排序列
    private String mSortType;       // 排序类型
    private boolean mFilterInStore = false;  // 只看有货

    private SortTextView mPriceSort;
    private SortTextView mSalesSort;
    private SortTextView mDiscountSort;
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
        mPriceSort = (SortTextView) aQuery.id(R.id.sort_price).clicked(this).getView();
        mSalesSort = (SortTextView) aQuery.id(R.id.sort_sales).clicked(this).getView();
        mDiscountSort = (SortTextView) aQuery.id(R.id.sort_discount).clicked(this).getView();
        aQuery.id(R.id.filter_in_store).getCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFilterInStore = isChecked;
                refresh();
            }
        });

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
            refresh();
        }
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
            GoodsApi.search(mKeywords, mCurrentPage, mSortColumn, mSortType, mFilterInStore,
                    mGoodsListCallback);
    }

    // 商品列表回调
    Listener<JSONObject> mGoodsListCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            try {
                List<Goods> goodsList = parseResponse(response);
                execOnLoadDataSuccess(goodsList);
                execOnLoadDataFinish();
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
        isMoreData = response.getJSONObject("paginated").getInt("more") != 0;

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
            case R.id.sort_price:
                mDiscountSort.setSortType(SortTextView.SORT_TYPE_NONE);
                mSalesSort.setSortType(SortTextView.SORT_TYPE_NONE);
                mPriceSort.toggle();
                mSortColumn = mPriceSort.getSortColumn();
                mSortType = mPriceSort.getSortType();
                refresh();
                break;
            case R.id.sort_sales:
                mDiscountSort.setSortType(SortTextView.SORT_TYPE_NONE);
                mPriceSort.setSortType(SortTextView.SORT_TYPE_NONE);
                mSalesSort.toggle();
                mSortColumn = mSalesSort.getSortColumn();
                mSortType = mSalesSort.getSortType();
                refresh();
                break;
            case R.id.sort_discount:
                mPriceSort.setSortType(SortTextView.SORT_TYPE_NONE);
                mSalesSort.setSortType(SortTextView.SORT_TYPE_NONE);
                mDiscountSort.toggle();
                mSortColumn = mDiscountSort.getSortColumn();
                mSortType = mDiscountSort.getSortType();
                refresh();
                break;
        }
    }
}
