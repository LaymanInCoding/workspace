package com.witmoon.xmb.activity.shopping;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.androidquery.AQuery;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.SearchActivity;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by de on 2016/3/16
 */
public class MabaoFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener,View.OnClickListener{
    private View view;
    private RadioButton aff_btn,cro_btn,fea_btn;
    private ViewPager mViewPage;
    private Fragment[] fragments = {new AaffordableFragment(),new Duty_freeFragment(),new MabaoFeatureFragment()};
    private SegmentedGroup segmented;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_shopping, container, false);
            segmented = (SegmentedGroup) view.findViewById(R.id.segmented1);
            aff_btn = (RadioButton) view.findViewById(R.id.aff_btn);
            cro_btn = (RadioButton) view.findViewById(R.id.cro_btn);
            fea_btn = (RadioButton) view.findViewById(R.id.fea_btn);
            mViewPage = (ViewPager) view.findViewById(R.id.viewpager);
            aff_btn.performClick();

            AssetManager mgr = getActivity().getAssets();//得到AssetManager
            Typeface tf=Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
            aff_btn.setTypeface(tf);
            cro_btn.setTypeface(tf);
            fea_btn.setTypeface(tf);
        }

        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
        init();
        Toolbar mToolbar = ((BaseActivity) getActivity()).getToolBar();
        AQuery aQuery = new AQuery(getActivity(), mToolbar);
        aQuery.id(R.id.top_toolbar).gone();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.current_tab_index = 2;
    }

    public void init(){
        mViewPage.setOnPageChangeListener(new mOnPageChangeListener());
        segmented.setOnCheckedChangeListener(this);
        view.findViewById(R.id.search_imags).setOnClickListener(this);
        mViewPage.setAdapter(new CanulacirclePagerAdapter(getChildFragmentManager()));
        mViewPage.setCurrentItem(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_imags:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                break;
            default:
                // Nothing to do
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.aff_btn:
                mViewPage.setCurrentItem(0);
                break;
            case R.id.cro_btn:
                mViewPage.setCurrentItem(1);
                break;
            case R.id.fea_btn:
                mViewPage.setCurrentItem(2);
            default:
                // Nothing to do
        }
    }

    public class CanulacirclePagerAdapter extends FragmentPagerAdapter {

        public CanulacirclePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments[position];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

    public class mOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if(position == 0){
                aff_btn.performClick();
            }else if(position == 1){
                cro_btn.performClick();
            }else if(position ==2){
                fea_btn.performClick();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
