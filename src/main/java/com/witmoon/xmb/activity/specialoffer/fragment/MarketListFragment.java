package com.witmoon.xmb.activity.specialoffer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.specialoffer.adapter.ComingSoonAdapter;
import com.witmoon.xmb.activity.specialoffer.adapter.MLAdapter;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.model.Market;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * "最新特卖"和"即将推出"卖场列表
 * Created by zhyh on 2015/7/21.
 */
public class MarketListFragment extends Fragment implements AbsListView.OnScrollListener,
        AdapterView.OnItemClickListener {
    private ListView mRootView;

    private List<Market> mMarketList = new ArrayList<>();
    private BaseAdapter mAdapter;

    private String mType;
    private int mState;
    private int mPageNo = 1;

    private int mLastItemIndex;
    private boolean mIsMoreData = true;

    public static MarketListFragment newInstance(String type) {
        MarketListFragment fragment = new MarketListFragment();

        Bundle bundle = new Bundle();
        bundle.putString("TYPE", type);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString("TYPE");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = new ListView(getActivity());
            mRootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            mRootView.setId(R.id.id_stickynavlayout_innerscrollview);
            mRootView.setOnScrollListener(this);
            mRootView.setOnItemClickListener(this);

            if (mType.equals("1")) {
                mAdapter = new MLAdapter(mMarketList);
            } else {
                mAdapter = new ComingSoonAdapter(mMarketList);
            }
            mRootView.setAdapter(mAdapter);

            HomeApi.homeMarket(mType, mPageNo, mCallback);
        }

        if (mRootView.getParent() != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }

        return mRootView;
    }

    // 网络响应回调接口
    private Listener<JSONObject> mCallback = new Listener<JSONObject>() {

        @Override
        public void onSuccess(JSONObject response) {
            try {
                List<Market> markets = parseResponse(response);
                mMarketList.addAll(markets);
                mAdapter.notifyDataSetChanged();

                mIsMoreData = response.getJSONObject("paginated").getInt("more") != 0;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private List<Market> parseResponse(JSONObject respObj) throws JSONException {
        JSONArray jsonArray = respObj.getJSONArray("data");
        final List<Market> markets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            try {
                markets.add(Market.parse(obj));
            } catch (JSONException ignored) {
            }
        }
        return markets;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && mLastItemIndex ==
                mAdapter.getCount() - 1) {
            if (!mIsMoreData) {
                AppContext.showToastShort("没有更多数据");
                return;
            }
            mPageNo++;
            HomeApi.homeMarket(mType, mPageNo, mCallback);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        mLastItemIndex = firstVisibleItem + visibleItemCount - 1;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mType.equals("1")) {
            Market market = mMarketList.get(position);
            MarketPlaceActivity.start(getActivity(), market.getId());
        }
    }
}
