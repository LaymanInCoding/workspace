package com.witmoon.xmb.activity.mbq.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mbq.adapter.CircleAdapter;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.circle.CircleCategory;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchCircle extends BaseActivity {

    private EditText searchText;
    private TextView cancelView;
    private EmptyLayout emptyLayout;
    private CircleAdapter adapter;
    private ArrayList<CircleCategory> mDatas = new ArrayList<>();
    private Activity activity = this;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_mbq_circle_search;
    }

    @Override
    protected void  initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        IntentFilter refresh = new IntentFilter(Const.INTENT_ACTION_REFRESH_MY_MBQ_SEARCH);
        registerReceiver(refreshSearch, refresh);
        cancelView = (TextView) findViewById(R.id.cancel_btn);
        if (cancelView!=null) {
            cancelView.setOnClickListener(this);
        }
        searchText = (EditText) findViewById(R.id.search);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    requestData(v.getText().toString().trim());
                    InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                }
                return false;
            }
        });

        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new CircleAdapter(mDatas,this);
        adapter.setOnItemClickListener(new CircleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CircleCategory circleCategory) {
                Intent intent = new Intent(SearchCircle.this, CircleActivity.class);
                intent.putExtra("circle_id", circleCategory.getCircle_id());
                intent.putExtra("circle_logo",circleCategory.getCircle_logo());
                intent.putExtra("circle_name",circleCategory.getCircle_name());
                intent.putExtra("circle_post_cnt",circleCategory.getCircle_post_cnt() + "个话题");
                intent.putExtra("circle_is_join",circleCategory.getUser_is_join());
                startActivity(intent);
            }

            @Override
            public void onItemButtonClick(int circle_id) {
                XmbUtils.joinCircle(SearchCircle.this, circle_id, new Listener<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            if (response.getInt("status") == 1) {
                                if (XmbUtils.popupWindow != null && XmbUtils.popupWindow.isShowing()) {
                                    XmbUtils.popupWindow.dismiss();
                                }
                                XmbUtils.showMessage(SearchCircle.this, response.getString("info"));
                                Intent intent = new Intent(Const.INTENT_ACTION_REFRESH_MY_MBQ);
                                sendBroadcast(intent);
                            } else {
                                XmbUtils.showMessage(SearchCircle.this, response.getString("info"));
                            }
                        } catch (JSONException e) {
                            XmbUtils.showMessage(SearchCircle.this, "网络异常，请稍后重试！");
                        }
                    }
                });
            }
        });
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        if (emptyLayout!=null)emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        mRootView.setAdapter(adapter);
    }

    private BroadcastReceiver refreshSearch = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            adapter.notifyDataSetChanged();
        }
    };

    public void requestData(String keyword){
        if (keyword.equals("")){
            CommonUtil.show(this, "请输入关键字", 1000);
            return;
        }
        CircleApi.search_circle(keyword, AppContext.getLoginUid(), new Listener<JSONObject>() {
            @Override
            public void onPreExecute() {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
            }

            @Override
            public void onSuccess(JSONObject response) {
                mDatas.clear();
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mDatas.add(CircleCategory.parse(jsonArray.getJSONObject(i)));
                    }
                    adapter.notifyDataSetChanged();
                    if (mDatas.size() == 0){
                        emptyLayout.setErrorType(EmptyLayout.NODATA);
                    }else{
                        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    }
                } catch (JSONException e) {
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
                }
            }

            @Override
            public void onError(NetroidError error) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
        });
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.cancel_btn:
                finishActivity();
                break;
        }
    }

    public boolean onKeyUp(int keyCode,KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            finishActivity();
            return true;
        }
        return false;
    }

    private void finishActivity(){
        finish();
        overridePendingTransition(R.anim.pop_right_in, R.anim.pop_right_out);
    }

}
