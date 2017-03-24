package com.witmoon.xmb.activity.card;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.api.MabaoCardApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by ming on 2017/3/20.
 * 福利卡
 */
public class GiftCardFragment extends BaseFragment implements GiftCardAdapter.OnItemClickListener{

    private View view;
    private int page = 1;
    private String card_id;
    private GiftCardAdapter mAdapter;
    private EmptyLayout mEmptyLayout;
    private ArrayList<JSONObject> mCardList = new ArrayList<>();
    private boolean has_footer = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_entitycard, container, false);
            mRootView = (RecyclerView) view.findViewById(R.id.recyclerView);
            mEmptyLayout = (EmptyLayout) view.findViewById(R.id.empty_layout);
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRecRequest(1);
                }
            });
            layoutManager = new GridLayoutManager(this.getContext(), 2);
            layoutManager.setOrientation(GridLayoutManager.VERTICAL);
            mRootView.setHasFixedSize(true);
            mRootView.setLayoutManager(layoutManager);
            mAdapter = new GiftCardAdapter(getContext(), mCardList);
            mAdapter.setOnItemClickListener(this);
            stringAdapter = new HeaderViewRecyclerAdapter(mAdapter);
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return ((stringAdapter.getItemCount() - 1 == position && has_footer)) ? ((GridLayoutManager) layoutManager).getSpanCount() : 1;
                }
            });
            mRootView.setAdapter(stringAdapter);
            setRecRequest(1);
        }
        return view;
    }

    @Override
    public void setRecRequest(int current_page) {
        if (current_page == 1) {
            MabaoCardApi.get_welfare_card(1, listener);
        } else {
            MabaoCardApi.get_welfare_card(page, listener);
        }
    }

    private Listener<JSONObject> listener = new Listener<JSONObject>() {

        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            try {
                JSONArray card_list = response.getJSONArray("cards_list");
                for (int i = 0; i < card_list.length(); i++) {
                    JSONObject card_obj = card_list.getJSONObject(i);
                    mCardList.add(card_obj);
                }
                if (card_list.length() < 20) {
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
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(NetroidError error) {
            super.onError(error);
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }
    };

    @Override
    public void onItemClick(int position) {
        try {
            String id = mCardList.get(position).getString("goods_id");
            CommodityDetailActivity.start(getContext(), id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
