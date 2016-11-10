package com.witmoon.xmb.activity.goods;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.ImageBDInfo;
import com.witmoon.xmb.model.ImageInfo;
import com.witmoon.xmb.ui.HackyViewPager;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class PreviewImage extends BaseActivity implements OnPageChangeListener {

    private int index = 0;
    private ViewPager viewpager;
    private ArrayList<ImageInfo> ImgList;
    private SamplePagerAdapter pagerAdapter;
    // 屏幕宽度
    public float Width;
    protected ImageBDInfo bdInfo;
    protected ImageInfo imageInfo;
    // 屏幕高度
    public float Height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browseimage);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        Width = dm.widthPixels;
        Height = dm.heightPixels;

        findID();
        Listener();
        InData();
        setTitleColor_(R.color.black);
    }

    public void findID() {
        viewpager = (HackyViewPager) findViewById(R.id.bi_viewpager);
    }

    public void Listener() {
        viewpager.setOnPageChangeListener(this);
    }

    public void InData() {
        index = getIntent().getIntExtra("index", 0);
        ImgList = (ArrayList<ImageInfo>) getIntent().getSerializableExtra("data");
        imageInfo = ImgList.get(index);
        bdInfo = (ImageBDInfo) getIntent().getSerializableExtra("bdinfo");
        pagerAdapter = new SamplePagerAdapter();
        viewpager.setOffscreenPageLimit(ImgList.size());// ViewPager缓存
        viewpager.setAdapter(pagerAdapter);
        viewpager.setCurrentItem(index);
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return ImgList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            RelativeLayout mRelativeLayout = new RelativeLayout(getBaseContext());
            PhotoView photoView = new PhotoView(container.getContext());
            RelativeLayout.LayoutParams lrp = new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
            lrp.addRule(RelativeLayout.CENTER_IN_PARENT);
            final ProgressBar mProgressBar = new ProgressBar(getBaseContext());
            RelativeLayout.LayoutParams lrp1 = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            lrp1.addRule(RelativeLayout.CENTER_IN_PARENT);
            String path = ImgList.get(position).url;
            ImageLoader.getInstance().displayImage(path, photoView, AppContext.options_disk, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    mProgressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    //AppContext.showToast("图片加载失败");
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    mProgressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });
            mRelativeLayout.addView(photoView, lrp);
            mRelativeLayout.addView(mProgressBar, lrp1);
            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View arg0, float arg1, float arg2) {
                    finish();
                }
            });
            container.addView(mRelativeLayout, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            return mRelativeLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }
}