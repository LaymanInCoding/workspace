package com.witmoon.xmb.activity.me.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.R;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.Out_;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.XmbUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by de on 2015/12/5.
 */
public class Fill_info_Fragent extends BaseActivity implements View.OnClickListener{
    private EditText express_no,express_fee;
    private LinearLayout cwhi;
    private Out_ out;

    @Override
    protected int getLayoutResourceId() {
       return R.layout.fragment_fill_info;
    }

    protected int getActionBarTitleByResId() {
        return R.string.text_wait_for_out_price1;
    }
    @Override
    protected void configActionBar(Toolbar toolbar) {
        toolbar.setBackgroundColor(getResources().getColor(R.color.master_me));
        setTitleColor_(R.color.master_me);
    }
    @Override
    protected void initialize(Bundle savedInstanceState) {
        super.initialize(savedInstanceState);
        out = (Out_) getIntent().getSerializableExtra("order");
        cwhi = (LinearLayout) findViewById(R.id.fuc_Sa);
        express_no = (EditText) findViewById(R.id.express_no);
        express_fee = (EditText) findViewById(R.id.express_fee);
        findViewById(R.id.submit_button1).setOnClickListener(this);
        findViewById(R.id.submit_button).setOnClickListener(this);
        if (!out.getBack_tax().equals("1"))
        {
            cwhi.setVisibility(View.GONE);
        }
    }

    private boolean is_Check_()
    {
        boolean is_ = false;
        if (express_no.getText().toString().trim().equals(""))
        {
            XmbUtils.showMessage(Fill_info_Fragent.this,"请填写快递单号!");
        }else if (cwhi.getVisibility()==View.VISIBLE)
        {
            if (express_fee.getText().toString().equals(""))
            {
                XmbUtils.showMessage(Fill_info_Fragent.this, "请填写运费!");
            }else{
                is_ = true;
            }
        }else {
            is_ = true;
        }
        return is_;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.submit_button1:
                if (AppContext.isNetworkAvailable(getBaseContext()))
                {
                    if (is_Check_())
                    {
                        UserApi.fill_waybill(out.getOrder_id(), express_no.getText().toString().trim(), express_fee.getText().toString().trim(), new Listener<JSONObject>() {
                            @Override
                            public void onSuccess(JSONObject response) {
                                try {
                                    JSONObject js = response.getJSONObject("status");
                                    if (js.getString("error_code").equals("20003")) {
                                        AppContext.showToast(js.getString("error_desc"));
                                        return;
                                    }
                                    AppContext.showToast(js.getString("error_desc"));
                                    Intent intent = new Intent(Const.INTENT_ACTION_TUI);
                                    out.setRefund_status("2");
                                    intent.putExtra("order", out);
                                    sendBroadcast(intent);
                                    finish();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(NetroidError error) {
                                super.onError(error);
                                executeOnLoadDataError(error.getMessage());
                            }
                        });
                    }
                }else
                {
                    XmbUtils.showMessage(Fill_info_Fragent.this,"网络连接失败!");
                }
                break;
            case R.id.submit_button:
                finish();
                break;
        }
    }
    private void executeOnLoadDataError(String error) {

        String message = error;
        if (TextUtils.isEmpty(error)) {
            if (TDevice.hasInternet()) {
                message = getString(R.string.tip_load_data_error);
            } else {
                message = getString(R.string.tip_network_error);
            }
        }
        XmbUtils.showMessage(Fill_info_Fragent.this, message);
    }
}
