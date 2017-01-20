package com.witmoon.xmb.activity.service;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.goods.SearchResultListActivity;
import com.witmoon.xmb.activity.mbq.activity.SearchPost;
import com.witmoon.xmb.activity.service.adapter.SubAdapter;
import com.witmoon.xmb.api.ServiceApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.service.Shop;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * Created by de on 2017/1/4.
 */
public class ServiceSearchResultActivity extends BaseActivity {

    private String mKeywords;
    private int page = 1;
    private SubAdapter adapter;
    private EmptyLayout emptyLayout;
    private ArrayList<JSONObject> mArrayList = new ArrayList<>();

    public static void start(Context context, String keywords) {
        Intent intent = new Intent(context, ServiceSearchResultActivity.class);
        intent.putExtra("TYPE_VALUE", keywords);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_service_result;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.master_shopping_cart);
        mKeywords = getIntent().getStringExtra("TYPE_VALUE");
        EditText editText = (EditText) findViewById(R.id.edit_text);
        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        im.hideSoftInputFromWindow(editText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        editText.setText(mKeywords);
        editText.setSelection(mKeywords.length());
        mRootView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(this);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new SubAdapter(mArrayList, this);
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        mRootView.setAdapter(stringAdapter);
        setRecRequest(1);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecRequest(1);
            }
        });
    }

    @Override
    public void setRecRequest(int currentPage) {
//        ServiceApi.searchService(page, mKeywords, listener);
    }

    private Listener<JSONObject> listener = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            AppContext.instance().getXmbDB().service_search_insert(mKeywords);
            Logger.json(response.toString());
            try {
                JSONArray jsonArray = response.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    jsonObject.put("index", i + mArrayList.size());
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

            }
            mRootView.setVisibility(View.VISIBLE);
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
    };
}
