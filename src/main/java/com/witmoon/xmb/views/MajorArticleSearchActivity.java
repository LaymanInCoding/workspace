package com.witmoon.xmb.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.witmoon.xmb.R;
import com.witmoon.xmb.adapter.MajorArticleAdapter;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.MajorArticle;
import com.witmoon.xmb.presenter.MajorArticleSearchPresenter;
import com.witmoon.xmb.services.ArticleService;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import java.util.ArrayList;
import java.util.List;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;


public class MajorArticleSearchActivity extends BaseActivity {

    ArticleService mService;
    MajorArticleSearchPresenter mPresenter;
    EmptyLayout emptyLayout;
    ArrayList<MajorArticle> mDatas = new ArrayList<>();
    MajorArticleAdapter adapter;
    int page = 1;
    String keyword = "";
    ImageView sorshImageView;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_mbq_article_search;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        initRecycleView();
    }

    public void setOnCompleted(){
        //emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    public void setRecRequest(int currentPage){
        mPresenter.get_article_list(ArticleService.gen_article_search_list_params(keyword,page));
    }


    public void setOnError(){
        emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
    }

    public void setOnNext(List<MajorArticle> articles){
        if(articles.size() < 20){
            removeFooterView();
        }else{
            createLoadMoreView();
            resetStatus();
        }
        if (articles.size() == 0 && page == 1){
            emptyLayout.setErrorType(EmptyLayout.NODATA);
        }else{
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
        page += 1;
        mDatas.addAll(articles);
        stringAdapter.notifyDataSetChanged();
    }

    protected void initRecycleView(){
        mService = new ArticleService();
        mPresenter = new MajorArticleSearchPresenter(this,mService);
        mRootView = (RecyclerView) findViewById(R.id.recycleView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new MajorArticleAdapter(mDatas,this);
        emptyLayout = (EmptyLayout)findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        findViewById(R.id.cancel_search).setOnClickListener((View v)->finish());
        sorshImageView = (ImageView) findViewById(R.id.sorsh);
        emptyLayout.setOnLayoutClickListener((View v)-> mPresenter.get_article_list(ArticleService.gen_article_search_list_params(keyword,page)));
        adapter.setOnItemClickListener((int position) -> {
                Intent intent = new Intent(MajorArticleSearchActivity.this,MajorArticleDetailActivity.class);
                intent.putExtra("id",mDatas.get(position).id);
                intent.putExtra("title",mDatas.get(position).title);
                intent.putExtra("desc",mDatas.get(position).abstract_text);
                intent.putExtra("url",mDatas.get(position).url);
                startActivity(intent);
            });
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        mRootView.setAdapter(stringAdapter);
        EditText searchText = (EditText) findViewById(R.id.search_text);
        if (searchText!=null){
            searchText.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
                if ((actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
                    page = 1;
                    sorshImageView.setVisibility(View.GONE);
                    emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                    keyword = v.getText().toString();
                    mPresenter.get_article_list(ArticleService.gen_article_search_list_params(keyword,page));
                }
                return false;
            });
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
