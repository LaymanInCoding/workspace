package com.witmoon.xmb.activity.babycenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;


/**
 * Created by de on 2016/11/30.
 */
public class BabyGuideActivity extends Activity {
    public static final String KEY_FLAG_FIRST_LAUNCH = "app.baby.first.launch";
    private int[] mPagers = new int[]{R.mipmap.baby_guide_new1, R.mipmap.baby_guide_new2, R.mipmap
            .baby_guide_new3, R.mipmap.baby_guide_new4};
    private View[] mGuideImageViews = new View[mPagers.length];
    private ViewPager guidePager;
    private final LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout
            .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_baby_guide);
        for (int i = 0; i < mPagers.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(mParams);
            imageView.setImageResource(mPagers[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mGuideImageViews[i] = imageView;
        }
        guidePager = (ViewPager) findViewById(R.id.guide_pager);
        guidePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == mPagers.length - 1) {
                    mGuideImageViews[position].setOnClickListener(v -> {
                        AppContext.setProperty(BabyGuideActivity.KEY_FLAG_FIRST_LAUNCH, "false");
                        finish();
                    });
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        guidePager.setAdapter(new BabyGuideAdapter());
        setOnClick();
    }

    private void setOnClick() {
        mGuideImageViews[0].setOnClickListener(v -> guidePager.setCurrentItem(1, true));
        mGuideImageViews[1].setOnClickListener(v -> guidePager.setCurrentItem(2, true));
        mGuideImageViews[2].setOnClickListener(v -> guidePager.setCurrentItem(3, true));
    }

    class BabyGuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mPagers.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mGuideImageViews[position]);
            return mGuideImageViews[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mGuideImageViews[position]);
        }
    }

}
