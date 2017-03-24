package com.witmoon.xmb.activity.card;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.util.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import info.hoang8f.android.segmented.SegmentedGroup;

/**
 * Created by ming on 2017/3/20.
 */
public class CardActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.mabao_gongxiang_btn)
    RadioButton mMabaoGongxiangBtn;
    @BindView(R.id.welfare_btn)
    RadioButton mWelfareBtn;
    @BindView(R.id.card_use_help)
    TextView mCardUseHelp;
    @BindView(R.id.segmented1)
    SegmentedGroup mSegmentedGroup;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    private WelfareCardFragment mWelfareCardFragment;
    private GiftCardFragment mGiftCardFragment;
    private Unbinder mButterKnife;
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mabao_card);
        mButterKnife = ButterKnife.bind(this);
        setFont();
        setTitleColor_(R.color.main_kin);
        mMabaoGongxiangBtn.performClick();
        mSegmentedGroup.setOnCheckedChangeListener(this);
        mWelfareCardFragment = new WelfareCardFragment();
        mGiftCardFragment = new GiftCardFragment();
        mFragments.add(mWelfareCardFragment);
        mFragments.add(mGiftCardFragment);
        mViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager(), mFragments));
        mViewPager.addOnPageChangeListener(new mOnPageChangeListener());
        mViewPager.setCurrentItem(0);
    }

    private void setFont() {
        AssetManager mgr = getAssets();//得到AssetManager
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
        mMabaoGongxiangBtn.setTypeface(tf);
        mWelfareBtn.setTypeface(tf);
        mCardUseHelp.setTypeface(tf);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mButterKnife.unbind();
    }

    @OnClick({R.id.card_use_help, R.id.toolbar_left_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_use_help:
                startActivity(new Intent(this, ElecCardHelpActivity.class));
                break;
            case R.id.toolbar_left_img:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.mabao_gongxiang_btn:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.welfare_btn:
                mViewPager.setCurrentItem(1);
                break;
            default:
        }
    }

    class PagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mfragments;

        public PagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.mfragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mfragments.get(position);
        }

        @Override
        public int getCount() {
            return mfragments.size();
        }
    }

    class mOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                mMabaoGongxiangBtn.performClick();
            } else if (position == 1) {
                mWelfareBtn.performClick();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    public void setTitleColor_(int mColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            if (mColor == R.color.black) {
                tintManager.setStatusBarTintColor(getResources().getColor(R.color.black));
            } else {
                tintManager.setStatusBarTintDrawable(getResources().getDrawable(R.drawable.bg_status));
            }
        }
    }

    //获取高度
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
