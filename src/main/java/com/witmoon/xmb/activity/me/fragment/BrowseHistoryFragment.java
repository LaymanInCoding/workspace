package com.witmoon.xmb.activity.me.fragment;

import android.net.http.LoggingEventHandler;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.me.adapter.BrowseHistoryAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.base.BaseRecyclerViewFragmentV2;
import com.witmoon.xmb.model.BrowseHistory;
import com.witmoon.xmb.model.ListEntity;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 浏览历史
 * Created by zhyh on 2015/6/22.
 */
public class BrowseHistoryFragment extends BaseRecyclerViewFragmentV2 {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BaseActivity activity = (BaseActivity) getActivity();
        Toolbar toolbar = activity.getToolBar();
        TextView empty = (TextView) toolbar.findViewById(R.id.toolbar_right_text);
        empty.setVisibility(View.VISIBLE);
        empty.setText(R.string.text_empty);
        empty.setOnClickListener(this);
    }

    @Override
    protected BaseRecyclerAdapter getListAdapter() {
        return new BrowseHistoryAdapter(getActivity());
    }

    @Override
    protected void requestData() {
        UserApi.browseHistory(mCurrentPage, getDefaultListener());
    }

    @Override
    protected boolean isNeedListDivider() {
        return true;
    }

    @Override
    protected ListEntity parseResponse(JSONObject response) throws Exception {
        JSONArray historyArray = response.getJSONArray("data");
        final List<BrowseHistory> dataList = new ArrayList<>();
        for (int i = 0; i < historyArray.length(); i++) {
            JSONObject historyObject = historyArray.getJSONObject(i);
            BrowseHistory history = new BrowseHistory();
            history.setGoodsId(historyObject.getString("goods_id"));
            history.setGoodsName(historyObject.getString("goods_name"));
            history.setPhoto(historyObject.getString("goods_thumb"));
            history.setBrowseTime(historyObject.getString("add_time"));
            dataList.add(history);
        }
        final boolean isMoreData = response.getJSONObject("paginated").getInt("more") !=1;
        return new ListEntity() {
            @Override
            public List<?> getList() {
                return dataList;
            }

            @Override
            public boolean hasMoreData() {
                return isMoreData;
            }
        };
    }

    @Override
    protected void onItemClick(View view, int position) {
        BrowseHistory goods = (BrowseHistory) mAdapter.getData().get(position);
        CommodityDetailActivity.start(getActivity(), goods.getGoodsId());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.toolbar_right_text) {
            UserApi.cleanBrowseHistory(new Listener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
                    if (tt.first) {
                        refresh();
                        XmbUtils.showMessage(getActivity(), "操作成功");
                        return;
                    }
                    AppContext.showToast(tt.second);
                }

                @Override
                public void onError(NetroidError error) {
                    XmbUtils.showMessage(getActivity(), "操作失败");
                }
            });
        }
    }
}
