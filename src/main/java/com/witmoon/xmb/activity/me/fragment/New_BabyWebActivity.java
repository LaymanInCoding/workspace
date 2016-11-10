package com.witmoon.xmb.activity.me.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.main.fragment.WebVaccineFragment;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.ui.widget.EmptyLayout;

/**
 * Created by de on 2016/1/27
 */
public class New_BabyWebActivity extends BaseActivity {
    private WebView mWebView;
    private boolean is_show;
    private EmptyLayout error_layout;
    private Handler mHandler = new Handler();
    private String url = ApiHelper.BASE_URL + "discovery/knowledge_index/android";

    @Override
    protected int getActionBarTitleByResId() {
        return R.string.text_new_baby;
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        setTitleColor_(R.color.master_me);
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_me));
        ImageView leftBtn = (ImageView) toolbar.findViewById(R.id.toolbar_left_img);
        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else
                    onBackPressed();
            }
        });
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.web_util_activity;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        super.initialize(savedInstanceState);
        mWebView = (WebView) findViewById(R.id.web_view);
        WebSettings setting = mWebView.getSettings();
        setting.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "vaccine");
        error_layout = (EmptyLayout) findViewById(R.id.error_layout);
        init();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                finish();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    private void init() {
        setmDeleteText("搜索").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(New_BabyWebActivity.this, SearchWebActivity.class));
            }
        });
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
        mWebView.loadUrl(url);
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
            Log.e("string", string);
            mHandler.post(new Runnable() {
                public void run() {
                    // 此处调用 HTML 中的javaScript 函数
                    Log.e("string+\"/android\"", string + "/android");
                    Intent intent = new Intent(New_BabyWebActivity.this, WebVaccineFragment.class);
                    intent.putExtra("url", string + "/android");
                    startActivity(intent);
                }
            });
        }
    }
}
