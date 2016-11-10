package com.witmoon.xmb.presenter;

import com.witmoon.xmb.model.mbq.MbqArticleCollectResponse;
import com.witmoon.xmb.services.ArticleService;
import com.witmoon.xmb.util.XmbUtils;
import com.witmoon.xmb.views.MajorArticleDetailActivity;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MajorArticleDetailPresenter {
    MajorArticleDetailActivity mView;
    ArticleService mService;

    public MajorArticleDetailPresenter(MajorArticleDetailActivity view, ArticleService service) {
        mView = view;
        mService = service;
    }

    public void collect_handler(HashMap<String,String> hashMap) {
        mService.getApi()
                .collect(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MbqArticleCollectResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.setOnError();
                    }

                    @Override
                    public void onNext(MbqArticleCollectResponse response) {
                        XmbUtils.showMessage(mView,response.info);
                        mView.setCollectStatus(response.status);
                    }
                });
    }

    public void check_collect(HashMap<String,String> hashMap) {
        mService.getApi()
                .check_collect(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MbqArticleCollectResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.setOnError();
                    }

                    @Override
                    public void onNext(MbqArticleCollectResponse response) {
                        mView.setCollectStatus(response.status);
                    }
                });
    }
}
