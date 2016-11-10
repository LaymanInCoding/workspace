package com.witmoon.xmb.activity.user.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.NormalPostJSONRequest;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.model.URLs;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录界面
 * Created by zhyh on 2015/6/19.
 */
public class LoginFragment extends BaseFragment {

    public static final String REQ_TAG = "LOGIN";
    public static final int LOGIN_REQ_CODE = 2;

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mUsernameEditText = (EditText) view.findViewById(R.id.login_uname_edit);
        mPasswordEditText = (EditText) view.findViewById(R.id.login_pwd_edit);

        ToggleButton pwdSwitch = (ToggleButton) view.findViewById(R.id.login_pwd_switch);
        pwdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPasswordEditText.setTransformationMethod(!isChecked ?
                        HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
            }
        });

        AQuery aQuery = new AQuery(getActivity(), view);
        aQuery.id(R.id.login_fast_register).clicked(this);
        aQuery.id(R.id.login_retrieve_pwd).clicked(this);

        aQuery.id(R.id.submit_button).clicked(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BaseActivity baseActivity = (BaseActivity) getActivity();
        baseActivity.setToolBarTitle(R.string.text_login);
        baseActivity.setToolBarBackground(R.color.master_login);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_fast_register:  // 注册
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.REGISTER);
                break;
            case R.id.login_retrieve_pwd:   // 找回密码
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.RETRIEVE_PWD);
                break;
            case R.id.submit_button:
                if (prepareLogin()) {
                    showWaitDialog(R.string.progress_login);
                    String username = mUsernameEditText.getText().toString();
                    String password = mPasswordEditText.getText().toString();
                    UserApi.login1(username, password, loginCallback);
//                    AppContext.instance().setIsLogin(true);
//                    getActivity().setResult(Activity.RESULT_OK);
//                    getActivity().finish();
//                    doLogin();
                }
                break;
        }
    }

    // 登录回调
    private Listener<JSONObject> loginCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {

        }
    };

    // 用户登陆
    private void doLogin() {
        Map<String, String> pm = new HashMap<>();
        pm.put("name", mUsernameEditText.getText().toString());
        pm.put("password", mPasswordEditText.getText().toString());
        Request request = new NormalPostJSONRequest(URLs.SIGNIN, pm, new
                Listener<JSONObject>() {
                    ProgressDialog mProgressDialog;
                    Context mContext;
                    @Override
                    public void onPreExecute() {
                        mContext = getActivity();
                        mProgressDialog = ProgressDialog.show(mContext, "", "正在登陆...", true,
                                true, new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        Netroid.cancelRequest(REQ_TAG);
                                    }
                                });
                    }

                    @Override
                    public void onSuccess(JSONObject response) {
                        TwoTuple<Boolean, String> twoTuple = CommonUtil.networkStatus(response);
                        if (twoTuple.first) {
                            // 登录成功
                            getActivity().finish();
                            return;
                        }
                        CommonUtil.showLong(mContext, twoTuple.second);
                    }

                    @Override
                    public void onFinish() {
                        mProgressDialog.cancel();
                    }
                });
        Netroid.addRequest(request, REQ_TAG);
    }

    // 准备登录
    private boolean prepareLogin() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.no_network);
            return false;
        }
        String username = mUsernameEditText.getText().toString();
        if (TextUtils.isEmpty(username)) {
            mUsernameEditText.setError("用户名不能为空");
            mUsernameEditText.requestFocus();
            return false;
        }
        String password = mPasswordEditText.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordEditText.setError("密码不能为空");
            mPasswordEditText.requestFocus();
            return false;
        }
        return true;
    }
}
