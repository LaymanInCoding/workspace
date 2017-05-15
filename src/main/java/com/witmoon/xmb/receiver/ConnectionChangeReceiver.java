package com.witmoon.xmb.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.util.XmbUtils;

public class ConnectionChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {

        } else {
            int status = AppContext.instance().refreshToken();
            if (status == 0) {
//                Logger.d("receiver");
                AppContext.instance().logout();
                XmbUtils.showMessage(context, "登录过期，请重新登录");
            }
        }
    }
}
