package com.witmoon.xmb.activity.mbq.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PostDetailActivity extends BaseActivity {

    private ImageView toolbar_right_img;
    private int post_id;
    private LinearLayout headerView;
    private EmptyLayout emptyLayout;
    private TextView toolbar_title_text;
    private Boolean is_choose_show = false;
    private Drawable drawable1, drawable2;
    private View choose_area, post_pic_layout, post_up_layout, post_all_layout;
    private ImageView post_pic, post_up, post_all;
    private View post_collect, post_comment;
    private int current_choose = 1;
    private int type = 1;
    private int is_collect = 0;
    private ImageView collect_img;
    private HashMap<String, String> share_info = new HashMap<>();
    private WebView webView;
    private String isImage = "1";
    private String poster = "1";
    private String desc = "";
    private String title = "";
    private int page = 1;
    private int user_id = 0;
    private int comment_id = 0;
    private String url;

//    @Override
//    public void setRecRequest(int page0) {
//        CircleApi.circle_detail(post_id, page, type, user_id, comment_id, new Listener<JSONObject>() {
//            @Override
//            public void onError(NetroidError error) {
//                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
//            }
//
//            @Override
//            public void onSuccess(JSONObject response) {
//                Logger.json(response.toString());
//                if (response.has("post_detail")) {
////                    try {
//////                        getShareInfo(response.getJSONObject("post_detail"));
////                    } catch (JSONException e) {
////                        e.printStackTrace();
////                    }
//                }
//                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
//            }
//        });
//    }

    private void setShare_info(){
        share_info.put("title", title);
        share_info.put("desc", desc);
        share_info.put("url", ApiHelper.BASE_URL + "circle/mpost/" + post_id + "/1/1");
    }

    private BroadcastReceiver refreshCurrentActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            isImage = "1";
            url = ApiHelper.BASE_URL + "circle/mpost/" + post_id + "/" + isImage + "/" + poster;
            webView.loadUrl(url);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(refreshCurrentActivity);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected String getActionBarTitle() {
        return "全部";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        initView();
        IntentFilter refreshActivity = new IntentFilter(Const.INTENT_ACTION_REFRESH_POST);
        registerReceiver(refreshCurrentActivity, refreshActivity);
    }

    private void initView() {
        post_id = getIntent().getIntExtra("post_id", 0);
        desc = getIntent().getStringExtra("post_content");
        title = getIntent().getStringExtra("post_title");
        Logger.d(post_id);
        Logger.d(desc);
        Logger.d(title);
        setShare_info();
        url = ApiHelper.BASE_URL + "circle/mpost/" + post_id + "/" + isImage + "/" + poster;
        headerView = (LinearLayout) getLayoutInflater().inflate(R.layout.header_mbq_post, null, false);
        toolbar_right_img = (ImageView) findViewById(R.id.toolbar_right_img);
        toolbar_right_img.setImageResource(R.mipmap.mbq_share);
        toolbar_title_text = (TextView) findViewById(R.id.toolbar_title_text);
        drawable1 = getResources().getDrawable(R.drawable.arrow_up);
        drawable1.setBounds(0, 0, drawable1.getMinimumWidth(), drawable1.getMinimumHeight());//必须设置图片大小，否则不显示
        drawable2 = getResources().getDrawable(R.drawable.arrow_down);
        drawable2.setBounds(0, 0, drawable2.getMinimumWidth(), drawable2.getMinimumHeight());//必须设置图片大小，否则不显示
        toolbar_title_text.setCompoundDrawables(null, null, drawable2, null);
        toolbar_title_text.setCompoundDrawablePadding(20);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        choose_area = findViewById(R.id.choose_area);
        post_pic_layout = findViewById(R.id.post_pic_layout);
        post_up_layout = findViewById(R.id.post_up_layout);
        post_all_layout = findViewById(R.id.post_all_layout);
        post_all = (ImageView) findViewById(R.id.post_all);
        post_up = (ImageView) findViewById(R.id.post_up);
        post_pic = (ImageView) findViewById(R.id.post_pic);
        post_collect = findViewById(R.id.post_collect);
        collect_img = (ImageView) findViewById(R.id.collect_img);
        post_comment = findViewById(R.id.post_comment);
        post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(PostDetailActivity.this, LoginActivity.class));
                    return;
                }
                Intent intent = new Intent(PostDetailActivity.this, PostCommentActivity.class);
                intent.putExtra("post_id", post_id + "");
                intent.putExtra("comment_reply_id", "0");
                startActivity(intent);
            }
        });
        post_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post_collect.setClickable(false);
                if (!AppContext.instance().isLogin()) {
                    startActivity(new Intent(PostDetailActivity.this, LoginActivity.class));
                    return;
                }
                CircleApi.collect_post(post_id, new Listener<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getInt("status") == 1) {
                                is_collect = is_collect == 1 ? 0 : 1;
                                if (XmbUtils.popupWindow != null && XmbUtils.popupWindow.isShowing()) {
                                    XmbUtils.popupWindow.dismiss();
                                }
                                if (is_collect == 1) {
                                    collect_img.setImageResource(R.mipmap.mbq_collect_active);
                                } else {
                                    collect_img.setImageResource(R.mipmap.mbq_collect_inactive);
                                }
                                XmbUtils.showMessage(PostDetailActivity.this, response.getString("info"));
                            } else {
                                XmbUtils.showMessage(PostDetailActivity.this, response.getString("info"));
                            }
                        } catch (JSONException e) {
                            XmbUtils.showMessage(PostDetailActivity.this, "网络异常，请稍后重试！");
                        }
                        post_collect.setClickable(true);
                    }
                });
            }
        });


        post_all_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_choose = 1;
                if (type == current_choose) {
                    return;
                }
                setImage();
                isImage = "1";
                url = ApiHelper.BASE_URL + "circle/mpost/" + post_id + "/" + isImage + "/" + poster;
                Logger.d(url);
                webView.loadUrl(url);
            }
        });


        toolbar_right_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d(share_info.toString());
                XmbUtils.showMbqShare(PostDetailActivity.this, findViewById(R.id.mbq_share_container), share_info);
            }
        });

        post_up_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_choose = 2;
                if (type == current_choose) {
                    return;
                }
                setImage();
                isImage = "2";
                url = ApiHelper.BASE_URL + "circle/mpost/" + post_id + "/" + isImage + "/" + poster;
                Logger.d(url);
                webView.loadUrl(url);
            }
        });

        post_pic_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_choose = 3;
                if (type == current_choose) {
                    return;
                }
                setImage();
                isImage = "3";
                url = ApiHelper.BASE_URL + "circle/mpost/" + post_id + "/" + isImage + "/" + poster;
                Logger.d(url);
                webView.loadUrl(url);
            }
        });
//        setRecRequest(1);
        toolbar_title_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_choose_show) {
                    showArea(1);
                } else {
                    showArea(2);
                }
            }
        });
        webView = (WebView) findViewById(R.id.webview);

        //如果访问的页面中有Javascript，则webview必须设置支持Javascript
        webView.getSettings().setJavaScriptEnabled(true);
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
        });
        webView.loadUrl(url);
        webView.addJavascriptInterface(new JavaInterfaceObject(PostDetailActivity.this), "xmbapp");
    }

    private void setImage() {
        if (current_choose == 1) {
            post_all.setImageResource(R.mipmap.mbq_post_all_white);
            post_up.setImageResource(R.mipmap.mbq_post_up_gray);
            post_pic.setImageResource(R.mipmap.mbq_post_pic_gray);
            type = 1;
        } else if (current_choose == 2) {
            post_all.setImageResource(R.mipmap.mbq_post_all_gray);
            post_up.setImageResource(R.mipmap.mbq_post_up_white);
            post_pic.setImageResource(R.mipmap.mbq_post_pic_gray);
            type = 2;
        } else if (current_choose == 3) {
            post_all.setImageResource(R.mipmap.mbq_post_all_gray);
            post_up.setImageResource(R.mipmap.mbq_post_up_gray);
            post_pic.setImageResource(R.mipmap.mbq_post_pic_white);
            type = 3;
        }
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        showArea(2);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AppContext.instance().isLogin()) {
            CircleApi.check_is_collect(post_id, new Listener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getInt("status") == 1) {
                            is_collect = 1;
                            collect_img.setImageResource(R.mipmap.mbq_collect_active);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void showArea(int t) {
        if (t == 2) {
            is_choose_show = false;
            toolbar_title_text.setCompoundDrawables(null, null, drawable2, null);
            choose_area.setVisibility(View.GONE);
        } else {
            toolbar_title_text.setCompoundDrawables(null, null, drawable1, null);
            is_choose_show = true;
            choose_area.setVisibility(View.VISIBLE);
        }
    }

    public class JavaInterfaceObject {
        private Context mContext;

        public JavaInterfaceObject(Context context) {
            this.mContext = context;
        }

        @JavascriptInterface
        public void postComment(String post_id, String comment_id, String user_name) {
            Logger.t("post_id").d(post_id);
            Logger.t("comment_id").d(comment_id);
            Logger.t("user_name").d(user_name);
            if (!AppContext.instance().isLogin()) {
                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                return;
            }
            Intent intent = new Intent(mContext, PostCommentActivity.class);
            intent.putExtra("post_id", post_id + "");
            intent.putExtra("comment_reply_id", comment_id);
            mContext.startActivity(intent);
        }
    }
}
