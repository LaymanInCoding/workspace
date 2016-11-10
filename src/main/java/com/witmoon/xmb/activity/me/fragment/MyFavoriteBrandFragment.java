package com.witmoon.xmb.activity.me.fragment;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.activity.goods.CommodityListActivity;
import com.witmoon.xmb.activity.me.adapter.MyFavoriteBrandAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.base.BaseRecyclerViewFragmentV2;
import com.witmoon.xmb.model.ListEntity;
import com.witmoon.xmb.util.TwoTuple;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的收藏之品牌
 * Created by zhyh on 2015/6/23.
 */
public class MyFavoriteBrandFragment extends BaseRecyclerViewFragmentV2 {

    @Override
    protected LinearLayoutManager getLayoutManager() {
        return new GridLayoutManager(getActivity(), 2);
    }

    @Override
    protected BaseRecyclerAdapter getListAdapter() {
        return new MyFavoriteBrandAdapter();
    }

    @Override
    protected void requestData() {
        UserApi.brandCollectionList(mCurrentPage, getDefaultListener());
    }

    @Override
    protected ListEntity parseResponse(JSONObject response) throws Exception {
        JSONArray brandArray = response.getJSONArray("data");
    //    Log.e("data_list",response.toString());
        final List<Map<String, String>> dataList = new ArrayList<>(brandArray.length());
        for (int i = 0; i < brandArray.length(); i++) {
            JSONObject brandObject = brandArray.getJSONObject(i);
            Map<String, String> tmap = new HashMap<String, String>();
            tmap.put("id", brandObject.getString("rec_id"));
            tmap.put("brandId", brandObject.getString("brand_id"));
            tmap.put("brandName", brandObject.getString("brand_name"));
            tmap.put("brandLogo", brandObject.getString("brand_logo"));
            dataList.add(tmap);
        }
        final boolean isMoreData = response.getJSONObject("paginated").getInt("more") != 0;
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
    protected boolean onItemLongClick(View view, final int position) {
        new AlertDialog.Builder(getActivity()).setMessage("确定要删除本收藏吗?")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, String> brandMap = (Map<String, String>) mAdapter.getData()
                                .get(position);
                        UserApi.cancelBrandCollection(brandMap.get("id"), mCancelCallback);
                    }
                }).setCancelable(true).show();
        return super.onItemLongClick(view, position);
    }

    @Override
    protected void onItemClick(View view, int position) {
        Map<String, String> brandMap = (Map<String, String>) mAdapter.getData().get(position);
        CommodityListActivity.start(getActivity(), brandMap.get("brandId"), brandMap.get
                ("brandName"), CommodityListActivity.BY_BRAND);
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
