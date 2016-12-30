package com.witmoon.xmb.activity.goods.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.main.ShakeActivity;
import com.witmoon.xmb.activity.main.SignInActivity;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.Advertisement;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.ui.widget.PagerSlidingTabStrip;
import com.witmoon.xmb.util.TimeUtill;
import com.witmoon.xmb.util.TwoTuple;
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

/**
 * Created by de on 2016/1/12
 */
public class Group_Buying_Fragment extends BaseFragment {
    private View mRootView;
    private boolean isRun = true;
    private String[] titles = {"单品团", "品牌团"};
    private Fragment[] mFragments = new Fragment[2];

    // 广告自动播放ViewPager及PagerIndicator
    private AutoScrollViewPager mAutoScrollViewPager;
    private CirclePageIndicator mAutoScrollIndicator;
    private ViewPager viewPager;
    private PagerSlidingTabStrip indicator;
    private Thread thread, thread1;
    private List<Map<String, String>> brand_Group;
    private EmptyLayout emptyLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TimeUtill.mRemoveAll();
        TimeUtill.removeAll();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.fragment_group_buying, container, false);
            mAutoScrollViewPager = (AutoScrollViewPager) mRootView.findViewById(R.id
                    .auto_scroll_pager);
            emptyLayout = (EmptyLayout) mRootView.findViewById(R.id.error_layout);
            mAutoScrollIndicator = (CirclePageIndicator) mRootView.findViewById(R.id
                    .auto_scroll_indicator);

            viewPager = (ViewPager) mRootView.findViewById(R.id
                    .id_stickynavlayout_viewpager);
            indicator = (PagerSlidingTabStrip) mRootView.findViewById(R.id
                    .id_stickynavlayout_indicator);

            // 请求网络数据
            HomeApi.group_buy(advertisementCallback);
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
        public void onPreExecute() {
            super.onPreExecute();
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            Log.e("Response", response.toString());
//            TwoTuple<Boolean, String> tu = ApiHelper.parseResponseStatus(response);
//            if (!tu.first) {
//                AppContext.showToastShort(tu.second);
//                return;
//            }
            try {
                JSONObject dataJson = response.getJSONObject("data");
                List<Advertisement> ads = Advertisement.parse(dataJson.getJSONArray("top_ads"));
                initAutoScrollPager(ads);
                brand_Group = group_topics(dataJson.getJSONArray("group_topics"));
                mFragments[0] = Sku_GroupFragment.newInstance((ArrayList<Map<String, String>>) group_goods(dataJson.getJSONArray("group_goods")));
                mFragments[1] = Brand_Group_Fragment.newInstance((ArrayList) brand_Group);
                start();
                start1();
                //设置滑动变换图片
                //默认图片
                indicator.setIcoColorResource1(R.mipmap.sku_n);
                //选中图片
                indicator.setIndicatorIcoColorResource1(R.mipmap.sku_y);
                //-------------------------------------------------------------------  第一条目，第二条目
                //默认图片
                indicator.setIcoColorResource(R.mipmap.brand_n);
                //选中图片
                indicator.setIndicatorIcoColorResource(R.mipmap.brand_y);

                viewPager.setAdapter(new MainFragmentPagerAdapter(getChildFragmentManager()));
                indicator.setViewPager(viewPager);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }

        @Override
        public void onError(NetroidError error) {
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }
    };

    private List<Map<String, String>> group_goods(JSONArray array) throws JSONException {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            Map<String, String> map = new HashMap<>();
            Map<String, String> timeMap = new HashMap<>();
            map.put("promote_price", jsonObject.getString("promote_price"));
//            map.put("price_integer", jsonObject.getString("price_integer"));
//            map.put("price_decimal", jsonObject.getString("price_decimal"));
            map.put("id", jsonObject.getString("id"));
            map.put("name", jsonObject.getString("name"));
//            map.put("brief", jsonObject.getString("brief"));
            map.put("brand_name", jsonObject.getString("brand_name"));
            map.put("salesnum", jsonObject.getString("salesnum"));
            map.put("gmt_end_time", jsonObject.getString("gmt_end_time"));
            timeMap.put("time", Long.parseLong(jsonObject.getString("gmt_end_time")) - System.currentTimeMillis() / 1000 + "");
//            map.put("goods_style_name", jsonObject.getString("goods_style_name"));
            map.put("short_name", jsonObject.getString("short_name"));
//            map.put("short_style_name", jsonObject.getString("short_style_name"));
            map.put("market_price", jsonObject.getString("market_price"));
            map.put("shop_price", jsonObject.getString("shop_price"));
            map.put("goods_img", jsonObject.getString("goods_img"));
            map.put("thumb", jsonObject.getString("thumb"));
            mapList.add(map);
            TimeUtill.add(timeMap);
        }
        return mapList;
    }

    private List<Map<String, String>> group_topics(JSONArray array) throws JSONException {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            Map<String, String> map = new HashMap<>();
            Map<String, String> map1 = new HashMap<>();
            map.put("id", jsonObject.getString("id"));
            map.put("title", jsonObject.getString("title"));
            map1.put("time", Long.parseLong(jsonObject.getString("end_time")) / 1000 + "");
            map.put("ad_code", jsonObject.getString("ad_code"));
            mapList.add(map);
            TimeUtill.mAdd(map1);
        }
        return mapList;
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
                GroupBuyActivity.start(getActivity(), ad.getId(), "");
            }
        });
    }

    public class MainFragmentPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.TitleIconTabProvider {

        public MainFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getPageIconResId(int position) {
            return R.mipmap.blacklist;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }
    }

    int result = 0;

    public void start() {
        thread = new Thread() {
            public void run() {
                while (isRun) {
                    try {
                        if (null == TimeUtill.mlist || result == TimeUtill.mlist.size()) {
                            break;
                        }
                        sleep(1000);
                        if (null != TimeUtill.mlist)
                            for (Map<String, String> map : TimeUtill.mlist) {
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

    int result1 = 0;

    public void start1() {
        thread1 = new Thread() {
            public void run() {
                while (isRun) {
                    try {
                        if (null == TimeUtill.list || result1 == TimeUtill.list.size()) {
                            break;
                        }
                        sleep(1000);
                        if (null != TimeUtill.list)
                            for (Map<String, String> map : TimeUtill.list) {
                                if (!"售完".equals(map.get("time"))) {
                                    if ("1".equals(map.get("time"))) {
                                        map.put("time", "售完");
                                        result1++;
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
        thread1.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRun = false;
        thread = null;
        thread1 = null;
    }
}
