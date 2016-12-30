package com.witmoon.xmb.activity.mbq.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.mbq.activity.CircleActivity;
import com.witmoon.xmb.activity.mbq.activity.CollectActivity;
import com.witmoon.xmb.activity.mbq.adapter.MyCircleAdapter;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.views.CollectArticleActivity;
import com.witmoon.xmb.views.MajorArticleActivity;
import com.witmoon.xmb.views.MajorVoiceActivity;
import com.witmoon.xmb.activity.webview.InteractiveWebViewActivity;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.circle.CircleCategory;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.SharedPreferencesUtil;
import com.witmoon.xmb.util.XmbUtils;
import com.witmoon.xmblibrary.autoscrollviewpager.AutoScrollViewPager;
import com.xmb.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

public class MyCircleFragment extends BaseFragment implements MyCircleAdapter.OnItemClickListener {
    private View view, headerView;
    private AutoScrollViewPager mAutoScrollViewPager;
    private CirclePageIndicator mCirclePageIndicator;
    private List<Map<String, String>> advertisements = new ArrayList<>();
    private MyCircleAdapter adapter;
    private ArrayList<Object> mDatas = new ArrayList<>();
    private View voice_container, article_container, article_collect_container, collectView, story_container;
    private EmptyLayout emptyLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_my_circle, container, false);
            emptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
            headerView = inflater.inflate(R.layout.header_my_circle_fragment, container, false);
            initHeadView();
            mRootView = (RecyclerView) view.findViewById(R.id.recycle_view);
            mAutoScrollViewPager = (AutoScrollViewPager) headerView.findViewById(R.id.auto_scroll_pager);
            mCirclePageIndicator = (CirclePageIndicator) headerView.findViewById(R.id.auto_scroll_indicator);
            layoutManager = new LinearLayoutManager(this.getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRootView.setHasFixedSize(true);
            mRootView.setLayoutManager(layoutManager);

            adapter = new MyCircleAdapter(mDatas, this.getContext());
            adapter.setOnItemClickListener(this);
            stringAdapter = new HeaderViewRecyclerAdapter(adapter);
            stringAdapter.addHeaderView(headerView);
            mRootView.setAdapter(stringAdapter);

            View ad_container = headerView.findViewById(R.id.ad_container);
            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) ad_container.getLayoutParams();
            linearParams.width = MainActivity.screen_width;
            linearParams.height = MainActivity.screen_width * 350 / 750;
            ad_container.setLayoutParams(linearParams);

            IntentFilter logout = new IntentFilter(Const.INTENT_ACTION_REFRESH);
            getActivity().registerReceiver(loginout, logout);

            IntentFilter my_mbq = new IntentFilter(Const.INTENT_ACTION_REFRESH_MY_MBQ);
            getActivity().registerReceiver(refresh_my_mbq, my_mbq);
            emptyLayout.setOnLayoutClickListener(v -> {
                requestAdData();
                requestData();
                requestSearchHotWord();
            });
            requestAdData();
            requestData();
            requestSearchHotWord();
        }

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    private BroadcastReceiver loginout = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestData();
        }
    };
    private BroadcastReceiver refresh_my_mbq = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestData();
        }
    };

    private void initHeadView() {
        voice_container = headerView.findViewById(R.id.voice_container);
        article_container = headerView.findViewById(R.id.article_container);
        article_collect_container = headerView.findViewById(R.id.article_collect_container);
        story_container = headerView.findViewById(R.id.story_container);
        collectView = headerView.findViewById(R.id.collect_container);
        voice_container.setOnClickListener(this);
        article_container.setOnClickListener(this);
        story_container.setOnClickListener(this);
        article_collect_container.setOnClickListener(this);
        collectView.setOnClickListener(this);
    }

    private void requestSearchHotWord() {
        CircleApi.get_hot_search_keyword(new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Logger.json(response.toString());
                if (response.has("data")) {
                    try {
                        SharedPreferencesUtil.remove(getContext(), Const.MBQ_SEARCH_HOT_KEYWORD);
                        SharedPreferencesUtil.put(getContext(), Const.MBQ_SEARCH_HOT_KEYWORD, response.getJSONArray("data").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    SharedPreferencesUtil.remove(getContext(), Const.MBQ_SEARCH_HOT_KEYWORD);
                }
            }
        });
    }

    private void requestAdData() {
        CircleApi.get_circle_ads(new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Logger.json(jsonObject.toString());
                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("ad_type", jsonObject.getString("ad_type"));
                        map.put("act_id", jsonObject.getString("ad_con"));
                        map.put("ad_img", jsonObject.getString("ad_img"));
                        map.put("ad_name", jsonObject.getString("ad_name"));
                        advertisements.add(map);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                initAutoScrollPager(advertisements);
            }
        });
    }

    private void resetData() {
        mDatas.clear();
    }

    private void requestData() {
        if (AppContext.instance().isLogin()) {
            CircleApi.get_user_circle(new Listener<JSONObject>() {

                @Override
                public void onPreExecute() {
                    //emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                }

                @Override
                public void onError(NetroidError error) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }

                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        resetData();
                        JSONArray jsonArray = response.getJSONArray("user_circle");
                        if (jsonArray.length() == 0) {
                            setDataInit(0);
                        } else {
                            setDataInit(jsonArray.length());
                        }
                        SharedPreferencesUtil.remove(getActivity(), Const.MY_JOIN_CIRCLE_KEY);
                        JSONArray tmpJsonArray = new JSONArray();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            tmpJsonArray.put(i, jsonArray.getJSONObject(i).getInt("circle_id"));
                            mDatas.add(i + 1, CircleCategory.parse(jsonArray.getJSONObject(i)));
                        }
                        SharedPreferencesUtil.put(getActivity(), Const.MY_JOIN_CIRCLE_KEY, tmpJsonArray.toString());
                        analyticRecommend(response);

                        Intent intent = new Intent(Const.INTENT_ACTION_REFRESH_MY_MBQ_SEARCH);
                        getActivity().sendBroadcast(intent);
                    } catch (JSONException e) {
                        setDataInit(0);
                    }
                    stringAdapter.notifyDataSetChanged();
                    emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                }
            });
        }

        if (!AppContext.instance().isLogin()) {
            //setDataInit(0);
            CircleApi.get_recommend_cat(new Listener<JSONObject>() {
                @Override
                public void onPreExecute() {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                }

                @Override
                public void onError(NetroidError error) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }

                @Override
                public void onSuccess(JSONObject response) {
                    resetData();
                    SharedPreferencesUtil.remove(getActivity(), Const.MY_JOIN_CIRCLE_KEY);
                    analyticRecommend(response);
                    stringAdapter.notifyDataSetChanged();
                    emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                }
            });
        }

    }

    private void analyticRecommend(JSONObject response) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("title", "推荐麻包圈");
        hashMap.put("type", "text");
        mDatas.add(hashMap);
        try {
            JSONArray jsonArray = response.getJSONArray("recommend");
            for (int i = 0; i < jsonArray.length(); i++) {
                mDatas.add(CircleCategory.parse(jsonArray.getJSONObject(i)));
            }
            if (jsonArray.length() == 0) {
                HashMap<String, String> hashMapNo = new HashMap<>();
                hashMapNo.put("title", "暂无推荐的麻包圈，赶紧催促编辑更新吧！");
                hashMapNo.put("type", "no_circle");
                mDatas.add(hashMapNo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDataInit(int t) {
        HashMap<String, String> hashMap = new HashMap<>();
        if (t != 0) {
            hashMap.put("title", "我的麻包圈（" + t + "）");
        } else {
            hashMap.put("title", "我的麻包圈");
        }
        hashMap.put("type", "text");
        mDatas.add(0, hashMap);

        if (t == 0) {
            HashMap<String, String> hashMapNo = new HashMap<>();
            hashMapNo.put("title", "您还没有加入任何圈子，点击加入吧。");
            hashMapNo.put("type", "no_circle");
            mDatas.add(1, hashMapNo);
        }
    }

    // 初始化广告轮播
    private void initAutoScrollPager(final List<Map<String, String>> advertisements) {

        mAutoScrollViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return advertisements.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                SimpleDraweeView imageView = new SimpleDraweeView(getActivity());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageURI(Uri.parse(advertisements.get(position).get("ad_img")));
                container.addView(imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        mCirclePageIndicator.setViewPager(mAutoScrollViewPager);
        mCirclePageIndicator.setSnap(true);
        mAutoScrollViewPager.setScrollFactgor(5);
        mAutoScrollViewPager.setOffscreenPageLimit(4);
        mAutoScrollViewPager.startAutoScroll(5000);
        mAutoScrollViewPager.setOnPageClickListener(new AutoScrollViewPager.OnPageClickListener() {
            @Override
            public void onPageClick(AutoScrollViewPager pager, int position) {
                if (null != advertisements.get(position).get("ad_type")) {
                    int type = Integer.parseInt(advertisements.get(position).get("ad_type"));
                    //专题  2商品 3网页 4团购 5帖子
                    String id = advertisements.get(position).get("act_id");
                    String name = advertisements.get(position).get("ad_name");
                    if (type == 1) {
                        MarketPlaceActivity.start(getActivity(), id);
                    } else if (type == 2) {
                        CommodityDetailActivity.start(getActivity(), id);
                    } else if (type == 3) {
                        Intent intent = new Intent(getActivity(), InteractiveWebViewActivity.class);
                        intent.putExtra("title", name);
                        intent.putExtra("url", id);
                        startActivity(intent);
                    } else if (type == 4) {
                        GroupBuyActivity.start(getContext(), id);
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(CircleCategory circleCategory) {
        Intent intent = new Intent(getActivity(), CircleActivity.class);
        intent.putExtra("circle_id", circleCategory.getCircle_id());
        intent.putExtra("circle_logo", circleCategory.getCircle_logo());
        intent.putExtra("circle_name", circleCategory.getCircle_name());
        intent.putExtra("circle_post_cnt", circleCategory.getCircle_post_cnt() + "个话题");
        intent.putExtra("circle_is_join", circleCategory.getUser_is_join());
        startActivity(intent);
    }

    @Override
    public void onItemButtonClick(int circle_id) {
        if (!AppContext.instance().isLogin()) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        } else {
            CircleApi.join_circle(circle_id, new Listener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getInt("status") == 1) {
                            XmbUtils.showMessage(getActivity(), response.getString("info"));
                            requestData();
                        } else {
                            XmbUtils.showMessage(getActivity(), response.getString("info"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    @Override
    public void onItemMessageClick() {
        MbqFragment.mViewPager.setCurrentItem(2);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.voice_container:
                startActivity(new Intent(getActivity(), MajorVoiceActivity.class));
                break;
            case R.id.article_container:
                startActivity(new Intent(getActivity(), MajorArticleActivity.class));
                break;
            case R.id.story_container:
                Intent intent = new Intent();
                intent.putExtra("title", "睡前故事");
                intent.putExtra("share_right", "1");
                intent.putExtra("url", ApiHelper.BASE_URL + "discovery/story");
                intent.setClass(getContext(), InteractiveWebViewActivity.class);
                startActivity(intent);
                break;
            case R.id.article_collect_container:
                if (AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), CollectArticleActivity.class));
                    break;
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
            case R.id.collect_container:
                if (AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), CollectActivity.class));
                    break;
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    break;
                }
        }
    }
}
