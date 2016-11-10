package com.witmoon.xmb.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.WelcomeActivity;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.webview.InteractiveWebViewActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.SharedPreferencesUtil;
import com.witmoon.xmb.util.SystemUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 *
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            SharedPreferencesUtil.remove(context, Const.MBQ_MESSAGE_TIP);
            try {
                JSONObject jsonObject = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                if((AppContext.getLoginUid()+"").equals(jsonObject.getString("uid"))){
                    SharedPreferencesUtil.put(context, Const.MBQ_MESSAGE_TIP,jsonObject.getString("new_message"));
                    Intent intent1 = new Intent(Const.MBQ_MESSAGE_PUSH);
                    context.sendBroadcast(intent1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if(JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())){
            try {
                JSONObject jsonObject = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                if(SystemUtils.isAppAlive(context, "com.witmoon.xmb") && WelcomeActivity.is_opened == 1) {
                    String id = jsonObject.getString("id");
                    String type = jsonObject.getString("type");
                    if (type.equals("goods")) {
                        Intent intent_goods = new Intent(context, CommodityDetailActivity.class);
                        intent_goods.putExtra("GOODS_ID", id);
                        intent_goods.putExtra("ACTION_ID", "");
                        intent_goods.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent_goods);
                    }else if(type.equals("topic")){
                        Intent intent_topic = new Intent(context, MarketPlaceActivity.class);
                        intent_topic.putExtra("M_ID", id);
                        intent_topic.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent_topic);
                    }else if(type.equals("group")){
                        Intent intent_group = new Intent(context, GroupBuyActivity.class);
                        intent_group.putExtra("LINK", id);
                        intent_group.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent_group);
                    }else if(type.equals("web")){
                        Intent intent_group = new Intent(context, InteractiveWebViewActivity.class);
                        intent_group.putExtra("url", id);
                        intent_group.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent_group);
                    }
                }else{
                    Intent launchIntent = context.getPackageManager().
                            getLaunchIntentForPackage("com.witmoon.xmb");
                    launchIntent.setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                    Bundle args = new Bundle();
                    args.putString("type", jsonObject.getString("type"));
                    args.putString("id", jsonObject.getString("id"));
                    launchIntent.putExtra("PUSH_ARGS", args);
                    context.startActivity(launchIntent);
                }
            }catch (JSONException e) {
                Intent launchIntent = context.getPackageManager().
                        getLaunchIntentForPackage("com.witmoon.xmb");
                launchIntent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                context.startActivity(launchIntent);
            }
        }
    }
}
