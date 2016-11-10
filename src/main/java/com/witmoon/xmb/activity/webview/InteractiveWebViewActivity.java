package com.witmoon.xmb.activity.webview;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class InteractiveWebViewActivity extends BaseActivity {

    private EmptyLayout error_layout;
    private WebView webView;
    private String url;
    private ImageView toolbar_right_image;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.interactive_webview;
    }

    @Override
    protected String getActionBarTitle() {
        return getIntent().getStringExtra("title") != null ? getIntent().getStringExtra("title") : "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(loginReceiver);
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        webView = (WebView) findViewById(R.id.webview);
        error_layout = (EmptyLayout) findViewById(R.id.error_layout);
        initWebView();
    }

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    private BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            XmbUtils.clearCookies(context);
            CookieManager.getInstance().setCookie("xiaomabao.com", "ECS_ID=" + ApiHelper.mSessionID + ";Domain=.xiaomabao.com");
            webView.addJavascriptInterface(new JavaScriptObject(InteractiveWebViewActivity.this), "xmbapp");
            webView.loadUrl(url);
        }
    };

    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
    public void initWebView() {
        IntentFilter login = new IntentFilter(Const.INTENT_WEB_REFRESH);
        registerReceiver(loginReceiver, login);

        url = getIntent().getStringExtra("url");
        // 设置可以访问文件
        webView.getSettings().setAllowFileAccess(true);
        //如果访问的页面中有Javascript，则webview必须设置支持Javascript
        webView.getSettings().setJavaScriptEnabled(true);
        if (url.endsWith(".html")) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                error_layout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                error_layout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                error_layout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        XmbUtils.clearCookies(this);
        CookieManager.getInstance().setCookie("xiaomabao.com", "ECS_ID=" + ApiHelper.mSessionID + ";Domain=.xiaomabao.com");
        webView.addJavascriptInterface(new JavaScriptObject(InteractiveWebViewActivity.this), "xmbapp");
        webView.loadUrl(url);
    }

    public class JavaScriptObject {
        Context mContxt;

        public JavaScriptObject(Context mContxt) {
            this.mContxt = mContxt;
        }

        @JavascriptInterface
        public void showLogin() {
            if (!AppContext.instance().isLogin()) {
                startActivity(new Intent(mContxt, LoginActivity.class));
            } else {
                webView.post(new Runnable() {
                    @Override
                    @SuppressLint({"JavascriptInterface", "SetJavaScriptEnabled"})
                    public void run() {
                        XmbUtils.clearCookies(mContxt);
                        CookieManager.getInstance().setCookie("xiaomabao.com", "ECS_ID=" + ApiHelper.mSessionID + ";Domain=.xiaomabao.com");
                        webView.addJavascriptInterface(new JavaScriptObject(InteractiveWebViewActivity.this), "xmbapp");
                        webView.loadUrl(url);
                    }
                });
            }
        }


        @JavascriptInterface
        public void showShare(String logo,String title, String desc,String url) {
            HashMap<String,String> share_info = new HashMap<>();
            share_info.put("title",title);
            share_info.put("desc",desc);
            share_info.put("url",url);
            XmbUtils.showMbqShare(InteractiveWebViewActivity.this,findViewById(R.id.mbq_story_container),share_info);
        }


        @JavascriptInterface
        public void showGood(String goods_id) {
            CommodityDetailActivity.start(mContxt, goods_id);
        }

        @JavascriptInterface
        public void showTopic(String topic_id) {
            MarketPlaceActivity.start(mContxt, topic_id);
        }

        @JavascriptInterface
        public void showGroup(String group_id) {
            GroupBuyActivity.start(mContxt, group_id);
        }

        @JavascriptInterface
        public void finishView() {
            finish();
        }

        @JavascriptInterface
        public void refreshToolkit() {
            Intent intent = new Intent(Const.REFRESH_TOOLKIT);
            sendBroadcast(intent);
        }

        @JavascriptInterface
        public void showWebView(String url, String title) {
            Intent intent = new Intent(InteractiveWebViewActivity.this, InteractiveWebViewActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            startActivity(intent);
        }
    }

}
