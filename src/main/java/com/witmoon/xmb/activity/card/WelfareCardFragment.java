package com.witmoon.xmb.activity.card;

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
 * Created by ming on 2017/3/20.
 */
public class WelfareCardFragment extends BaseFragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private View view;
    private String[] tabTitles = new String[]{"实体卡", "电子卡"};
    private EntityCardFragment mEntityCardFragment;
    private ElectronicFragment mElectronicFragment;
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEntityCardFragment = new EntityCardFragment();
        mElectronicFragment = new ElectronicFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_welfare_card, container, false);
            mTabLayout = (TabLayout) view.findViewById(R.id.tablayout);
            mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
            mFragments.add(mEntityCardFragment);
            mFragments.add(mElectronicFragment);
            mViewPager.setAdapter(new PageAdapter(getChildFragmentManager(),mFragments));
            mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles[0]));
            mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles[1]));
            mTabLayout.setupWithViewPager(mViewPager);
            mTabLayout.getTabAt(0).setText(tabTitles[0]);
            mTabLayout.getTabAt(1).setText(tabTitles[1]);
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
