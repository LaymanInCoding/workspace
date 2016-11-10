package com.witmoon.xmb.activity.specialoffer.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.main.ShakeActivity;
import com.witmoon.xmb.activity.main.SignInActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.Advertisement;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.PagerSlidingTabStrip;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmblibrary.autoscrollviewpager.AutoScrollViewPager;
import com.xmb.viewpagerindicator.CirclePageIndicator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 上新(首页)Fragment
 * Created by zhyh on 2015/7/21.
 */
public class NewArrivalFragmentV2 extends BaseFragment {
    private View mRootView;

    private String[] titles = {"最新特卖", "即将推出"};
    private Fragment[] mFragments = new Fragment[2];

    // 广告自动播放ViewPager及PagerIndicator
    private AutoScrollViewPager mAutoScrollViewPager;
    private CirclePageIndicator mAutoScrollIndicator;
    private ViewPager viewPager;
    private PagerSlidingTabStrip indicator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_new_arrival_v2, container, false);
            mRootView.findViewById(R.id.shake).setOnClickListener(this);
            mRootView.findViewById(R.id.signin).setOnClickListener(this);
            mRootView.findViewById(R.id.favorite).setOnClickListener(this);
            mRootView.findViewById(R.id.award).setOnClickListener(this);
            mAutoScrollViewPager = (AutoScrollViewPager) mRootView.findViewById(R.id
                    .auto_scroll_pager);

            mAutoScrollIndicator = (CirclePageIndicator) mRootView.findViewById(R.id
                    .auto_scroll_indicator);

            viewPager = (ViewPager) mRootView.findViewById(R.id
                    .id_stickynavlayout_viewpager);
            indicator = (PagerSlidingTabStrip) mRootView.findViewById(R.id
                    .id_stickynavlayout_indicator);
            mFragments[0] = MarketListFragment.newInstance("1");
            mFragments[1] = MarketListFragment.newInstance("2");
            viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
                @Override
                public Fragment getItem(int position) {
                    return mFragments[position];
                }

                @Override
                public int getCount() {
                    return titles.length;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    return titles[position];
                }
            });
            indicator.setViewPager(viewPager);
            // 请求网络数据
            HomeApi.carouselAdvertisement(advertisementCallback);
            HomeApi.promotionAdvertisement(mPromotionCallback);
        }
        if (mRootView.getParent() != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

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
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                ShakeActivity.startActivity(getActivity());
                break;
            case R.id.signin:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                SignInActivity.startActivity(getActivity());
                break;
            case R.id.favorite:
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    return;
                }
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.FAVORITE);
                break;
            case R.id.award:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.GROUP_BUYING);
                break;
        }
    }
}
