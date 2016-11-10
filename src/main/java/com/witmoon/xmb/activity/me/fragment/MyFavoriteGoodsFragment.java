package com.witmoon.xmb.activity.me.fragment;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.me.adapter.MyFavoriteGoodsAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.base.BaseRecyclerViewFragmentV2;
import com.witmoon.xmb.model.FavoriteGoods;
import com.witmoon.xmb.model.ListEntity;
import com.witmoon.xmb.util.TwoTuple;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的收藏之商品
 * Created by zhyh on 2015/6/23.
 */
public class MyFavoriteGoodsFragment extends BaseRecyclerViewFragmentV2 {
    @Override
    protected BaseRecyclerAdapter getListAdapter() {
        MyFavoriteGoodsAdapter adapter = new MyFavoriteGoodsAdapter();
        adapter.setOnAddCartClickListener(new MyFavoriteGoodsAdapter.OnAddCartClickListener() {
            @Override
            public void addToCart(String goodsId) {
//                GoodsApi.addToCart(goodsId,"", 1, mAddCartCallback);
                CommodityDetailActivity.start(getActivity(), goodsId);
            }
        });
        return adapter;
    }

    // 添加到购物车回调
    private Listener<JSONObject> mAddCartCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            AppContext.showToastShort("添加成功");
        }
    };

    @Override
    protected void requestData() {
        UserApi.collectionList(mCurrentPage, getDefaultListener());
    }

    @Override
    protected ListEntity parseResponse(final JSONObject respObj) throws Exception {
        final List<FavoriteGoods> dataList = new ArrayList<>();
        JSONArray goodsArray = respObj.getJSONArray("data");
        for (int i = 0; i < goodsArray.length(); i++) {
            JSONObject goodsObj = goodsArray.getJSONObject(i);
            dataList.add(FavoriteGoods.parse(goodsObj));
        }
        return new ListEntity() {
            @Override
            public List<?> getList() {
                return dataList;
            }

            @Override
            public boolean hasMoreData() {
                try {
                    return respObj.getJSONObject("paginated").getInt("more") != 0;
                } catch (JSONException e) {
                    return false;
                }
            }
        };
    }

    @Override
    protected void onItemClick(View view, int position) {
        FavoriteGoods goods = (FavoriteGoods) mAdapter.getData().get(position);
        CommodityDetailActivity.start(getActivity(), goods.getGoodsId());
    }

    @Override
    protected boolean onItemLongClick(View view, final int position) {
        new AlertDialog.Builder(getActivity()).setMessage("确定要删除本收藏吗?")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FavoriteGoods goods = (FavoriteGoods) mAdapter.getData().get(position);
                        UserApi.cancelCollect(goods.getId(), mCancelCallback);
                    }
                }).setCancelable(true).show();
        return super.onItemLongClick(view, position);
    }

    // 取消收藏回调接口
    private Listener<JSONObject> mCancelCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                AppContext.showToastShort(tt.second);
                return;
            }
            refresh();
            AppContext.showToastShort("操作成功");
        }
    };
}
