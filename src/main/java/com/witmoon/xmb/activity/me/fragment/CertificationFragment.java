package com.witmoon.xmb.activity.me.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.shoppingcart.OrderConfirmActivity;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseFragment;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.UIHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 实名认证界面
 * Created by zhyh on 2015/6/21.
 */
public class CertificationFragment extends BaseFragment implements View.OnClickListener{
    private Button submitBtn;
    private EditText mName,mIdCard;
    private String str_name,str_card;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_certification, container, false);
        submitBtn = (Button) view.findViewById(R.id.submit_button);
        mName = (EditText)view.findViewById(R.id.real_name);
        mIdCard = (EditText)view.findViewById(R.id.id_card);
        submitBtn.setOnClickListener(this);
        return view;
    }
    private void init()
    {
        //验证身份回调接口
        Listener<JSONObject>listener = new Listener<JSONObject>() {
        @Override
        public void onSuccess(JSONObject response) {
            TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
                Log.e("response", response.toString());
                try {
                    String dataObj = response.getString("msg");
                    AppContext.showToastShort(dataObj);
                    //判断是否正确
                    if (dataObj.trim().equals("修改成功")) {   //保存用户信息
                        AppContext.setProperty("user.identity_card", mIdCard + "");
                        getActivity().finish();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//            }
//            AppContext.showToastShort(twoTuple.second);
        }
    };
        str_name = mName.getText().toString().trim();
        str_card = mIdCard.getText().toString().trim();
        //检查格式
        if(checkWord())
        {
            UserApi.CheckIdCard(str_card,AppContext.getLoginInfo().getUid()+"", ApiHelper.mSessionID+"",str_name,listener);
        }
    }

    private boolean  checkWord()
    {

        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.no_network);
            return false;
        }
        if (TextUtils.isEmpty(str_name)) {
            mName.setError("姓名不能为空！");
            mName.requestFocus();
            return false;
        }
        if (TextUtils.isEmpty(str_card)) {
            mIdCard.setError("身份不能为空");
            mIdCard.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.submit_button:
                init();
                break;
        }
    }
}
