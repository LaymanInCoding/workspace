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
import android.widget.EditText;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.main.fragment.WebVaccineFragment;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.ui.widget.EmptyLayout;

/**
 * Created by de on 2016/1/27
 */
public class SearchWebActivity extends BaseActivity {

    private WebView mWebView;
    private boolean is_show;
    private EmptyLayout error_layout;
    private String url = ApiHelper.BASE_URL+"discovery/knowledge_search/android/";
    private EditText search_text;
    private Handler mHandler = new Handler();

    @Override
    protected int getActionBarTitleByResId() {
        return R.string.text_new_baby;
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
        super.initialize(savedInstanceState);
        mWebView = (WebView) findViewById(R.id.web_view);
        WebSettings setting = mWebView.getSettings();
        setting.setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "vaccine");
        error_layout = (EmptyLayout) findViewById(R.id.error_layout);
        search_text = (EditText) mToolBar.findViewById(R.id.edit_text);
        init();
    }

    private void init() {
        search_text.setVisibility(View.VISIBLE);
        setmDeleteText("搜索").setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (search_text.getText().toString().length()>0)
                {
                    Log.e("search url:",url+search_text.getText().toString());
                    mWebView.loadUrl(url+search_text.getText().toString());
                }else{
                    AppContext.showToast("搜索内容不能为空。");
                }
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
                    search_text.setText("");
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
                    Log.e("string+\"/android\"",string+"/android");
                    Intent intent = new Intent(SearchWebActivity.this, WebVaccineFragment.class);
                    intent.putExtra("url",string+"/android");
                    startActivity(intent);
                }
            });
        }
    }
}