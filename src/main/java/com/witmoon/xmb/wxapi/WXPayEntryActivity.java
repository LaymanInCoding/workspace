package com.witmoon.xmb.wxapi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.activity.service.ServicePaySuccessActivity;
import com.witmoon.xmb.activity.shoppingcart.OrderSubmitSuccessActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.wxapi.simcpux.Constants;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);

        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                if (MainActivity.CURRENT_ORDER_TYPE == "service") {
                    Intent intent = new Intent(Const.WX_CALLBACK);
                    intent.putExtra("code", 1);
                    sendBroadcast(intent);
                }else if(MainActivity.CURRENT_ORDER_TYPE == "goods"){
                    Intent intent = new Intent(Const.WX_CALLBACK);
                    intent.putExtra("code", 2);
                    sendBroadcast(intent);
                }
                finish();
            }
            if (resp.errCode == -1) {
                Intent intent = new Intent(Const.WX_CALLBACK);
                intent.putExtra("code", 0);
                sendBroadcast(intent);
                CommonUtil.show(this, "支付失败", 1000);
                finish();
            } else if (resp.errCode == -2) {
                finish();
            }
        }
    }
}
