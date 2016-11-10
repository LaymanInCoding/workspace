package com.witmoon.xmb.activity.me.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.witmoon.xmb.base.BaseFragment;

/**
 * Created by de on 2016/1/19.
 */
public class LogisticsFragment extends BaseFragment {
    private String  type;
    private String invoice;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        WebView webView = new WebView(getActivity());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl("http://m.kuaidi.com/all/"+type+"/"+invoice+".html");
        return webView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            type = arguments.getString("type");
            invoice = arguments.getString("invoice");
        }

    }
}
