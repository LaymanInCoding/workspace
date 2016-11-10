package com.witmoon.xmb.activity.service;

import android.os.Bundle;
import android.widget.TextView;

import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseActivity;

public class ReturnSubmitSuccessActivity extends BaseActivity {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.content_return_submit_success;
    }

    @Override
    protected String getActionBarTitle() {
        return "申请退款成功";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        TextView text3 = (TextView) findViewById(R.id.text3);
        TextView returning_money = (TextView) findViewById(R.id.returning_money);
        returning_money.setText(getIntent().getStringExtra("returning_money"));
        text3.setText(getIntent().getStringExtra("current_date"));
    }

}
