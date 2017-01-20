package com.witmoon.xmb;

import android.content.Context;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by de on 2017/1/17.
 */
public class UmengStatic{

    //友盟点击统计
    public static void registStat(Context context,String id){
        if (context != null){
            MobclickAgent.onEvent(context,id);
        }
    }

}
