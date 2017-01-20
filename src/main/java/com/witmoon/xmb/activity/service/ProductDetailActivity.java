package com.witmoon.xmb.activity.service;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.ui.widget.EmptyLayout;

public class ProductDetailActivity extends BaseActivity {
    private EmptyLayout emptyLayout;
    private WebView webview;
    private int product_id;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_product_detail;
    }

    @Override
    protected String getActionBarTitle() {
        return "产品详情";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        product_id = getIntent().getIntExtra("product_id", 0);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);

        findViewById(R.id.add_to_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(ProductDetailActivity.this, LoginActivity.class));
                    return;
                }
                Intent intent = new Intent(ProductDetailActivity.this, ServiceCart.class);
                intent.putExtra("product_id", product_id);
                startActivity(intent);
            }
        });
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        webview = (WebView) findViewById(R.id.webview);
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                webview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
        });
        webview.loadUrl(ApiHelper.BASE_URL + "service/product_preview/" + product_id + "/" + System.currentTimeMillis());

    }

}


