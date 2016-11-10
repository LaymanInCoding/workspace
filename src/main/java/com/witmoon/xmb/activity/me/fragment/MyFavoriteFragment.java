package com.witmoon.xmb.activity.me.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.widget.PagerSlidingTabStrip;

/**
 * 我的收藏界面
 * Created by zhyh on 2015/6/23.
 */
public class MyFavoriteFragment extends BaseFragment {
    private static final String[] TITLES = {"商品", "品牌"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_indicator_viewpager, container, false);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        viewPager.setAdapter(new MyFavoriteViewPagerAdapter(getFragmentManager()));
        PagerSlidingTabStrip indicator = (PagerSlidingTabStrip) view.findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);
        indicator.setIndicatorColorResource(R.color.master_me);

        return view;
    }


    public class MyFavoriteViewPagerAdapter extends FragmentPagerAdapter {

        public MyFavoriteViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new MyFavoriteGoodsFragment();
            }
            return new MyFavoriteBrandFragment();
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }
}
