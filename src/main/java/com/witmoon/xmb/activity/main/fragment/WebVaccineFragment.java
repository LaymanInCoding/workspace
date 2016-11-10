package com.witmoon.xmb.activity.main.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.ui.widget.EmptyLayout;

/**
 * Created by de on 2016/1/22
 */
public class WebVaccineFragment extends BaseActivity{
    private WebView mWebView;
    private String url;
    private boolean is_show;
    private Handler mHandler = new Handler();
    private EmptyLayout error_layout;
    private int actionBarTitle = R.string.text_vaccine_;
    @Override
    protected int getActionBarTitleByResId() {
        return actionBarTitle;
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        setTitleColor_(R.color.master_me);
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_me));
    }
    @Override
    protected int getLayoutResourceId() {
        return R.layout.web_util_activity;
    }
    @Override
    protected void initialize(Bundle savedInstanceState) {
        url = getIntent().getStringExtra("url");
        actionBarTitle = getIntent().getIntExtra("action_bar_title", R.string.text_vaccine_);
        super.initialize(savedInstanceState);
        mWebView = (WebView) findViewById(R.id.web_view);
        WebSettings setting = mWebView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "vaccine");
        error_layout = (EmptyLayout) findViewById(R.id.error_layout);
        error_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!is_show) {
                    error_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                is_show = true;
                error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
        });
        String postData = "session[uid]="+ ApiHelper.mSessionID + "&session[sid]=" + AppContext.getLoginUid();
        mWebView.postUrl(url, postData.getBytes());
    }

    final class DemoJavaScriptInterface {
        DemoJavaScriptInterface() {
        }

        /**
         * This is not called on the UI thread. Post a runnable to invoke
         * 这不是呼吁界面线程。发表一个运行调用
         * loadUrl on the UI thread.
         * loadUrl在UI线程。
         */
        @JavascriptInterface
        public void clickOnAndroid(final String string, final String title) {
            mHandler.post(new Runnable() {
                public void run() {
                    // 此处调用 HTML 中的javaScript 函数
                    Intent intent = new Intent(WebVaccineFragment.this, WebVaccineFragment.class);
                    intent.putExtra("url",string+"/android");
                    startActivity(intent);
                }
            });
        }
    }
}
