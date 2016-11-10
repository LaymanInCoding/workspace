package com.witmoon.xmb.activity.user.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.Request;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.NormalPostJSONRequest;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 密码找回类型选择界面
 * Created by zhyh on 2015/6/20.
 */
public class RetrieveTypeFragment extends BaseFragment {

    private String username;
    private int checkType;

    private String mPhoneNo;
    private String mEmail;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        username = arguments.getString("username");
        checkType = arguments.getInt("checkType", 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retrieve_type, container, false);

        // 初始化视图, 验证方式> checkType(0: 邮箱+手机; 1: 手机; 2:邮箱)
        TextView byEmailText = (TextView) view.findViewById(R.id.retrieve_by_email);
        TextView byPhoneText = (TextView) view.findViewById(R.id.retrieve_by_phone);

        if (checkType == 1) {
            byEmailText.setVisibility(View.GONE);
        } else if (checkType == 2) {
            byPhoneText.setVisibility(View.GONE);
        }

        byEmailText.setOnClickListener(this);
        byPhoneText.setOnClickListener(this);

        return view;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.retrieve_by_phone:
                retrieve(0);    // 0:通过手机找回
                break;
            case R.id.retrieve_by_email:
                retrieve(1);    // 1:通过邮箱找回
                break;
        }
    }

    // 请求服务器找回密码
    private void retrieve(final int retrieveType) {
        Map<String, String> pm = new HashMap<>();
        pm.put("name", username);
        pm.put("type", retrieveType + "");
        Request request = new NormalPostJSONRequest("", pm, new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                Context context = getActivity();
                TwoTuple<Boolean, String> tw = CommonUtil.networkStatus(response);
                if (tw.first) {
                    Bundle args = new Bundle();
                    if (retrieveType == 0) {
                        args.putInt("operation", WritePasswordFragment.OPERATION_RETRIEVE);
                        args.putString("telephone", mPhoneNo);
                        args.putString("type","101");
                        UIHelper.showSimpleBack(context, SimpleBackPage.WRITE_CHECK_CODE, args);
                    } else if (retrieveType == 1) {
                        args.putInt("operation", WritePasswordFragment.OPERATION_REGISTER);
                        args.putString("telephone", mEmail);
                        args.putString("type","1");
                        UIHelper.showSimpleBack(context, SimpleBackPage.REGISTER_SUCCESS, args);
                    }
                    return;
                }
                CommonUtil.showLong(context, tw.second);
            }
        });
        Netroid.addRequest(request);
    }
}
