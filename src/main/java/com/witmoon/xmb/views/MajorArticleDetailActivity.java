package com.witmoon.xmb.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.Article;
import com.witmoon.xmb.presenter.MajorArticleDetailPresenter;
import com.witmoon.xmb.services.ArticleService;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.XmbUtils;

import java.util.HashMap;

public class MajorArticleDetailActivity extends BaseActivity {

    EmptyLayout emptyLayout;
    WebView webView;
    String url;
    String article_id;
    ArticleService mService;
    MajorArticleDetailPresenter mPresenter;
    public int is_collect = 0;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_major_article_detail;
    }

    @Override
    protected int getActionBarTitleByResId() {
        return R.string.major_article_detail;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        initView();
        mService = new ArticleService();
        mPresenter = new MajorArticleDetailPresenter(this,mService);
        article_id = getIntent().getStringExtra("id");
        if (AppContext.instance().isLogin()){
            mPresenter.check_collect(ArticleService.gen_collect_params(article_id));
        }
    }

    public void collectArticle(){
        mPresenter.collect_handler(ArticleService.gen_collect_params(article_id));
    }

    public void setCollectStatus(int collect){
        is_collect = collect;
    }

    public void setOnError(){
        XmbUtils.showMessage(this,"网络异常,请重试~");
    }

    public void initView(){
        webView = (WebView) findViewById(R.id.webview);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        ImageView toolbar_right_img = (ImageView) findViewById(R.id.toolbar_right_img);
        ((TextView)findViewById(R.id.toolbar_title_text)).setText(getIntent().getStringExtra("title"));
        toolbar_right_img.setImageResource(R.mipmap.mbq_share);
        toolbar_right_img.setOnClickListener((View v) -> {
                HashMap<String,String> share_info = new HashMap();
                share_info.put("title", getIntent().getStringExtra("title"));
                share_info.put("desc", getIntent().getStringExtra("desc"));
                share_info.put("url", getIntent().getStringExtra("url"));
                XmbUtils.showMbqArticleShare(MajorArticleDetailActivity.this, findViewById(R.id.share_container),share_info,is_collect);
        });
        url = getIntent().getStringExtra("url");
        // 设置可以访问文件
        webView.getSettings().setAllowFileAccess(true);
        //如果访问的页面中有Javascript，则webview必须设置支持Javascript
        webView.getSettings().setJavaScriptEnabled(true);
        if (url.endsWith(".html")){
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }else{
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        }
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        XmbUtils.clearCookies(this);
        CookieManager.getInstance().setCookie("xiaomabao.com", "ECS_ID=" + ApiHelper.mSessionID + ";Domain=.xiaomabao.com");
        webView.addJavascriptInterface(new JavaScriptObject(MajorArticleDetailActivity.this), "xmbapp");
        webView.loadUrl(url);
    }

    public class JavaScriptObject {
        Context mContxt;

        public JavaScriptObject(Context mContxt) {
            this.mContxt = mContxt;
        }
        @JavascriptInterface
        public void showLogin(){
            if(!AppContext.instance().isLogin()){
                startActivity(new Intent(mContxt, LoginActivity.class));
            }else{
                webView.post(new Runnable() {
                    @Override
                    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
                    public void run() {
                        XmbUtils.clearCookies(mContxt);
                        CookieManager.getInstance().setCookie("xiaomabao.com", "ECS_ID=" + ApiHelper.mSessionID + ";Domain=.xiaomabao.com");
                        webView.addJavascriptInterface(new JavaScriptObject(MajorArticleDetailActivity.this), "xmbapp");
                        webView.loadUrl(url);
                    }
                });
            }
        }
        @JavascriptInterface
        public void showGood(String goods_id){
            CommodityDetailActivity.start(mContxt, goods_id);
        }
        @JavascriptInterface
        public void showTopic(String topic_id){
            MarketPlaceActivity.start(mContxt, topic_id);
        }
        @JavascriptInterface
        public void showGroup(String group_id){
            GroupBuyActivity.start(mContxt, group_id);
        }

        @JavascriptInterface
        public void finishView(){
            finish();
        }

        @JavascriptInterface
        public void refreshToolkit(){
            Intent intent = new Intent(Const.REFRESH_TOOLKIT);
            sendBroadcast(intent);
        }

        @JavascriptInterface
        public void showWebView(String url,String title){
            Intent intent = new Intent(MajorArticleDetailActivity.this, MajorArticleDetailActivity.class);
            intent.putExtra("url", url);
            intent.putExtra("title", title);
            startActivity(intent);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


}
