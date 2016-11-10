package com.witmoon.xmb.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.witmoon.xmb.R;
import com.witmoon.xmb.adapter.MajorArticleAdapter;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.MajorArticle;
import com.witmoon.xmb.presenter.MajorCollectPresenter;
import com.witmoon.xmb.services.ArticleService;
import com.witmoon.xmb.services.VoiceService;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import java.util.ArrayList;
import java.util.List;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;


public class CollectArticleActivity extends BaseActivity {
    ArticleService mService;
    MajorCollectPresenter mPresenter;
    EmptyLayout emptyLayout;
    ArrayList<MajorArticle> mDatas = new ArrayList<>();
    MajorArticleAdapter adapter;
    int page = 1;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_major_article;
    }

    @Override
    protected int getActionBarTitleByResId() {
        return R.string.collect_article;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        initRecycleView();
    }

    public void setOnCompleted() {
        //emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    public void setRecRequest(int currentPage) {
        mPresenter.get_collect_list(ArticleService.gen_collect_list_params(page));
    }


    public void setOnError() {
        emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
    }

    public void setOnNext(List<MajorArticle> articles) {
        if (articles.size() < 20) {
            removeFooterView();
        } else {
            createLoadMoreView();
            resetStatus();
        }
        if (articles.size() == 0 && page == 1) {
            emptyLayout.setErrorType(EmptyLayout.NODATA);
        } else {
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
        page += 1;
        mDatas.addAll(articles);
        stringAdapter.notifyDataSetChanged();
    }

    protected void initRecycleView() {
        mService = new ArticleService();
        mPresenter = new MajorCollectPresenter(this, mService);
        mRootView = (RecyclerView) findViewById(R.id.recycleView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new MajorArticleAdapter(mDatas, this);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setOnLayoutClickListener((View v) -> mPresenter.get_article_list(VoiceService.gen_voice_list_params(page)));
        adapter.setOnItemClickListener((int position) -> {
            Intent intent = new Intent(CollectArticleActivity.this, MajorArticleDetailActivity.class);
            intent.putExtra("id", mDatas.get(position).id);
            intent.putExtra("title", mDatas.get(position).title);
            intent.putExtra("desc", mDatas.get(position).abstract_text);
            intent.putExtra("url", mDatas.get(position).url);
            startActivity(intent);
        });
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        mRootView.setAdapter(stringAdapter);
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mPresenter.get_collect_list(ArticleService.gen_collect_list_params(page));
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
