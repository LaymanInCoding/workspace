package com.witmoon.xmb.activity.babycenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.babycenter.Adapter.DayNumberAdapter;
import com.witmoon.xmb.activity.babycenter.Adapter.RecGoodsAdapter;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.mbq.activity.PostDetailActivity;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.activity.webview.InteractiveWebViewActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.MengbaoApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.DateUtil;
import com.witmoon.xmb.util.HttpUtility;
import com.witmoon.xmb.util.SDCardUtils;
import com.witmoon.xmb.util.SharedPreferencesUtil;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.WeakAsyncTask;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import library.BitmapUtil;
import library.CropHandler;
import library.CropHelper;
import library.CropParams;

public class BabyCenterFragment extends BaseFragment implements DialogInterface.OnClickListener, CropHandler {

    private boolean first_baby_launch;
    private View rootView;
    private int current_type = 0;
    private RecyclerView horizonta_recycleview;
    private DayNumberAdapter daynum_adapter;
    private ArrayList<HashMap<String, String>> daynumArrayList = new ArrayList<>();
    private int current_daynum = 0;
    private EmptyLayout emptyLayout;
    private View left_btn, right_btn;
    private ImageView me_avatar_img;
    Date start_date = null, current_date, end_date;
    private View add_tools;
    private String btype = "";
    private TextView add_tools2;
    private View status1_container, status2_container;
    private int cnt = 0, over = 0;
    private BabyCenterFragment _this = this;
    private TextView baby_weight_textview, baby_length_textview, due_date_textview, baby_weight_textview_hint, baby_length_textview_hint, due_date_textview_hint, dayinfo_summary;
    private CropParams mCropParams;
    private File mAvatar;
    private View jump_setting, backtotoday;
    private View more_message_img;
    private int dayCnt = 0;
    private boolean is_babypic_change = false;
    private RecyclerView recgood_recycler;


    private void configToolbar() {
        Toolbar toolbar = ((BaseActivity) getActivity()).getToolBar();
        AQuery aQuery = new AQuery(getActivity(), toolbar);
        aQuery.id(R.id.top_toolbar).visible();
        aQuery.id(R.id.toolbar_right_img).gone();
        aQuery.id(R.id.toolbar_logo_img).gone();
        aQuery.id(R.id.toolbar_right_img1).gone();
        aQuery.id(R.id.toolbar_right_img2).gone();
        aQuery.id(R.id.toolbar_title_text).visible().text("我的状态");
        ((MainActivity) getActivity()).setTitleColor_(R.color.main_kin);
    }

    private void hideConfigToolbar() {
        Toolbar toolbar = ((BaseActivity) getActivity()).getToolBar();
        AQuery aQuery = new AQuery(getActivity(), toolbar);
        aQuery.id(R.id.top_toolbar).gone();
    }

    private void checkFirstOpen() {
        String flag = AppContext.getProperty(BabyGuideActivity.KEY_FLAG_FIRST_LAUNCH);
        Log.e("flag", flag);
        if (TextUtils.isEmpty(flag) || flag.equals("true")) {
            first_baby_launch = true;
        } else {
            first_baby_launch = false;
        }
        Log.e("first-launch", first_baby_launch + "");
        if (first_baby_launch && AppContext.instance().isLogin()) {
            Intent intent = new Intent(getActivity(), BabyGuideActivity.class);
            startActivity(intent);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_my_status, container, false);
            recgood_recycler = (RecyclerView) rootView.findViewById(R.id.recgood_recycler);
            status1_container = rootView.findViewById(R.id.status1_container);
            status2_container = rootView.findViewById(R.id.status2_container);
            emptyLayout = (EmptyLayout) rootView.findViewById(R.id.error_layout);
            emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setCurrentStatus();
                }
            });
            mCropParams = new CropParams(getActivity());
            setCurrentStatus();
            IntentFilter refresh_login = new IntentFilter(Const.INTENT_ACTION_LOGIN);
            getActivity().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    hideConfigToolbar();
                    daynumArrayList.clear();
                    horizonta_recycleview.scrollToPosition(0);
                    daynum_adapter.notifyDataSetChanged();
                    setCurrentStatus();
                }
            }, refresh_login);

            IntentFilter refresh_logout = new IntentFilter(Const.INTENT_ACTION_LOGOUT);
            getActivity().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    SharedPreferencesUtil.remove(getActivity(), "pic_wall");
                    backtotoday.setVisibility(View.GONE);
                    jump_setting.setVisibility(View.GONE);
                    daynumArrayList.clear();
                    horizonta_recycleview.scrollToPosition(0);
                    daynum_adapter.notifyDataSetChanged();
                    LinearLayout tools_container = (LinearLayout) rootView.findViewById(R.id.tools_container);
                    tools_container.removeAllViews();
                    setCurrentStatus();
                }
            }, refresh_logout);

            IntentFilter refresh_status = new IntentFilter(Const.REFRESH_MENGBAO);
            getActivity().registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    daynumArrayList.clear();
                    horizonta_recycleview.scrollToPosition(0);
                    daynum_adapter.notifyDataSetChanged();
                    setCurrentStatus();
                }
            }, refresh_status);
            setFont();
        }
        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        return rootView;
    }

    private void setCurrentStatus() {
        if (AppContext.instance().isLogin()) {
            if (AppContext.getLoginInfo().getIs_baby_add().equals("true")) {
                checkFirstOpen();
                current_type = 1;
            } else {
                current_type = 0;
            }
        } else {
            current_type = 0;
        }
        if (current_type == 0) {
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            status1_container.setVisibility(View.VISIBLE);
            status2_container.setVisibility(View.GONE);
            bindMyStatusEvent();
            if (MainActivity.current_tab_index == 0) {
                configToolbar();
            }
        } else {
            status1_container.setVisibility(View.GONE);
            status2_container.setVisibility(View.GONE);
            if (daynumArrayList.size() != 0) {
                status2_container.setVisibility(View.VISIBLE);
                if (MainActivity.current_tab_index == 0) {
                    hideConfigToolbar();
                }
            }
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            bindMengbaoEvent();
            initView();
        }
    }

    private void initView() {
        requestData("");
    }

    private void requestToolkit(String dateStr) {
        MengbaoApi.getToolkits(dateStr, new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                LinearLayout tools_container = (LinearLayout) rootView.findViewById(R.id.tools_container);
                tools_container.removeAllViews();
                try {
                    JSONArray toolsArray = response.getJSONArray("data");
                    if (toolsArray.length() == 0) {
                        tools_container.setVisibility(View.GONE);
                        add_tools.setVisibility(View.VISIBLE);
                        add_tools2.setVisibility(View.GONE);
                        return;
                    }
                    for (int i = 0; i < toolsArray.length(); i++) {
                        final JSONObject jsonObject = toolsArray.getJSONObject(i);
                        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_remind_tool, null);
                        linearLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getActivity(), InteractiveWebViewActivity.class);
                                try {
                                    intent.putExtra("url", jsonObject.getString("toolkit_url"));
                                    intent.putExtra("title", jsonObject.getString("toolkit_name"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                startActivity(intent);
                            }
                        });
                        TextView titleView = (TextView) linearLayout.findViewById(R.id.tool_name);
                        TextView contentView = (TextView) linearLayout.findViewById(R.id.tool_desc);
                        ImageView imageView = (ImageView) linearLayout.findViewById(R.id.tool_img);
                        LinearLayout tool_detail_container = (LinearLayout) linearLayout.findViewById(R.id.tool_detail_container);
                        if (jsonObject.has("toolkit_detail")) {
                            contentView.setVisibility(View.GONE);
                            tool_detail_container.removeAllViews();
                            tool_detail_container.setVisibility(View.VISIBLE);
                            JSONArray details = jsonObject.getJSONArray("toolkit_detail");
                            if (details.length() == 0) {
                                tool_detail_container.setVisibility(View.GONE);
                                contentView.setVisibility(View.VISIBLE);
                            }
                            int length = details.length() > 3 ? 3 : details.length();
                            for (int j = 0; j < length; j++) {
                                LinearLayout linearLayout_child = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_remind_tool_item, null);
                                TextView left_text = (TextView) linearLayout_child.findViewById(R.id.left_c);
                                TextView right_text = (TextView) linearLayout_child.findViewById(R.id.right_c);
                                JSONObject detail = details.getJSONObject(j);
                                left_text.setText(detail.getString("t1"));
                                right_text.setText(detail.getString("t2"));
                                tool_detail_container.addView(linearLayout_child);
                            }
                        }
                        TextView tool_remind_time = (TextView) linearLayout.findViewById(R.id.tool_remind_time);
                        if (!jsonObject.getString("toolkit_remind_time").equals("")) {
                            tool_remind_time.setVisibility(View.VISIBLE);
                            tool_remind_time.setText(jsonObject.getString("toolkit_remind_time"));
                        }
                        titleView.setText(jsonObject.getString("toolkit_name"));
                        contentView.setText(jsonObject.getString("toolkit_desc"));
                        Netroid.displayImage(jsonObject.getString("toolkit_icon"), imageView);
                        tools_container.addView(linearLayout);
                    }
                    tools_container.setVisibility(View.VISIBLE);
                    add_tools.setVisibility(View.GONE);
                    add_tools2.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    tools_container.setVisibility(View.GONE);
                    add_tools.setVisibility(View.VISIBLE);
                    add_tools2.setVisibility(View.GONE);
                }
            }
        });
    }

    Handler handler = new Handler();
    //要用handler来处理多线程可以使用runnable接口，这里先定义该接口
    //线程中运行该接口的run函数
    Runnable update_thread = new Runnable() {
        public void run() {
            daynumArrayList.clear();
            horizonta_recycleview.scrollToPosition(0);
            daynum_adapter.notifyDataSetChanged();
            for (int i = 0; i < cnt; i++) {
                HashMap<String, String> hashMap = new HashMap<>();
                String dateStr = DateUtil.getDateBeforeOrAfterMD(start_date, i);
                hashMap.put("date", dateStr);
                if (i == current_daynum && over == 0) {
                    hashMap.put("is_checked", "1");
                } else {
                    hashMap.put("is_checked", "0");
                }
                hashMap.put("daynum", (i + 1) + "天");
                daynumArrayList.add(hashMap);
            }
            horizonta_recycleview.scrollToPosition(current_daynum + 1);
        }
    };

    private void requestData(final String dateStr) {
        MengbaoApi.getMengbaoInfo(dateStr, new Listener<JSONObject>() {
            @Override
            public void onPreExecute() {
                if (daynumArrayList.size() == 0) {
                    status1_container.setVisibility(View.GONE);
                    status2_container.setVisibility(View.GONE);
                    configToolbar();
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (daynumArrayList.size() == 0) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }

            @Override
            public void onSuccess(JSONObject response) {
                Log.e("response", response.toString());
                try {
                    btype = response.getString("type");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (response.has("recommend_posts")) {
                    try {
                        setRecommendPosts(response.getJSONArray("recommend_posts"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (response.has("recommend_topics")) {
                    try {
                        setRecommendTopics(response.getJSONArray("recommend_topics"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (response.has("recommend_goods")) {
                    try {
                        JSONArray goodsArray = response.getJSONArray("recommend_goods");
                        ArrayList<JSONObject> list = new ArrayList<>();
                        RecGoodsAdapter recAdapter = new RecGoodsAdapter(getActivity(), list);
                        for (int i = 0; i < goodsArray.length(); i++) {
                            JSONObject obj = goodsArray.getJSONObject(i);
                            list.add(obj);
                        }
                        recAdapter.notifyDataSetChanged();
                        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
                        recgood_recycler.setLayoutManager(manager);
                        recgood_recycler.setAdapter(recAdapter);
                        recgood_recycler.setHasFixedSize(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (response.has("day_info")) {
                    try {
                        rootView.findViewById(R.id.top_container).setVisibility(View.VISIBLE);
                        setDayInfo(response.getJSONObject("day_info"));
                    } catch (JSONException e) {
                        rootView.findViewById(R.id.top_container).setVisibility(View.GONE);
                    }
                } else {
                    if (!is_babypic_change) {
                        if (!AppContext.getLoginInfo().getBaby_photo().equals("")) {
                            ImageLoader.getInstance().displayImage(AppContext.getLoginInfo().getBaby_photo(), me_avatar_img, AppContext.options_disk);
                        } else {
                            me_avatar_img.setImageResource(R.mipmap.touxiang);
                        }
                    }
                    rootView.findViewById(R.id.top_container).setVisibility(View.GONE);
                }
                if (response.has("remind")) {
                    try {
                        setRemindTopics(response.getJSONArray("remind"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (daynumArrayList.size() == 0) {
                    try {
                        start_date = DateUtil.strToDate(response.get("start_date").toString());
                        current_date = DateUtil.strToDate(response.get("current_date").toString());
                        end_date = DateUtil.strToDate(response.get("end_date").toString());
                        dayCnt = DateUtil.getGapCount(start_date, current_date) + 1;
                        if (dayCnt >= 260 && btype.equals("pregnant")) {
                            jump_setting.setVisibility(View.VISIBLE);
                        } else {
                            jump_setting.setVisibility(View.GONE);
                        }
                        cnt = DateUtil.getGapCount(start_date, end_date) + 1;
                        current_daynum = DateUtil.getGapCount(start_date, current_date);
                        if (current_daynum > cnt - 1) {
                            over = 1;
                            current_daynum = 1;
                        }
                        if (start_date.compareTo(current_date) > 0) {
                            current_daynum = 1;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < 3; i++) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        if (start_date != null) {
                            String dateStr = DateUtil.getDateBeforeOrAfterMD(start_date, current_daynum + i - 1);
                            hashMap.put("date", dateStr);
                        }
                        if (i == 1 && over == 0) {
                            hashMap.put("is_checked", "1");
                        } else {
                            hashMap.put("is_checked", "0");
                        }
                        hashMap.put("daynum", (current_daynum + i) + "天");
                        daynumArrayList.add(hashMap);
                    }
                    scrollToPlace(0);
                    hideToolBar();
                    emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    status2_container.setVisibility(View.VISIBLE);
                    handler.postDelayed(update_thread, 500);
                }
            }
        });
        requestToolkit(dateStr);
    }

    private void setDayInfo(JSONObject jsonObject) throws JSONException {

        View overdue_t_line = rootView.findViewById(R.id.overdue_t_line);
        View overdue_t_container = rootView.findViewById(R.id.overdue_t_container);
        if (!jsonObject.has("overdue_daynum")) {
            overdue_t_line.setVisibility(View.GONE);
            overdue_t_container.setVisibility(View.GONE);
        } else {
            overdue_t_line.setVisibility(View.VISIBLE);
            overdue_t_container.setVisibility(View.VISIBLE);
            due_date_textview.setText(jsonObject.getString("overdue_daynum"));
        }

        baby_length_textview.setText(jsonObject.getString("baby_length"));
        baby_weight_textview.setText(jsonObject.getString("baby_weight"));
        if (jsonObject.getString("content").equals("")) {
            rootView.findViewById(R.id.info_container).setVisibility(View.GONE);
        } else {
            rootView.findViewById(R.id.info_container).setVisibility(View.VISIBLE);
            dayinfo_summary.setText(jsonObject.getString("content"));
        }
        if (jsonObject.has("images") && btype.equals("pregnant")) {
            me_avatar_img.setClickable(false);
            baby_weight_textview_hint.setText("胎儿体重");
            baby_length_textview_hint.setText("胎儿身长");
            ImageLoader.getInstance().displayImage(jsonObject.getString("images"), me_avatar_img, AppContext.options_disk);
        } else {
            me_avatar_img.setClickable(true);
            baby_weight_textview_hint.setText("宝宝体重");
            baby_length_textview_hint.setText("宝宝身高");
            if (AppContext.getLoginInfo().getBaby_photo().equals("")) {
                me_avatar_img.setImageResource(R.mipmap.touxiang);
            } else {
                if (!is_babypic_change) {
                    if (!AppContext.getLoginInfo().getBaby_photo().equals("")) {
                        ImageLoader.getInstance().displayImage(AppContext.getLoginInfo().getBaby_photo(), me_avatar_img, AppContext.options_disk);
                    } else {
                        me_avatar_img.setImageResource(R.mipmap.touxiang);
                    }
                }
            }
        }
    }

    private void setRecommendPosts(JSONArray postsJSONArray) throws JSONException {
        LinearLayout mb_posts_container = (LinearLayout) rootView.findViewById(R.id.mb_posts_container);
        mb_posts_container.removeAllViews();
        for (int i = 0; i < postsJSONArray.length(); i++) {
            final JSONObject jsonObject = postsJSONArray.getJSONObject(i);
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_mengbao_post, null);
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), PostDetailActivity.class);
                    try {
                        intent.putExtra("post_id", Integer.parseInt(jsonObject.getString("post_id")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getActivity().startActivity(intent);
                }
            });
            TextView titleView = (TextView) linearLayout.findViewById(R.id.post_title);
            TextView contentView = (TextView) linearLayout.findViewById(R.id.post_content);
            TextView replyView = (TextView) linearLayout.findViewById(R.id.post_reply_cnt);
            TextView viewView = (TextView) linearLayout.findViewById(R.id.post_view_cnt);
            ImageView imageView = (ImageView) linearLayout.findViewById(R.id.post_img);
            titleView.setText(jsonObject.getString("post_title"));
            contentView.setText(jsonObject.getString("post_content"));
            replyView.setText(jsonObject.getString("reply_cnt"));
            viewView.setText(jsonObject.getString("view_cnt"));
            Netroid.displayImage(jsonObject.getJSONArray("post_imgs").get(0).toString(), imageView);
            mb_posts_container.addView(linearLayout);
        }
    }

    private void setRecommendTopics(JSONArray topicsJSONArray) throws JSONException {
        ImageView topic_img_01 = (ImageView) rootView.findViewById(R.id.topic_img_01);
        ImageView topic_img_02 = (ImageView) rootView.findViewById(R.id.topic_img_02);
        final JSONObject topic1 = topicsJSONArray.getJSONObject(0);
        final JSONObject topic2 = topicsJSONArray.getJSONObject(1);
        Netroid.displayImage(topic1.getString("ad_img"), topic_img_01);
        Netroid.displayImage(topic2.getString("ad_img"), topic_img_02);

        topic_img_01.setOnClickListener(new topicClickHandler(topic1));
        topic_img_02.setOnClickListener(new topicClickHandler(topic2));
    }

    private void setRemindTopics(JSONArray remindJSONArray) {
        LinearLayout remind_container = (LinearLayout) rootView.findViewById(R.id.remind_container);
        remind_container.removeAllViews();
        if (remindJSONArray.length() == 0) {
            rootView.findViewById(R.id.layout_mengbao_remind).setVisibility(View.GONE);
        }
        for (int i = 0; i < remindJSONArray.length(); i++) {
            final JSONObject jsonObject;
            try {
                jsonObject = remindJSONArray.getJSONObject(i);
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.item_tool, null);
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), InteractiveWebViewActivity.class);
                        try {
                            intent.putExtra("url", jsonObject.getString("url"));
                            intent.putExtra("title", jsonObject.getString("title"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                    }
                });
                TextView titleView = (TextView) linearLayout.findViewById(R.id.tool_name);
                TextView contentView = (TextView) linearLayout.findViewById(R.id.tool_desc);
                ImageView imageView = (ImageView) linearLayout.findViewById(R.id.tool_img);
                titleView.setText(jsonObject.getString("title"));
                contentView.setText(jsonObject.getString("summary"));
                Netroid.displayImage(jsonObject.getString("icon"), imageView);
                remind_container.addView(linearLayout);
                rootView.findViewById(R.id.layout_mengbao_remind).setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                rootView.findViewById(R.id.layout_mengbao_remind).setVisibility(View.GONE);
            }
        }
    }

    private class topicClickHandler implements View.OnClickListener {
        private JSONObject jsonObject;

        public topicClickHandler(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        public void onClick(View v) {
            try {
                if (null != jsonObject.get("ad_type")) {
                    int type = Integer.parseInt(jsonObject.getString("ad_type"));
                    //专题  2商品 3网页 4团购 5帖子
                    String id = jsonObject.getString("act_id");
                    if (type == 1) {
                        MarketPlaceActivity.start(getActivity(), id, jsonObject.getString("ad_name"));
                    } else if (type == 2) {
                        CommodityDetailActivity.start(getActivity(), id);
                    } else if (type == 3) {
                        Intent intent = new Intent(getActivity(), InteractiveWebViewActivity.class);
                        intent.putExtra("url", id);
                        intent.putExtra("title", jsonObject.getString("ad_name"));
                        startActivity(intent);
                    } else if (type == 4) {
                        GroupBuyActivity.start(getContext(), id);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindMengbaoEvent() {
        left_btn = rootView.findViewById(R.id.left_btn);
        right_btn = rootView.findViewById(R.id.right_btn);

        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_daynum == 0) {
                    return;
                }
                scrollToPlace(-1);
            }
        });

        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (current_daynum >= daynumArrayList.size() - 1) {
                    return;
                }
                scrollToPlace(1);
            }
        });

        View jump_to_circle = rootView.findViewById(R.id.jump_to_circle);
        jump_to_circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mTabHost.setCurrentTab(3);
            }
        });

        View jump_to_shop = rootView.findViewById(R.id.jump_to_shop);
        jump_to_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mTabHost.setCurrentTab(2);
            }
        });
    }

    private void bindMyStatusEvent() {
        RelativeLayout in_pregnant_status_img = (RelativeLayout) rootView.findViewById(R.id.my_status1);
        RelativeLayout baby_born_status_img = (RelativeLayout) rootView.findViewById(R.id.my_status2);

        baby_born_status_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.instance().isLogin()) {
//                    startActivity(new Intent(getActivity(),BabyGuideFragment.class));
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ChildStatus);
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });

        in_pregnant_status_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContext.instance().isLogin()) {
                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.OverdueSetting);
                } else {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            }
        });
    }

    private void scrollToPlace(int p) {
        if (p != 0) {
            HashMap<String, String> hashMap1 = daynumArrayList.get(current_daynum);
            hashMap1.put("is_checked", "0");
            daynumArrayList.set(current_daynum, hashMap1);
            current_daynum += p;
            HashMap<String, String> hashMap2 = daynumArrayList.get(current_daynum);
            hashMap2.put("is_checked", "1");
            daynumArrayList.set(current_daynum, hashMap2);
            daynum_adapter.notifyDataSetChanged();
        }
        if ((dayCnt - 1) == current_daynum) {
            backtotoday.setVisibility(View.GONE);
        } else {
            if (over == 1 && current_daynum <= daynumArrayList.size() && daynumArrayList.get(current_daynum).get("is_checked").equals("1")) {
                backtotoday.setVisibility(View.VISIBLE);
            } else if (over == 0 && (dayCnt - 1) != current_daynum) {
                backtotoday.setVisibility(View.VISIBLE);
            } else {
                backtotoday.setVisibility(View.GONE);
            }
        }
        if (p == 0) {
        } else {
            fling(p * horizonta_recycleview.getWidth() / 3 * current_daynum, 0);
            requestData(DateUtil.getDateBeforeOrAfter(start_date, current_daynum));
        }
    }

    public boolean fling(int velocityX, int velocityY) {
        int lastVisibleView = layoutManager.findLastVisibleItemPosition();
        int firstVisibleView = layoutManager.findFirstVisibleItemPosition();
        View firstView = layoutManager.findViewByPosition(firstVisibleView);
        View lastView = layoutManager.findViewByPosition(lastVisibleView);
        int leftMargin = (horizonta_recycleview.getWidth() - lastView.getWidth()) / 2;
        int rightMargin = (horizonta_recycleview.getWidth() - firstView.getWidth()) / 2 + firstView.getWidth();
        int leftEdge = lastView.getLeft();
        int rightEdge = firstView.getRight();
        int scrollDistanceLeft = leftEdge - leftMargin;
        int scrollDistanceRight = rightMargin - rightEdge;

        //if(user swipes to the left)
        if (velocityX > 0)
            horizonta_recycleview.smoothScrollBy(scrollDistanceLeft - (lastVisibleView - current_daynum) * firstView.getWidth(), 0);
        else
            horizonta_recycleview.smoothScrollBy(-scrollDistanceRight + (current_daynum - firstVisibleView) * firstView.getWidth(), 0);
        return true;
    }

    private void setFont() {
        AssetManager mgr = getActivity().getAssets();
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/font.otf");
        TextView mb_remind_post = (TextView) rootView.findViewById(R.id.mb_remind_post);
        TextView mb_remind = (TextView) rootView.findViewById(R.id.mb_remind);
        TextView mb_tools = (TextView) rootView.findViewById(R.id.mb_tools);
        TextView mb_remind_goods = (TextView) rootView.findViewById(R.id.mb_remind_goods);
        add_tools2 = (TextView) rootView.findViewById(R.id.add_tools2);
        mb_tools.setTypeface(tf);
        mb_remind.setTypeface(tf);
        mb_remind_post.setTypeface(tf);
        mb_remind_goods.setTypeface(tf);
        add_tools2.setTypeface(tf);

        baby_weight_textview = (TextView) rootView.findViewById(R.id.baby_weight);
        baby_length_textview = (TextView) rootView.findViewById(R.id.baby_length);
        due_date_textview = (TextView) rootView.findViewById(R.id.due_date_text);
        baby_weight_textview_hint = (TextView) rootView.findViewById(R.id.baby_weight_text_hint);
        baby_length_textview_hint = (TextView) rootView.findViewById(R.id.baby_length_text_hint);
        due_date_textview_hint = (TextView) rootView.findViewById(R.id.due_date_text_hint);
        dayinfo_summary = (TextView) rootView.findViewById(R.id.dayinfo_summary);
        more_message_img = rootView.findViewById(R.id.more_message_img);
        more_message_img.setOnClickListener(v -> {
            UIHelper.showSimpleBack(getContext(), SimpleBackPage.BABYMOREMESSAGE);
        });
        baby_weight_textview.setTypeface(tf);
        baby_length_textview.setTypeface(tf);
        due_date_textview.setTypeface(tf);
        baby_weight_textview_hint.setTypeface(tf);
        baby_length_textview_hint.setTypeface(tf);
        due_date_textview_hint.setTypeface(tf);

        LinearLayout goods_container1 = (LinearLayout) rootView.findViewById(R.id.goods_container1);
        goods_container1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MainActivity.screen_width * 175 / 750));

        bindBabyCenterEvent();

        horizonta_recycleview = (RecyclerView) rootView.findViewById(R.id.horizonta_recycleview);
        layoutManager = new LinearLayoutManager(this.getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        horizonta_recycleview.setHasFixedSize(true);
        horizonta_recycleview.setLayoutManager(layoutManager);
        me_avatar_img = (ImageView) rootView.findViewById(R.id.me_avatar_img);
        daynum_adapter = new DayNumberAdapter(daynumArrayList, getActivity());
        daynum_adapter.setOnItemClickListener(new DayNumberAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position < current_daynum) {
                    scrollToPlace(-(current_daynum - position));
                } else if (position > current_daynum) {
                    scrollToPlace(+(position - current_daynum));
                } else {
                    scrollToPlace(1);
                }
            }
        });
        me_avatar_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XmbUtils.AvatarDialogFragment avatarDialogFragment = new XmbUtils.AvatarDialogFragment();
                avatarDialogFragment.setOnClickListener(_this);
                avatarDialogFragment.show(getActivity().getFragmentManager(), "AvatarDialog");
            }
        });
        horizonta_recycleview.setAdapter(daynum_adapter);
        IntentFilter refresh_toolkit = new IntentFilter(Const.REFRESH_TOOLKIT);
        getActivity().registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                requestToolkit(DateUtil.getDateBeforeOrAfter(start_date, current_daynum));
            }
        }, refresh_toolkit);

        jump_setting = rootView.findViewById(R.id.jump_setting);
        jump_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.ChildStatus);
            }
        });

        backtotoday = rootView.findViewById(R.id.backtotoday);
        backtotoday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (over == 1) {
                    HashMap<String, String> hashMap = daynumArrayList.get(current_daynum);
                    hashMap.put("is_checked", "0");
                    daynumArrayList.set(current_daynum, hashMap);
                    current_daynum = 1;
                    daynum_adapter.notifyDataSetChanged();
                    requestData("");
                } else {
                    resetCurrentDaynum();
                }
                backtotoday.setVisibility(View.GONE);
            }
        });
    }

    private void resetCurrentDaynum() {
        HashMap<String, String> hashMap1 = daynumArrayList.get(current_daynum);
        hashMap1.put("is_checked", "0");
        daynumArrayList.set(current_daynum, hashMap1);
        current_daynum = DateUtil.getGapCount(start_date, current_date);
        HashMap<String, String> hashMap2 = daynumArrayList.get(current_daynum);
        hashMap2.put("is_checked", "1");
        daynumArrayList.set(current_daynum, hashMap2);
        daynum_adapter.notifyDataSetChanged();
        fling(horizonta_recycleview.getWidth() / 3 * current_daynum, 0);
        requestData(DateUtil.getDateBeforeOrAfter(start_date, current_daynum));
    }

    private void bindBabyCenterEvent() {
        View baby_record_container = rootView.findViewById(R.id.baby_record_container);
        View knowledge_container = rootView.findViewById(R.id.knowledge_container);
        View eatable_container = rootView.findViewById(R.id.eatable_container);
        View treasure_container = rootView.findViewById(R.id.treasure_container);
        add_tools = rootView.findViewById(R.id.add_tools);

        baby_record_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BabyRecord);
            }
        });

        knowledge_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InteractiveWebViewActivity.class);
                intent.putExtra("url", ApiHelper.BASE_URL + "discovery/knowledge_index");
                intent.putExtra("title", "知识库");
                startActivity(intent);
            }
        });

        eatable_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InteractiveWebViewActivity.class);
                intent.putExtra("url", ApiHelper.BASE_URL + "safefood/category");
                intent.putExtra("title", "能不能吃");
                startActivity(intent);
            }
        });

        treasure_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InteractiveWebViewActivity.class);
                intent.putExtra("url", "http://www.xiaomabao.com/tools/jewel.html");
                intent.putExtra("title", "百宝箱");
                startActivity(intent);
            }
        });

        add_tools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InteractiveWebViewActivity.class);
                intent.putExtra("url", ApiHelper.BASE_URL + "mengbao/toolkit");
                intent.putExtra("title", "添加工具到首页");
                startActivity(intent);
            }
        });
        add_tools2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InteractiveWebViewActivity.class);
                intent.putExtra("url", ApiHelper.BASE_URL + "mengbao/toolkit");
                intent.putExtra("title", "添加工具到首页");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.current_tab_index = 0;
        if (current_type == 0 || daynumArrayList.size() == 0) {
            configToolbar();
        } else {
            hideConfigToolbar();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        CropHelper.handleResult(this, requestCode, resultCode, data);
    }

    @Override
    public CropParams getCropParams() {
        return mCropParams;
    }

    @Override
    public void onPhotoCropped(Uri uri) {
        if (!mCropParams.compress)
            saveAvatarBitmap(BitmapUtil.decodeUriAsBitmap(getActivity(), uri));
    }

    @Override
    public void onCompressed(Uri uri) {
        saveAvatarBitmap(BitmapUtil.decodeUriAsBitmap(getActivity(), uri));
    }

    @Override
    public void onCancel() {
    }

    @Override
    public void onFailed(String message) {
    }

    @Override
    public void handleIntent(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onDestroy() {
        CropHelper.clearCacheDir();
        super.onDestroy();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mCropParams.refreshUri();
        if (which == 0) {   // 从相册中选择图片
            mCropParams.enable = true;
            mCropParams.compress = false;
            Intent intent = CropHelper.buildGalleryIntent(mCropParams);
            startActivityForResult(intent, CropHelper.REQUEST_CROP);
        } else {
            if (!SDCardUtils.isSDCardEnable()) {
                XmbUtils.showMessage(getActivity(), "SD卡不可用.");
                return;
            }
            mCropParams.enable = true;
            mCropParams.compress = false;
            Intent intent = CropHelper.buildCameraIntent(mCropParams);
            startActivityForResult(intent, CropHelper.REQUEST_CAMERA);
        }
    }

    private void saveAvatarBitmap(Bitmap avatar) {
        me_avatar_img.setImageBitmap(avatar);
        mAvatar = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "children_avatar.png");
        OutputStream os = null;
        try {
            os = new FileOutputStream(mAvatar);
            avatar.compress(Bitmap.CompressFormat.PNG, 100, os);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                    new ModifyAsyncTask(BabyCenterFragment.this).execute();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private class ModifyAsyncTask extends WeakAsyncTask<Void, Void, String, BabyCenterFragment> {

        public ModifyAsyncTask(BabyCenterFragment babyCenterFragment) {
            super(babyCenterFragment);
        }

        @Override
        protected String doInBackground(BabyCenterFragment babyCenterFragment,
                                        Void... params) {
            Map<String, File> fm = null;
            if (mAvatar != null) {
                fm = new HashMap<>();
                fm.put("photo", mAvatar);
            }
            Map<String, String> pm = new HashMap<>();
            pm.put("baby_id", AppContext.getLoginInfo().getBaby_id());
            try {
                String response = HttpUtility.post("http://api.xiaomabao.com/athena/save_baby_photo",
                        null, ApiHelper.getParamMap(pm), fm);
                JSONObject respObj = new JSONObject(response);
                if (respObj.getString("status").equals("1")) {
                    is_babypic_change = true;
                    return null;
                } else {
                    return "设置失败";
                }
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(BabyCenterFragment babyCenterFragment, String result) {
            if (result != null) {
                XmbUtils.showMessage(getActivity(), result);
                return;
            }
            XmbUtils.showMessage(getActivity(), "设置成功");
        }
    }

}
