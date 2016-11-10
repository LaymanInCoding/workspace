package com.witmoon.xmb.activity.shopping;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.main.ShakeActivity;
import com.witmoon.xmb.activity.main.SignInActivity;
import com.witmoon.xmb.activity.mbq.activity.CollectActivity;
import com.witmoon.xmb.activity.shopping.adapter.CatAdapter;
import com.witmoon.xmb.activity.shopping.adapter.SelectedActivityAdapter;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.activity.webview.InteractiveWebViewActivity;
import com.witmoon.xmb.api.ShoppingApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.UIHelper;
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

public class AaffordableFragment extends BaseFragment{
    private View view;
    private View headerView;
    private EmptyLayout emptyLayout;
    private AutoScrollViewPager mAutoScrollViewPager;
    private CirclePageIndicator mCirclePageIndicator;
    private SelectedActivityAdapter adapter;
    private ArrayList<Map<String, Object>> mDatas = new ArrayList<>();
    private CatAdapter cat_adapter;
    private ArrayList<Map<String,String>> categoryList = new ArrayList<>();
    private RecyclerView cat_recycle_view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_affordable, container, false);
            headerView = inflater.inflate(R.layout.header_afford_fragment_new, container, false);
            setFont();
            cat_adapter = new CatAdapter(categoryList, getActivity());
            cat_adapter.setOnItemClickListener(new CatAdapter.OnItemClickListener() {
                @Override
                public void onItemnClick(Map<String, String> map) {
                    Bundle bundle = new Bundle();
                    bundle.putString("cat_id",map.get("id"));
                    bundle.putString("cat_name",map.get("name"));
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SUBCLASS,bundle);
                }
            });
            cat_recycle_view = (RecyclerView) headerView.findViewById(R.id.cat_recycle_view);
            GridLayoutManager gridlayoutManager = new GridLayoutManager(this.getContext(),2);
            gridlayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            cat_recycle_view.setHasFixedSize(true);
            cat_recycle_view.setLayoutManager(gridlayoutManager);
            cat_recycle_view.setAdapter(cat_adapter);
            emptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);View ad_container = headerView.findViewById(R.id.ad_container);
            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) ad_container.getLayoutParams();
            linearParams.width = MainActivity.screen_width;
            linearParams.height = MainActivity.screen_width * 350 / 750;
            ad_container.setLayoutParams(linearParams);
            mAutoScrollViewPager = (AutoScrollViewPager) headerView.findViewById(R.id.auto_scroll_pager);
            mCirclePageIndicator = (CirclePageIndicator) headerView.findViewById(R.id.auto_scroll_indicator);
            mRootView = (RecyclerView) view.findViewById(R.id.recyclerView);
            layoutManager = new LinearLayoutManager(this.getContext());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRootView.setHasFixedSize(true);
            mRootView.setLayoutManager(layoutManager);
            adapter = new SelectedActivityAdapter(mDatas, getActivity());
            stringAdapter = new HeaderViewRecyclerAdapter(adapter);
            stringAdapter.addHeaderView(headerView);
            mRootView.setAdapter(stringAdapter);
            emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRecRequest(1);
                }
            });
            setRecRequest(1);
        }

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    private void setFont(){
        AssetManager mgr = getActivity().getAssets();//得到AssetManager
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
        TextView qbfl = (TextView) headerView.findViewById(R.id.qbfl);
        TextView mbjx = (TextView) headerView.findViewById(R.id.mbjx);
        headerView.findViewById(R.id.sign_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), SignInActivity.class));
                }else{
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });
        headerView.findViewById(R.id.shake_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), ShakeActivity.class));
                }else{
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });
        headerView.findViewById(R.id.raffle_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AppContext.instance().isLogin()) {
                    Intent intent = new Intent(getActivity(), InteractiveWebViewActivity.class);
                    intent.putExtra("title", "幸运大转盘");
                    intent.putExtra("url", "http://api.xiaomabao.com/circle/raffle");
                    startActivity(intent);
                }else{
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });
        qbfl.setTypeface(tf);
        mbjx.setTypeface(tf);
    }

    public void setRecRequest(int current_page){
        ShoppingApi.get_affordable(new Listener<JSONObject>() {
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
                try {
                    JSONArray today_recommend_top_array = response.getJSONArray("today_recommend_top");
                    JSONArray today_recommend_bottom_array = response.getJSONArray("today_recommend_bot");
                    initBanner(today_recommend_top_array);
                    initCat(response.getJSONArray("category"));
                    initSelectedActivities(today_recommend_bottom_array);
                    emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                } catch (JSONException e) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }
        });
    }

    public void initSelectedActivities(JSONArray today_recommend_top_array){
        for (int i = 0; i < today_recommend_top_array.length(); i++) {
            Map<String, Object> map = new HashMap<>();
            try {
                JSONObject jsonObject = today_recommend_top_array.getJSONObject(i);
                map.put("ad_type", jsonObject.getString("ad_type"));
                map.put("act_id", jsonObject.getString("act_id"));
                map.put("ad_name", jsonObject.getString("ad_name"));
                map.put("ad_img", jsonObject.getString("ad_img"));
                map.put("goods",jsonObject.getJSONArray("goods"));
            } catch (JSONException e) {

            }
            mDatas.add(map);
        }
        adapter.notifyDataSetChanged();
    }

    public void initCat(JSONArray categoryJsonArray) {
        for(int i = 0;i < categoryJsonArray.length(); i++){
            HashMap<String,String> hashMap = new HashMap<>();
            try {
                hashMap.put("id",categoryJsonArray.getJSONObject(i).getString("cat_id"));
                hashMap.put("url",categoryJsonArray.getJSONObject(i).getString("icon"));
                hashMap.put("name",categoryJsonArray.getJSONObject(i).getString("cat_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            categoryList.add(hashMap);
        }
        cat_adapter.notifyDataSetChanged();
    }

    public void initBanner(JSONArray today_recommend_top_array){
        ArrayList<Map<String, String>> maplist = new ArrayList<>();
        for (int i = 0; i < today_recommend_top_array.length(); i++) {
            Map<String, String> map = new HashMap<>();
            try {
                JSONObject jsonObject = today_recommend_top_array.getJSONObject(i);
                map.put("ad_type", jsonObject.getString("ad_type"));
                map.put("act_id", jsonObject.getString("act_id"));
                map.put("ad_img", jsonObject.getString("ad_img"));
                map.put("ad_name", jsonObject.getString("ad_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            maplist.add(map);
        }
        initAutoScrollPager(maplist);
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
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
                     if (type == 1) {
                         MarketPlaceActivity.start(getActivity(), id, advertisements.get(position).get("ad_name"));
                    } else if (type == 2) {
                        CommodityDetailActivity.start(getActivity(), id);
                    } else if (type == 3) {
                        Intent intent = new Intent(getActivity(), InteractiveWebViewActivity.class);
                        intent.putExtra("url", id);
                        startActivity(intent);
                    } else if (type == 4) {
                        GroupBuyActivity.start(getContext(), id);
                    }
                 }
            }
        });
    }
}
