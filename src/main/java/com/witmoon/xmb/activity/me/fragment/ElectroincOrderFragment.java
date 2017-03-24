package com.witmoon.xmb.activity.me.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/3/21.
 * 电子卡订单
 */
public class ElectroincOrderFragment extends BaseFragment {
    private View view;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String[] tabTitles = new String[]{"全部", "待付款", "已完成"};
    private List<Fragment> mFragments = new ArrayList<>();


    private CardOrderFragment createFragment(String order_status) {
        Bundle bundle = new Bundle();
        bundle.putString("order_status", order_status);
        CardOrderFragment fragment = new CardOrderFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_my_card_order, container, false);
            mTabLayout = (TabLayout) view.findViewById(R.id.tablayout);
            mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
            mTabLayout = (TabLayout) view.findViewById(R.id.tablayout);
            mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
            mFragments.add(createFragment(""));
            mFragments.add(createFragment("waitpay"));
            mFragments.add(createFragment("waitcomplete"));
            mViewPager.setAdapter(new PageAdapter(getChildFragmentManager(), mFragments));
            mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles[0]));
            mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles[1]));
            mTabLayout.setupWithViewPager(mViewPager);
            mTabLayout.getTabAt(0).setText(tabTitles[0]);
            mTabLayout.getTabAt(1).setText(tabTitles[1]);
            mTabLayout.getTabAt(2).setText(tabTitles[2]);
        }
        return view;
    }


    static class PageAdapter extends FragmentPagerAdapter {
        private List<Fragment> fragments;

        public PageAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
