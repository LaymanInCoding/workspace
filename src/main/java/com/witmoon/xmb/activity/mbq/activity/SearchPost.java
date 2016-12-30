package com.witmoon.xmb.activity.mbq.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.witmoon.xmb.activity.common.SearchActivity;
import com.witmoon.xmb.activity.goods.SearchResultListActivity;
import com.witmoon.xmb.activity.mbq.adapter.PostAdapter;
import com.witmoon.xmb.api.CircleApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.circle.CircleCategory;
import com.witmoon.xmb.model.circle.CirclePost;
import com.witmoon.xmb.ui.FlowTagLayout;
import com.witmoon.xmb.ui.TagAdapter;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.SharedPreferencesUtil;
import com.witmoon.xmb.util.SystemBarTintManager;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.EndlessRecyclerOnScrollListener;
import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;


public class SearchPost extends AppCompatActivity {
    private EndlessRecyclerOnScrollListener recyclerViewScrollListener;
    private LinearLayoutManager layoutManager;
    private HeaderViewRecyclerAdapter stringAdapter;
    private EmptyLayout mEmptyLayout;
    private PostAdapter adapter;
    private ArrayList<CirclePost> mDatas = new ArrayList<>();
    private EditText searchText;
    private int page = 1;
    private String keyword = "";
    private FlowTagLayout mBGAFlowLayout;
    private TagAdapter mAdapter;
    private View tagContainer;
    private RecyclerView mRootView;
    private ArrayList<String> search_keywords = new ArrayList<>();


    private void createLoadMoreView() {
        removeFooterView();
        View loadMoreView = LayoutInflater
                .from(this)
                .inflate(R.layout.view_load_more, mRootView, false);
        stringAdapter.addFooterView(loadMoreView);
    }

    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public void setTitleColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintDrawable(getResources().getDrawable(R.drawable.bg_status));
        }
    }

    protected void removeFooterView() {
        stringAdapter.removeFooterView();
        mRootView.removeOnScrollListener(recyclerViewScrollListener);
    }

    protected void resetStatus() {
        mRootView.removeOnScrollListener(recyclerViewScrollListener);
        recyclerViewScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                setRecRequest(currentPage);
            }
        };
        mRootView.addOnScrollListener(recyclerViewScrollListener);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mbq_post_search);
        initialize();
        setTitleColor();
    }

    private void initialize() {
        tagContainer = findViewById(R.id.container);
        View cancelView = findViewById(R.id.toolbar_right_text);
        cancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mEmptyLayout = (EmptyLayout) findViewById(R.id.empty_layout);
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
        Logger.e(search_keywords.toString());
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
                startActivity(intent);
            }
        });
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        mRootView.setAdapter(stringAdapter);
        mRootView.setVisibility(View.GONE);
        getKeyWord();
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

    public void setRecRequest(int page0) {
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


}
