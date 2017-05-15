package com.witmoon.xmb.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.witmoon.xmb.MainActivity;
import com.witmoon.xmb.R;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.util.CommonUtil;
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
        Logger.d(resp.toString());
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
                CommonUtil.show(this, "用户已取消", 1000);
                finish();
            }
        }
    }
}
