package com.witmoon.xmb.activity.card;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.util.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/3/23.
 */
public class ElecCardHelpActivity extends AppCompatActivity {
    private TextView title;
    private ImageView back;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private String[] tabTitles = new String[]{"购买流程", "使用流程", "企业专属特权", "购卡章程"};
    private List<Fragment> mFragments = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_help);
        title = (TextView) findViewById(R.id.toolbar_title_text);
        title.setText("帮助中心");
        back = (ImageView) findViewById(R.id.toolbar_left_img);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mFragments.add(PagerFragment.createInstance(R.mipmap.card_help_1));
        mFragments.add(PagerFragment.createInstance(R.mipmap.card_help_2));
        mFragments.add(PagerFragment.createInstance(R.mipmap.card_help_3_new));
        mFragments.add(PagerFragment.createInstance(R.mipmap.card_help_4));
        mViewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), mFragments));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles[0]));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles[1]));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles[2]));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles[3]));
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText(tabTitles[0]);
        mTabLayout.getTabAt(1).setText(tabTitles[1]);
        mTabLayout.getTabAt(2).setText(tabTitles[2]);
        mTabLayout.getTabAt(3).setText(tabTitles[3]);
        setFont();
        setTitleColor_(R.color.main_kin);
    }

    private void setFont() {
        AssetManager mgr = getAssets();//得到AssetManager
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
        title.setTypeface(tf);
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
