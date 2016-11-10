package com.witmoon.xmb.activity.user.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.VerifyCodeGenerator;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONObject;

/**
 * 找回密码Fragment
 * Created by zhyh on 2015/6/20.
 */
public class RetrievePasswordFragment extends BaseFragment {
    private EditText mUsernameEditText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_retrieve_pwd, container, false);
        AQuery mAQuery = new AQuery(getActivity(), view);

        final int height = (int) TDevice.dpToPixel(40);
        final int width = (int) TDevice.dpToPixel(120);
        final int fontSize = (int) TDevice.dpToPixel(32);
        mAQuery.id(R.id.verify_code_img).image(VerifyCodeGenerator.getInstance().width(width)
                .height(height).fontSize(fontSize).generateBitmap()).clicked(new View
                .OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView) v).setImageBitmap(VerifyCodeGenerator.getInstance().width(width).height(height)
                        .fontSize(fontSize).generateBitmap());
            }
        });

        mUsernameEditText = mAQuery.id(R.id.login_uname_edit).getEditText();
        final EditText verifyCodeText = mAQuery.id(R.id.login_vcode_edit).getEditText();
        mAQuery.id(R.id.next_step_btn).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = mUsernameEditText.getText().toString();
                if (!CommonUtil.isUsernameValid(uname)) {
                    mUsernameEditText.setError("请输入正确的用户名");
                    mUsernameEditText.requestFocus();
                    return;
                }
                String verifyCode = verifyCodeText.getText().toString();
                if (!verifyCode.equalsIgnoreCase(VerifyCodeGenerator.getInstance().getCode())) {
                    verifyCodeText.setError("验证码不正确");
                    verifyCodeText.requestFocus();
                    return;
                }
                // 用户名和验证码输入正确, 去服务器获取账号信息
                UserApi.signed(uname, signedCallback);
            }
        });
        return view;
    }

    // 根据用户名取回用户找回密码相关的信息, 如手机号
    private Listener<JSONObject> signedCallback = new Listener<JSONObject>() {
        ProgressDialog mProgressDialog;

        @Override
        public void onPreExecute() {
            mProgressDialog = ProgressDialog.show(getActivity(), "", "请稍候...", true, true, new
                    DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                        }
                    });
        }

        @Override
        public void onSuccess(JSONObject response) {
            Log.e("response_找回密码",response.toString());
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (!twoTuple.first) {
                if (twoTuple.second.equals("null"))
                {
                    AppContext.showToastShort("您还未注册账户！");
                    return;
                }
                AppContext.showToastShort(twoTuple.second);
                return;
            }
            String username = mUsernameEditText.getText().toString();
            Bundle bundle = new Bundle();
            bundle.putInt("operation", WritePasswordFragment.OPERATION_RETRIEVE);
            bundle.putString("telephone", username);
            bundle.putString("type","101");
            UIHelper.showSimpleBack(getActivity(), SimpleBackPage.WRITE_CHECK_CODE, bundle);
        }
        @Override
        public void onFinish() {
            mProgressDialog.cancel();
        }
    };
}
