package com.witmoon.xmb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.lzy.okgo.OkGo;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.witmoon.xmb.api.ApiHelper;
import com.witmoon.xmb.api.Netroid;
import com.witmoon.xmb.api.UserApi;
import com.witmoon.xmb.base.BaseApplication;
import com.witmoon.xmb.base.Const;
import com.witmoon.xmb.db.XmbDB;
import com.witmoon.xmb.model.User;
import com.witmoon.xmb.util.CommonUtil;
import com.witmoon.xmb.util.TwoTuple;
import com.witmoon.xmb.util.TypefaceUtil;
import com.xiaoneng.menu.Ntalker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import cn.jpush.android.api.JPushInterface;

/**
 * 应用Application实例
 * Created by zhyh on 2015/6/15.
 */
public class AppContext extends BaseApplication {

    private static final String KEY_LOGIN_ID = "key_login_id";
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    public static String mDownloadPath = "";
    private XmbDB mXmbDB;
    private static AppContext instance;
    public static boolean IS_ADDBABY = true;
    public static DisplayImageOptions options_disk, options_memory;
    private int status = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        options_disk = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(false) // default
                .cacheInMemory(false) // default
                .cacheOnDisk(true) // default
                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .build();
        OkGo.init(this);
        options_memory = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(false) // default
                .cacheInMemory(true) // default
                .cacheOnDisk(false) // default
                .considerExifParams(false) // default
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
                .bitmapConfig(Bitmap.Config.ARGB_8888) // default
                .displayer(new SimpleBitmapDisplayer()) // default
                .build();

        Netroid.initialize(this);
        Fresco.initialize(this);
        initImageLoader(this);
        Logger.init().hideThreadInfo();
        // 初始化Netroid网络工具及Fresco
        // 初始化数据库
        initializeDatabase();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/FZLTXHJW.TTF"); // font from assets: "assets/fonts/Roboto-Regular.ttf
        //Bugly会为您检测使用环境并自动完成配置
        CrashReport.initCrashReport(AppContext.this, "900007590", false);
        mDownloadPath = "/" + getString(R.string.app_name) + "/download";

        //初始化tbs x5 webview
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                Log.e("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub

            }
        };
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Log.d("app", "onDownloadFinish");
            }

            @Override
            public void onInstallFinish(int i) {
                Log.d("app", "onInstallFinish");
            }

            @Override
            public void onDownloadProgress(int i) {
                Log.d("app", "onDownloadProgress:" + i);
            }
        });

        QbSdk.initX5Environment(getApplicationContext(), cb);
        TbsDownloader.needDownload(getApplicationContext(), false);
    }

    public String getCachePath() {
        File cacheDir;
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir = getExternalCacheDir();
        else
            cacheDir = getCacheDir();
        if (!cacheDir.exists())
            cacheDir.mkdirs();
        return cacheDir.getAbsolutePath();
    }

    // 获取AppContext实例
    public static AppContext instance() {
        return instance;
    }

    private void initializeDatabase() {
        try {
            String dbPath = initDatabaseFromLocal();
            mXmbDB = XmbDB.getInstance(this, dbPath);
            if (!mXmbDB.tabIsExist("search_ser")) {
                mXmbDB.addServiceTable();
            }
            if (!mXmbDB.tabIsExist("search")) {
                //可能升级数据库偏差   ----   所以换种思路
                mXmbDB.addTable();
            }
            if (!mXmbDB.tabIsExist("search_mbq")) {
                //可能升级数据库偏差   ----   所以换种思路
                mXmbDB.addMbqSearchTable();
            } else {
                Log.e("search", "数据库已存在！");
            }
        } catch (IOException e) {
            Log.e("INIT_DB", e.getMessage());
        }
    }

    public static void showToast(String msg) {
        CommonUtil.show(instance, msg, 1500);
    }

    public static void showToastShort(String msg) {
        CommonUtil.show(instance, msg, 1000);
    }

    /**
     * 初始化数据库对象, 初次启动需要将数据库文件拷贝到目标路径
     *
     * @throws IOException
     */
    private String initDatabaseFromLocal() throws IOException {
        File dbFileDir = getDir("db_files", MODE_PRIVATE);
        File targetDbFile = new File(dbFileDir, XmbDB.DB_NAME);
        if (!targetDbFile.exists()) {
            InputStream is = getAssets().open("city.db");
            OutputStream os = new FileOutputStream(targetDbFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            is.close();
            os.flush();
            os.close();
        }
        return targetDbFile.getAbsolutePath();
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you
        // may tune some of them,
        // or you can create default configuration by
        // ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .memoryCacheSize(2 * 1024 * 1024) //缓存到内存的最大数据
                .memoryCacheSize(20 * 1024 * 1024) //设置内存缓存的大小
                .diskCacheFileCount(500)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

    // ---------------------------------- 用户登录相关 ---------------------------------

    // 用户注销
    public void logout() {
        MobclickAgent.onProfileSignOff();
        saveLoginUid(0);
        clearLoginInfo();
        ApiHelper.clearSession();
        // 发布退出登录广播
        Intent intent = new Intent(Const.INTENT_ACTION_LOGOUT);
        sendBroadcast(intent);
        Intent intent2 = new Intent(Const.INTENT_ACTION_REFRESH_MY_MBQ);
        sendBroadcast(intent2);
        Intent intent3 = new Intent(Const.INTENT_ACTION_REFRESH_FEATURE);
        sendBroadcast(intent3);
    }

    public int refreshToken() {
        if (AppContext.instance().isLogin()) {
            UserApi.refresh_token(new Listener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    Logger.json(response.toString());
                    if (response.has("info")) {
                        status = 0;
                    } else {
                        status = 1;
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    logout();
                }
            });
            return status;
        } else
            return -1;
    }

    // 初始化用户登录信息
    public void initLoginInfo() {

        final User user = getLoginInfo();
        if (user == null || user.getUid() == 0) {
            Logger.d("user");
            logout();
            return;
        }
        //取得登陆类型作比对，用作自动登录。
        //本地登录
        if (user.getIs_Or().equals("ZH")) {
            UserApi.login1(user.getType(), user.getSin_name(), new Listener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
                    if (!twoTuple.first) {
                        logout();
                        return;
                    }
                    try {
                        User user2 = User.parse(response.getJSONObject("data").getJSONObject("user"), user.getType(), user.getSin_name(), "ZH");
                        if (user2.getUid() != user.getUid()) {
                            Intent intent = new Intent(Const.INTENT_ACTION_REG_PUSH);
                            sendBroadcast(intent);

                            Intent intent2 = new Intent(Const.INTENT_WEB_REFRESH);
                            sendBroadcast(intent2);
                        }
                        JSONObject sessionObj = response.getJSONObject("data").getJSONObject("session");
                        String sessionID = sessionObj.getString("sid");
                        user2.setSid(sessionID);
                        AppContext.saveLoginInfo(user2);
                        if (!sessionID.equals("")) {
                            ApiHelper.setSessionID(sessionID);
                        }
                        int login = Ntalker.getInstance().login(AppContext.this, String.valueOf(user.getUid()),
                                user.getName(), "1");// 登录时调
                        if (0 != login) {
                        }
                    } catch (JSONException e) {
                        logout();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                }
            });
        } else {
            UserApi.login(user.getType(), user.getSin_name(), user.getAvatar(), user.getName(), new Listener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject response) {
                    TwoTuple<Boolean, String> twoTuple = ApiHelper.parseResponseStatus(response);
                    if (!twoTuple.first) {
                        logout();
                        return;
                    }
                    try {
                        JSONObject sessionObj = response.getJSONObject("data").getJSONObject("session");
                        if (user.getUid() == AppContext.getLoginUid()) {
                            Intent intent = new Intent(Const.INTENT_ACTION_REG_PUSH);
                            sendBroadcast(intent);
                        }
                        String sessionID = sessionObj.getString("sid");
                        ApiHelper.setSessionID(sessionID);
                        User tmpUser = User.parse(response.getJSONObject("data").getJSONObject("user"), user.getType(), user.getSin_name(), "");
                        tmpUser.setSid(sessionID);
                        AppContext.saveLoginInfo(tmpUser);
                        int login = Ntalker.getInstance().login(AppContext.this, String.valueOf(user.getUid()),
                                user.getName(), "1");// 登录时调
                        if (0 != login) {
                            Log.e("小能", "userid登录失败");
                            Log.e("打印错误码：", login + "");
                        }
                    } catch (JSONException e) {
                        logout();
                    }
                }

                @Override
                public void onError(NetroidError error) {
                    //logout();
                }
            });
        }
    }

    // 保存登录信息
    public static void saveLoginInfo(User user) {
        saveLoginUid(user.getUid());
        setProperty("user.identity_card", user.getIdentity_card() + "");
        setProperty("user.account", user.getAccount());
        setProperty("user.pwd", user.getPassword());
        setProperty("user.name", user.getName());
        setProperty("user.avatar", user.getAvatar());
        //用户ID
        setProperty("user.uid", String.valueOf(user.getUid()));
        setProperty("user.sid", user.getSid());
        setProperty("user.baby_birthday", user.getBabyBirthday());
        setProperty("user.due_day", user.getDueDay());
        setProperty("user.parent_sex", user.getParentSex());
        setProperty("user.baby_sex", user.getChildSex());
        setProperty("user.type", user.getType());
        setProperty("user.sin_name", user.getSin_name());
        setProperty("user.is_or", user.getIs_Or());
        setProperty("user.baby_id", user.getBaby_id());
        setProperty("user.baby_birthday", user.getBaby_birthday());
        setProperty("user.baby_gender", user.getBaby_gender());
        setProperty("user.baby_photo", user.getBaby_photo());
        setProperty("user.mobile_phone", user.getMobile_phone());
        setProperty("user.baby_nickname", user.getBaby_nickname());
        setProperty("user.is_baby_add", user.getIs_baby_add());
    }

    // 获取登录用户信息
    public static User getLoginInfo() {
        User user = new User();
        user.setAccount(getProperty("user.account"));
        user.setPassword(getProperty("user.pwd"));
        user.setName(getProperty("user.name"));
        user.setUid(getLoginUid());
        user.setSid(getProperty("user.sid"));
        user.setAvatar(getProperty("user.avatar"));
        user.setBabyBirthday(getProperty("user.baby_birthday"));
        user.setDueDay(getProperty("user.due_day"));
        user.setIdentity_card(getProperty("user.identity_card"));
        user.setParentSex(getProperty("user.parent_sex"));
        user.setChildSex(getProperty("user.baby_sex"));
        user.setType(getProperty("user.type"));
        user.setSin_name(getProperty("user.sin_name"));
        user.setIs_Or(getProperty("user.is_or"));
        user.setBaby_id(getProperty("user.baby_id"));
        user.setBaby_birthday(getProperty("user.baby_birthday"));
        user.setBaby_gender(getProperty("user.baby_gender"));
        user.setBaby_photo(getProperty("user.baby_photo"));
        user.setBaby_nickname(getProperty("user.baby_nickname"));
        user.setMobile_phone(getProperty("user.mobile_phone"));
        user.setIs_baby_add(getProperty("user.is_baby_add"));
        return user;
    }

    // 清除登录信息
    public static void clearLoginInfo() {
        String[] keys = {
                KEY_LOGIN_ID,
                "user.account",
                "user.pwd",
                "user.name",
                "user.avatar",
                "user.uid",
                "user.sid",
                "user.identity_card",
                "user.type",
                "user.sin_name",
                "loginType",
                "user.baby_id",
                "user.baby_birthday",
                "user.baby_gender",
                "user.baby_photo",
                "user.baby_nickname",
                "user.is_baby_add"

        };
        removeProperty(keys);
    }

    // 判断用户是否登录
    public boolean isLogin() {
        return getLoginUid() != 0;
    }

    // 获取登录用户id
    public static int getLoginUid() {
        return getPreferences().getInt(KEY_LOGIN_ID, 0);
    }

    public static void saveLoginUid(int uid) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putInt(KEY_LOGIN_ID, uid);
        editor.apply();
    }

    // ----------------------------------- 用户登录相关END ------------------------------------

    public static void setProperty(String key, String value) {
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getProperty(String key) {
        return getPreferences().getString(key, "");
    }

    public static void removeProperty(String... keys) {
        for (String key : keys) {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.remove(key);
            apply(editor);
        }
    }

    public XmbDB getXmbDB() {
        return mXmbDB;
    }
    // --------------------------------------------------------------------------

    /**
     * 清除缓存目录
     *
     * @param dir     目标目录
     * @param curTime 当前系统时间
     * @return 清除数量
     */
    private int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    // 清除缓存
    public void clearAppCache() {
        clearCacheFolder(getFilesDir(), System.currentTimeMillis());
        clearCacheFolder(getCacheDir(), System.currentTimeMillis());
        clearCacheFolder(getExternalCacheDir(), System.currentTimeMillis());
    }

    //获取渠道
    public static String getChannels() {
        return AnalyticsConfig.getChannel(context());
    }

    //获取器当前的版本号

    public static String geVerSion() {
        String infoVsion = null;
        PackageManager manager = context().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(context().getPackageName(), 0);
            infoVsion = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return infoVsion;
    }

    //保存登录类型
    public static void isOrLogin(boolean isLogin) {
        setProperty("loginType", isLogin + "");
    }

    //设置宝宝添加状态
    public static void addBaby(String isLogin) {
        setProperty("user.is_baby_add", isLogin + "");
    }

    //设置宝宝名字、性别
    public static void addBaby_name(String isLogin, String baby_gender, String baby_photo) {
        setProperty("user.baby_nickname", isLogin + "");
        setProperty("user.baby_gender", baby_gender + "");
        setProperty("user.baby_photo", baby_photo + "");
    }

    //取出，做比对
    public static boolean is_login_or() {
        return Boolean.parseBoolean(getProperty("loginType"));
    }

    //判断当前的网络
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
        } else {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            NetworkInfo[] info = cm.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
