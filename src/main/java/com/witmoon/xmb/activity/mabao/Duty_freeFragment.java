package com.witmoon.xmb.activity.mabao;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.mabao.adapter.Duty_freeAdapter;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.api.FriendshipApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.BoderScrollView;
import com.witmoon.xmb.ui.MyListView;
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

/**
 * Created by de on 2016/3/16.
 */
public class Duty_freeFragment extends BaseFragment {
    private View view;
    private AutoScrollViewPager mAutoScrollViewPager;
    private CirclePageIndicator mCirclePageIndicator;
    private ImageView[] imageViews = new ImageView[8];
    private TextView[] textViews = new TextView[8];
    private List<Map<String, String>> mDatas;
    private Duty_freeAdapter adapter;
    private LinearLayout mLinearLayout;
    private EmptyLayout emptyLayout;
    private int imgId[] = {R.id.goods_brand1, R.id.goods_brand2, R.id.goods_brand3, R.id.goods_brand4, R.id.goods_brand5, R.id.goods_brand6, R.id.goods_brand7, R.id.goods_brand8};
    private int[] textId = {R.id.text_brand1, R.id.text_brand2, R.id.text_brand3, R.id.text_brand4, R.id.text_brand5, R.id.text_brand6, R.id.text_brand7, R.id.text_brand8};
    private View headerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            mDatas = new ArrayList<>();
            adapter = new Duty_freeAdapter((ArrayList<Map<String, String>>) mDatas, getActivity());
            view = inflater.inflate(R.layout.fragment_duty_free, container, false);
            mRootView = (RecyclerView) view.findViewById(R.id.linearlistview);
            layoutManager = new LinearLayoutManager(this.getContext());
            mRootView.setHasFixedSize(true);
            mRootView.setLayoutManager(layoutManager);
            headerView = inflater.inflate(R.layout.header_duty_free, mRootView, false);

            View ad_container = headerView.findViewById(R.id.ad_container);
            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) ad_container.getLayoutParams();
            linearParams.width = MainActivity.screen_width;
            linearParams.height = MainActivity.screen_width * 232 / 640;
            ad_container.setLayoutParams(linearParams);
            emptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
            mAutoScrollViewPager = (AutoScrollViewPager) headerView.findViewById(R.id.auto_scroll_pagers);
            mCirclePageIndicator = (CirclePageIndicator) headerView.findViewById(R.id.auto_scroll_indicators);
            mLinearLayout = (LinearLayout) headerView.findViewById(R.id.add_goods);
            stringAdapter = new HeaderViewRecyclerAdapter(adapter);
            stringAdapter.addHeaderView(headerView);
            mRootView.setAdapter(stringAdapter);
            emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FriendshipApi.taxfreeStore(listener);
                }
            });
            FriendshipApi.taxfreeStore(listener);

        }
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        return view;
    }

    Listener<JSONObject> listener = new Listener<JSONObject>() {
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
            ArrayList<Map<String, String>> maplist = new ArrayList<>();
            try {
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
                JSONArray category = response.getJSONArray("category");
                for (int i = 0; i < category.length(); i++) {
                    final JSONObject categoryJson = category.getJSONObject(i);
                    imageViews[i] = (ImageView) headerView.findViewById(imgId[i]);
                    textViews[i] = (TextView) headerView.findViewById(textId[i]);
                    Netroid.displayImage(categoryJson.getString("c_img"), imageViews[i]);
                    textViews[i].setText(categoryJson.getString("c_name"));
                    imageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();
                            try {
                                bundle.putString("cat_id",categoryJson.getString("c_id"));
                                bundle.putString("is_type","1");
                                bundle.putString("cat_name",categoryJson.getString("c_name"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            UIHelper.showSimpleBack(getActivity(),SimpleBackPage.SUBCLASS_BOM,bundle);
                        }
                    });
                }

                JSONArray recommend_goods = response.getJSONArray("recommend_goods");
                for (int i = 0; i < recommend_goods.length(); i++) {
                    final JSONObject recommend_goodsJson = recommend_goods.getJSONObject(i);
                    Map<String, String> map = new HashMap<String, String>();
                    View myView = LayoutInflater.from(getActivity()).inflate(R.layout.duty_free_h, mLinearLayout, false);
                    ImageView imageView = (ImageView) myView.findViewById(R.id.h_goods_img);
                    TextView name = (TextView) myView.findViewById(R.id.goods_name);
                    TextView price = (TextView) myView.findViewById(R.id.goods_price);
                    Netroid.displayImage(recommend_goodsJson.getString("goods_thumb"), imageView);
                    name.setText(recommend_goodsJson.getString("goods_name"));
                    price.setText(recommend_goodsJson.getString("goods_price"));
                    myView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                CommodityDetailActivity.start(getActivity(), recommend_goodsJson.getString("goods_id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mLinearLayout.addView(myView);
                }

                JSONArray today_recommend_bot = response.getJSONArray("today_recommend_bot");
                for (int i = 0; i < today_recommend_bot.length(); i++) {
                    Map<String, String> map = new HashMap<>();
                    JSONObject jsonObject = today_recommend_bot.getJSONObject(i);
                    map.put("act_img", jsonObject.getString("ad_img"));
                    map.put("act_id", jsonObject.getString("act_id"));
                    mDatas.add(map);
                }
                stringAdapter.notifyDataSetChanged();
            } catch (JSONException e ){
                e.printStackTrace();
            }
            mRootView.setVisibility(View.VISIBLE);
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
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
                    Netroid.displayAdImage(advertisements.get(position).get("ad_img"), imageView);
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
                        Bundle args = new Bundle();
                        if (type == 1) {
                            MarketPlaceActivity.start(getActivity(), id);
                        } else if (type == 2) {
                            CommodityDetailActivity.start(getActivity(), id);
                        } else if (type == 3) {
                            args.putSerializable("URL", id);
                            Log.e("URL", id);
                            UIHelper.showSimpleBack(getActivity(), SimpleBackPage.VACCINE_INDEX, args);
                        } else if (type == 4) {
                            GroupBuyActivity.start(getContext(), id);
                        } else if (type == 5) {
                        }
                    }
                }
            });
        }
    };
}