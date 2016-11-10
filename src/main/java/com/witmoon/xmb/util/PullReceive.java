package com.witmoon.xmb.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.witmoon.xmb.AppContext;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by de on 2016/3/11.
 */
public class PullReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.EXTRA_REGISTRATION_ID.equals(intent.getAction()))
        {
            Log.e("个人注册ID", JPushInterface.getRegistrationID(context));
            AppContext.showToast(bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID)+"");
        }
    }
}
