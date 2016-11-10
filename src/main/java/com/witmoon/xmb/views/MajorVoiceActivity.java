package com.witmoon.xmb.views;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.witmoon.xmb.R;
import com.witmoon.xmb.adapter.MajorVoiceAdapter;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.Voice;
import com.witmoon.xmb.presenter.MajorVoicePresenter;
import com.witmoon.xmb.services.VoiceService;
import com.witmoon.xmb.ui.widget.EmptyLayout;

import java.util.ArrayList;
import java.util.List;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;


public class MajorVoiceActivity extends BaseActivity {

    protected MajorVoiceAdapter adapter;
    protected EmptyLayout emptyLayout;
    protected ArrayList<Voice> mDatas = new ArrayList<>();

    MajorVoicePresenter mPresenter;
    VoiceService mService;
    MediaPlayer player = null;
    int page = 1;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_major_voice;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        initRecycleView();
    }

    public void setOnCompleted(){
        //emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    public void setRecRequest(int currentPage){
        mPresenter.get_voice_list(VoiceService.gen_voice_list_params(page));
    }


    public void setOnError(){
        emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
    }

    public void setOnNext(List<Voice> voices){
        if(voices.size() < 20){
            removeFooterView();
        }else{
            createLoadMoreView();
            resetStatus();
        }
        if (voices.size() == 0 && page == 1){
            emptyLayout.setErrorType(EmptyLayout.NODATA);
        }else{
            emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
        }
        page += 1;
        mDatas.addAll(voices);
        stringAdapter.notifyDataSetChanged();
    }

    protected void initRecycleView(){
        mService = new VoiceService();
        mPresenter = new MajorVoicePresenter(this,mService);
        mRootView = (RecyclerView) findViewById(R.id.recycleView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new MajorVoiceAdapter(mDatas,this);
        emptyLayout = (EmptyLayout)findViewById(R.id.error_layout);
        emptyLayout.setOnLayoutClickListener((View v)-> mPresenter.get_voice_list(VoiceService.gen_voice_list_params(page)));
        findViewById(R.id.search_textview).setOnClickListener((View v)->startActivity(new Intent(MajorVoiceActivity.this,MajorVoiceSearchActivity.class)));
        adapter.setOnItemClickListener(new MajorVoiceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(MajorVoiceActivity.this,MajorVoiceDetailActivity.class);
                intent.putExtra("voice_id",mDatas.get(position).id);
                startActivity(intent);
            }

            @Override
            public void onVoiceClick(int position) {
                play(position);
            }
        });
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        mRootView.setAdapter(stringAdapter);
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mPresenter.get_voice_list(VoiceService.gen_voice_list_params(page));
    }

    protected void play(int position){
        Uri uri = Uri.parse(mDatas.get(position).voice_file);
        if (player != null){
            player.release();
        }
        player = MediaPlayer.create(this,uri);
        player.setOnErrorListener((MediaPlayer mp, int what, int extra) ->true);
        player.start();
        mPresenter.voice_click(VoiceService.gen_voice_click_params(mDatas.get(position).id));
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
