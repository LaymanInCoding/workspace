package com.witmoon.xmb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.webview.InteractiveWebViewActivity;
import com.xiaoneng.menu.Ntalker;

import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.api.JPushInterface;

/**
 * 欢迎界面Activity
 * Created by zhyh on 2015/8/25.
 */
public class WelcomeActivity extends AppCompatActivity {
    public static final String KEY_FLAG_FIRST_LAUNCH = "app.first.launch";
    public static int is_opened = 0;
    // 企业
    String siteid = "kf_9761";
    String sdkkey = "4AE38950-F352-47F3-94EA-97C189F48B0F";
    private Timer timer = new Timer();
    private boolean isFirstLaunch;      // 是否为第一次运行

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XN_init();

        // 判断是否为第一次运行
        String flag = AppContext.getProperty(KEY_FLAG_FIRST_LAUNCH);
        if (TextUtils.isEmpty(flag) || flag.equals("true")) {
            isFirstLaunch = true;
        }

        // 倒计时, 结束后跳转到主界面
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runSwitchPage();
            }
        }, 2500);
    }

    // 根据是否为第一次启动应用, 跳转到引导页面或App主界面
    private void runSwitchPage() {
        if (isFirstLaunch) {
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("PUSH_ARGS", getIntent().getBundleExtra("PUSH_ARGS"));
            startActivity(intent);
        }
        is_opened = 1;
        finish();
    }

    private void XN_init()
    {
        int initSDK = Ntalker.getInstance().initSDK(this, siteid, sdkkey);// 初始化SDK
        if (0 == initSDK) {
            Log.e("初始化SDK", "初始化SDK成功");
        } else {
            Log.e("初始化SDK", "初始化SDK失败");
        }
        Ntalker.getInstance().enableDebug(false);// 是否开启debug模式
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
