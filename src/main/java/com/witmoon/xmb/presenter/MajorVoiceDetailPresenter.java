package com.witmoon.xmb.presenter;

import com.witmoon.xmb.model.Voice;
import com.witmoon.xmb.services.VoiceService;
import com.witmoon.xmb.views.MajorVoiceDetailActivity;

import org.json.JSONObject;

import java.util.HashMap;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MajorVoiceDetailPresenter {
    MajorVoiceDetailActivity mView;
    VoiceService mService;

    public MajorVoiceDetailPresenter(MajorVoiceDetailActivity view, VoiceService service) {
        mView = view;
        mService = service;
    }

    public void voice_click(HashMap<String,String> hashMap){
        mService.getApi()
                .voice_click(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JSONObject>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(JSONObject jsonObject) {
                    }
                });
    }

    public void voice_detail(HashMap<String,String> hashMap) {
        mService.getApi()
                .voice_detail(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Voice>() {
                    @Override
                    public void onCompleted() {
                        mView.setOnCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.setOnError();
                    }

                    @Override
                    public void onNext(Voice voice) {
                        mView.setOnNext(voice);
                    }
                });
    }
}
