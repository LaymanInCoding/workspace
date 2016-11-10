package com.witmoon.xmb.activity.goods;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.MySwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.adapter.CommodityListAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.GoodsApi;
import com.witmoon.xmb.api.UserApi;
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

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 商品列表Activity
 * Created by zhyh on 2015-07-11.
 */
public class CommodityListActivity extends BaseActivity {

    public static final int BY_CATEGORY = 0x01;
    public static final int BY_BRAND = 0x02;

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
    private int mByType;
    private String mTypeValue;  // 列表类型值, 一般为分类ID或品牌ID
    private String mTypeName;   // 列表类型名, 一般为分类名或品牌名

    private int mTotalPage;
    private int mCurrentPage;
    private String mSortColumn;     // 排序列
    private String mSortType;       // 排序类型
    private boolean mFilterInStore = false;  // 只看有货

    private SortTextView mPriceSort;
    private SortTextView mSalesSort;
    private SortTextView mDiscountSort;

    private ImageView mCollectionImage;

    public static void start(Context context, String typeValue, String typeName, int byType) {
        Intent intent = new Intent(context, CommodityListActivity.class);
        intent.putExtra("TYPE_VALUE", typeValue);
        intent.putExtra("BY_TYPE", byType);
        intent.putExtra("TYPE_NAME", typeName);
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
        setTitleColor_(R.color.master_special_offer);
        Intent intent = getIntent();
        mByType = intent.getIntExtra("BY_TYPE", BY_CATEGORY);
        mTypeValue = intent.getStringExtra("TYPE_VALUE");
        mTypeName = intent.getStringExtra("TYPE_NAME");

        AQuery aQuery = new AQuery(this);
        aQuery.id(R.id.sort_price_layout).clicked(this);
        aQuery.id(R.id.sort_discount_layout).clicked(this);
        aQuery.id(R.id.sort_sales_layout).clicked(this);
        mPriceSort = (SortTextView) aQuery.id(R.id.sort_price).getView();
        mSalesSort = (SortTextView) aQuery.id(R.id.sort_sales).getView();
        mDiscountSort = (SortTextView) aQuery.id(R.id.sort_discount).getView();
        aQuery.id(R.id.filter_in_store).getCheckBox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFilterInStore = isChecked;
                refresh();
            }
        });

        // 绑定分享和收藏监听
        if (mByType == BY_BRAND) {
            aQuery.id(R.id.toolbar_share).visibility(View.VISIBLE).clicked(this);
            mCollectionImage = aQuery.id(R.id.toolbar_right_img).visibility(View.VISIBLE).clicked
                    (this).getImageView();
        }
        aQuery.id(R.id.toolbar_title_text).text(mTypeName);

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
        ItemClickSupport itemClickSupport = ItemClickSupport.addTo(mRecyclerView);
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position, long id) {
                Goods goods = (Goods) mAdapter.getData().get(position);
                CommodityDetailActivity.start(CommodityListActivity.this, goods.getId());
            }
        });

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

    // 刷新数据
    private void refresh() {
        mCurrentPage = 1;
        mState = STATE_REFRESH;
        requestData();
    }

    // 请求数据
    private void requestData() {
        if (mByType == BY_CATEGORY)
            GoodsApi.goodsListByCategory(mTypeValue, mCurrentPage, mSortColumn, mSortType,
                    mFilterInStore, mGoodsListCallback);
        else if (mByType == BY_BRAND)
            GoodsApi.goodsListByBrand(mTypeValue, mCurrentPage, mSortColumn, mSortType,
                    mFilterInStore, mGoodsListCallback);
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
            } catch (JSONException e) {
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
        JSONObject dataObj = response.getJSONObject("data");
        if (mByType == BY_BRAND && dataObj.has("is_collect") && dataObj.getInt("is_collect") == 1) {
            mCollectionImage.setImageResource(R.mipmap.icon_heart_red_64x64);
        }
        mTotalPage = dataObj.getInt("page_count");

        List<Goods> goodsList = new ArrayList<>();
        JSONArray goodsArray = dataObj.getJSONArray("goods");
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
        if (mTotalPage == 1) {
            mAdapter.setState(BaseRecyclerAdapter.STATE_LESS_ONE_PAGE);
            return;
        }
        if (mCurrentPage == mTotalPage) {
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
            case R.id.sort_price_layout:
                mDiscountSort.setSortType(SortTextView.SORT_TYPE_NONE);
                mSalesSort.setSortType(SortTextView.SORT_TYPE_NONE);
                mPriceSort.toggle();
                mSortColumn = mPriceSort.getSortColumn();
                mSortType = mPriceSort.getSortType();
                refresh();
                break;
            case R.id.sort_sales_layout:
                mDiscountSort.setSortType(SortTextView.SORT_TYPE_NONE);
                mPriceSort.setSortType(SortTextView.SORT_TYPE_NONE);
                mSalesSort.toggle();
                mSortColumn = mSalesSort.getSortColumn();
                mSortType = mSalesSort.getSortType();
                refresh();
                break;
            case R.id.sort_discount_layout:
                mPriceSort.setSortType(SortTextView.SORT_TYPE_NONE);
                mSalesSort.setSortType(SortTextView.SORT_TYPE_NONE);
                mDiscountSort.toggle();
                mSortColumn = mDiscountSort.getSortColumn();
                mSortType = mDiscountSort.getSortType();
                refresh();
                break;
            case R.id.toolbar_right_img:    // 收藏品牌
                UserApi.collectBrand(mTypeValue, new BrandCollectCallback());
                break;
            case R.id.toolbar_share:
                share();
                break;
        }
    }

    // 收藏品牌回调接口
    private class BrandCollectCallback extends Listener<JSONObject> {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToast(twoTuple.second);
                return;
            }
            AppContext.showToast("收藏成功");
            mCollectionImage.setImageResource(R.mipmap.icon_heart_red_64x64);
        }
    }

    // 分享
    private void share() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle("分享自小麻包母婴商城");
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://www.xiaomabao.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(mTypeName);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        oks.setImageUrl("");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.xiaomabao.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.xiaomabao.com");
        oks.setSilent(true);
        // 启动分享GUI
        oks.show(this);
    }
}
