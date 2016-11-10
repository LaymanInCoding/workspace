package com.witmoon.xmb.activity.me.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.main.fragment.WebVaccineFragment;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.ui.widget.EmptyLayout;


/**
 * Created by de on 2016/1/22
 */
public class WebUtilFragment extends BaseFragment {
    private WebView mWebView;
    private Handler mHandler = new Handler();
    private String url;
    private boolean is_show;
    private EmptyLayout error_layout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle mBundle = getArguments();
        url = mBundle.getString("URL");
    }

    @Override
    public boolean onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web_util_fragment, container, false);
        mWebView = (WebView) view.findViewById(R.id.web_view);
        error_layout = (EmptyLayout) view.findViewById(R.id.error_layout);
        error_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //设置传入接口，js方法名字
        mWebView.addJavascriptInterface(new DemoJavaScriptInterface(), "vaccine");
        final String postData = "?session[sid]=" + ApiHelper.mSessionID + "&session[uid]=" + AppContext.getLoginUid()+"&"+System.currentTimeMillis();
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
//        mWebView.postUrl(url, postData.getBytes());
        mWebView.loadUrl(url+postData);
        return view;
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
                    Log.e("string", string);
                    Intent intent = new Intent(getActivity(), WebVaccineFragment.class);
                    intent.putExtra("url", string);
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * 调试你的javascript。
     */
    final class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.d("WebView", message);
            result.confirm();
            return true;
        }
    }
}
