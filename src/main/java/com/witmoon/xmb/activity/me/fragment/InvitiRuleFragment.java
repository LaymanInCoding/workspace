package com.witmoon.xmb.activity.me.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.widget.EmptyLayout;

/**
 * Created by ming on 2017/3/31.
 */
public class InvitiRuleFragment extends BaseFragment {

    private EmptyLayout mEmptyLayout;
    private WebView mWebView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_rule, container, false);
        mWebView = (WebView) view.findViewById(R.id.webview);
        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.empty_layout);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(ApiHelper.BASE_URL + "agreement/invite");
    }
}
