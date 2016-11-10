package com.witmoon.xmb.activity.me.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.util.MD5Utils;
import com.witmoon.xmb.util.TwoTuple;

import org.json.JSONObject;

/**
 * 修改登陆密码界面
 * Created by zhyh on 2015/6/22.
 */
public class ChangePasswordFragment extends BaseFragment {
    private EditText mOldPwdEdit;
    private EditText mNewPwdEdit;
    private EditText mConfirmPwdEdit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_pwd, container, false);

        mOldPwdEdit = (EditText) view.findViewById(R.id.old_password);
        mNewPwdEdit = (EditText) view.findViewById(R.id.new_password);
        mConfirmPwdEdit = (EditText) view.findViewById(R.id.confirm_pwd);
        view.findViewById(R.id.submit_button).setOnClickListener(this);

        return view;
    }

    private boolean checkInput() {
        if (!mNewPwdEdit.getText().toString().equals(mConfirmPwdEdit.getText().toString())) {
            AppContext.showToastShort("两次密码输入不一致");
            return false;
        }
        return true;
    }

    // 回调接口
    private Listener<JSONObject> mModPwdCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            Log.e("response",response.toString());
            TwoTuple<Boolean, String> tt = ApiHelper.parseResponseStatus(response);
            if (!tt.first) {
                AppContext.showToastShort(tt.second);
                return;
            }
            AppContext.instance().logout();
            AppContext.showToastShort("修改密码成功, 需要重新登录");
            // 注册成功后跳转到登录页面
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        }

        @Override
        public void onError(NetroidError error) {
            AppContext.showToastShort("操作失败");
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submit_button) {
            if (checkInput()) {
                String oldPwd = mOldPwdEdit.getText().toString();
                String pwd = mNewPwdEdit.getText().toString();
                UserApi.modifyPwd(MD5Utils.getMD5Code(oldPwd), MD5Utils.getMD5Code(pwd),
                        mModPwdCallback);
            }
        }
    }
}
