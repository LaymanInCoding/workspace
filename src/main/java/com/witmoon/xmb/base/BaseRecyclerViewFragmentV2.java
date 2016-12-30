package com.witmoon.xmb.base;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.MySwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.model.ListEntity;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TLog;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmblibrary.observablescrollview.ObservableRecyclerView;
import com.witmoon.xmblibrary.recyclerview.itemdecoration.linear.DividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * RecyclerView
 * Created by zhyh on 2015/6/15.
 */
public abstract class BaseRecyclerViewFragmentV2 extends BaseFragment implements
        BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemLongClickListener {

    private static final String TAG = "BaseRecycleViewFragment";
    protected boolean is_dow = false;
    protected MySwipeRefreshLayout mSwipeRefresh;
    protected ObservableRecyclerView mRecycleView;
    protected LinearLayoutManager mLayoutManager;
    protected BaseRecyclerAdapter mAdapter;
    protected EmptyLayout mErrorLayout;

    protected int mStoreEmptyState = -1;
    protected String mStoreEmptyMessage;

    protected int mCurrentPage;

    // 滚动监听, 用于控制加载更多数据(翻页)
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
        return R.layout.fragment_swipe_refresh_recyclerview;
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
        mErrorLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                refresh();
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

        mLayoutManager = getLayoutManager();
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setHasFixedSize(true);

        if (mAdapter != null) {
            mRecycleView.setAdapter(mAdapter);
            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        } else {
            mAdapter = getListAdapter();
            mAdapter.setOnItemClickListener(this);
            mAdapter.setOnItemLongClickListener(this);
            mRecycleView.setAdapter(mAdapter);

            if (requestDataWhenViewCreated()) {
                mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                refresh();
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
    protected boolean requestDataWhenViewCreated() {
        return true;
    }

    // 是否需要分隔线
    protected boolean isNeedListDivider() {
        return true;
    }

    // RecyclerView所需要的布局管理器, 默认为LinearLayoutManager
    protected LinearLayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    // 加载更多
    public void loadMore() {
        if (mState == STATE_NONE) {
            if (mAdapter.getState() == BaseRecyclerAdapter.STATE_LOAD_MORE) {
                TLog.log(TAG, "开始加载更多数据.");
                mCurrentPage++;
                mState = STATE_LOAD_MORE;
                requestData();
            }
        }
    }

    //解析数据
    protected ListEntity parseResponse(JSONObject response) throws Exception {
        return null;
    }

    // 刷新数据
    public void refresh() {
        mCurrentPage = 1;
        mState = STATE_REFRESH;
        requestData();
    }

    // 发送获取网络数据请求
    protected void requestData() {
    }

    // 获取网络请求监听器
    protected Listener<JSONObject> getDefaultListener() {
        return new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
                if (!tt.first) {
                    executeOnLoadDataError(tt.second);
                    return;
                }
                try {
                    ListEntity listEntity = parseResponse(response);
                    executeOnLoadDataSuccess(listEntity);
                    if (mState == STATE_REFRESH) {
                        onRefreshNetworkSuccess();
                    }
                    executeOnLoadFinish();
                } catch (Exception e) {
                    executeOnLoadDataError(null);
                }
                if (is_dow) {
                    try {
                        if (response.getJSONArray("data").length() == 0) {
                            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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

    // 数据加载出现错误时调用
    protected void executeOnLoadDataError(String error) {
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
        mAdapter.notifyDataSetChanged();
    }

    // 数据加载成功时调用, 用于更新状态
    protected void executeOnLoadDataSuccess(ListEntity listEntity) {
        if (mState == STATE_REFRESH)
            mAdapter.clear();
        mAdapter.addData(listEntity.getList());
        mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        if (listEntity.getList().size() == 0 && mState == STATE_REFRESH) {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
            return;
        }
        if (!listEntity.hasMoreData()) {
            if (mState == STATE_REFRESH)
                mAdapter.setState(BaseRecyclerAdapter.STATE_LESS_ONE_PAGE);
            else
                mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE);
        } else {
            mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_MORE);
        }
    }

    // 数据加载完成时调用, 包括成功与失败都会调用
    protected void executeOnLoadFinish() {
        mSwipeRefresh.setRefreshing(false);
        mState = STATE_NONE;
    }

    // 列表刷新成功时调用
    protected void onRefreshNetworkSuccess() {
        // TODO do nothing
    }
}
