package com.witmoon.xmb.activity.mabao;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.mabao.adapter.AddordableAdapter;
import com.witmoon.xmb.activity.mabao.adapter.RefreshAdapter;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.webview.InteractiveWebViewActivity;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.BoderScrollView;
import com.witmoon.xmb.ui.MyGridView;
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

import cn.easydone.swiperefreshendless.EndlessRecyclerOnScrollListener;
import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by de on 2016/3/16.
 */
public class AaffordableFragment extends BaseFragment implements AddordableAdapter.OnItemClickListener {
    private View view,headerView;
    private AutoScrollViewPager mAutoScrollViewPager;
    private CirclePageIndicator mCirclePageIndicator;
    private ImageView[] topImages = new ImageView[3];
    private ImageView[] botImages = new ImageView[4];
    private ImageView[] allImages = new ImageView[8];
    private TextView[] allText = new TextView[8];
    private ArrayList<Map<String, String>> mDatas;
    private AddordableAdapter adapter;
    private ImageView group_img;
    private EmptyLayout emptyLayout;
    private int page = 1;
    private Boolean has_footer = false;
    private int[] topId = {R.id.ll_left_image, R.id.ll_center_image, R.id.ll_right_image};
    private int[] botId = {R.id.center_img_left, R.id.center_right, R.id.bottom_img_left, R.id.bottom_right};
    private int[] textId = {R.id.text_brands1, R.id.text_brands2, R.id.text_brands3, R.id.text_brands4, R.id.text_brands5, R.id.text_brands6, R.id.text_brands7, R.id.text_brands8};
    private int[] allId = {R.id.goods_brands1, R.id.goods_brands2, R.id.goods_brands3, R.id.goods_brands4, R.id.goods_brands5, R.id.goods_brands6, R.id.goods_brands7, R.id.goods_brands8};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            mDatas = new ArrayList<>();
            view = inflater.inflate(R.layout.fragment_affordable, container, false);
            headerView = inflater.inflate(R.layout.header_afford_fragment, container, false);
            emptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
            View ad_container = headerView.findViewById(R.id.ad_container);
            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) ad_container.getLayoutParams();
            linearParams.width = MainActivity.screen_width;
            linearParams.height = MainActivity.screen_width * 232 / 640;
            ad_container.setLayoutParams(linearParams);
            mAutoScrollViewPager = (AutoScrollViewPager) headerView.findViewById(R.id.auto_scroll_pager);
            mCirclePageIndicator = (CirclePageIndicator) headerView.findViewById(R.id.auto_scroll_indicator);
            mRootView = (RecyclerView) view.findViewById(R.id.recyclerView);
            layoutManager = new GridLayoutManager(this.getContext(), 2);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


            mRootView.setHasFixedSize(true);
            mRootView.setLayoutManager(layoutManager);
            adapter = new AddordableAdapter(mDatas, getActivity(),"0");
            adapter.setOnItemClickListener(this);
            stringAdapter = new HeaderViewRecyclerAdapter(adapter);
            stringAdapter.addHeaderView(headerView);

            ((GridLayoutManager)layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return ((stringAdapter.getItemCount() - 1 == position && has_footer) || position == 0) ? ((GridLayoutManager) layoutManager).getSpanCount() : 1;
                }
            });

            group_img = (ImageView) headerView.findViewById(R.id.group_img);
            mRootView.setAdapter(stringAdapter);
            emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setRecRequest(1);
                }
            });
            group_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.GROUP_BUYING);
                }
            });
            setRecRequest(1);
        }

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    public void setRecRequest(int current_page){
        if(page == 1){
            FriendshipApi.affordablePlanet(listener);
        }else{
            FriendshipApi.affordablePlanets(page + "", listener);
        }
    }

    Listener<JSONObject> listener = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            if(page == 1) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }
        }

        @Override
        public void onSuccess(JSONObject response) {
            try {
                if (response.has("group_buy_img")) {
                    Netroid.displayImage(response.getString("group_buy_img"),group_img);
                    ArrayList<Map<String, String>> maplist = new ArrayList<>();
                    JSONArray today_recommend_top_array = response.getJSONArray("today_recommend_top");
                    for (int i = 0; i < today_recommend_top_array.length(); i++) {
                        Map<String, String> map = new HashMap<>();
                        JSONObject jsonObject = today_recommend_top_array.getJSONObject(i);
                        map.put("ad_type", jsonObject.getString("ad_type"));
                        map.put("act_id", jsonObject.getString("act_id"));
                        map.put("ad_img", jsonObject.getString("ad_img"));
                        maplist.add(map);
                    }
                    initAutoScrollPager(maplist);
                    JSONArray top_array = response.getJSONArray("today_recommend_mid");
                    for (int i = 0; i < top_array.length(); i++) {
                        final JSONObject topJson = top_array.getJSONObject(i);
                        topImages[i] = (ImageView) headerView.findViewById(topId[i]);
                        Netroid.displayImage(topJson.getString("ad_img"), topImages[i]);
                        topImages[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try {
                                    MarketPlaceActivity.start(getActivity(), topJson.getString("act_id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    JSONArray bot_array = response.getJSONArray("today_recommend_bot");
                    for (int i = 0; i < bot_array.length(); i++) {
                        final JSONObject botJson = bot_array.getJSONObject(i);
                        botImages[i] = (ImageView) headerView.findViewById(botId[i]);
                        botImages[i].setVisibility(View.VISIBLE);
                        botImages[i].setMaxWidth(MainActivity.screen_width/2);
                        botImages[i].setMaxHeight(MainActivity.screen_width / 2);
                        Netroid.displayImage(botJson.getString("ad_img"), botImages[i]);
                        botImages[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    MarketPlaceActivity.start(getActivity(), botJson.getString("act_id"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    JSONArray category = response.getJSONArray("category");
                    for (int i = 0; i < category.length(); i++) {
                        final JSONObject categoryJson = category.getJSONObject(i);
                        allImages[i] = (ImageView) headerView.findViewById(allId[i]);
                        allText[i] = (TextView) headerView.findViewById(textId[i]);
                        Netroid.displayImage(categoryJson.getString("icon"), allImages[i]);
                        allText[i].setText(categoryJson.getString("cat_name"));
                        allImages[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                try {
                                    bundle.putString("cat_id",categoryJson.getString("cat_id"));
                                    bundle.putString("cat_name",categoryJson.getString("cat_name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                UIHelper.showSimpleBack(getActivity(),SimpleBackPage.SUBCLASS,bundle);
                            }
                        });
                    }
                }
                JSONArray recommend_goods = response.getJSONArray("recommend_goods");
                for (int i = 0; i < recommend_goods.length(); i++) {
                    JSONObject recommend_goodsJson = recommend_goods.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("goods_id", recommend_goodsJson.getString("goods_id"));
                    map.put("goods_thumb", recommend_goodsJson.getString("goods_thumb"));
                    map.put("goods_name", recommend_goodsJson.getString("goods_name"));
                    map.put("goods_price", recommend_goodsJson.getString("goods_price"));
                    mDatas.add(map);
                }
                if(recommend_goods.length() < 20){
                    removeFooterView();
                    has_footer = false;
                }else{
                    if(page == 1) {
                        has_footer = true;
                    }
                    createLoadMoreView();
                    resetStatus();
                }
                page += 1;
                stringAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
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
                        MarketPlaceActivity.start(getActivity(), id);
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

    @Override
    public void onItemButtonClick(Map<String, String> map) {
        CommodityDetailActivity.start(getActivity(), map.get("goods_id"));
    }
}
