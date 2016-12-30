package com.witmoon.xmb.activity.specialoffer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.SearchActivity;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.specialoffer.adapter.GroupBuyAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.ui.widget.CountDownTextView;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.TimeUtill;
import com.witmoon.xmb.util.TwoTuple;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by ZCM on 2016/1/19
 */
public class GroupBuyActivity extends BaseActivity {

    private boolean isrun = true;

    private Thread thread;

    private String mLink;

    private List<Map<String, Object>> mDatas = new ArrayList<>();

    private CountDownTextView time_text;

    private List<Map<String, Object>> mDataList = new ArrayList<>();

    private List<Map<String, Object>> mDataGrid = new ArrayList<>();

    private List<Map<String, Object>> mDataBottomGrid = new ArrayList<>();

    private JSONObject mData;

    private View headView;

    private SimpleDraweeView top_group_image;

    private GroupBuyAdapter adapter;

    private EmptyLayout emptyLayout;

    private Boolean has_footer = false;

    private ImageView searchImg;

    public static void start(Context context, String link) {
        Intent intent = new Intent(context, GroupBuyActivity.class);
        intent.putExtra("LINK", link);
        context.startActivity(intent);
    }

    public static void start(Context context, String link, String boy) {
        Intent intent = new Intent(context, GroupBuyActivity.class);
        intent.putExtra("LINK", link);
        context.startActivity(intent);
    }

    @Override
    protected int getActionBarTitleByResId() {
        return R.string.text_jing_group;
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        toolbar.setBackgroundColor(getResources().getColor(R.color.main_kin));
        setTitleColor_(R.color.main_kin);
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        TimeUtill.byeRemoveAll();
        mLink = getIntent().getStringExtra("LINK");
        initView();
        HomeApi.getTopic(mLink, topicCallback);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_group_buying;
    }

    private void initView() {
        searchImg = (ImageView) findViewById(R.id.toolbar_right_img);
        searchImg.setImageResource(R.mipmap.search_imags);
        searchImg.setVisibility(View.VISIBLE);
        searchImg.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
        mRootView = (RecyclerView) findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this, 2);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return ((stringAdapter.getItemCount() - 1 == position && has_footer) || position <= mDataList.size() || position == mDataList.size() + mDataGrid.size() + 1) ? ((GridLayoutManager) layoutManager).getSpanCount() : 1;
            }
        });
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        headView = getLayoutInflater().inflate(R.layout.header_group_buy, mRootView, false);
        time_text = (CountDownTextView) headView.findViewById(R.id.count_down_text);
        top_group_image = (SimpleDraweeView) headView.findViewById(R.id.top_group_image);
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) top_group_image.getLayoutParams();
        linearParams.width = MainActivity.screen_width;
        linearParams.height = MainActivity.screen_width * 350 / 750;
        top_group_image.setLayoutParams(linearParams);
        adapter = new GroupBuyAdapter(mDatas, this, this);
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        stringAdapter.addHeaderView(headView);
        mRootView.setAdapter(stringAdapter);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
    }

    //团购回调接口
    private Listener<JSONObject> topicCallback = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            Log.e("Response",response.toString());
//            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
//            if (!twoTuple.first) {
//                AppContext.showToastShort(twoTuple.second);
//                return;
//            }
            try {
                mData = response;
                Netroid.displayImage(mData.getString("top_banner"), top_group_image);
                time_text.setTime(Long.parseLong(mData.getString("end_time")) - System.currentTimeMillis() / 1000);
                mDataList = parseGroupHotResponse(mData);
                mDataGrid = parseGroupNormalResponse(mData);
                Map<String, Object> bottom_banner = new HashMap<String, Object>();
                bottom_banner.put("type", "banner");
                mDataBottomGrid = parseGroupBottomTopicResponse(mData);
                mDatas.addAll(mDataList);
                mDatas.addAll(mDataGrid);
                mDatas.add(bottom_banner);
                mDatas.addAll(mDataBottomGrid);
                start();

            } catch (JSONException e) {
                AppContext.showToastShort(e.getMessage());
            }
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            mRootView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFinish() {
            super.onFinish();
        }

        @Override
        public void onError(NetroidError error) {
            super.onError(error);
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
        }
    }

    // 解析团购界面上部数据
    private List<Map<String, Object>> parseGroupHotResponse(JSONObject hotSaleList) {
        List<Map<String, Object>> hotList = new ArrayList<>();
        try {
            JSONArray mArray = hotSaleList.getJSONArray("hot_goods");
            for (int i = 0; i < mArray.length(); i++) {
                JSONObject detail = mArray.getJSONObject(i);
                Map<String, Object> dm = new HashMap<>();
                Map<String, String> timeMap = new HashMap<>();
                dm.put("goods_id", detail.getString("goods_id"));
//                dm.put("shop_price", detail.getString("shop_price"));
                dm.put("goods_thumb", detail.getString("goods_thumb"));
//                dm.put("salesnum", detail.getString("salesnum"));
//                dm.put("goods_img", detail.getString("goods_img"));
                dm.put("goods_name", detail.getString("goods_name"));
                dm.put("goods_price", detail.getString("goods_price"));
//                dm.put("org_price", detail.getString("org_price"));
//                dm.put("origin_pic", detail.getString("origin_pic"));
//                dm.put("origin_name", detail.getString("origin_name"));
//                dm.put("goods_brief", detail.getString("goods_brief"));
//                dm.put("end_time", detail.getString("gmt_end_time"));
                dm.put("type", "hot");
                long def_time = Long.parseLong(detail.getString("gmt_end_time")) - System.currentTimeMillis() / 1000;
                if (def_time > 0) {

                    timeMap.put("time", def_time + "");
                } else {
                    timeMap.put("time", "售完");
                }
//                dm.put("market_price", detail.getString("market_price"));
                TimeUtill.byeAdd(timeMap);
                hotList.add(dm);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hotList;
    }

    //解析团购中部数据
    private List<Map<String, Object>> parseGroupNormalResponse(JSONObject hotSaleList) {
        List<Map<String, Object>> hotList = new ArrayList<>();
        try {
            JSONArray mArray = hotSaleList.getJSONArray("normal_goods");
            for (int i = 0; i < mArray.length(); i++) {
                JSONObject detail = mArray.getJSONObject(i);
                Map<String, Object> dm = new HashMap<>();
                dm.put("goods_id", detail.getString("goods_id"));
                dm.put("org_price", detail.getString("org_price"));
                dm.put("goods_thumb", detail.getString("goods_thumb"));
                dm.put("discount", detail.getString("discount"));
                dm.put("goods_name", detail.getString("goods_name"));
                dm.put("goods_brief", detail.getString("goods_brief"));
                dm.put("market_price", detail.getString("market_price"));
                dm.put("type", "mid");
                hotList.add(dm);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hotList;
    }

    //解析团购界面底部数据
    private List<Map<String, Object>> parseGroupBottomTopicResponse(JSONObject hotSaleList) {
        List<Map<String, Object>> hotList = new ArrayList<>();
        try {
            JSONArray mArray = hotSaleList.getJSONArray("group_topics");
            for (int i = 0; i < mArray.length(); i++) {
                JSONObject detail = mArray.getJSONObject(i);
                Map<String, Object> dm = new HashMap<>();
                dm.put("ad_code", detail.getString("ad_code"));
                dm.put("goods_id", detail.getString("id"));
                dm.put("end_time", detail.getString("end_time"));
                dm.put("title", detail.getString("title"));
                dm.put("type", "bot");
                dm.put("index", i);
                hotList.add(dm);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return hotList;
    }

//    private void setmListViewHeight(ListView listView) {
//        if (listView == null) return;
//        GroupBuyAdapter adapter;
//        adapter = (GroupBuyAdapter) listView.getAdapter();
//        if (adapter == null) {
//            // pre-condition
//            return;
//        }
//        int totalHeight = 0;
//        for (int i = 0; i < adapter.getCount(); i++) {
//            View listItem = adapter.getView(i, null, listView);
//            listItem.measure(0, 0);
//            totalHeight += listItem.getMeasuredHeight();
//        }
//        ViewGroup.LayoutParams params = listView.getLayoutParams();
//        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
//        listView.setLayoutParams(params);
//    }

    int result = 0;

    public void start() {
        thread = new Thread() {
            public void run() {
                while (isrun) {
                    try {
                        if (null == TimeUtill.byeLit || result == TimeUtill.byeLit.size()) {
                            break;
                        }
                        sleep(1000);
                        if (null != TimeUtill.byeLit)
                            for (Map<String, String> map : TimeUtill.byeLit) {
                                if (!"售完".equals(map.get("time"))) {
                                    if ("1".equals(map.get("time"))) {
                                        map.put("time", "售完");
                                        result++;
                                    } else {
                                        map.put("time", "" + (Integer.parseInt(map.get("time")) - 1));
                                    }
                                }
                            }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isrun = false;
        thread = null;
    }
}

