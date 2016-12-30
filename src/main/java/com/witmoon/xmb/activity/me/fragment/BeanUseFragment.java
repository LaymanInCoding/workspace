package com.witmoon.xmb.activity.me.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.orhanobut.logger.Logger;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.ui.widget.EmptyLayout;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by de on 2016/12/26.
 */
public class BeanUseFragment extends BaseFragment {

    @BindView(R.id.tv_bean_num)
    TextView mTvBeanNum;
    @BindView(R.id.empty_layout)
    EmptyLayout mEmptyLayout;
    @BindView(R.id.use_bean_et)
    EditText mUseBeanEt;
    @BindView(R.id.convert_tv)
    TextView mConvertTv;

    private String s;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bean_use, container, false);
        s = getArguments().getString("mabaobean_number");
        ButterKnife.bind(this, view);
        initView();
        request();
        return view;
    }

    private void initView() {
        Logger.e(s);
        mUseBeanEt.setText(s);
        mUseBeanEt.setSelection(s.length());
        if (!s.toString().equals("")) {
            mConvertTv.setText("(可抵扣" + Double.parseDouble(s.toString()) / 100 + "元)");
        }
        mUseBeanEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("CharSe", s.length() + "");
                if (!s.toString().equals("")) {
                    mConvertTv.setVisibility(View.VISIBLE);
                    mConvertTv.setText("(可抵扣" + Double.parseDouble(s.toString()) / 100 + "元)");
                } else {
                    mConvertTv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void request() {
        UserApi.getUserBean(mListener);
    }

    Listener<JSONObject> mListener = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        }

        @Override
        public void onSuccess(JSONObject response) {
            Logger.json(response.toString());
            mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
            try {
                mTvBeanNum.setText(response.getInt("number") + "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(NetroidError error) {
            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        }
    };

    @OnClick({R.id.empty_layout, R.id.bean_help_tv, R.id.submit_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.empty_layout:
                request();
                break;
            case R.id.bean_help_tv:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BeanHelp);
                break;
            case R.id.submit_tv:
                if (mUseBeanEt.getText().toString().equals("")) {
                    XmbUtils.showMessage(getContext(), "请输入抵扣金额");
                } else if (Double.parseDouble(mUseBeanEt.getText().toString()) >
                        Double.parseDouble(mTvBeanNum.getText().toString())) {
                    XmbUtils.showMessage(getContext(), "数量已超最大使用上限");
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("mabaobean_number", mUseBeanEt.getText().toString());
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    getActivity().finish();
                }
        }
    }
}
