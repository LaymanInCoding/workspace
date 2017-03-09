package com.witmoon.xmb.activity.user.fragment;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.MD5Utils;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 填写校验码Fragment
 * Created by zhyh on 2015/6/20.
 */
public class WriteCheckCodeFragment extends BaseFragment {
    private EditText mVCodeEditText;    // 校验码输入
    private TextView countDownTextView;
    private String mPhoneNo;
    private String mCheckCode;
    private String code;
    private String type;
    // 操作类型, 默认为注册操作
    private int mOperation;
    private CountDownTimer mCountDownTimer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mPhoneNo = args.getString("telephone");
        type = args.getString("type");
//        mCheckCode = args.getString("checkCode");
        mOperation = args.getInt("operation");
//        if (TextUtils.isEmpty(mCheckCode)) {
        UserApi.phoneCode(mPhoneNo, type, phoneCodeCallback);
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write_code, container, false);

        TextView tip = (TextView) view.findViewById(R.id.register_write_code_tip);
        tip.setText(String.format(getString(R.string.tip_write_phone_check_code), mPhoneNo));
        mVCodeEditText = (EditText) view.findViewById(R.id.register_vcode_edit);

        // 重发校验码倒计时
        countDownTextView = (TextView) view.findViewById(R.id.register_count_down_text);

        countDownTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAndStartCountDownTimer();
//                if (type.equals("100")) {
//                    UserApi.verifyPhone(mPhoneNo, phoneCodeCallback);
//                } else
                UserApi.phoneCode(mPhoneNo, type, phoneCodeCallback);
            }
        });
        createAndStartCountDownTimer();

        // 下一步, 检测短信验证码是否正确
        view.findViewById(R.id.next_step_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = mVCodeEditText.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVCodeEditText.setError("请输入短信校验码");
                    mVCodeEditText.requestFocus();
                    return;
                }
                if (!MD5Utils.getMD5Code(code).equals(mCheckCode)) {
                    mVCodeEditText.setError("短信校验码不正确");
                    mVCodeEditText.requestFocus();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("telephone", mPhoneNo);
                bundle.putInt("operation", mOperation);
                bundle.putString("phoneCode", code);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.WRITE_PASSWORD, bundle);
                // 暂时去掉服务器端验证
//                UserApi.verifyPhoneCode(mPhoneNo, code, verifyPhoneCodeCallback);
            }
        });
        return view;
    }

    // 重新发送手机验证码回调接口
    private Listener<JSONObject> phoneCodeCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> result = ApiHelper.parseRetrieveStatus(response);
            if (!result.first) {
                AppContext.showToastShort(result.second);
                return;
            }
            // 重新发送成功后更新验证码
            try {
                mCheckCode = response.getJSONObject("data").getString("phoneCode");
            } catch (JSONException e) {
                AppContext.showToastShort("发送短信校验码出错");
            }
            AppContext.showToastShort("发送短信验证码成功");
        }
    };

    // 手机短信验证码检测回调接口
    private Listener<JSONObject> verifyPhoneCodeCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> result = ApiHelper.parseResponseStatus(response);
            if (!result.first) {
                AppContext.showToastShort(result.second);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString("telephone", mPhoneNo);
            Log.e("phoneCode", mVCodeEditText.getText().toString().trim());
            bundle.putString("phoneCode", mVCodeEditText.getText().toString().trim());
            UIHelper.showSimpleBack(getActivity(), SimpleBackPage.WRITE_PASSWORD, bundle);
        }
    };

    // 倒计时处理
    private void createAndStartCountDownTimer() {
        countDownTextView.setTextColor(getResources().getColor(R.color.text_view_hint));
        countDownTextView.setClickable(false);
        mCountDownTimer = new CountDownTimer(180000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countDownTextView.setText((int) (millisUntilFinished / 1000) + "秒后重发");
            }

            @Override
            public void onFinish() {
                countDownTextView.setClickable(true);
                countDownTextView.setTextColor(getResources().getColor(R.color.black));
                countDownTextView.setText("重发");
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //退出页面关闭
        mCountDownTimer.cancel();
    }
}
