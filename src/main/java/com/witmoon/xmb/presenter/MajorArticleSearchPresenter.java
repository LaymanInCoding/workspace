package com.witmoon.xmb.presenter;

import com.witmoon.xmb.model.MajorArticle;
import com.witmoon.xmb.services.ArticleService;
import com.witmoon.xmb.views.MajorArticleSearchActivity;

import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MajorArticleSearchPresenter {
    MajorArticleSearchActivity mView;
    ArticleService mService;

    public MajorArticleSearchPresenter(MajorArticleSearchActivity view, ArticleService service) {
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
                        mView.setOnError();
                    }

                    @Override
                    public void onNext(List<MajorArticle> articles) {
                        mView.setOnNext(articles);
                    }
                });
    }
}
