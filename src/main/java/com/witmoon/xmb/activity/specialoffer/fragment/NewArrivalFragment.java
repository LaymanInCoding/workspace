package com.witmoon.xmb.activity.specialoffer.fragment;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.image.NetworkImageView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.main.ShakeActivity;
import com.witmoon.xmb.activity.main.SignInActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.specialoffer.adapter.MarketListAdapter;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.Advertisement;
import com.witmoon.xmb.model.Market;
import com.witmoon.xmb.ui.widget.EdgeObserverScrollView;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmblibrary.autoscrollviewpager.AutoScrollViewPager;
import com.witmoon.xmblibrary.linearlistview.LinearListView;
import com.witmoon.xmblibrary.linearlistview.listener.OnItemClickListener;
import com.xmb.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 特卖页面"上新"Fragment
 * Created by zhyh on 2015/5/11.
 */
public class NewArrivalFragment extends BaseFragment {

    private View mRootView;
    private EdgeObserverScrollView mScrollView;

    private DecimalFormat format = new DecimalFormat("#.#");

    // 广告自动播放ViewPager及PagerIndicator
    private AutoScrollViewPager mAutoScrollViewPager;
    private CirclePageIndicator mAutoScrollIndicator;

    private CheckedTextView mNewestBtn;
    private CheckedTextView mComingBtn;

    // 最新特卖及即将推出两个LinearListView
    private LinearListView mNewestListView;
    private LinearListView mComingListView;

    // 最新特卖及即将推出两个LinearListView适配器
    private MarketListAdapter mComingAdapter;
    private MarketListAdapter mNewestAdapter;

    // 最新特卖及即将推出两个LinearListView对应的数据集合
    private List<Market> mNewestData = new ArrayList<>();
    private List<Market> mComingData = new ArrayList<>();

    private int mNewestPageNo;
    private int mComingPageNo;

    private View mBackTopView;
    private LinearLayout mSliderLayout;
    private boolean isNewestShown = true;
    private View mSliderIndicator;
    private int mSliderLayoutHeight;
    private int mSliderLayoutTop;
    private int mScrollViewTop;

    /**
     * 手机屏幕宽度
     */
    private int screenWidth;
    private WindowManager mWindowManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_new_arrival, container, false);

            mRootView.findViewById(R.id.shake).setOnClickListener(this);
            mRootView.findViewById(R.id.signin).setOnClickListener(this);
            mRootView.findViewById(R.id.favorite).setOnClickListener(this);

            mScrollView = (EdgeObserverScrollView) mRootView.findViewById(R.id.scrollView);
            mComingBtn = (CheckedTextView) mRootView.findViewById(R.id.coming);
            mComingBtn.setOnClickListener(this);
            mNewestBtn = (CheckedTextView) mRootView.findViewById(R.id.newest);
            mNewestBtn.setOnClickListener(this);
            mSliderIndicator = mRootView.findViewById(R.id.slider);

            mAutoScrollViewPager = (AutoScrollViewPager) mRootView.findViewById(
                    R.id.auto_scroll_pager);
            mAutoScrollIndicator = (CirclePageIndicator) mRootView.findViewById(
                    R.id.auto_scroll_indicator);

            mNewestListView = (LinearListView) mRootView.findViewById(R.id.linearlistview);
            mComingListView = (LinearListView) mRootView.findViewById(R.id.linearlistview2);

            mSliderLayout = (LinearLayout) mRootView.findViewById(R.id.slider_layout);
            mBackTopView = mRootView.findViewById(R.id.back_top_btn);

            bindListeners();
            process();
        }

        if (mRootView.getParent() != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
        return mRootView;
    }

    protected void bindListeners() {
        mScrollView.setObserverListener(new EdgeObserverScrollView.OnEdgeObserverListener() {
            @Override
            public void onBottom() {
                if (isNewestShown) {
                    HomeApi.homeMarket("1", mNewestPageNo++, mNewestCallback);
                } else {
                }
            }
        });
        mScrollView.setOnScrollListener(new EdgeObserverScrollView.OnScrollListener() {
            @Override
            public void onScroll(int scrollY) {
                mSliderLayoutHeight = mSliderLayout.getHeight();
                mSliderLayoutTop = mSliderLayout.getTop();
                mScrollViewTop = mScrollView.getTop();
                if (scrollY > mSliderLayoutTop) {
                    mBackTopView.setVisibility(View.VISIBLE);
                } else if (scrollY <= mSliderLayoutTop + mSliderLayoutHeight) {
                    mBackTopView.setVisibility(View.GONE);
                }
            }
        });
        mNewestListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                MarketPlaceActivity.start(getActivity(), "");
            }
        });
        mComingListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                MarketPlaceActivity.start(getActivity(), "");
            }
        });
    }

    protected void process() {
        mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = mWindowManager.getDefaultDisplay().getWidth();

        // 请求网络数据
        HomeApi.carouselAdvertisement(advertisementCallback);
        HomeApi.promotionAdvertisement(mPromotionCallback);

        mComingAdapter = new MarketListAdapter(mComingData);
        mNewestAdapter = new MarketListAdapter(mNewestData);
        mNewestListView.setLinearAdapter(mNewestAdapter);
        mComingListView.setLinearAdapter(mComingAdapter);
    }

    // 加载广告回调
    private Listener<JSONObject> advertisementCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> tu = ApiHelper.parseResponseStatus(response);
            if (!tu.first) {
                AppContext.showToastShort(tu.second);
                return;
            }
            try {
                List<Advertisement> ads = Advertisement.parse(response.getJSONArray("data"));
                initAutoScrollPager(ads);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(NetroidError error) {
            AppContext.showToastShort(error.getMessage());
        }
    };


    // 首页中部促销广告回调接口
    private Listener<JSONObject> mPromotionCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            try {
                Map<String, Advertisement> advertisementMap = Advertisement.parsePosition(response
                        .getJSONArray("data"));
                initPromotion(advertisementMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    // 促销广告初始化
    private void initPromotion(Map<String, Advertisement> advertisementMap) {
        if (mRootView != null) {
            SimpleDraweeView ad_1 = (SimpleDraweeView) mRootView.findViewById(R.id.advertisement_1);
            bindPromotionClickListener(ad_1, advertisementMap.get("9").getLink());
            ad_1.setImageURI(Uri.parse(advertisementMap.get("9").getPicture()));

            SimpleDraweeView ad_2 = (SimpleDraweeView) mRootView.findViewById(R.id.advertisement_2);
            bindPromotionClickListener(ad_2, advertisementMap.get("10").getLink());
            ad_2.setImageURI(Uri.parse(advertisementMap.get("10").getPicture()));

            SimpleDraweeView ad_3 = (SimpleDraweeView) mRootView.findViewById(R.id.advertisement_3);
            bindPromotionClickListener(ad_3, advertisementMap.get("11").getLink());
            ad_3.setImageURI(Uri.parse(advertisementMap.get("11").getPicture()));

            SimpleDraweeView ad_4 = (SimpleDraweeView) mRootView.findViewById(R.id.advertisement_4);
            bindPromotionClickListener(ad_4, advertisementMap.get("12").getLink());
            ad_4.setImageURI(Uri.parse(advertisementMap.get("12").getPicture()));

            SimpleDraweeView ad_5 = (SimpleDraweeView) mRootView.findViewById(R.id.advertisement_5);
            bindPromotionClickListener(ad_5, advertisementMap.get("13").getLink());
            ad_5.setImageURI(Uri.parse(advertisementMap.get("13").getPicture()));
        }
    }

    private void bindPromotionClickListener(View view, final String link) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
    }

    private Listener<JSONObject> mNewestCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            try {
                List<Market> markets = parseResponse(response);
                mNewestData.addAll(markets);
                mNewestAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private List<Market> parseResponse(JSONObject respObj) throws JSONException {
        JSONArray jsonArray = respObj.getJSONArray("data");
        final List<Market> markets = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            try {
                markets.add(Market.parse(obj));
            } catch (JSONException ignored) {
            }
        }
        return markets;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        HomeApi.homeMarket("1", mNewestPageNo++, mNewestCallback);
        HomeApi.homeMarket("2", mComingPageNo++, mNewestCallback);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // 初始化广告轮播
    private void initAutoScrollPager(final List<Advertisement> advertisements) {
        mAutoScrollViewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return advertisements.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                SimpleDraweeView imageView = new SimpleDraweeView(getActivity());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setImageURI(Uri.parse(advertisements.get(position).getPicture()));
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
        mAutoScrollIndicator.setViewPager(mAutoScrollViewPager);
        mAutoScrollIndicator.setSnap(true);
        mAutoScrollViewPager.setScrollFactgor(5);
        mAutoScrollViewPager.setOffscreenPageLimit(4);
        mAutoScrollViewPager.startAutoScroll(5000);
        mAutoScrollViewPager.setOnPageClickListener(new AutoScrollViewPager.OnPageClickListener() {
            @Override
            public void onPageClick(AutoScrollViewPager pager, int position) {
                Advertisement ad = advertisements.get(position);
                try {
                    JSONObject linkObj = new JSONObject(ad.getLink());
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shake:
                ShakeActivity.startActivity(getActivity());
                break;
            case R.id.signin:
                SignInActivity.startActivity(getActivity());
                break;
            case R.id.coming:
                if (isNewestShown) {
                    ObjectAnimator.ofFloat(mSliderIndicator, "translationX", screenWidth / 2)
                            .setDuration(350).start();
                    mComingBtn.setChecked(true);
                    mNewestBtn.setChecked(false);
                    isNewestShown = false;
                    mNewestListView.setVisibility(View.GONE);
                    mComingListView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.newest:
                if (!isNewestShown) {
                    ObjectAnimator.ofFloat(mSliderIndicator, "translationX", 0).setDuration(350)
                            .start();
                    isNewestShown = true;
                    mComingBtn.setChecked(false);
                    mNewestBtn.setChecked(true);
                    mNewestListView.setVisibility(View.VISIBLE);
                    mComingListView.setVisibility(View.GONE);
                }
                break;
            case R.id.back_top_btn:
                mScrollView.scrollTo(0, 0);
                break;
        }
    }
}
