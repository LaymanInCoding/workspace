package com.witmoon.xmb.activity.common;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.adapter.Search_adapter;
import com.witmoon.xmb.activity.service.adapter.SubAdapter;
import com.witmoon.xmb.api.CommonApi;
import com.witmoon.xmb.api.ServiceApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.ui.FlowTagLayout;
import com.witmoon.xmb.ui.TagAdapter;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by de on 2017/1/4.
 */
public class ServiceSearchActivity extends BaseActivity {
    private EditText mSearchEdit;
    private TextView mSearch_no;
    private ArrayList<String> mString = new ArrayList<>();
    private ArrayList<JSONObject> mArrayList = new ArrayList<>();
    private Context mContext = ServiceSearchActivity.this;
    private AppContext mAppContext = AppContext.instance();
    private ArrayList<String> mList;
    private LinearLayout mLinearLayout;
    private RecyclerView mLinearListView;
    private FlowTagLayout mBGAFlowLayout;
    private TagAdapter mAdapter;
    private Search_adapter adapter;
    private EmptyLayout mEmptyLayout;
    private View search_container;
    private View sort_container;
    private int page = 1;
    private String mKeywords = "";
    private String sort = "normal";
    private SubAdapter searchAdapter;
    private TextView sort_normal, sort_new, sort_hot;
    private View no_service_container;
    private TextView more_service_tv;


    private HeaderViewRecyclerAdapter adapterWrapper;
    private View footerView;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_service_search;
    }


    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.master_me);
        no_service_container = findViewById(R.id.no_service_container);
        more_service_tv = (TextView) no_service_container.findViewById(R.id.more_service_tv);
        more_service_tv.setOnClickListener(this);
        sort_normal = (TextView) findViewById(R.id.sort_normal);
        sort_normal.setOnClickListener(this);
        sort_new = (TextView) findViewById(R.id.sort_new);
        sort_new.setOnClickListener(this);
        sort_hot = (TextView) findViewById(R.id.sort_hot);
        sort_hot.setOnClickListener(this);
        mRootView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        searchAdapter = new SubAdapter(mArrayList, this);
        stringAdapter = new HeaderViewRecyclerAdapter(searchAdapter);
        mRootView.setAdapter(stringAdapter);
        search_container = findViewById(R.id.search_layout);
        sort_container = findViewById(R.id.sort_container);
        mEmptyLayout = (EmptyLayout) findViewById(R.id.empty_layout);
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mEmptyLayout.setOnClickListener(v -> setRecRequest(1));
        mBGAFlowLayout = (FlowTagLayout) findViewById(R.id.search_BGAFlowLayout);
        mAdapter = new TagAdapter(this, mString);
        mBGAFlowLayout.setAdapter(mAdapter);
        mLinearLayout = (LinearLayout) findViewById(R.id.search_boom);
        mSearch_no = (TextView) findViewById(R.id.search_no);
        findViewById(R.id.toolbar_right_text).setOnClickListener(this);
        mLinearListView = (RecyclerView) findViewById(R.id.search_listView);
        mSearchEdit = (EditText) findViewById(R.id.edit_text);
        mBGAFlowLayout.setOnTagClickListener(((parent, view, position) -> {
            View tagView = parent.getAdapter().getView(position, null, null);
            mKeywords = (String) tagView.getTag();
            mSearchEdit.setText(mKeywords);
            mSearchEdit.setSelection(mKeywords.length());
            page = 1;
            requestSearch(mKeywords, sort, page);
        }));
        mBGAFlowLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_NONE);
        mSearchEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //防止搜索事件响应两次
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(ServiceSearchActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    sort = "normal";
                    doSearch();
                }
                return false;
            }
        });
        initData();
        setRecRequest(1);
    }

    @Override
    public void setRecRequest(int currentPage) {
        CommonApi.getServiceSearch(listener);
    }

    public void initData() {
        mList = (ArrayList<String>) mAppContext.getXmbDB().search_service();
        if (mList.size() > 0) {
            adapter = new Search_adapter(mContext, mList, true);
            adapter.setOnItemDeleteListener(new Search_adapter.OnItemDeleteListener() {
                @Override
                public void onItemDelete(int position) {
                    mAppContext.getXmbDB().search_delete_oneservice(mList.get(position));
                    mList.remove(position);
                    adapter.notifyDataSetChanged();
                    if (mList.size() == 0) {
                        mSearch_no.setVisibility(View.VISIBLE);
                        mLinearLayout.setVisibility(View.GONE);
                    }
                }
            });
            adapter.setOnItemClickListener(new Search_adapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    mArrayList.clear();
                    mKeywords = mList.get(position);
                    mSearchEdit.setText(mKeywords);
                    mSearchEdit.setSelection(mKeywords.length());
                    page = 1;
                    requestSearch(mKeywords, sort, page);
                }
            });
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            mLinearListView.setLayoutManager(manager);
            adapterWrapper = new HeaderViewRecyclerAdapter(adapter);
            footerView = LayoutInflater.from(this).inflate(R.layout.search_footer_layout, mLinearListView, false);
            footerView.setOnClickListener(v -> {
                AppContext.instance().getXmbDB().search_delete_service();
                AppContext.showToast("清除成功！");
                initData();
            });
            adapterWrapper.addFooterView(footerView);
            mLinearListView.setAdapter(adapterWrapper);
            mSearch_no.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mSearch_no.setVisibility(View.VISIBLE);
            mLinearLayout.setVisibility(View.GONE);
        }
    }

    public void requestSearch(String keywords, String sort, int page) {
        search_container.setVisibility(View.GONE);
        ServiceApi.searchService(page, keywords, sort, searchListener);
    }

    private Listener<JSONObject> searchListener = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            AppContext.instance().getXmbDB().service_search_insert(mKeywords);
            try {
                JSONArray jsonArray = response.getJSONArray("data");
                if (jsonArray.length() == 0 && page == 1) {
                    removeFooterView();
                    no_service_container.setVisibility(View.VISIBLE);
                    sort_container.setVisibility(View.GONE);
                } else {
                    sort_container.setVisibility(View.VISIBLE);
                    no_service_container.setVisibility(View.GONE);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        mArrayList.add(jsonObject);
                    }
                    if (jsonArray.length() < 20) {
                        createNoMoreView();
                    } else {
                        createLoadMoreView();
                        resetStatus();
                    }
                    page += 1;
                    stringAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {

            }
            mRootView.setVisibility(View.VISIBLE);
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
    };

    public void doSearch() {
        mKeywords = mSearchEdit.getText().toString();
        if (TextUtils.isEmpty(mKeywords)) {
            XmbUtils.showMessage(mContext, "请输入搜索关键字");
        } else {
            sort_normal.setTextColor(Color.parseColor("#d66363"));
            sort_new.setTextColor(Color.parseColor("#3d3d3e"));
            sort_hot.setTextColor(Color.parseColor("#3d3d3e"));
            mArrayList.clear();
            page = 1;
            mSearchEdit.setText(mKeywords);
            mSearchEdit.setSelection(mKeywords.length());
            requestSearch(mKeywords, sort, page);
        }
    }

    public void sort_request(String sort) {
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mArrayList.clear();
        searchAdapter.notifyDataSetChanged();
        page = 1;
        requestSearch(mKeywords, sort, page);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_right_text:
                finish();
                break;
            case R.id.sort_normal:
                sort_normal.setTextColor(Color.parseColor("#d66363"));
                sort_new.setTextColor(Color.parseColor("#3d3d3e"));
                sort_hot.setTextColor(Color.parseColor("#3d3d3e"));
                sort_request("normal");
                break;
            case R.id.sort_new:
                sort_new.setTextColor(Color.parseColor("#d66363"));
                sort_normal.setTextColor(Color.parseColor("#3d3d3e"));
                sort_hot.setTextColor(Color.parseColor("#3d3d3e"));
                sort_request("new");
                break;
            case R.id.sort_hot:
                sort_hot.setTextColor(Color.parseColor("#d66363"));
                sort_new.setTextColor(Color.parseColor("#3d3d3e"));
                sort_normal.setTextColor(Color.parseColor("#3d3d3e"));
                sort_request("hot");
                break;
            case R.id.more_service_tv:
                finish();
                break;
        }
    }

    Listener<JSONObject> listener = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            try {
                JSONArray list = response.getJSONArray("data");
                for (int i = 0; i < list.length(); i++) {
                    String word = list.getString(i);
                    mString.add(word);
                }
                mAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}


