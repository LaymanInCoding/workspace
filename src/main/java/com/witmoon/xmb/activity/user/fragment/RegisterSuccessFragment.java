package com.witmoon.xmb.activity.user.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.base.BaseFragment;

/**
 * 注册成功Fragment
 * 去掉了邮箱注册, 因此本类注释掉部分相关代码
 * Created by zhyh on 2015/6/20.
 */
public class RegisterSuccessFragment extends BaseFragment {

    public static final String TYPE_PHONE = "PHONE";
    public static final String TYPE_EMAIL = "EMAIL";

    private String mType = TYPE_PHONE;
    private String typeValue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mType = bundle.getString("type");
        typeValue = bundle.getString("value");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_success, container, false);

        AQuery aQuery = new AQuery(getActivity(), view);

        aQuery.id(R.id.register_success_value).text(typeValue);
        aQuery.id(R.id.register_finish).clicked(this);
        if (mType.equals(TYPE_PHONE)) {
//            aQuery.id(R.id.register_bind_email_btn).clicked(this);
        } else {
//            mAQuery.id(R.id.register_finish).gone();
            aQuery.id(R.id.register_success_img).image(R.mipmap.register_success_email);
            aQuery.id(R.id.register_success_tip).text("已发送一封验证邮件到该邮箱, 请激活");
//            aQuery.id(R.id.register_bind_email_btn).text("前往邮箱").clicked(this);
        }

        return view;
    }

    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.register_bind_email_btn:
//                if (mType.equals(TYPE_PHONE)) {
//                    UIHelper.showSimpleBack(getActivity(), SimpleBackPage.BIND_EMAIL);
//                    return;
//                }
//                CommonUtil.showShort(getActivity(), "前往邮箱");
//                break;
            case R.id.register_finish:
                // 注册成功后跳转到登录页面
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}
