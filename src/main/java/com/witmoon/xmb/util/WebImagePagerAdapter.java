package com.witmoon.xmb.util;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.witmoon.xmb.api.Netroid;

/**
 * Created by zhyh on 2015/5/7.
 */
public class WebImagePagerAdapter extends PagerAdapter {

    private SparseArray<ImageView> mPageViews;

    private Context mContext;
    private String[] mDatas;

    public WebImagePagerAdapter(Context context, String[] urls) {
        this.mContext = context;
        mDatas = urls;
        mPageViews = new SparseArray<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = mPageViews.get(position);
        if (imageView == null) {
            imageView = new ImageView(this.mContext);
            mPageViews.put(position, imageView);
        }
        Netroid.displayImage(mDatas[position], imageView);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public int getCount() {
        return mDatas.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mPageViews.get(position));
    }
}
