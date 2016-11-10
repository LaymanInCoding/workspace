package com.witmoon.xmb.activity.babycenter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.witmoon.xmb.base.BaseFragment;

public class CalOverdueFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        WebView webView = new WebView(container.getContext());
        webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        webView.loadUrl("file:///android_asset/duedate.html");

        return webView;
    }
}
