package com.witmoon.xmb.activity.webview;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;
import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.XmbUtils;

import java.util.HashMap;

public class InteractiveWebViewActivity extends BaseActivity {

    private EmptyLayout error_layout;
    private WebView webView;
    private String url;
    private ImageView toolbar_right_image;
    private HashMap<String, String> share_info = new HashMap<>();

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
        toolbar_right_image = (ImageView) findViewById(R.id.toolbar_right_img);
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
        //        url = "https://chongzhike.com/XMBGQYr";
        url = getIntent().getStringExtra("url");
        if (url.equals("http://www.xiaomabao.com/daily/prize") ||
                url.equals(ApiHelper.BASE_URL + "discovery/knowledge_index") ||
                url.equals(ApiHelper.BASE_URL + "discovery/story") ||
                url.startsWith(ApiHelper.HOME_URL + "activity") ||
                url.startsWith(ApiHelper.HOME_URL + "blood")) {
            toolbar_right_image.setVisibility(View.VISIBLE);
            share_info.put("title", getIntent().getStringExtra("title"));
            share_info.put("desc", getIntent().getStringExtra("title"));
            share_info.put("url", url);
        }
        toolbar_right_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmbUtils.showMbqShare(InteractiveWebViewActivity.this, findViewById(R.id.mbq_story_container), share_info);
            }
        });
//        Logger.e(url);
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
                Logger.e(url);
                // 如下方案可在非微信内部WebView的H5页面中调出微信支付
                if (url.startsWith("weixin://wap/pay?")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                    return true;
                } else if (parseScheme(url)) {
                    try {
                        Intent intent;
                        intent = Intent.parseUri(url,
                                Intent.URI_INTENT_SCHEME);
                        intent.addCategory("android.intent.category.BROWSABLE");
                        intent.setComponent(null);
                        // intent.setSelector(null);
                        startActivity(intent);
//
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            }
        });
        XmbUtils.clearCookies(this);
        CookieManager.getInstance().setCookie("xiaomabao.com", "ECS_ID=" + ApiHelper.mSessionID + ";Domain=.xiaomabao.com");
        webView.addJavascriptInterface(new JavaScriptObject(InteractiveWebViewActivity.this), "xmbapp");
        webView.loadUrl(url);
    }

    public boolean parseScheme(String url) {

        if (url.contains("platformapi/startapp")) {
            return true;
        } else if ((Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
                && (url.contains("platformapi") && url.contains("startapp"))) {
            return true;
        } else {
            return false;
        }
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
        public void jumpToCart() {
            UIHelper.showSimpleBack(InteractiveWebViewActivity.this, SimpleBackPage.SHOPPING_CART);
        }

        @JavascriptInterface
        public void showShare(String logo, String title, String desc, String url) {
            HashMap<String, String> share_info = new HashMap<>();
            share_info.put("title", title);
            share_info.put("desc", desc);
            share_info.put("url", url);
            XmbUtils.showMbqShare(InteractiveWebViewActivity.this, findViewById(R.id.mbq_story_container), share_info);
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
