package com.witmoon.xmb.activity.mbq.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mbq.adapter.CommentAdapter;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.util.HtmlHttpImageGetter;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.URLImageGetter;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

public class PostDetailActivity extends BaseActivity {

    private ImageView toolbar_right_img;
    private int page = 1;
    private int post_id;
    private ArrayList<JSONObject> mDatas = new ArrayList<>();
    private CommentAdapter adapter;
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
    private int user_id = 0;
    private int is_collect = 0;
    private ImageView collect_img;
    private int comment_id = 0;
    private HashMap<String, String> share_info = new HashMap<>();

    private BroadcastReceiver refreshCurrentActivity = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mDatas.size() < 20) {
                mDatas.clear();
                page = 1;
                setRecRequest(page);
            } else {
                TextView post_comment_total = (TextView) headerView.findViewById(R.id.post_comment_total);
                post_comment_total.setText(post_comment_total.getText().toString() + 1);
            }
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
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new CommentAdapter(mDatas, this);
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        stringAdapter.addHeaderView(headerView);
        mRootView.setAdapter(stringAdapter);
        post_id = getIntent().getIntExtra("post_id", 0);
        choose_area = findViewById(R.id.choose_area);
        post_pic_layout = findViewById(R.id.post_pic_layout);
        post_up_layout = findViewById(R.id.post_up_layout);
        post_all_layout = findViewById(R.id.post_all_layout);
        post_all = (ImageView) findViewById(R.id.post_all);
        post_up = (ImageView) findViewById(R.id.post_up);
        post_pic = (ImageView) findViewById(R.id.post_pic);
        post_collect = findViewById(R.id.post_collect);
        collect_img = (ImageView) findViewById(R.id.collect_img);
        comment_id = getIntent().getIntExtra("comment_id", 0);
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
                setImage();
            }
        });


        toolbar_right_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmbUtils.showMbqShare(PostDetailActivity.this, findViewById(R.id.mbq_share_container), share_info);
            }
        });

        post_up_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_choose = 2;
                setImage();
            }
        });

        post_pic_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_choose = 3;
                setImage();
            }
        });

        setRecRequest(page);

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
        page = 1;
        mDatas.clear();
        comment_id = 0;
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        showArea(2);
        setRecRequest(page);
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

    private void setHeadView(JSONObject jsonObject) throws JSONException {
        ImageView post_userhead = (ImageView) headerView.findViewById(R.id.post_userhead);
        Netroid.displayBabyImage(jsonObject.getString("author_userhead"), post_userhead);
        TextView post_username = (TextView) headerView.findViewById(R.id.post_username);
        post_username.setText(jsonObject.getString("author_name"));
        TextView post_floor = (TextView) headerView.findViewById(R.id.post_floor);
        post_floor.setText("楼主");
        user_id = jsonObject.getInt("author_id");
        TextView post_title = (TextView) headerView.findViewById(R.id.post_title);
        post_title.setText(jsonObject.getString("post_title"));
        HtmlTextView post_content = (HtmlTextView) headerView.findViewById(R.id.post_content);
//        post_content.setHtml(jsonObject.getString("post_content"), new HtmlHttpImageGetter(post_content,null,true));
        post_content.setHtml(jsonObject.getString("post_content"), new URLImageGetter(post_content));
        share_info.put("title", jsonObject.getString("post_title"));
        share_info.put("desc", jsonObject.getString("post_content"));
        share_info.put("url", "http://api.xiaomabao.com/circle/post/" + post_id);
        LinearLayout post_imgs_container = (LinearLayout) headerView.findViewById(R.id.post_imgs_container);
        post_imgs_container.removeAllViews();
//        JSONArray imgsJsonArray = jsonObject.getJSONArray("post_imgs");
//        if (imgsJsonArray.length() == 0) {
//            post_imgs_container.setVisibility(View.GONE);
//        } else {
//            post_imgs_container.setVisibility(View.VISIBLE);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            for (int i = 0; i < imgsJsonArray.length(); i++) {
//                ImageView riv = new ImageView(this);
//                layoutParams.width = MainActivity.screen_width - 50;
//                layoutParams.setMargins(0, 0, 0, 20);
//                riv.setMaxHeight(MainActivity.screen_width * 5);
//                riv.setScaleType(ImageView.ScaleType.FIT_XY);
//                riv.setAdjustViewBounds(true);
//                riv.setLayoutParams(layoutParams);
//                Netroid.displayImage(imgsJsonArray.getString(i), riv);
//                post_imgs_container.addView(riv);
//            }
//        }

        TextView circle_name = (TextView) headerView.findViewById(R.id.circle_name);
        circle_name.setText(jsonObject.getString("circle_name"));

        TextView post_comment_total = (TextView) headerView.findViewById(R.id.post_comment_total);
        post_comment_total.setText(jsonObject.getString("reply_cnt"));
    }

    @Override
    public void setRecRequest(int page0) {
        CircleApi.circle_detail(post_id, page, type, user_id, comment_id, new Listener<JSONObject>() {
            @Override
            public void onError(NetroidError error) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(JSONObject response) {
                Log.e("Response", response.toString());
                if (response.has("post_detail")) {
                    try {
                        setHeadView(response.getJSONObject("post_detail"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    JSONArray jsonArray = response.getJSONArray("comments");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mDatas.add(jsonArray.getJSONObject(i));
                    }
                    if (jsonArray.length() < 20) {
                        removeFooterView();
                    } else {
                        createLoadMoreView();
                        resetStatus();
                    }
                    page += 1;
                    stringAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
        });
    }

}
