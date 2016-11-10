package com.witmoon.xmb.activity.friendship.fragment;

import com.witmoon.xmb.activity.friendship.adapter.ActivityAdapter;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.base.BaseRecyclerAdapter;
import com.witmoon.xmb.base.BaseRecyclerViewFragmentV2;
import com.witmoon.xmb.model.ListEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 活动Fragment
 * Created by zhyh on 2015/6/13.
 */
public class ActivityFragment extends BaseRecyclerViewFragmentV2 {

    @Override
    protected BaseRecyclerAdapter getListAdapter() {
        ActivityAdapter activityAdapter = new ActivityAdapter();
        activityAdapter.setOnParticipateInClickListener(new ActivityAdapter
                .OnParticipateInClickListener() {
            @Override
            public void onParticipateInClick(String link) {
                try {
                    JSONObject linkObj = new JSONObject(link);
                    int type = linkObj.getInt("type");
                    if (type == 1) {
                        MarketPlaceActivity.start(getActivity(), linkObj.getString("id"));
                    } else if (type == 2) {
                        CommodityDetailActivity.start(getActivity(), linkObj.getString("id"));
                    }
                } catch (JSONException ignored) {
                }
            }
        });
        return activityAdapter;
    }

    @Override
    protected void requestData() {
        FriendshipApi.activeList(mCurrentPage, getDefaultListener());
    }

    @Override
    protected ListEntity parseResponse(JSONObject respObj) throws Exception {
        final List<Map<String, String>> mDataList = new ArrayList<>();
        JSONArray activeArray = respObj.getJSONArray("data");
        for (int i = 0; i < activeArray.length(); i++) {
            JSONObject activeObj = activeArray.getJSONObject(i);
            Map<String, String> tmap = new HashMap<>();
            tmap.put("content", activeObj.getString("act_content"));
            tmap.put("title", activeObj.getString("act_name"));
            tmap.put("image", activeObj.getString("act_imgs"));
            tmap.put("link", activeObj.getString("act_link"));
            tmap.put("name", activeObj.getString("nick_name"));
            tmap.put("avatar", activeObj.getString("header_img"));
            mDataList.add(tmap);
        }
        final boolean isMore = respObj.getJSONObject("paginated").getInt("more") != 0;
        return new ListEntity() {
            @Override
            public List<?> getList() {
                return mDataList;
            }

            @Override
            public boolean hasMoreData() {
                return isMore;
            }
        };
    }
}
