package com.witmoon.xmb.activity.me.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.common.SimpleBackActivity;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by de on 2016/12/26.
 */
public class BeanSendFragment extends BaseFragment {

    @BindView(R.id.tv_bean_num)
    TextView mTvBeanNum;
    @BindView(R.id.send_account_et)
    EditText mSendAccountEt;
    @BindView(R.id.send_num_et)
    EditText mSendNumEt;
    private String bean_num;
    private String send_num;
    private String friend_account;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_bean, container, false);
        bean_num = getArguments().getString("bean_num");
        ButterKnife.bind(this, view);
        mTvBeanNum.setText(bean_num);
        return view;
    }

    @OnClick({R.id.bean_help_tv, R.id.submit_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bean_help_tv:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BeanHelp);
                break;
            case R.id.submit_tv:
                send_num = mSendNumEt.getText().toString();
                friend_account = mSendAccountEt.getText().toString();
                request();
                break;
        }
    }

    private void request() {
        UserApi.sendBean(send_num, friend_account, new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Logger.json(response.toString());
                try {
                    XmbUtils.showMessage(getContext(), response.getString("info"));
                    if (response.getInt("code") == 200) {
                        getActivity().sendBroadcast(new Intent(Const.INTENT_ACTION_REFRESH_BEAN));
                        Observable.timer(1, TimeUnit.SECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Action1<Long>() {
                                    @Override
                                    public void call(Long aLong) {
                                        getActivity().finish();
                                    }
                                });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
