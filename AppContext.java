package com.xiaomabao.weidian;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.WindowManager;

import com.xiaomabao.weidian.models.LoginBaseInfo;
import com.xiaomabao.weidian.models.ShareInfo;
import com.xiaomabao.weidian.models.ShopBase;
import com.xiaomabao.weidian.util.SharedPreferencesUtil;
import com.xiaomabao.weidian.util.TypefaceUtil;
import com.xiaomabao.weidian.util.XmbDB;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


/**
 * 应用Application实例
 * 初始化全局变量
 */
public class AppContext extends Application {

    public static int width = 0;
    public static int height = 0;

    public static AppContext appContext;
    public static String DBPath;
    private ArrayList<ShopBase.ShopBaseInfo.ShopShareInfo> shopShareInfoArrayList = new ArrayList<>();

    public static AppContext instance(){
        return appContext;
    }

    public void resetShopBaseInfo(){
        boolean has_default = false;
        int index  = 0;
        for (int i = 0; i < this.shopShareInfoArrayList.size(); i++) {
            ShopBase.ShopBaseInfo.ShopShareInfo share_info = this.shopShareInfoArrayList.get(i);
            if (share_info.is_default.equals("1")) {
                has_default = true;
                index = i;
            }
        }
        if (has_default == false && this.shopShareInfoArrayList.size() == 1){
            AppContext.setShopBaseInfo(appContext,shopShareInfoArrayList.get(0));
        }
        if (this.shopShareInfoArrayList.size() > 1 && has_default == false){
            AppContext.setShopBaseInfo(appContext,shopShareInfoArrayList.get(0));
        }
        if (has_default == true){
            AppContext.setShopBaseInfo(appContext,shopShareInfoArrayList.get(index));
        }
    }

    public void setShopShareInfoArrayList(ArrayList<ShopBase.ShopBaseInfo.ShopShareInfo> shareInfoArrayList){
        this.shopShareInfoArrayList = shareInfoArrayList;
        for (int i = 0; i < this.shopShareInfoArrayList.size(); i++) {
            ShopBase.ShopBaseInfo.ShopShareInfo share_info = this.shopShareInfoArrayList.get(i);
            if (share_info.is_default.equals("1")) {
                AppContext.setShopBaseInfo(appContext,share_info);
            }
        }
        resetShopBaseInfo();
    }

    public ArrayList<ShopBase.ShopBaseInfo.ShopShareInfo> getShopShareInfoArrayList(){
        return this.shopShareInfoArrayList;
    }

    public void removeShopShareInfoByIndex(int index){
        this.shopShareInfoArrayList.remove(index);
        if (this.shopShareInfoArrayList.size() == 0){
            clearShopBaseInfo(appContext);
        }
    }

    public void updateShopShareInfoByIndex(int postion,ShopBase.ShopBaseInfo.ShopShareInfo shareInfo){
        this.shopShareInfoArrayList.set(postion, shareInfo);
        resetShopBaseInfo();
    }

    public void setDefaultShareInfo(String share_id){
        for(int i = 0; i < shopShareInfoArrayList.size();i++){
            shopShareInfoArrayList.get(i).is_default = "0";
            if(shopShareInfoArrayList.get(i).id.equals(share_id)){
                shopShareInfoArrayList.get(i).is_default = "1";
            }
        }
        resetShopBaseInfo();
    }

    public void addeShopShareInfoByIndex(ShopBase.ShopBaseInfo.ShopShareInfo shareInfo){
        this.shopShareInfoArrayList.add(shareInfo);
        resetShopBaseInfo();
    }

    public void updateShopShareInfoDefault(ShopBase.ShopBaseInfo.ShopShareInfo shareInfo){
        for(int i = 0; i < shopShareInfoArrayList.size();i++){
            shopShareInfoArrayList.get(i).is_default = "0";
            if (shopShareInfoArrayList.get(i).id.equals(shareInfo.id)){
                shopShareInfoArrayList.get(i).is_default = "1";
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/FZLTXHJW.TTF");
        initDeviceInfo();
        try {
            AppContext.DBPath = initDatabaseFromLocal();
        } catch (IOException e) {
            Log.e("data", e.getMessage());
            e.printStackTrace();
        }
        appContext = this;
    }


    public void initDeviceInfo() {
        WindowManager wm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        AppContext.width = wm.getDefaultDisplay().getWidth();
        AppContext.height = wm.getDefaultDisplay().getHeight();
    }

    public static void saveShopBaseInfo(Context context, LoginBaseInfo loginBaseInfo) {
        SharedPreferencesUtil.put(context, "shop_id", loginBaseInfo.shop_id);
        SharedPreferencesUtil.put(context, "shop_share_url", loginBaseInfo.shop_share_url);
        SharedPreferencesUtil.put(context, "shop_share_vip_url", loginBaseInfo.shop_share_vip_url);
        SharedPreferencesUtil.put(context, "shop_share_qr", loginBaseInfo.shop_share_qr);
        SharedPreferencesUtil.put(context, "shop_share_vip_qr", loginBaseInfo.shop_share_vip_qr);
        SharedPreferencesUtil.put(context, "invite_url", loginBaseInfo.shop_invite_url);
        SharedPreferencesUtil.put(context, "shop_invite_qr", loginBaseInfo.shop_invite_qr);
        SharedPreferencesUtil.put(context, "shop_preview_url", loginBaseInfo.shop_preview_url);
        SharedPreferencesUtil.put(context, "user_id", loginBaseInfo.user_id);
        SharedPreferencesUtil.put(context, "username", loginBaseInfo.username);
        SharedPreferencesUtil.put(context, "shop_name", loginBaseInfo.shop_name);
        SharedPreferencesUtil.put(context, "shop_avatar", loginBaseInfo.shop_avatar);
        SharedPreferencesUtil.put(context, "shop_background", loginBaseInfo.shop_background);
        SharedPreferencesUtil.put(context, "shop_description", loginBaseInfo.shop_description);
        SharedPreferencesUtil.put(context, "token", loginBaseInfo.token);
    }

    public static void setLoginPhone(Context context, String login_phone) {
        SharedPreferencesUtil.put(context, "login_phone", login_phone);
    }

    public static String getLoginPhone(Context context) {
        return SharedPreferencesUtil.get(context, "login_phone", "") + "";
    }

    public static void setLoginPassword(Context context, String login_password) {
        SharedPreferencesUtil.put(context, "login_password", login_password);
    }

    public static String getLoginPassword(Context context) {
        return SharedPreferencesUtil.get(context, "login_password", "") + "";
    }

    public static String getShopPreviewUrl(Context context) {
        return SharedPreferencesUtil.get(context, "shop_preview_url", "") + "";
    }

    public static String getShopShareQr(Context context) {
        return SharedPreferencesUtil.get(context, "shop_share_qr", "") + "";
    }

    public static String getShopShareVipQr(Context context) {
        return SharedPreferencesUtil.get(context, "shop_share_vip_qr", "") + "";
    }

    public static String getShopInviteUrl(Context context) {
        return SharedPreferencesUtil.get(context, "invite_url", "") + "";
    }

    public static String getShopInviteQr(Context context) {
        return SharedPreferencesUtil.get(context, "shop_invite_qr", "") + "";
    }

    public static void setDeviceId(Context context, String device_id) {
        SharedPreferencesUtil.put(context, "device_id", device_id);
    }

    public static String getDeviceId(Context context) {
        return SharedPreferencesUtil.get(context, "device_id", "") + "";
    }

    public static void setShopName(Context context, String shop_name) {
        SharedPreferencesUtil.put(context, "shop_name", shop_name);
    }

    public static String getShopShareUrl(Context context) {
        return SharedPreferencesUtil.get(context, "shop_share_url", "") + "";
    }

    public static String getShopShareVipUrl(Context context) {
        return SharedPreferencesUtil.get(context, "shop_share_vip_url", "") + "";
    }

    public static void setShopAvater(Context context, String shop_avatar) {
        SharedPreferencesUtil.put(context, "shop_avatar", shop_avatar);
    }

    public static void setShopDescription(Context context, String shop_description) {
        SharedPreferencesUtil.put(context, "shop_description", shop_description);
    }

    public static void setShopBackground(Context context, String shop_background) {
        SharedPreferencesUtil.put(context, "shop_background", shop_background);
    }

    public static String getShopName(Context context) {
        return SharedPreferencesUtil.get(context, "shop_name", "") + "";
    }

    public static String getShopAvater(Context context) {
        return SharedPreferencesUtil.get(context, "shop_avatar", "") + "";
    }

    public static String getShopBackground(Context context) {
        return SharedPreferencesUtil.get(context, "shop_background", "") + "";
    }

    public static String getShopDescription(Context context) {
        return SharedPreferencesUtil.get(context, "shop_description", "") + "";
    }

    public static String getCardBindStatus(Context context) {
        return SharedPreferencesUtil.get(context, "card_bind_status", "") + "";
    }

    public static String getCardRealName(Context context) {
        return SharedPreferencesUtil.get(context, "real_name", "") + "";
    }

    public static String getCardDepositBank(Context context) {
        return SharedPreferencesUtil.get(context, "deposit_bank", "") + "";
    }

    public static String getCardBranchBank(Context context) {
        return SharedPreferencesUtil.get(context, "branch_bank", "") + "";
    }

    public static String getCardNo(Context context) {
        return SharedPreferencesUtil.get(context, "card_no", "") + "";
    }

    public static String getToken(Context context) {
        return SharedPreferencesUtil.get(context, "token", "") + "";
    }

    public static void setIsGuide(Context context) {
        SharedPreferencesUtil.put(context, "is_guide", "true");
    }

    public static void setCardBindStatus(Context context, int card_bind_status) {
        SharedPreferencesUtil.put(context, "card_bind_status", card_bind_status + "");
    }

    public static void setCardRealName(Context context, String real_name) {
        SharedPreferencesUtil.put(context, "real_name", real_name);
    }

    public static void setDepositBank(Context context, String deposit_bank) {
        SharedPreferencesUtil.put(context, "deposit_bank", deposit_bank);
    }

    public static void setBranchBank(Context context, String branch_bank) {
        SharedPreferencesUtil.put(context, "branch_bank", branch_bank);
    }

    public static void setCardNo(Context context, String card_no) {
        SharedPreferencesUtil.put(context, "card_no", card_no);
    }

    public static boolean getIsGuide(Context context) {
        return (SharedPreferencesUtil.get(context, "is_guide", "") + "").equals("true");
    }

    public static void setShopBaseInfo(Context mView, ShopBase.ShopBaseInfo.ShopShareInfo shop_share_info){
        AppContext.setShopName(mView,shop_share_info.shop_name);
        AppContext.setShopAvater(mView, shop_share_info.shop_avatar);
        AppContext.setShopBackground(mView,shop_share_info.shop_background);
        AppContext.setShopDescription(mView, shop_share_info.shop_description);
    }

    public static void clearShopBaseInfo(Context mView){
        AppContext.setShopName(mView,"");
        AppContext.setShopAvater(mView, "");
        AppContext.setShopBackground(mView,"");
        AppContext.setShopDescription(mView, "");
    }

    public static void resetShopInfo(Context mView, ShopBase shop_base) {
        AppContext.setCardBindStatus(mView, shop_base.data.card_bind_status);
        AppContext.setCardRealName(mView, shop_base.data.real_name);
        AppContext.setCardNo(mView, shop_base.data.card_no);
        AppContext.setDepositBank(mView, shop_base.data.deposit_bank);
        AppContext.setBranchBank(mView, shop_base.data.branch_bank);
    }

    public static void clearLoginInfo(Context context) {
        SharedPreferencesUtil.remove(context, "token");
    }

    public static void setCurrentUpdateVersion(Context context) {
        int version = 1;
        if ((SharedPreferencesUtil.get(context, "UpdateVersion", "") + "").equals("")) {
        } else {
            version = Integer.parseInt(SharedPreferencesUtil.get(context, "UpdateVersion", "") + "") + 1;
        }
        SharedPreferencesUtil.put(context, "UpdateVersion", version + "");
    }

    public static int getCurrentUpdateVersion(Context context) {
        if ((SharedPreferencesUtil.get(context, "UpdateVersion", "") + "").equals("")) {
            return 0;
        }
        return Integer.parseInt(SharedPreferencesUtil.get(context, "UpdateVersion", "") + "");
    }

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

    public static Float getVersionName(Activity activity) {
        String versionName = null;
        PackageManager manager = activity.getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(activity.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return Float.valueOf(versionName);
    }
}
