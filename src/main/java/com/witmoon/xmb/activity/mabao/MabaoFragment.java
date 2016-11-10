package com.witmoon.xmb.activity.mabao;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.SearchActivity;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.widget.PagerSlidingTabStrip;
import com.witmoon.xmblibrary.linearlistview.util.DensityUtil;

/**
 * Created by de on 2016/3/16
 */
public class MabaoFragment extends BaseFragment {
    private View view;
    private ImageView search_imags;
    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPage;
    private String[] str_title = {"实惠星球","全球闪购"};
    private Fragment[] fragments = {new AaffordableFragment(),new Duty_freeFragment()};
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_mabao, container, false);
            FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.frameLayout);
            for(int i = 1; i <= 1 ; i++){
                FrameLayout.LayoutParams layoutParams =  new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getActivity(), 28));
                ImageView imageView = new ImageView(getActivity());
                imageView.setLayoutParams(layoutParams);
                layoutParams.setMargins(MainActivity.screen_width / 2 * i, 0, 0, 0);
                layoutParams.width = DensityUtil.dip2px(getActivity(),1);
                layoutParams.height = DensityUtil.dip2px(getActivity(),28);
                imageView.setBackgroundColor(getResources().getColor(R.color.white));
                frameLayout.addView(imageView);
            }
            mPagerSlidingTabStrip = (PagerSlidingTabStrip) view.findViewById(R.id.id_stickynavlayout_indicator);
            mViewPage = (ViewPager) view.findViewById(R.id.id_stickynavlayout_viewpager);
            search_imags = (ImageView) view.findViewById(R.id.search_imags);
            init();
        }

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }

        Toolbar mToolbar = ((BaseActivity) getActivity()).getToolBar();
        AQuery aQuery = new AQuery(getActivity(), mToolbar);
        aQuery.id(R.id.top_toolbar).gone();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void init() {
        mViewPage.setOnPageChangeListener(new mOnPageChangeListener());
        mViewPage.setAdapter(new CanulacirclePagerAdapter(getChildFragmentManager()));
        mViewPage.setCurrentItem(0);
        mPagerSlidingTabStrip.setViewPager(mViewPage);

        search_imags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
    }

    public class mOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.current_tab_index = 1;
    }

    public class CanulacirclePagerAdapter extends FragmentPagerAdapter {

        public CanulacirclePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return str_title.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return str_title[position];
        }
    }
}
