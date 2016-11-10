package com.witmoon.xmb.activity.user.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 手机号注册界面Fragment
 * Created by zhyh on 2015/4/28.
 */
public class TelephoneRegisterFragment extends BaseFragment {

    private EditText mPhoneEdit;
    private CheckBox mLicenseCheckBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_telephone, container, false);

        AQuery mAQuery = new AQuery(getActivity(), view);
        mAQuery.id(R.id.register_phone_next).clicked(this);
        mAQuery.id(R.id.register_area_select_layout).clicked(this);
        mAQuery.id(R.id.register_license).clicked(this);

        mPhoneEdit = mAQuery.id(R.id.register_phone_edit).getEditText();
        mLicenseCheckBox = mAQuery.id(R.id.register_agree_license_checkbox).getCheckBox();

        return view;
    }

    // 验证手机号(通过则发送短信验证码)回调
    private Listener<JSONObject> validPhoneCallback = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            showWaitDialog();

        }

        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> status = ApiHelper.parseResponseStatus(response);
            hideWaitDialog();
            if (!status.first) {
                AppContext.showToastShort(status.second);
                return;
            }
            // 启动手机校验码Activity
            Bundle args = new Bundle();
            args.putInt("operation", WritePasswordFragment.OPERATION_REGISTER);
            args.putString("telephone", mPhoneEdit.getText().toString());
            args.putString("type","100");
            try {
                args.putString("checkCode", response.getJSONObject("data").getString("phoneCode"));
            } catch (JSONException e) {
                AppContext.showToastShort("发送短信校验码出错");
            }
            UIHelper.showSimpleBack(getActivity(), SimpleBackPage.WRITE_CHECK_CODE, args);
        }
        @Override
        public void onError(NetroidError error) {
            hideWaitDialog();
            AppContext.showToast(error.toString());
            AppContext.showToastShort("操作失败.");
        }
    };

    /**
     * 单击事件响应方法
     */
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register_phone_next:
                // 输入有误, 返回
                if (!checkInput()) return;
                final String phone = mPhoneEdit.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle
                        ("确认手机号").setMessage("我们将向您的手机发送验证短信, 请确认您的手机号: " + phone);
                builder.setPositiveButton("好", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserApi.verifyPhone(phone, validPhoneCallback);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            case R.id.register_area_select_layout:
                break;
            case R.id.register_license:
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SERVICE_PROVISION);
                break;
        }
    }

    private boolean checkInput() {
        String phone = mPhoneEdit.getText().toString();
        if (!CommonUtil.isMobilePhone(phone)) {
            mPhoneEdit.setError("手机号输入不正确");
            mPhoneEdit.requestFocus();
            return false;
        }
        if (!mLicenseCheckBox.isChecked()) {
            AppContext.showToast("您没有同意《小麻包注册协议》");
            return false;
        }
        return true;
    }
}
