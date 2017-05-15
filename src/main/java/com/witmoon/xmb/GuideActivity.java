package com.witmoon.xmb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xmb.viewpagerindicator.LinePageIndicator;

/**
 * App引导界面Activity
 * Created by zhyh on 2015/8/25.
 */
public class GuideActivity extends AppCompatActivity{

    private int[] mPagers = new int[]{R.mipmap.page_guide_01, R.mipmap.page_guide_02_new, R.mipmap
            .page_guide_03, R.mipmap.page_guide_04};
    private View[] mGuideImageViews = new View[mPagers.length];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        final LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout
                .LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        for (int i = 0; i < mPagers.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(mParams);
            imageView.setImageResource(mPagers[i]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            mGuideImageViews[i] = imageView;
        }

        final ImageView nextStepBtn = (ImageView) findViewById(R.id.submit_button);
        final ViewPager guidePager = (ViewPager) findViewById(R.id.view_pager);
        guidePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == mPagers.length - 1) {
                    nextStepBtn.setVisibility(View.VISIBLE);
                } else {
                    nextStepBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        nextStepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(GuideActivity.this, MainActivity.class);
                    GuideActivity.this.startActivity(intent);
                    GuideActivity.this.finish();
                    AppContext.setProperty(WelcomeActivity.KEY_FLAG_FIRST_LAUNCH, "false");
            }
        });
        guidePager.setAdapter(new GuideAdapter());
        LinePageIndicator indicator = (LinePageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(guidePager);
    }

    class GuideAdapter extends PagerAdapter {

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
