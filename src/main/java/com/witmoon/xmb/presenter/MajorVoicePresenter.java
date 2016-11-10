package com.witmoon.xmb.presenter;

import com.witmoon.xmb.views.MajorVoiceActivity;
import com.witmoon.xmb.model.Voice;
import com.witmoon.xmb.services.VoiceService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MajorVoicePresenter {
    MajorVoiceActivity mView;
    VoiceService mService;

    public MajorVoicePresenter(MajorVoiceActivity view, VoiceService service) {
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

    public void get_voice_list(HashMap<String,String> hashMap) {
        mService.getApi()
                .voice_list(hashMap)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Voice>>() {
                    @Override
                    public void onCompleted() {
                        mView.setOnCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.setOnError();
                    }

                    @Override
                    public void onNext(List<Voice> voices) {
                        mView.setOnNext(voices);
                    }
                });
    }
}
