package com.witmoon.xmb.activity.user.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.MD5Utils;
import com.witmoon.xmb.util.StringUtils;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONObject;

/**
 * 设置密码界面
 * Created by zhyh on 2015/6/20.
 */
public class WritePasswordFragment extends BaseFragment {
    public static final String REQ_TAG = "WRITE_PWD";

    public static final int OPERATION_REGISTER = 0x01;
    public static final int OPERATION_RETRIEVE = 0x02;

    private EditText mPwdText;          // 密码域
    private EditText mConfirmPwdText;   // 验证密码域
    // 注册手机号
    private String mTelephone;
    // 操作类型, 默认为注册操作
    private int mOperation = OPERATION_REGISTER;

    private String phoneCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle argument = getArguments();
        mTelephone = argument.getString("telephone");
        mOperation = argument.getInt("operation");
        phoneCode = argument.getString("phoneCode");
        Log.e("phoneCode",phoneCode);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_pwd, container, false);

        mPwdText = (EditText) view.findViewById(R.id.register_pwd_text);
        mConfirmPwdText = (EditText) view.findViewById(R.id.register_confirm_pwd_text);

        ToggleButton pwdToggle = (ToggleButton) view.findViewById(R.id.login_pwd_switch);
        pwdToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPwdText.setTransformationMethod(!isChecked ?
                        HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
            }
        });

        ToggleButton rePwdToggle = (ToggleButton) view.findViewById(R.id.login_repwd_switch);
        rePwdToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConfirmPwdText.setTransformationMethod(!isChecked ?
                        HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
            }
        });

        // 下一步按钮响应
        view.findViewById(R.id.next_step_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = mPwdText.getText().toString();
                String rePwd = mConfirmPwdText.getText().toString();
                if (checkInput(pwd, rePwd)) {
                    String pwdEncrypted = StringUtils.getMD5(pwd);
                    if (mOperation == OPERATION_REGISTER)
                        UserApi.signup(MD5Utils.getMD5Code(phoneCode),"",mTelephone, pwdEncrypted, signupCallback);
                    else
                        UserApi.resetPassword(mTelephone, pwdEncrypted, MD5Utils.getMD5Code(phoneCode), resetPwdCallback);
                }
            }
        });

        return view;
    }

    // 重置密码回调接口
    private Listener<JSONObject> resetPwdCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            Log.e("resetPwdCallback",response.toString());
            TwoTuple<Boolean, String> result = ApiHelper.parseResponseStatus(response);
            if (!result.first) {
                AppContext.showToastShort(result.second);
                return;
            }
            // 重置密码成功后跳转到登录页面
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            AppContext.showToastShort("重置密码成功, 请使用新密码登录");
        }

        @Override
        public void onError(NetroidError error) {
            super.onError(error);
            AppContext.showToastShort(error.getMessage());
        }
    };

    // 注册回调接口
    private Listener<JSONObject> signupCallback = new Listener<JSONObject>() {
        ProgressDialog mProgressDialog;

        @Override
        public void onPreExecute() {
            mProgressDialog = ProgressDialog.show(getActivity(), "", "请稍候...", true, true, new
                    DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Netroid.cancelRequest(REQ_TAG);
                        }
                    });
        }

        @Override
        public void onSuccess(JSONObject response) {
            Context context = getActivity();
            Log.e("login_response",response.toString());
            TwoTuple<Boolean, String> result = ApiHelper.parseResponseStatus(response);
            if (result.first) {
                Bundle bundle = new Bundle();
                bundle.putString("value", mTelephone);
                bundle.putString("type", RegisterSuccessFragment.TYPE_PHONE);
                UIHelper.showSimpleBack(context, SimpleBackPage.REGISTER_SUCCESS, bundle);
                return;
            }
            AppContext.showToastShort(result.second);
        }

        @Override
          public void onError(NetroidError error) {
            mProgressDialog.cancel();
            Log.e("cancel", error.toString());
        }

        @Override
        public void onFinish() {
            mProgressDialog.cancel();
        }
    };

    // 检测用户输入是否合法
    private boolean checkInput(String pwd, String rePwd) {
        if (!CommonUtil.isPasswordValid(pwd)) {
            mPwdText.setError("密码格式不正确");
            mPwdText.requestFocus();
            mPwdText.selectAll();
            return false;
        }
        if (!pwd.equals(rePwd)) {
            mConfirmPwdText.setError("两次密码输入不一致");
            mConfirmPwdText.requestFocus();
            mConfirmPwdText.selectAll();
            return false;
        }
        return true;
    }
}
