package com.witmoon.xmb.activity.shoppingcart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.witmoon.xmb.R;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.UIHelper;

public class OrderPaySuccessActivity extends BaseActivity {

    private String type;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_order_pay_success;
    }

    @Override
    protected String getActionBarTitle() {
        return "支付成功";
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        setTitleColor_(R.color.main_kin);
        Button detail_btn = (Button) findViewById(R.id.detailButton);
        type = getIntent().getStringExtra("TYPE");
        if (type.equals("card")) {
            sendBroadcast(new Intent(Const.INTENT_REFRESH_CARD_ORDER));
        } else {
            sendBroadcast(new Intent(Const.INTENT_REFRESH_GOODS_ORDER));
        }
        detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("ORDER_TYPE", "");
                if (type.equals("card")) {
                    bundle.putSerializable("order_sn", getIntent().getStringExtra("ORDER_SN"));
                    UIHelper.showSimpleBack(OrderPaySuccessActivity.this, SimpleBackPage.CardOrderDetail, bundle);
                } else {
                    bundle.putSerializable("ORDER_SN", getIntent().getStringExtra("ORDER_SN"));
                    UIHelper.showSimpleBack(OrderPaySuccessActivity.this, SimpleBackPage.ORDER_DETAIL, bundle);
                }
                finish();
            }
        });

//        findViewById(R.id.finish_done).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("ORDER_ID", getIntent().getStringExtra("ORDER_ID"));
//                bundle.putSerializable("ORDER_TYPE", "");
//                UIHelper.showSimpleBack(OrderPaySuccessActivity.this, SimpleBackPage.ORDER_DETAIL, bundle);
//                finish();
//            }
//        });
    }


}
