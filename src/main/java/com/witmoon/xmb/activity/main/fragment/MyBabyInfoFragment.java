package com.witmoon.xmb.activity.main.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.baby.DynamicPost;
import com.witmoon.xmb.activity.main.adapter.BabyInfoAdapter;
import com.witmoon.xmb.api.HomeApi;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.RecordDetails;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.BitmapUtils;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cn.easydone.swiperefreshendless.EndlessRecyclerOnScrollListener;
import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

public class MyBabyInfoFragment extends BaseFragment implements View.OnClickListener {
    public static final String KEY_FLAG_FIRST_LAUNCH_IMG = "app.first.launchs";
    View rootView,headView;
    private ImageView mBabyPic, shou_img, img_th;
    private TextView mBabyName;
    private TextView mBabyInfo;
    private int mPageNo = 1;
    private LinearLayout growth_tip_container, security_tip_container, weather_tip_container;
    private ArrayList<Object> recordArrayList;
    private BabyInfoAdapter infoAdapter;
    private boolean is_load = false;
    public static HashMap<String,Integer> diaryHashMap = new HashMap<>();
    //当前页数
    private int pagers;
    private EmptyLayout emptyLayout;
    //总共页数
    private int maxPager;

    private BroadcastReceiver loginIn = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            is_load = false;
            rootView = null;
        }
    };

    public void setRecRequest(int currentPage){
        HomeApi.mblisener(currentPage, babyRecord);
    }

    private BroadcastReceiver Mre = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String act = intent.getStringExtra("act");
            int index = intent.getIntExtra("index",1);
            if(act != null && act.equals("del")){
                RecordDetails recordDetails = (RecordDetails) recordArrayList.get(index);
                if(recordDetails.getIs_first() == 1 && index != recordArrayList.size() - 1){
                    RecordDetails recordDetails2 = (RecordDetails) recordArrayList.get(index + 1);
                    if(recordDetails2.getIs_first() == 0){
                        recordDetails2.setIs_first(1);
                        recordArrayList.remove(index+1);
                        recordArrayList.add(index + 1, recordDetails2);
                    }
                }
                recordArrayList.remove(index);
                diaryHashMap.put(recordDetails.getGroup(),diaryHashMap.get(recordDetails.getGroup()) - 1);
                infoAdapter.notifyDataSetChanged();
                if (recordArrayList.size() <= 0) {
                    img_th.setVisibility(View.VISIBLE);
                } else {
                    img_th.setVisibility(View.GONE);
                }
                return;
            }
            if (recordArrayList != null) {
                recordArrayList.clear();
                MyBabyInfoFragment.diaryHashMap.clear();
                setRecRequest(1);
            }
            if (AppContext.getLoginInfo().getBaby_gender().equals("0")) {
                mBabyInfo.setText("来自 " + AppContext.getLoginInfo().getName() + " 的 " + "小萌男");
            } else {
                mBabyInfo.setText("来自 " + AppContext.getLoginInfo().getName() + " 的 " + "小萌妮");
            }
            if (AppContext.getLoginInfo().getBaby_photo().length() > 5)
                if (AppContext.getLoginInfo().getBaby_photo().toString().substring(0, 4).equals("http")) {
                    Netroid.displayImage(AppContext.getLoginInfo().getBaby_photo(), mBabyPic);
                } else {
                    mBabyPic.setImageBitmap(BitmapUtils.getCompressedImage(AppContext.getLoginInfo().getBaby_photo(), 2));
                }
            mBabyName.setText(AppContext.getLoginInfo().getBaby_nickname());
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.current_tab_index = 0;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configToolbar();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_my_baby, container, false);
            headView = inflater.inflate(R.layout.header_mybabyinfo, container, false);
            recordArrayList = new ArrayList<>();
            mBabyPic = (ImageView) headView.findViewById(R.id.baby_pic);
            mBabyPic.setOnClickListener(this);
            mBabyName = (TextView) headView.findViewById(R.id.my_baby_name);
            mBabyInfo = (TextView) headView.findViewById(R.id.my_baby_info);
            mRootView = (RecyclerView) rootView.findViewById(R.id.diary_of_baby);
            layoutManager = new LinearLayoutManager(this.getContext());
            mRootView.setHasFixedSize(true);
            mRootView.setLayoutManager(layoutManager);
            growth_tip_container = (LinearLayout) headView.findViewById(R.id.growth_tip_container);
            security_tip_container = (LinearLayout) headView.findViewById(R.id.security_tip_container);
            weather_tip_container = (LinearLayout) headView.findViewById(R.id.weather_tip_container);
            img_th = (ImageView) headView.findViewById(R.id.img_th);
            if (AppContext.getLoginInfo().getBaby_gender().equals("0")) {
                mBabyInfo.setText("来自 " + AppContext.getLoginInfo().getName() + " 的 " + "小萌男");
            } else {
                mBabyInfo.setText("来自 " + AppContext.getLoginInfo().getName() + " 的 " + "小萌妮");
            }
            infoAdapter = new BabyInfoAdapter(recordArrayList, getActivity());

            stringAdapter = new HeaderViewRecyclerAdapter(infoAdapter);
            stringAdapter.addHeaderView(headView);
            mRootView.setAdapter(stringAdapter);
            emptyLayout = (EmptyLayout) rootView.findViewById(R.id.error_layout);
            emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeApi.anxintip(anxintip);
                    setRecRequest(1);
                    if (AppContext.getLoginInfo().getBaby_photo().length() > 5) {
                        if (AppContext.getLoginInfo().getBaby_photo().toString().substring(0, 4).equals("http")) {
                            Netroid.displayImage(AppContext.getLoginInfo().getBaby_photo(), mBabyPic);
                        } else {
                            mBabyPic.setImageBitmap(BitmapUtils.getCompressedImage(AppContext.getLoginInfo().getBaby_photo(), 2));
                        }
                    }
                }
            });
            if (AppContext.getLoginInfo().getBaby_photo().length() > 5) {
                if (AppContext.getLoginInfo().getBaby_photo().toString().substring(0, 4).equals("http")) {
                    Netroid.displayImage(AppContext.getLoginInfo().getBaby_photo(), mBabyPic);
                } else {
                    mBabyPic.setImageBitmap(BitmapUtils.getCompressedImage(AppContext.getLoginInfo().getBaby_photo(), 2));
                }
            }

            setRecRequest(1);
            mBabyName.setText(AppContext.getLoginInfo().getBaby_nickname());

            IntentFilter loginFilter = new IntentFilter(Const.INTENT_ACTION_BABY);
            getActivity().registerReceiver(Mre, loginFilter);
            IntentFilter login = new IntentFilter(Const.INTENT_ACTION_LOGIN);
            getActivity().registerReceiver(loginIn, login);
            HomeApi.anxintip(anxintip);
        }

        if (rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
        String flag = AppContext.getProperty(KEY_FLAG_FIRST_LAUNCH_IMG);
        shou_img = (ImageView) rootView.findViewById(R.id.shou_img);
        shou_img.setOnClickListener(this);
        if (TextUtils.isEmpty(flag) || flag.equals("true")) {
            shou_img.setVisibility(View.VISIBLE);
        } else {
            shou_img.setVisibility(View.GONE);
        }
        return rootView;
    }

    private Listener<JSONObject> anxintip = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            try {
                if (response.getString("status").equals("1")) {
                    JSONArray jsonArray = response.getJSONArray("data");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    try {
                        if (jsonObject.getJSONArray("data").length() == 0) {

                        }
                    } catch (JSONException e) {
                        growth_tip(jsonObject.getJSONObject("data"));
                    }
                    safetytips(jsonArray.getJSONObject(1).getJSONArray("data"));
                    weather_tip(jsonArray.getJSONObject(2).getJSONObject("data"));
                } else {
                    AppContext.showToast("数据加载失败。");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
    };
    private Listener<JSONObject> babyRecord = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            is_load = false;
        }

        @Override
        public void onError(NetroidError error) {
            super.onError(error);
            emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }

        @Override
        public void onSuccess(JSONObject response) {
            try {
                if (response.getString("status").equals("1")) {
                    JSONObject jsonObject = response.getJSONObject("data");
                    pagers = jsonObject.getInt("page");
                    maxPager = jsonObject.getInt("max_page");
                    if(maxPager == 1){
                        removeFooterView();
                    }
                    if(mPageNo < maxPager){
                        createLoadMoreView();
                    }

                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    if(jsonArray.length() == 0){
                        removeFooterView();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject reJson = jsonArray.getJSONObject(i);
                        JSONArray rearray = reJson.getJSONArray("data");
                        String group = reJson.getString("dategroup");
                        if(!diaryHashMap.containsKey(group)){
                            diaryHashMap.put(group,0);
                        }else{
                            diaryHashMap.put(group,diaryHashMap.get(group) + rearray.length());
                        }
                        for (int j = 0; j < rearray.length(); j++) {
                            int is_first = 0;
                            diaryHashMap.put(group, diaryHashMap.get(group) + 1);
                            if(j == 0 && diaryHashMap.get(group).toString().equals("1")){
                                is_first = 1;
                            }
                            recordArrayList.add(RecordDetails.parse(rearray.getJSONObject(j),is_first));
                        }
                    }

                    if (recordArrayList.size() <= 0) {
                        img_th.setVisibility(View.VISIBLE);
                    } else {
                        img_th.setVisibility(View.GONE);
                    }
                    is_load = true;
                    mPageNo++;
                } else {
                    AppContext.showToast("数据加载错误。");
                }
                infoAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    };

    private void configToolbar() {
        Toolbar toolbar = ((BaseActivity) getActivity()).getToolBar();
        AQuery aQuery = new AQuery(getActivity(), toolbar);
        aQuery.id(R.id.top_toolbar).visible();
        aQuery.id(R.id.toolbar_right_img).visible().image(R.mipmap.icon_camera).clicked(this);
        aQuery.id(R.id.toolbar_logo_img).gone();
        aQuery.id(R.id.toolbar_right_img1).gone();
        aQuery.id(R.id.toolbar_right_img2).gone();
        aQuery.id(R.id.toolbar_title_text).visible().text("萌宝");
        ((MainActivity) getActivity()).setTitleColor_(R.color.main_kin);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.shou_img:
                shou_img.setVisibility(View.GONE);
                AppContext.setProperty(KEY_FLAG_FIRST_LAUNCH_IMG, "false");
                break;
            case R.id.baby_pic:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.UPDATEBABY);
                break;
            case R.id.toolbar_right_img:
                //UIHelper.showSimpleBack(getContext(), SimpleBackPage.UPRECORD);
                Intent intent = new Intent(getContext(), DynamicPost.class);
                startActivity(intent);
                break;
        }
    }

    private void growth_tip(final JSONObject jsonObject) throws JSONException {
        if (jsonObject.length() == 0) {
            return;
        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_growth_tip, growth_tip_container, false);
        TextView content = (TextView) view.findViewById(R.id.growth_tip_info);
        content.setText(jsonObject.getString("summary"));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebVaccineFragment.class);
                try {
                    intent.putExtra("url", jsonObject.getString("url"));
                    intent.putExtra("action_bar_title", R.string.text_grow_info);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
        growth_tip_container.addView(view);
    }

    private void weather_tip(final JSONObject jsonObject) throws JSONException {
        if (jsonObject.length() == 0) {
            return;
        }
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_weather_tip, weather_tip_container, false);
        TextView content = (TextView) view.findViewById(R.id.weather_tip_info);
        content.setText(jsonObject.getString("content"));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebVaccineFragment.class);
                try {
                    intent.putExtra("url", jsonObject.getString("url"));
                    intent.putExtra("action_bar_title", R.string.text_weather_info);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intent);
            }
        });
        weather_tip_container.addView(view);
    }

    private void safetytips(JSONArray jsonArray) throws JSONException {
        if (jsonArray.length() != 0) {
            security_tip_container.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            final JSONObject object = jsonArray.getJSONObject(i);
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_security_tip, security_tip_container, false);
            TextView vaccine_name = (TextView) view.findViewById(R.id.vaccine_name);
            TextView vaccine_free = (TextView) view.findViewById(R.id.vaccine_free);
            TextView vaccine_useful = (TextView) view.findViewById(R.id.vaccine_useful);
            TextView inoculate_time = (TextView) view.findViewById(R.id.inoculate_time);
            vaccine_name.setText(object.getString("title"));
            vaccine_free.setText(object.getString("free"));
            vaccine_useful.setText(object.getString("usefull"));

            inoculate_time.setText("接种时间：" + object.getString("act_day") + "" + ((object.getString("act_week").equals("")) ? "" : "  " + object.getString("act_week")));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WebVaccineFragment.class);
                    try {
                        intent.putExtra("url", object.getString("url") + "/android");
                        intent.putExtra("action_bar_title", R.string.text_anxin_info);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            });
            security_tip_container.addView(view);
        }
    }
}
