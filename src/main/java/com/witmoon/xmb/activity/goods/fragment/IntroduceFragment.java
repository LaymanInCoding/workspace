package com.witmoon.xmb.activity.goods.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.util.ImageLoaders;

/**
 * 商品介绍Fragment
 * Created by zhyh on 2015-07-14.
 */
public class IntroduceFragment extends BaseFragment {

    private LinearLayout mContainer;

    public static IntroduceFragment newInstance(String[] gallery) {
        IntroduceFragment fragment = new IntroduceFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArray("key", gallery);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScrollView scrollView = new ScrollView(container.getContext());
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        scrollView.setId(R.id.id_stickynavlayout_innerscrollview);
        mContainer = new LinearLayout(container.getContext());
        mContainer.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mContainer.setOrientation(LinearLayout.VERTICAL);
        mContainer.setPadding(5, 5, 5, 5);
        scrollView.addView(mContainer);
        return scrollView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String[] gallery = bundle.getStringArray("key");
        if (gallery == null || gallery.length < 1) return;
        for (String aGallery : gallery) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                    .MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setAdjustViewBounds(true);
            Netroid.displayImage(aGallery, imageView);
            mContainer.addView(imageView);
        }
    }
}
