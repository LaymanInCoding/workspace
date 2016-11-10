package com.witmoon.xmb.presenter;

import android.util.Log;

import com.witmoon.xmb.model.MajorArticle;
import com.witmoon.xmb.services.ArticleService;
import com.witmoon.xmb.views.MajorArticleActivity;

import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MajorArticlePresenter {
    MajorArticleActivity mView;
    ArticleService mService;

    public MajorArticlePresenter(MajorArticleActivity view, ArticleService service) {
        mView = view;
        mService = service;
    }

    public void get_article_list(HashMap<String,String> hashMap) {
        mService.getApi()
                .article_list(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<MajorArticle>>() {
                    @Override
                    public void onCompleted() {
                        mView.setOnCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("error",e.getMessage());
                        mView.setOnError();
                    }

                    @Override
                    public void onNext(List<MajorArticle> articles) {
                        mView.setOnNext(articles);
                    }
                });
    }
}
