package com.witmoon.xmb.activity.common;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.adapter.Search_adapter;
import com.witmoon.xmb.activity.goods.SearchResultListActivity;
import com.witmoon.xmb.api.CommonApi;
import com.witmoon.xmb.ui.FlowTagLayout;
import com.witmoon.xmb.ui.TagAdapter;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.SystemBarTintManager;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;

/**
 * 搜索界面
 * Created by zhyh on 2015/8/6.
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mSearchEdit;
    private TextView mSearch_no;
    private ArrayList<String> mString = new ArrayList<>();
    private Context mContext = SearchActivity.this;
    private AppContext mAppContext = AppContext.instance();
    private ArrayList<String> mList;
    private LinearLayout mLinearLayout;
    int index = 0;
    private RecyclerView mLinearListView;
    //  RadioGroup ----
    private FlowTagLayout mBGAFlowLayout;
    private TagAdapter mAdapter;
    private Search_adapter adapter;
    private EmptyLayout mEmptyLayout;

    private HeaderViewRecyclerAdapter adapterWrapper;
    private View footerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initialize();
        setTitleColor();
    }

    public void setTitleColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintDrawable(getResources().getDrawable(R.drawable.bg_status));
        }
    }

    //获取高度
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

    private void initialize() {
        mEmptyLayout = (EmptyLayout) findViewById(R.id.empty_layout);
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mBGAFlowLayout = (FlowTagLayout) findViewById(R.id.search_BGAFlowLayout);
        mAdapter = new TagAdapter(this, mString);
        mBGAFlowLayout.setAdapter(mAdapter);
        mBGAFlowLayout.setOnTagClickListener(((parent, view, position) -> {
            View tagView = parent.getAdapter().getView(position, null, null);
            String tag = (String) tagView.getTag();
            SearchResultListActivity.start(SearchActivity.this, tag.trim());
        }));
        mBGAFlowLayout.setTagCheckedMode(FlowTagLayout.FLOW_TAG_CHECKED_NONE);
        mSearchEdit = (EditText) findViewById(R.id.edit_text);
        mSearchEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //防止搜索事件响应两次
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(SearchActivity.this.getCurrentFocus()
                                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    doSearch();
                }
                return false;
            }
        });
        mLinearLayout = (LinearLayout) findViewById(R.id.search_boom);
        mSearch_no = (TextView) findViewById(R.id.search_no);
        findViewById(R.id.toolbar_right_text).setOnClickListener(this);
        mLinearListView = (RecyclerView) findViewById(R.id.search_listView);
        initData();
        CommonApi.getSearchWord(listener);
    }

    public void initData() {
        mList = (ArrayList<String>) mAppContext.getXmbDB().search_name();
        if (mList.size() > 0) {
            adapter = new Search_adapter(mContext, mList);
            adapter.setOnItemDeleteListener(new Search_adapter.OnItemDeleteListener() {
                @Override
                public void onItemDelete(int position) {
                    mAppContext.getXmbDB().search_delete_one(mList.get(position));
                    mList.remove(position);
                    adapter.notifyDataSetChanged();
                    if (mList.size() == 0) {
                        mSearch_no.setVisibility(View.VISIBLE);
                        mLinearLayout.setVisibility(View.GONE);
                    }
                }
            });
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            mLinearListView.setLayoutManager(manager);
            adapterWrapper = new HeaderViewRecyclerAdapter(adapter);
            footerView = LayoutInflater.from(this).inflate(R.layout.search_footer_layout, mLinearListView, false);
            footerView.setOnClickListener(v -> {
                AppContext.instance().getXmbDB().search_delete();
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

    public void doSearch() {
        String keyword = mSearchEdit.getText().toString();
        if (TextUtils.isEmpty(keyword)) {
            XmbUtils.showMessage(mContext, "请输入搜索关键字");
        } else {
            SearchResultListActivity.start(this, keyword);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.toolbar_right_text:
                finish();
                break;
        }
    }

    Listener<JSONObject> listener = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            try {
                if (response.has("keywords")) {
                    JSONArray list = response.getJSONArray("keywords");
                    for (int i = 0; i < list.length(); i++) {
                        String word = list.getString(i);
                        mString.add(word);
                    }
                    mAdapter.notifyDataSetChanged();
                }
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