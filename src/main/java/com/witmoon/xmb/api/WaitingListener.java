package com.witmoon.xmb.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.duowan.mobile.netroid.Listener;

import org.json.JSONObject;

/**
 * Created by zhyh on 2015/7/2
 */
public abstract class WaitingListener extends Listener<JSONObject> {

    private Context mContext;
    private ProgressDialog mProgressDialog;

    public WaitingListener(Context context) {
        this.mContext = context;
    }

    @Override
    public void onPreExecute() {
        mProgressDialog = ProgressDialog.show(mContext, "", "请稍候...", true, true, new
                DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
//                Netroid.cancelRequest(REQ_TAG);
            }
        });
    }

    @Override
    public void onFinish() {
        mProgressDialog.cancel();
    }
}
