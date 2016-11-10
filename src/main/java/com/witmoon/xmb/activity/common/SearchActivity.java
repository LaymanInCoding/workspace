package com.witmoon.xmb.activity.common;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.adapter.Search_adapter;
import com.witmoon.xmb.activity.goods.SearchResultListActivity;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.ui.SearChMyView.BGAFlowLayout;
import com.witmoon.xmblibrary.linearlistview.LinearListView;
import com.witmoon.xmblibrary.linearlistview.listener.OnItemClickListener;

import java.util.ArrayList;

/**
 * 搜索界面
 * Created by zhyh on 2015/8/6.
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private EditText mSearchEdit;
    private TextView mSearch_no;
    private String[] mString = {"童车", "哈罗闪", "汤美天地", "花王", "纸尿裤", "奶粉", "美德乐", "跨境购"};
    private int[] mColors = {Color.parseColor("#7ECEF4"), Color.parseColor("#84CCC9"), Color.parseColor("#88ABDA"), Color.parseColor("#7DC1DD"), Color.parseColor("#B6B8DE")};
    private Context mContext = SearchActivity.this;
    private AppContext mAppContext = AppContext.instance();
    private ArrayList<String> mList;
    private LinearLayout mLinearLayout;
    int index = 0;
    private LinearListView mLinearListView;
    //  RadioGroup ----
    private BGAFlowLayout mBGAFlowLayout;

    @Override
    protected int getActionBarTitleByResId() {
        return R.string.text_add_to_shopping_search;
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        setTitleColor_(R.color.master_shopping_cart);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        findViewById(R.id.submit_button).setOnClickListener(this);
        mBGAFlowLayout = (BGAFlowLayout) findViewById(R.id.search_BGAFlowLayout);
        mSearchEdit = (EditText) findViewById(R.id.edit_text_2);
        mLinearLayout = (LinearLayout) findViewById(R.id.search_boom);
        mSearch_no = (TextView) findViewById(R.id.search_no);
        findViewById(R.id.delete_search).setOnClickListener(this);
        mLinearListView = (LinearListView) findViewById(R.id.search_listView);
        for (int i = 0; i < mString.length; i++) {
            mBGAFlowLayout.addView(getLabel(mString[i]), new ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT));
        }
        initData();
    }

    public void initData() {
        mList = (ArrayList<String>) mAppContext.getXmbDB().search_name();
        if (mList.size() > 0) {
            mLinearListView.setLinearAdapter(new Search_adapter(mContext, mList));
            mSearch_no.setVisibility(View.GONE);
            mLinearLayout.setVisibility(View.VISIBLE);
        } else {
            mSearch_no.setVisibility(View.VISIBLE);
            mLinearLayout.setVisibility(View.GONE);
        }

        mLinearListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(LinearListView parent, View view, int position, long id) {
                SearchResultListActivity.start(SearchActivity.this, mAppContext.getXmbDB().search_name().get(position));
            }
        });
    }

    private TextView getLabel(String text) {
        final RadioButton label = new RadioButton(this);
        label.setTextColor(Color.WHITE);
        label.setGravity(Gravity.CENTER);
        label.setTag(1);
        label.setButtonDrawable(android.R.color.transparent);
        //防止下标溢出
        index++;
        if (index == mColors.length) {
            index = 0;
        }
        label.setBackgroundResource(R.drawable.bg_rounded_grey_border);
        label.setBackgroundColor(mColors[index]);
        label.setSingleLine(true);
        label.setEllipsize(TextUtils.TruncateAt.END);
        int padding = BGAFlowLayout.dp2px(this, 5);
        label.setPadding(padding, padding, padding, padding);
        label.setText(text);
        //实例的事件
        label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchResultListActivity.start(SearchActivity.this, label.getText().toString().trim());
            }
        });
        return label;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_search:
                mAppContext.getXmbDB().search_delete();
                AppContext.showToast("清除成功！");
                initData();
                break;
            case R.id.submit_button:
                // TODO: 2015/8/6 搜索
                String keyword = mSearchEdit.getText().toString();
                if (TextUtils.isEmpty(keyword)) {
                    AppContext.showToast("请输入搜索关键字");
                    return;
                }
                SearchResultListActivity.start(this, keyword);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}