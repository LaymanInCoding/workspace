package com.witmoon.xmb.activity.user.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.NormalPostJSONRequest;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.model.URLs;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 邮箱注册Fragment
 * Created by zhyh on 2015/4/28.
 */
public class EmailRegisterFragment extends BaseFragment {

    public static final String REQ_TAG = "EMAIL";

    private AQuery mAQuery;

    private EditText usernameText;
    private EditText emailText;
    private EditText pwdText;
    private EditText rePwdText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_email, container, false);

        mAQuery = new AQuery(getActivity(), view);
        mAQuery.id(R.id.register_phone_next).clicked(this);

        // 取得各输入域
        usernameText = mAQuery.id(R.id.register_username_text).getEditText();
        emailText = mAQuery.id(R.id.register_email_text).getEditText();
        pwdText = mAQuery.id(R.id.register_pwd_text).getEditText();
        rePwdText = mAQuery.id(R.id.register_confirm_pwd_text).getEditText();

        return view;
    }

    /**
     * 当前Fragment单击事件发生时响应函数
     *
     * @param view
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_phone_next:
                if (checkRegister()) {
//                    signup();   //  发送登陆请求
                }
                break;
        }
    }

    // 登陆请求
    private void signup() {
        Map<String, String> pm = new HashMap<>();
        pm.put("name", emailText.getText().toString());
        pm.put("type", "2");
        pm.put("password", pwdText.getText().toString());

        Request request = new NormalPostJSONRequest(URLs.SIGNUP, pm, new
                Listener<JSONObject>() {
                    ProgressDialog mProgressDialog;

                    @Override
                    public void onPreExecute() {
                        mProgressDialog = ProgressDialog.show(getActivity(), "", "请稍候...", true,
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
                            String email = emailText.getText().toString();
                            Bundle args = new Bundle();
                            args.putString("type", RegisterSuccessFragment.TYPE_EMAIL);
                            args.putString("value", email);
                            UIHelper.showSimpleBack(getActivity(), SimpleBackPage
                                    .REGISTER_SUCCESS, args);
                            return;
                        }
                        CommonUtil.showLong(getActivity(), twoTuple.second);
                    }

                    @Override
                    public void onFinish() {
                        mProgressDialog.cancel();
                    }
                });
        Netroid.addRequest(request, REQ_TAG);
    }

    /**
     * 检验数据是否合法
     *
     * @return 数据合法返回true, 否则返回false
     */
    private boolean checkRegister() {
        String username = usernameText.getText().toString();
        if (!CommonUtil.isUsernameValid(username)) {
            usernameText.setError("用户名由4-20个字母、数字及下划线组成.");
            usernameText.requestFocus();
            return false;
        }
        String email = emailText.getText().toString();
        if (!CommonUtil.isEmail(email)) {
            emailText.setError("请输入正确的Email");
            emailText.requestFocus();
            return false;
        }
        String pwd = pwdText.getText().toString();
        if (pwd.length() < 6 || pwd.length() > 20) {
            pwdText.setError(getString(R.string.tip_pwd_style));
            pwdText.requestFocus();
            return false;
        }
        String rePwd = rePwdText.getText().toString();
        if (!rePwd.equals(pwd)) {
            rePwdText.setError("两次密码输入不一致");
            rePwdText.requestFocus();
            return false;
        }
        if (!mAQuery.id(R.id.register_agree_license_checkbox).isChecked()) {
            CommonUtil.showShort(getActivity(), "您没有同意《小麻包注册协议》");
            return false;
        }
        return true;
    }
}
