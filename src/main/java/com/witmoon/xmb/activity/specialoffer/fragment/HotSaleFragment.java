package com.witmoon.xmb.activity.specialoffer.fragment;

import android.os.Bundle;
import android.view.View;

import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.specialoffer.adapter.HotSaleAdapter;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.base.BaseRecyclerViewFragmentV2;
import com.witmoon.xmb.model.ListEntity;
import com.witmoon.xmb.model.Market;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 最后疯抢Fragment
 * Created by zhyh on 2015/5/11.
 */
public class HotSaleFragment extends BaseRecyclerViewFragmentV2 {

    private String type;

    public static HotSaleFragment newInstance(String menuId) {
        HotSaleFragment fragment = new HotSaleFragment();
        Bundle bundle = new Bundle();
        bundle.putString("MENU_ID", menuId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        type = bundle.getString("MENU_ID");
    }

    @Override
    protected ListEntity parseResponse(final JSONObject response) throws Exception {
        final List<Market> markets = new ArrayList<>();
        JSONArray array = response.getJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
            Market market = Market.parse(array.getJSONObject(i));
            markets.add(market);
        }
        final boolean hashMore = response.getJSONObject("paginated").getInt("more") != 0;
        return new ListEntity() {
            @Override
            public List<?> getList() {
                return markets;
            }

            @Override
            public boolean hasMoreData() {
                return hashMore;
            }
        };
    }

    @Override
    protected BaseRecyclerAdapter getListAdapter() {
        return new HotSaleAdapter();
    }

    @Override
    protected boolean isNeedListDivider() {
        return false;
    }

    @Override
    protected void requestData() {
        HomeApi.functionList(type, mCurrentPage, getDefaultListener());
    }

    @Override
    public void onItemClick(View v, int position) {
        List<Market> markets = mAdapter.getData();
        MarketPlaceActivity.start(getActivity(), markets.get(position).getId());
    }
}
