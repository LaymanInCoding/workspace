package com.witmoon.xmb.activity.me.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.me.OrderTab;
import com.witmoon.xmb.activity.me.OrderType;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.widget.PagerSlidingTabStrip;

/**
 * 我的订单
 * Created by zhyh on 2015/6/22.
 */
public class MyOrderFragment extends BaseFragment {
    private ViewPager mViewPager;
    private OrderType mInitType;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mInitType = (OrderType) bundle.getSerializable("initType");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_indicator_viewpager, container, false);

        mViewPager = (ViewPager) view.findViewById(R.id.pager);
        PagerSlidingTabStrip indicator = (PagerSlidingTabStrip) view.findViewById(R.id.indicator);

        mViewPager.setAdapter(new OrderFragmentPagerAdapter(getFragmentManager()));
        indicator.setViewPager(mViewPager);
        indicator.setTabPaddingLeftRight(16);
        indicator.setDividerPadding(8);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        handleInitPage();
    }

    private void handleInitPage() {
        if (mInitType == null) return;
        switch (mInitType) {
            case TYPE_WAITING_FOR_PAYMENT:
                mViewPager.setCurrentItem(1);
                break;
            case TYPE_WAITING_FOR_SENDING:
                mViewPager.setCurrentItem(2);
                break;
            case TYPE_WAITING_FOR_RECEIVING:
                mViewPager.setCurrentItem(3);
                break;
            case TYPE_FINISHED:
                mViewPager.setCurrentItem(4);
                break;
        }
    }


    // 适配器
    class OrderFragmentPagerAdapter extends FragmentPagerAdapter {

        public OrderFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getResources().getString(OrderTab.values()[position].getResName());
        }

        @Override
        public Fragment getItem(int position) {
            return OrderFragment.newInstance(OrderTab.values()[position].getType());
        }

        @Override
        public int getCount() {
            return OrderTab.values().length;
        }
    }
}
