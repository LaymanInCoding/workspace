package com.witmoon.xmb.activity.mbq.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.mbq.MbqSearchAdapter;
import com.witmoon.xmb.activity.mbq.adapter.PostAdapter;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.circle.CirclePost;
import com.witmoon.xmb.ui.FlowTagLayout;
import com.witmoon.xmb.ui.TagAdapter;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;


public class SearchPost extends BaseActivity implements MbqSearchAdapter.OnItemClickListener {

    private EmptyLayout mEmptyLayout;
    private PostAdapter adapter;
    private ArrayList<CirclePost> mDatas = new ArrayList<>();
    private EditText searchText;
    private int page = 1;
    private String keyword = "";
    private FlowTagLayout mBGAFlowLayout;
    private TagAdapter mAdapter;
    private View tagContainer;
    private ArrayList<String> search_keywords = new ArrayList<>();

    private ArrayList<String> mList = new ArrayList<>();
    private MbqSearchAdapter searchAdapter;
    private TextView mSearch_no;
    private RecyclerView mSearchRV;
    private LinearLayout mLinearLayout;
    private HeaderViewRecyclerAdapter searchAdapterWrapper;
    private View footerView;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_mbq_post_search;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        tagContainer = findViewById(R.id.container);
        View cancelView = findViewById(R.id.toolbar_right_text);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLinearLayout = (LinearLayout) findViewById(R.id.search_boom);
        mSearch_no = (TextView) findViewById(R.id.search_no);
        mEmptyLayout = (EmptyLayout) findViewById(R.id.empty_layout);
        mSearchRV = (RecyclerView) findViewById(R.id.search_listView);
        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        searchText = (EditText) findViewById(R.id.edit_text);
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //防止搜索事件响应两次
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchPost.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    keyword = searchText.getText().toString();
                    mDatas.clear();
                    page = 1;
                    setRecRequest(page);
                }
                return false;
            }
        });
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    keyword = v.getText().toString().trim();
                    mDatas.clear();
                    page = 1;
                    setRecRequest(page);
                    InputMethodManager imm = (InputMethodManager) getSystemService
                            (getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                }
                return false;
            }
        });
        mBGAFlowLayout = (FlowTagLayout) findViewById(R.id.tag_layout);
        mAdapter = new TagAdapter(this, search_keywords);
        mBGAFlowLayout.setAdapter(mAdapter);
        mBGAFlowLayout.setOnTagClickListener(((parent, view, position) -> {
            View tagView = parent.getAdapter().getView(position, null, null);
            String tag = (String) tagView.getTag();
            Logger.e(tag);
            keyword = tag;
            searchText.setText(keyword);
            searchText.setSelection(searchText.length());
            mDatas.clear();
            page = 1;
            setRecRequest(page);
        }));
        mBGAFlowLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_NONE);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new PostAdapter(mDatas, this);
        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(SearchPost.this, PostDetailActivity.class);
                intent.putExtra("post_id", mDatas.get(position).getPost_id());
                intent.putExtra("post_content", mDatas.get(position).getPost_content());
                intent.putExtra("post_title", mDatas.get(position).getPost_title());
                startActivity(intent);
            }
        });
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        mRootView.setAdapter(stringAdapter);
        mRootView.setVisibility(View.GONE);
        getKeyWord();
        initData();
    }

    private void getKeyWord() {
        String hot_keywords = (String) SharedPreferencesUtil.get(this, Const.MBQ_SEARCH_HOT_KEYWORD, "");
        if (hot_keywords != null) {
            try {
                JSONArray jsonArray = new JSONArray(hot_keywords);
                for (int i = 0; i < jsonArray.length(); i++) {
                    search_keywords.add(jsonArray.getString(i));
                }
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {

            }
        }
    }

    public void initData() {
        mList = (ArrayList<String>) AppContext.instance().getXmbDB().mbq_service();
        if (mList.size() > 0) {
            searchAdapter = new MbqSearchAdapter(this, mList);
            searchAdapter.setOnItemDeleteListener(new MbqSearchAdapter.OnItemDeleteListener() {
                @Override
                public void onItemDelete(int position) {
                    AppContext.instance().getXmbDB().search_delete_onembq(mList.get(position));
                    mList.remove(position);
                    searchAdapter.notifyDataSetChanged();
                    if (mList.size() == 0) {
                        mSearch_no.setVisibility(View.VISIBLE);
                        mLinearLayout.setVisibility(View.GONE);
                    }
                }
            });
            searchAdapter.setOnItemClickListener(this);
            LinearLayoutManager manager = new LinearLayoutManager(this);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            mSearchRV.setLayoutManager(manager);
            searchAdapterWrapper = new HeaderViewRecyclerAdapter(searchAdapter);
            mSearchRV.setAdapter(searchAdapterWrapper);
            footerView = LayoutInflater.from(this).inflate(R.layout.search_footer_layout, mSearchRV, false);
            footerView.setOnClickListener(v -> {
                AppContext.instance().getXmbDB().search_delete_allmbq();
                AppContext.showToast("清除成功！");
                initData();
            });
            searchAdapterWrapper.addFooterView(footerView);
            mSearch_no.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mSearch_no.setVisibility(View.VISIBLE);
            mLinearLayout.setVisibility(View.GONE);
        }

    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        toolbar.findViewById(R.id.toolbar_title_text).setVisibility(View.GONE);
    }

    @Override
    public void setRecRequest(int currentPage) {
        if (keyword.equals("")) {
            CommonUtil.show(this, "请输入关键字", 1000);
            return;
        }
        CircleApi.search_post(keyword, page, new Listener<JSONObject>() {
            @Override
            public void onPreExecute() {
                if (page == 1) {
                    mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                }
            }

            @Override
            public void onSuccess(JSONObject response) {
                tagContainer.setVisibility(View.GONE);
                mLinearLayout.setVisibility(View.GONE);
                mSearch_no.setVisibility(View.GONE);
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        mDatas.add(CirclePost.parse(jsonArray.getJSONObject(i)));
                    }
                    mRootView.setVisibility(View.VISIBLE);
                    if (jsonArray.length() < 20) {
                        removeFooterView();
                    } else {
                        createLoadMoreView();
                        resetStatus();
                    }
                    page += 1;
                    adapter.notifyDataSetChanged();
                    if (mDatas.size() == 0) {
                        mEmptyLayout.setErrorType(EmptyLayout.NODATA);
                    } else {
                        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    }
                    AppContext.instance().getXmbDB().mbq_search_insert(keyword);
                } catch (JSONException e) {
                    mEmptyLayout.setErrorType(EmptyLayout.NODATA);
                }
            }

            @Override
            public void onError(NetroidError error) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        keyword = mList.get(position);
        page = 1;
        setRecRequest(page);
    }

}
