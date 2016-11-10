package com.witmoon.xmb.activity.me.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.witmoon.xmb.base.BaseFragment;

/**
 * 关于麻包界面
 * Created by zhyh on 2015/6/21.
 */
public class AboutFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        LinearLayout root = new LinearLayout(getActivity());
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup
//                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        root.setLayoutParams(layoutParams);
//        root.setOrientation(LinearLayout.VERTICAL);
//        root.setBackgroundColor(getResources().getColor(R.color.white));
//
//        TextView labelView = new TextView(getActivity());
//        LinearLayout.LayoutParams labelLayoutParams = new LinearLayout.LayoutParams(ViewGroup
//                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        labelLayoutParams.leftMargin = 24;
//        labelLayoutParams.topMargin = 100;
//        labelLayoutParams.rightMargin = 24;
//        labelView.setLayoutParams(labelLayoutParams);
//        labelView.setText("麻包介绍");
//        labelView.setTextColor(getResources().getColor(R.color.black));
//        labelView.setTextSize(18);
//        root.addView(labelView);
//
//        TextView aboutView = new TextView(getActivity());
//        LinearLayout.LayoutParams aboutLayoutParams = new LinearLayout.LayoutParams(ViewGroup
//                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        aboutLayoutParams.leftMargin = 24;
//        aboutLayoutParams.topMargin = 30;
//        aboutLayoutParams.rightMargin = 24;
//        aboutView.setLayoutParams(aboutLayoutParams);
//        aboutView.setText("小麻包平台理念");
//        root.addView(aboutView);
//
//        TextView aboutTextView = new TextView(getActivity());
//        LinearLayout.LayoutParams aboutTextLayoutParams = new LinearLayout.LayoutParams(ViewGroup
//                .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        aboutTextLayoutParams.leftMargin = 24;
//        aboutTextLayoutParams.topMargin = 20;
//        aboutTextLayoutParams.rightMargin = 24;
//        aboutTextView.setLayoutParams(aboutTextLayoutParams);
//        aboutTextView.setText("以“进口母婴品牌”为定位;\n以“一站式购物体验”为基础;");
//        root.addView(aboutTextView);
//        return root;

        WebView webView = new WebView(container.getContext());
        webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        webView.loadUrl("file:///android_asset/about.html");

        return webView;
    }
}
