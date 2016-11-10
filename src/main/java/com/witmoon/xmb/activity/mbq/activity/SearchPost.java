package com.witmoon.xmb.activity.mbq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mbq.adapter.PostAdapter;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.circle.CircleCategory;
import com.witmoon.xmb.model.circle.CirclePost;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;


public class SearchPost extends BaseActivity {

    private EmptyLayout emptyLayout;
    private PostAdapter adapter;
    private ArrayList<CirclePost> mDatas = new ArrayList<>();
    private EditText searchText;
    private int page = 1;
    private String keyword = "";
    private int[] search_text = new int[]{R.id.search_text1,R.id.search_text2,R.id.search_text3,R.id.search_text4,R.id.search_text5,R.id.search_text6,R.id.search_text7,R.id.search_text8,R.id.search_text9,R.id.search_text10,R.id.search_text11,R.id.search_text12,R.id.search_text13,R.id.search_text14,R.id.search_text15};

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_mbq_post_search;
    }

    @Override
    protected void  initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        View cancelView = findViewById(R.id.cancel_btn);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity();
            }
        });
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        searchText = (EditText) findViewById(R.id.search);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyword = v.getText().toString().trim();
                    mDatas.clear();
                    page = 1;
                    setRecRequest(page);
                    InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                }
                return false;
            }
        });
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new PostAdapter(mDatas,this);
        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(SearchPost.this, PostDetailActivity.class);
                intent.putExtra("post_id", mDatas.get(position).getPost_id());
                startActivity(intent);
            }
        });
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        mRootView.setAdapter(stringAdapter);
        mRootView.setVisibility(View.GONE);

        String hot_keywords = (String) SharedPreferencesUtil.get(this, Const.MBQ_SEARCH_HOT_KEYWORD,"");
        ArrayList<String> search_keywords = new ArrayList<>();
        if (hot_keywords != null){
            try {
                JSONArray jsonArray = new JSONArray(hot_keywords);
                for(int i =0; i < jsonArray.length(); i++){
                    search_keywords.add(jsonArray.getString(i));
                }
            } catch (JSONException e) {

            }
        }
        for(int i = 0; i < search_text.length; i++){
            TextView textView = (TextView) findViewById(search_text[i]);
            if (textView!=null){
                if(i < search_keywords.size()){
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(search_keywords.get(i));
                }else{
                    textView.setVisibility(View.INVISIBLE);
                }
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        keyword = ((TextView) v).getText().toString();
                        searchText.setText(keyword);
                        searchText.setSelection(searchText.length());
                        mDatas.clear();
                        page = 1;
                        setRecRequest(page);
                    }
                });
            }else{
                Log.e("error","1111");
            }
        }
    }

    @Override
    public void setRecRequest(int page0){
        if (keyword.equals("")){
            CommonUtil.show(this, "请输入关键字", 1000);
            return;
        }
        CircleApi.search_post(keyword, page, new Listener<JSONObject>() {
            @Override
            public void onPreExecute() {
                if(page == 1){
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                }
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mDatas.add(CirclePost.parse(jsonArray.getJSONObject(i)));
                    }
                    mRootView.setVisibility(View.VISIBLE);
                    if(jsonArray.length() < 20){
                        removeFooterView();
                    }else{
                        createLoadMoreView();
                        resetStatus();
                    }
                    page += 1;
                    adapter.notifyDataSetChanged();
                    if (mDatas.size() == 0) {
                        emptyLayout.setErrorType(EmptyLayout.NODATA);
                    } else {
                        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    }
                } catch (JSONException e) {
                    emptyLayout.setErrorType(EmptyLayout.NODATA);
                }
            }

            @Override
            public void onError(NetroidError error) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
        });
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
