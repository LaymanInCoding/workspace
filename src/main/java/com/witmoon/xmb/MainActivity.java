package com.witmoon.xmb;

import android.*;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.duowan.mobile.netroid.Listener;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.umeng.analytics.MobclickAgent;
import com.witmoon.xmb.activity.goods.CommodityDetailActivity;
import com.witmoon.xmb.activity.specialoffer.GroupBuyActivity;
import com.witmoon.xmb.activity.specialoffer.MarketPlaceActivity;
import com.witmoon.xmb.activity.user.LoginActivity;
import com.witmoon.xmb.activity.webview.InteractiveWebViewActivity;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseActivity;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.model.SimpleBackPage;
import com.witmoon.xmb.receiver.ConnectionChangeReceiver;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.FileUtils;
import com.witmoon.xmb.util.LocalImageHelper;
import com.witmoon.xmb.util.UIHelper;
import com.witmoon.xmb.util.XmbUtils;
import com.witmoon.xmblibrary.linearlistview.util.DensityUtil;
import com.xiaoneng.menu.Ntalker;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 项目主Activity
 * Created by zhyh on 2015/6/15.
 */
public class MainActivity extends BaseActivity implements TencentLocationListener {
    public static final String KEY_TAB_CHANGED = "tabIndex";
    public static String CURRENT_ORDER_TYPE = "goods"; //goods or service
    public static double longitude, latitude;
    private long doubleExitBeginTime;
    private DrawerLayout mDrawerLayout;
    public static int screen_width;
    public static FragmentTabHost mTabHost;
    public String version_description;
    public String download_url;
    public String size;
    public float latest_version;
    public static int current_tab_index = 2;
    public String app_name;
    private int mPreparedTabIndex = -1;
    private Intent intent;
    private TencentLocationManager mLocationManager;
    // 切换Tab页广播接收器
    private BroadcastReceiver mChangTabReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mPreparedTabIndex = intent.getIntExtra(KEY_TAB_CHANGED, 0);
        }
    };
    // 切换Tab页广播接收器
    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setAlias();
        }
    };
    private ConnectionChangeReceiver myReceiver;

    @Override
    public void onLocationChanged(TencentLocation location, int error, String reason) {
        //成功
        if (TencentLocation.ERROR_OK == error) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
        } else {
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = mActivity.get();
            if (activity != null && msg.what == 1) {
                if (AppContext.isNetworkAvailable(activity)) {
                    AppContext.instance().initLoginInfo();
                }
            }
        }
    }

    private final MyHandler mHandlerLogin = new MyHandler(this);

    // 线程类
    class ThreadShow implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000 * 60 * 10);
                    Message msg = new Message();
                    msg.what = 1;
                    mHandlerLogin.sendMessage(msg);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    System.out.println("thread error...");
                }
            }
        }
    }

    @Override
    public void onStatusUpdate(String name, int status, String desc) {
        // do your work

    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initialize(Bundle savedInstanceState) {
        registerReceiver(mChangTabReceiver, new IntentFilter(Const.INTENT_ACTION_MAIN_TAB));
        registerReceiver(mLoginReceiver, new IntentFilter(Const.INTENT_ACTION_REG_PUSH));
        checkVersion();
        WindowManager wm = (WindowManager) this
                .getSystemService(Context.WINDOW_SERVICE);

        MainActivity.screen_width = wm.getDefaultDisplay().getWidth();
        //注册网络监听事件
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        myReceiver = new ConnectionChangeReceiver();
        this.registerReceiver(myReceiver, filter);

        new Thread(new ThreadShow()).start();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
            }

            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
            }
        });

        if (LocalImageHelper.check_permision(this)) {
            LocalImageHelper.init(AppContext.instance());
            LocalImageHelper.getInstance().clearCache();
        }

        if (android.os.Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        2);
            }
        } else {
            // 创建定位请求
            TencentLocationRequest request = TencentLocationRequest.create();
            mLocationManager = TencentLocationManager.getInstance(this);
            // 修改定位请求参数, 定位周期 1 min
            request.setInterval(60 * 1000);
            request.setRequestLevel(0);
            // 开始定位
            mLocationManager.requestLocationUpdates(request, this);
        }

        mTabHost = (FragmentTabHost) findViewById(R.id.main_tab_host);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.main_tab_layout);
        initMainTabs();
        mTabHost.setCurrentTab(2);
        mTabHost.getTabWidget().setDividerDrawable(null);

        setTitleColor_(R.color.main_kin);


        Bundle bundle = getIntent().getBundleExtra("PUSH_ARGS");
        if (bundle != null) {
            String type = bundle.getString("type");
            String id = bundle.getString("id");
            if (type.equals("goods")) {
                Intent intent_goods = new Intent(this, CommodityDetailActivity.class);
                intent_goods.putExtra("GOODS_ID", id);
                intent_goods.putExtra("ACTION_ID", "");
                startActivity(intent_goods);
            } else if (type.equals("topic")) {
                MarketPlaceActivity.start(this, id);
            } else if (type.equals("group")) {
                GroupBuyActivity.start(this, id);
            } else if (type.equals("web")) {
                Intent intent = new Intent(this, InteractiveWebViewActivity.class);
                intent.putExtra("url", id);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                LocalImageHelper.init(AppContext.instance());
                LocalImageHelper.getInstance().clearCache();

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.

                return;
            }
        } else if (requestCode == 2) {
            // 创建定位请求
            TencentLocationRequest request = TencentLocationRequest.create();
            mLocationManager = TencentLocationManager.getInstance(this);
            // 修改定位请求参数, 定位周期 1 min
            request.setInterval(60 * 1000);
            request.setRequestLevel(0);
            // 开始定位
            mLocationManager.requestLocationUpdates(request, this);
        }
    }

    private void initMainTabs() {
        AssetManager mgr = getAssets();//得到AssetManager
        Typeface tf = Typeface.createFromAsset(mgr, "fonts/font.otf");//根据路径得到Typeface
        MainTab[] tabs = MainTab.values();
        for (MainTab tab : tabs) {
            View view = LayoutInflater.from(this).inflate(R.layout.vertical_image_text_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.indicator_img);
            imageView.setImageResource(tab.getResIcon());
            TextView textView = (TextView) view.findViewById(R.id.indicator_label);
            textView.setText(getString(tab.getResName()));
            textView.setTypeface(tf);
            mTabHost.addTab(mTabHost.newTabSpec(getString(tab.getResName())).setIndicator(view),
                    tab.getClz(), null);
        }
    }

    public void changeToTab(int index) {
        mTabHost.setCurrentTab(index);
        MainActivity.current_tab_index = index;
    }

    // 打开右边的抽屉布局
    public void openRightDrawer() {
        mDrawerLayout.openDrawer(Gravity.RIGHT);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (System.currentTimeMillis() - doubleExitBeginTime > 2000) {
                CommonUtil.show(this, "再按一次返回退出程序", 1000);
                doubleExitBeginTime = System.currentTimeMillis();
            } else {
                finish();
                MobclickAgent.onProfileSignOff();
                if (mLocationManager != null) {
                    mLocationManager.removeUpdates(this);
                }
                System.exit(0);
            }
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    public void checkVersion() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        });
        builder.setCancelable(false);
        UserApi.Upversion("android", new Listener<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {
                //先取版本号，和本地比对。
                Log.e("Response", response.toString());
                try {
                    latest_version = Float.parseFloat(response.getString("latest_version"));
                    version_description = response.getString("version_description");
                    download_url = response.getString("download_url");
                    size = response.getString("size");
                    app_name = download_url.substring(download_url.lastIndexOf("/") + 1);
                    if (latest_version > Float.parseFloat(AppContext.geVerSion())) {
                        XmbUtils.showUpdateWindow(MainActivity.this, latest_version + "", version_description, download_url, size);
                    } else {
                        FileUtils.cheanUpdateFile();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MainActivity.current_tab_index != mTabHost.getCurrentTab())
            changeToTab(MainActivity.current_tab_index);
        else
            MainActivity.current_tab_index = mTabHost.getCurrentTab();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != intent)
            stopService(intent);
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(this);
        }
        unregisterReceiver(mChangTabReceiver);
        unregisterReceiver(mLoginReceiver);
        System.exit(0);
    }

    private void setAlias() {
        // 调用 Handler 来异步设置别名
        Set<String> mSet = new HashSet<>();
        mSet.add(AppContext.getLoginUid() + "");
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, mSet));
    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            switch (code) {
                case 0:
                    break;
                case 6002:
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000);
                    break;
                default:
                    break;
            }
        }
    };
    private static final int MSG_SET_ALIAS = 1001;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(MainActivity.this,
                            null,
                            (Set) msg.obj,
                            mAliasCallback);
                    break;
                default:
            }
        }
    };
}
