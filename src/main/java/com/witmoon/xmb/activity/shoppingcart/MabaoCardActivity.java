package com.witmoon.xmb.activity.shoppingcart;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.shoppingcart.adapter.MabaoCardAdapter;
import com.witmoon.xmb.api.MabaoCardApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.MabaoCard;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.easydone.swiperefreshendless.HeaderViewRecyclerAdapter;


public class MabaoCardActivity extends BaseActivity implements View.OnClickListener {

    private EmptyLayout emptyLayout;
    private MabaoCardAdapter adapter;
    private View no_mb_card_view;
    private ArrayList<MabaoCard> arrayList = new ArrayList<>();
    private ArrayList<String> selectedCardList = new ArrayList<>();
    private int current_page = 1;
    private View next_container;

    private BroadcastReceiver bind_card_success_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                JSONObject jsonObject = new JSONObject(intent.getStringExtra("ARGS"));
                MabaoCard mabaoCard = MabaoCard.parse(jsonObject.getJSONObject("data"));
                mabaoCard.setIs_checked(1);
                arrayList.add(0, mabaoCard);
                stringAdapter.notifyDataSetChanged();
                mRootView.setVisibility(View.VISIBLE);
                no_mb_card_view.setVisibility(View.GONE);
                next_container.setVisibility(View.VISIBLE);
                selectedCardList.add(mabaoCard.getCard_no());
                XmbUtils.showMessage(MabaoCardActivity.this,jsonObject.getString("info"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_mb_card;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.master_shopping_cart);
        emptyLayout = (EmptyLayout) findViewById(R.id.error_layout);
        emptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        emptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                current_page = 1;
                setRecRequest(current_page);
            }
        });
        initRecycleView();
        setFont();
        setReceiver();
    }

    private void setReceiver(){
        IntentFilter loginFilter = new IntentFilter(Const.BIND_MABAO_CARD_SUCCESS);
        registerReceiver(bind_card_success_receiver, loginFilter);
    }

    private void unsetReceiver(){
        unregisterReceiver(bind_card_success_receiver);
    }

    private void initRecycleView(){
        mRootView = (RecyclerView) findViewById(R.id.recycle_view);
        layoutManager = new LinearLayoutManager(this);
        mRootView.setHasFixedSize(true);
        mRootView.setLayoutManager(layoutManager);
        adapter = new MabaoCardAdapter(arrayList,this);
        adapter.setOnItemClickListener(new MabaoCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                MabaoCard mabaoCard = arrayList.get(position);
                if (mabaoCard.getIs_checked() == 1){
                    selectedCardList.remove(mabaoCard.getCard_no());
                    mabaoCard.setIs_checked(0);
                }else{
                    selectedCardList.add(mabaoCard.getCard_no());
                    mabaoCard.setIs_checked(1);
                }
                arrayList.set(position, mabaoCard);
                stringAdapter.notifyDataSetChanged();
            }
        });
        stringAdapter = new HeaderViewRecyclerAdapter(adapter);
        stringAdapter.setAdapter(adapter);
        mRootView.setAdapter(stringAdapter);
        setRecRequest(current_page);
    }

    private Boolean check_is_show(MabaoCard mabaoCard){
        for (int i = 0,len = arrayList.size();i < len; i++){
            if (mabaoCard.getCard_no().equals(arrayList.get(i).getCard_no())){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public void setRecRequest(int currentPage){
        MabaoCardApi.get_card_list(current_page, new Listener<JSONObject>() {
            @Override
            public void onError(NetroidError error) {
                emptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
            }

            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject cardObject = jsonArray.getJSONObject(i);
                        MabaoCard mabaoCard = MabaoCard.parse(cardObject);
                        if (!check_is_show(mabaoCard)) {
                            arrayList.add(mabaoCard);
                        }
                    }
                    if (jsonArray.length() < 20) {
                        removeFooterView();
                    } else {
                        createLoadMoreView();
                        resetStatus();
                    }
                    if (arrayList.size() == 0){
                        no_mb_card_view.setVisibility(View.VISIBLE);
                        next_container.setVisibility(View.GONE);
                        mRootView.setVisibility(View.GONE);
                    }else{
                        mRootView.setVisibility(View.VISIBLE);
                        next_container.setVisibility(View.VISIBLE);
                    }
                    current_page += 1;
                    stringAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                emptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            }
        });
    }

    private void setFont(){
        AssetManager mgr = this.getAssets();
        Typeface tf=Typeface.createFromAsset(mgr, "fonts/font.otf");
        TextView toolbar_title_text = (TextView)findViewById(R.id.toolbar_title_text);
        TextView toolbar_right_text = (TextView)findViewById(R.id.toolbar_right_text);
        next_container = findViewById(R.id.next_container);
        no_mb_card_view = findViewById(R.id.no_mb_card_view);
        TextView mb_card_sorry_title = (TextView)findViewById(R.id.mb_card_sorry_title);
        TextView mb_card_sorry_desc = (TextView)findViewById(R.id.mb_card_sorry_desc);
        Button mb_card_use = (Button)findViewById(R.id.mb_card_use);
        if (mb_card_use != null) {
            mb_card_use.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("data", selectedCardList);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
        }
        if(mb_card_sorry_title != null) {
            mb_card_sorry_title.setTypeface(tf);
        }
        if(mb_card_sorry_desc != null) {
            mb_card_sorry_desc.setTypeface(tf);
        }
        if(toolbar_title_text != null) {
            toolbar_title_text.setText("选择麻包卡");
        }
        if(toolbar_right_text != null){
            toolbar_right_text.setText("绑定新卡");
            toolbar_right_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MabaoCardActivity.this,MabaoCardAddActivity.class));
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsetReceiver();
    }
}
