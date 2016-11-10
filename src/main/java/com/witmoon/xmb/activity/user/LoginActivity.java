package com.witmoon.xmb.activity.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import com.androidquery.AQuery;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.model.User;
import com.witmoon.xmb.ui.popupwindow.Popup;
import com.witmoon.xmb.ui.popupwindow.PopupDialog;
import com.witmoon.xmb.ui.popupwindow.PopupUtils;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.MD5Utils;
import com.witmoon.xmb.util.StringUtils;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.XmbUtils;
import com.xiaoneng.menu.Ntalker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 登陆Activity
 * Created by zhyh on 2015/4/28.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener,
        PlatformActionListener {

    public static final String REQ_TAG = "LOGIN";
    public static final int LOGIN_REQ_CODE = 2;
    public String sin_name, type;
    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private String password, username;
    private Button code_;
    PopupDialog popupDialog;
    private CountDownTimer mCountDownTimer;
    private EditText phone, checkCode;
    private String check_phoneCode;
    //判断登录类型--自动登录比对--
    private boolean is_in = true;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        final AQuery aQuery = new AQuery(this);
        mUsernameEditText = (EditText) findViewById(R.id.login_uname_edit);
        mPasswordEditText = (EditText) findViewById(R.id.login_pwd_edit);

        ToggleButton pwdSwitch = (ToggleButton) findViewById(R.id.login_pwd_switch);
        pwdSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPasswordEditText.setTransformationMethod(!isChecked ?
                        HideReturnsTransformationMethod.getInstance() :
                        PasswordTransformationMethod.getInstance());
            }
        });
        aQuery.id(R.id.login_fast_register).clicked(this);
        aQuery.id(R.id.login_retrieve_pwd).clicked(this);

        aQuery.id(R.id.submit_button).clicked(this);

        // 第三方登录
        aQuery.id(R.id.login_by_weichat).clicked(this);
        aQuery.id(R.id.login_by_qq).clicked(this);
        aQuery.id(R.id.login_by_weibo).clicked(this);
        ShareSDK.initSDK(this);
        UserApi.is_Gnore(new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getInt("ignore") != 1) {
                        aQuery.id(R.id.id_login).visibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void configActionBar(Toolbar toolbar) {
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_login));
    }

    @Override
    protected int getActionBarTitleByResId() {
        return R.string.text_login;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_fast_register:  // 注册
                UIHelper.showSimpleBack(this, SimpleBackPage.REGISTER);
                break;
            case R.id.login_retrieve_pwd:   // 找回密码
                UIHelper.showSimpleBack(this, SimpleBackPage.RETRIEVE_PWD);
                break;
            case R.id.submit_button:
                AppContext.isOrLogin(false);
                is_in = false;
                if (prepareLogin()) {
                    showWaitDialog(R.string.progress_login);
                    username = mUsernameEditText.getText().toString();
                    password = mPasswordEditText.getText().toString();
                    UserApi.login1(username, MD5Utils.getMD5Code(password), loginCallback);
                }
                break;
            case R.id.login_by_weibo:
                AppContext.isOrLogin(true);
                ShareSDK.initSDK(this);
                Platform weibo = ShareSDK.getPlatform(this, SinaWeibo.NAME);
                weibo.removeAccount();
                ShareSDK.removeCookieOnAuthorize(true);// 清理cookie
                weibo.setPlatformActionListener(this);
                weibo.showUser(null);
                break;
            case R.id.login_by_qq:
                AppContext.isOrLogin(true);
                ShareSDK.initSDK(this);
                Platform qq = ShareSDK.getPlatform(this, QQ.NAME);
                qq.removeAccount();
                qq.setPlatformActionListener(this);
                qq.showUser(null);
                break;
            case R.id.login_by_weichat:
                AppContext.isOrLogin(true);
                if (!WXAPIFactory.createWXAPI(LoginActivity.this, null).isWXAppInstalled()) {
                    AppContext.showToast("您未安装微信！");
                    break;
                }
                ShareSDK.initSDK(this);
                Platform weixin = ShareSDK.getPlatform(this, Wechat.NAME);
                weixin.removeAccount();
                weixin.setPlatformActionListener(this);
                weixin.showUser(null);
                break;
        }
    }

    // 登录回调
    private Listener<JSONObject> loginCallback = new Listener<JSONObject>() {
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            showWaitDialog(R.string.progress_login);
        }

        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
            if (twoTuple.first) {
                try {
                    User user;
                    JSONObject respData = response.getJSONObject("data");
                    String sessionID = respData.getJSONObject("session").getString("sid");
                    ApiHelper.setSessionID(sessionID);
                    if (is_in) {
                        //第三方
                        user = User.parse(respData.getJSONObject("user"), type, sin_name, "");
                    } else {
                        //本地
                        user = User.parse(respData.getJSONObject("user"), username, MD5Utils.getMD5Code(password), "ZH");
                        user.setSid(sessionID);
                    }
                    int login = Ntalker.getInstance().login(LoginActivity.this, String.valueOf(user.getUid()),
                            user.getName(), "1");// 登录时调
                    if (0 != login) {
                        Log.e("小能", "userid登录失败");
                        Log.e("错误码：", login + "");
                    }
                    AppContext.saveLoginInfo(user);  // 保存登录信息
                    user = null;
                    // 发布登录成功广播
                    XmbUtils.loginCallback(LoginActivity.this);
                } catch (JSONException ignored) {
                    CommonUtil.show(LoginActivity.this, "登录失败.", 1000);
                }
                finish();
                return;
            } else {
                try {
                    if (!AppContext.is_login_or()) {
                        if (response.getJSONObject("status").getString("error_code").equals("2008")) {
                            showDownUpPopupDialog();
                        }else{
                            AppContext.clearLoginInfo();     // 登录失败, 清除登录信息
                            CommonUtil.show(LoginActivity.this, twoTuple.second, 1000);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            CommonUtil.show(LoginActivity.this, twoTuple.second, 1000);
            AppContext.clearLoginInfo();     // 登录失败, 清除登录信息
        }

        @Override
        public void onError(NetroidError error) {
            AppContext.showToastShort("登录失败.");
            if (null != ShareSDK.class) {
                ShareSDK.removeCookieOnAuthorize(true);
            }
        }

        @Override
        public void onFinish() {
            hideWaitDialog();
        }
    };

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

    public void showDownUpPopupDialog() {
        Popup popup = new Popup();
        popup.setvWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popup.setvHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        popup.setClickable(true);
        popup.setContentView(R.layout.phone_verify);
        //设置触摸其他位置时关闭窗口
        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                int height = view.findViewById(R.id.flMaskLayer).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        PopupUtils.dismissPopupDialog();
                    }
                }
                return true;
            }
        };
        popup.setTouchListener(listener);
        popupDialog = PopupUtils.createPopupDialog(getBaseContext(), popup);
        popupDialog.showAtLocation(LoginActivity.this.findViewById(R.id.add_view), Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, popup.getxPos(), popup.getyPos());
        final View view = popupDialog.getContentView();
        //背景透明度设置
        view.findViewById(R.id.flMaskLayer).setAlpha(0.75f);
        View.OnClickListener l = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.submit_:
                        if (isCheck()) {
                            UserApi.signup(check_phoneCode, username, phone.getText().toString().trim(), StringUtils.getMD5(password), signupCallback);
                        }
                        break;
                    case R.id.down_button:
                        if (null != mCountDownTimer) {
                            mCountDownTimer.cancel();
                        }
                        PopupUtils.dismissPopupDialog();
                        break;
                    case R.id.get_verify_code:
                        if (!CommonUtil.isUsernameValid(phone.getText().toString().trim())) {
                            AppContext.showToast("请输入正确的手机号");
                            return;
                        }
                        code_.setEnabled(false);
                        createAndStartCountDownTimer(code_);
                        UserApi.verifyPhone(phone.getText().toString().trim(), validPhoneCallback);
                        break;
                    case R.id.flMaskLayer:
                        //点击其他关闭软键盘  // STOPSHIP: 2015/12/21
                        InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        im.hideSoftInputFromWindow(view.findViewById(R.id.flMaskLayer).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        break;
                }
            }
        };
        phone = (EditText) view.findViewById(R.id.login_uname_edit);
        checkCode = (EditText) view.findViewById(R.id.check_code);
        view.findViewById(R.id.down_button).setOnClickListener(l);
        view.findViewById(R.id.submit_).setOnClickListener(l);
        code_ = (Button) view.findViewById(R.id.get_verify_code);
        view.findViewById(R.id.flMaskLayer).setOnClickListener(l);
        code_.setOnClickListener(l);
    }

    private Listener<JSONObject> signupCallback = new Listener<JSONObject>() {
        ProgressDialog mProgressDialog;

        @Override
        public void onPreExecute() {
            mProgressDialog = ProgressDialog.show(LoginActivity.this, "", "请稍候...", true, true, new
                    DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Netroid.cancelRequest(REQ_TAG);
                        }
                    });
        }

        @Override
        public void onSuccess(JSONObject response) {
            Log.e("login_response", response.toString());
            TwoTuple<Boolean, String> result = ApiHelper.parseResponseStatus(response);
            if (result.first) {
                if (null != mCountDownTimer) {
                    mCountDownTimer.cancel();
                }
                PopupUtils.dismissPopupDialog();
                AppContext.showToastShort("验证成功请登录！");
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

    private Listener<JSONObject> validPhoneCallback = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            Log.e("response", response.toString());
            TwoTuple<Boolean, String> status = ApiHelper.parseResponseStatus(response);
            if (!status.first) {
                AppContext.showToastShort(status.second);
                mCountDownTimer.cancel();
                code_.setClickable(true);
                code_.setEnabled(true);
                code_.setTextColor(getResources().getColor(R.color.black));
                code_.setText("获取验证码");
                code_.setTextColor(Color.parseColor("#63A3C6"));
                return;
            }
            try {
                check_phoneCode = response.getJSONObject("data").getString("phoneCode");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(NetroidError error) {
            AppContext.showToast(error.toString());
            AppContext.showToastShort("操作失败.");
        }
    };

    // -------------------- 第三方登录回调接口 -------------------------

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        //取得type；sin_name;---   用做自动登录
        type = platform.getName().toLowerCase();
        sin_name = platform.getDb().getUserId();
        UserApi.login(platform.getName().toLowerCase(), platform.getDb().getUserId(),
                platform.getDb().getUserIcon(), platform.getDb().getUserName(), loginCallback);
    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {
        ShareSDK.removeCookieOnAuthorize(true);
        platform.removeAccount();
    }

    @Override
    public void onCancel(Platform platform, int i) {
        platform.removeAccount();
    }

    // 倒计时处理
    private void createAndStartCountDownTimer(final Button countDownTextView) {
        countDownTextView.setTextColor(getResources().getColor(R.color.text_view_hint));
        countDownTextView.setClickable(false);
        mCountDownTimer = new CountDownTimer(180000, 1000) {//
            @Override
            public void onTick(long millisUntilFinished) {
                countDownTextView.setText((int) (millisUntilFinished / 1000) + "秒后重新验证");
            }

            @Override
            public void onFinish() {
                countDownTextView.setClickable(true);
                countDownTextView.setEnabled(true);
                countDownTextView.setTextColor(getResources().getColor(R.color.black));
                countDownTextView.setText("获取验证码");
                countDownTextView.setTextColor(Color.parseColor("#63A3C6"));
            }
        }.start();
    }

    public boolean isCheck() {
        boolean is_ = false;
        String phoneS = phone.getText().toString().trim();
        String code = checkCode.getText().toString().trim();

        if (!CommonUtil.isUsernameValid(phoneS)) {
            AppContext.showToast("请输入正确的手机号");
        } else if (code.length() <= 0) {
            AppContext.showToast("验证码不可以为空");
        } else if (!MD5Utils.getMD5Code(code).equals(check_phoneCode)) {
            AppContext.showToast("短信校验码不正确");
        } else {
            is_ = true;
        }
        return is_;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
