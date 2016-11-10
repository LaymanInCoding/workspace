package com.witmoon.xmb.views;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.witmoon.xmb.R;
import com.witmoon.xmb.adapter.MajorVoiceAdapter;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.model.Voice;
import com.witmoon.xmb.presenter.MajorVoiceDetailPresenter;
import com.witmoon.xmb.services.VoiceService;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.XmbUtils;

import java.util.HashMap;

public class MajorVoiceDetailActivity extends BaseActivity {

    protected MajorVoiceAdapter adapter;
    protected EmptyLayout emptyLayout;
    protected Voice voice;

    MajorVoiceDetailPresenter mPresenter;
    VoiceService mService;
    MediaPlayer player = null;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_major_voice_detail;
    }

    @Override
    protected int getActionBarTitleByResId() {
        return R.string.major_voice_detail;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        initView();
        mService = new VoiceService();
        mPresenter = new MajorVoiceDetailPresenter(this,mService);
        mPresenter.voice_detail(VoiceService.gen_voice_detail_params(getIntent().getStringExtra("voice_id")));
    }

    public void setOnCompleted(){
        emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
    }

    public void setOnError(){
        emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
    }

    public void setOnNext(Voice voice){
        this.voice = voice;
        TextView abstractTextView = (TextView) findViewById(R.id.abstract_text);
        TextView authorTextView = (TextView) findViewById(R.id.author);
        TextView authorTitleTextView = (TextView) findViewById(R.id.author_title);
        TextView authorDescTextView = (TextView) findViewById(R.id.author_desc);
        ImageView headImageView = (ImageView) findViewById(R.id.head_img);
        ImageView voiceImageView = (ImageView) findViewById(R.id.voice_img);
        Netroid.displayBabyImage(voice.head_img,headImageView);
        Netroid.displayImage(voice.voice_img,voiceImageView);
        abstractTextView.setText(voice.abstract_text);
        authorTextView.setText(voice.author);
        authorTitleTextView.setText(voice.author_title);
        authorDescTextView.setText(voice.author_desc);
        findViewById(R.id.voice_file).setOnClickListener((View v)->play());
    }

    public void initView(){
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        ImageView toolbar_right_img = (ImageView) findViewById(R.id.toolbar_right_img);
        toolbar_right_img.setImageResource(R.mipmap.mbq_share);
        toolbar_right_img.setOnClickListener((View v) -> {
                HashMap<String,String> share_info = new HashMap();
                share_info.put("title", voice.abstract_text);
                share_info.put("desc", voice.abstract_text);
                share_info.put("url", voice.share_url);
                XmbUtils.showMbqShare(MajorVoiceDetailActivity.this, findViewById(R.id.share_container),share_info);
        });
    }

    protected void play(){
        Uri uri = Uri.parse(voice.voice_file);
        if (player != null){
            player.release();
        }
        player = MediaPlayer.create(this,uri);
        player.setOnErrorListener((MediaPlayer mp, int what, int extra) ->true);
        player.start();
        mPresenter.voice_click(VoiceService.gen_voice_click_params(voice.id));
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
