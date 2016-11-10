package com.witmoon.xmb.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.MySwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.cache.v2.CacheManager;
import com.witmoon.xmb.model.ListEntity;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TLog;
import com.witmoon.xmb.util.WeakAsyncTask;
import com.witmoon.xmblibrary.observablescrollview.ObservableRecyclerView;
import com.witmoon.xmblibrary.recyclerview.RecyclerViewHeader;
import com.witmoon.xmblibrary.recyclerview.itemdecoration.grid.GridDividerItemDecoration;
import com.witmoon.xmblibrary.recyclerview.itemdecoration.grid.GridStartOffsetItemDecoration;
import com.witmoon.xmblibrary.recyclerview.itemdecoration.linear.DividerItemDecoration;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * RecyclerView
 * Created by zhyh on 2015/6/15.
 */
public abstract class BaseRecyclerViewFragment extends BaseFragment implements
        BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener {

    private static final String TAG = "BaseRecycleViewFragment";

    protected MySwipeRefreshLayout mSwipeRefresh;
    protected ObservableRecyclerView mRecycleView;
    protected LinearLayoutManager mLayoutManager;
    protected BaseRecyclerAdapter mAdapter;
    protected EmptyLayout mErrorLayout;
    protected int mStoreEmptyState = -1;
    protected String mStoreEmptyMessage;
    protected SimpleDraweeView mMarketLogoImage;
    protected RecyclerViewHeader mRecyclerViewHeader;
    protected int mCurrentPage;
    protected int mCatalog = 1;

    private ParserTask mParserTask;

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

    protected int getLayoutRes() {
        return R.layout.activity_market_place1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutRes(), container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onDestroyView() {
        mStoreEmptyState = mErrorLayout.getErrorState();
        mStoreEmptyMessage = mErrorLayout.getMessage();
        super.onDestroyView();
    }

    // 初始化视图资源
    protected void initViews(View view) {
        mMarketLogoImage = (SimpleDraweeView) view.findViewById(R.id.market_place_logo);
        mRecyclerViewHeader = (RecyclerViewHeader) view.findViewById(R.id.recycler_header);
        mErrorLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mCurrentPage = 0;
                mState = STATE_REFRESH;
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                requestData(true);
            }
        });

        mSwipeRefresh = (MySwipeRefreshLayout) view.findViewById(R.id.srl_refresh);
        mSwipeRefresh.setColorSchemeResources(R.color.main_green, R.color.main_gray, R.color
                .main_black, R.color.main_purple);
        mSwipeRefresh.setOnRefreshListener(new MySwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        mRecycleView = (ObservableRecyclerView) view.findViewById(R.id.recycleView);
        mRecycleView.addOnScrollListener(mScrollListener);

        if (isNeedListDivider()) {
            // use a linear layout manager
            mRecycleView.addItemDecoration(new DividerItemDecoration(new ColorDrawable(Color
                    .GRAY)));
        }

        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mRecycleView.setHasFixedSize(true);
        mRecyclerViewHeader.attachTo(mRecycleView, true);
        mRecycleView.addItemDecoration(new GridDividerItemDecoration(getResources()
                .getDrawable(R.drawable.divider_x2), getResources()
                .getDrawable(R.drawable.divider_x2), 2));
        mRecycleView.addItemDecoration(new GridStartOffsetItemDecoration(4, 2));

        if (mAdapter != null) {
            mRecycleView.setAdapter(mAdapter);
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            mAdapter = getListAdapter();
            mAdapter.setOnItemClickListener(this);
            mAdapter.setOnItemLongClickListener(this);
            mRecycleView.setAdapter(mAdapter);

            if (requestDataIfViewCreated()) {
                mCurrentPage = 0;
                mState = STATE_REFRESH;
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                //requestData(requestDataFromNetWork());
                new ReadCacheTask(this).execute();
            } else {
                mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
        }

        if (mStoreEmptyState != -1) {
            mErrorLayout.setErrorType(mStoreEmptyState);
        }
        if (!TextUtils.isEmpty(mStoreEmptyMessage)) {
            mErrorLayout.setErrorMessage(mStoreEmptyMessage);
        }
    }

    // 获取RecyclerView适配器
    protected abstract BaseRecyclerAdapter getListAdapter();

    // View初始化完成之后是否需要请求数据
    protected boolean requestDataIfViewCreated() {
        return true;
    }

    // 是否需要分隔线
    protected boolean isNeedListDivider() {
        return true;
    }

    public void loadMore() {
        if (mState == STATE_NONE) {
            if (mAdapter.getState() == BaseRecyclerAdapter.STATE_LOAD_MORE) {
                TLog.log(TAG, "begin to load more data.");
                mCurrentPage++;
                mState = STATE_LOAD_MORE;
                requestData(false);
            }
        }
    }

    //解析数据
    protected ListEntity parseList(JSONObject json) throws Exception {
        return null;
    }

    public void refresh() {
        mCurrentPage = 0;
        mState = STATE_REFRESH;
        requestData(true);
    }

    protected void requestData(boolean refresh) {
        sendRequestData();
    }

    // 发送获取网络数据请求
    protected void sendRequestData() {
    }

    // 获取网络请求监听器
    protected Listener<JSONObject> getListener() {
        return new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                executeParserTask(response, false);
            }

            @Override
            public void onError(NetroidError error) {
                executeOnLoadDataError(error.getMessage());
                executeOnLoadFinish();
            }
        };
    }

    // -------------------------- 列表点击监听器 -----------------------------
    @Override
    public void onItemClick(View view) {
        onItemClick(view, mRecycleView.getChildAdapterPosition(view));
    }

    protected void onItemClick(View view, int position) {
    }

    @Override
    public boolean onItemLongClick(View view) {
        return onItemLongClick(view, mRecycleView.getChildAdapterPosition(view));
    }

    protected boolean onItemLongClick(View view, int position) {
        return false;
    }
    // -------------------------- 列表点击监听器 -----------------------------

    protected String getCacheKey() {
        return getCacheKeyPrefix() + mCatalog + "_" + mCurrentPage + "_" + TDevice.getPageSize();
    }

    public long getCacheExpire() {
        return Const.CACHE_EXPIRE_OND_DAY;
    }

    protected String getCacheKeyPrefix() {
        return null;
    }

    private void executeParserTask(JSONObject data, boolean fromCache) {
        cancelParserTask();
        mParserTask = new ParserTask(this, data, fromCache);
        mParserTask.execute();
    }

    private void cancelParserTask() {
        if (mParserTask != null) {
            mParserTask.cancel(true);
            mParserTask = null;
        }
    }

    protected void executeOnLoadDataError(String error) {
        if (mCurrentPage == 0) {
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
//        mAdapter.notifyDataSetChanged();
    }

    protected void executeOnLoadDataSuccess(List<?> data) {
        if (mState == STATE_REFRESH)
            mAdapter.clear();
        mAdapter.addData(data);
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        if (data.size() == 0 && mState == STATE_REFRESH) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
        } else if (data.size() < TDevice.getPageSize()) {
            if (mState == STATE_REFRESH)
                mAdapter.setState(BaseRecyclerAdapter.STATE_LESS_ONE_PAGE);
            else
                mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE);
        } else {
            mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_MORE);
        }
    }

    protected void executeOnLoadFinish() {
        mSwipeRefresh.setRefreshing(false);
        mState = STATE_NONE;
    }

    protected void onRefreshNetworkSuccess() {
        // TODO do nothing
    }

    // Parse model when request data success.
    private static class ParserTask extends AsyncTask<Void, Void, String> {
        private WeakReference<BaseRecyclerViewFragment> mInstance;
        private JSONObject responseData;
        private boolean parserError;
        private boolean fromCache;
        private List<?> list;

        public ParserTask(BaseRecyclerViewFragment instance, JSONObject data, boolean fromCache) {
            this.mInstance = new WeakReference<>(instance);
            this.responseData = data;
            this.fromCache = fromCache;
        }

        @Override
        protected String doInBackground(Void... params) {
            BaseRecyclerViewFragment instance = mInstance.get();
            if (instance == null) return null;
            try {
                ListEntity data = instance.parseList(responseData);
//                if (!fromCache) {
//                    UIHelper.sendNoticeBroadcast(instance.getActivity(), data);
//                }
                //new SaveCacheTask(instance, data, instance.getCacheKey()).execute();
                // save the cache
                if (!fromCache && instance.mCurrentPage == 0 && !TextUtils.isEmpty(instance
                        .getCacheKey())) {
                    CacheManager.setCache(instance.getCacheKey(), responseData.toString()
                                    .getBytes(),
                            instance.getCacheExpire(), CacheManager.TYPE_INTERNAL);
                }
                list = data.getList();
            } catch (Exception e) {
                e.printStackTrace();
                parserError = true;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            BaseRecyclerViewFragment instance = mInstance.get();
            if (instance != null) {
                if (parserError) {
                    //instance.readCacheData(instance.getCacheKey());
                    instance.executeOnLoadDataError(null);
                } else {
                    instance.executeOnLoadDataSuccess(list);
                    if (!fromCache) {
                        if (instance.mState == STATE_REFRESH) {
                            instance.onRefreshNetworkSuccess();
                        }
                    }
                    instance.executeOnLoadFinish();
                }
                if (fromCache) {
                    TLog.log(TAG, "key:" + instance.getCacheKey()
                            + ",set cache data finish ,begin to load network data.");
//                    instance.requestData(true);
                    instance.refresh();
                }
            }
        }
    }

    static class ReadCacheTask extends
            WeakAsyncTask<Void, Void, byte[], BaseRecyclerViewFragment> {

        public ReadCacheTask(BaseRecyclerViewFragment target) {
            super(target);
        }

        @Override
        protected byte[] doInBackground(BaseRecyclerViewFragment target,
                                        Void... params) {
            if (target == null) {
                TLog.log(TAG, "weak task target is null.");
                return null;
            }
            if (TextUtils.isEmpty(target.getCacheKey())) {
                TLog.log(TAG, "unset cache key.no cache.");
                return null;
            }

            byte[] data = CacheManager.getCache(target.getCacheKey());
            if (data == null) {
                TLog.log(TAG, "cache data is empty.:" + target.getCacheKey());
                return null;
            }

            TLog.log(TAG, "exist cache:" + target.getCacheKey() + " data:"
                    + data);

            return data;
        }

        @Override
        protected void onPostExecute(BaseRecyclerViewFragment target,
                                     byte[] result) {
            super.onPostExecute(target, result);
            if (target == null)
                return;
            if (result != null) {
                try {
                    target.executeParserTask(new JSONObject(new String(result)), true);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    TLog.log(TAG, "parser cache error :" + e.getMessage());
                }
            }
//            target.requestData(true);
            target.refresh();
        }
    }
}
