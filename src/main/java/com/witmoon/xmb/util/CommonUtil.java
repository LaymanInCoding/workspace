package com.witmoon.xmb.util;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 封装一些常用的工具方法
 * Created by zhyh on 2015/4/30.
 */
public class CommonUtil {

    //用于匹配手机号码
    public final static String REGEX_MOBILE = "^0?1[34578]\\d{9}$";
    // 邮箱匹配正则表达式
    public final static String REGEX_EMAIL = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1," +
            "3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
    public final static String REGEX_UNAME = "[\\w\\d_]{4,20}";
    public static final String REGEX_PWD = ".{4,20}";

    private static Pattern PATTERN_MOBILE;
    private static Pattern PATTERN_EMAIL;
    private static Pattern PATTERN_UNAME;
    private static Pattern PATTERN_PWD;

    static {
        PATTERN_MOBILE = Pattern.compile(REGEX_MOBILE);
        PATTERN_EMAIL = Pattern.compile(REGEX_EMAIL);
        PATTERN_UNAME = Pattern.compile(REGEX_UNAME);
        PATTERN_PWD = Pattern.compile(REGEX_PWD);
    }

    private CommonUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    // -------------------------- 输入校验 --------------------------

    /**
     * 判断是否为手机号码
     *
     * @param number 手机号码
     */
    public static boolean isMobilePhone(String number) {
        Matcher match = PATTERN_MOBILE.matcher(number);
        return match.matches();
    }

    /**
     * 验证身份证号码
     * @param idCard 居民身份证号码15位或18位，最后一位可能是数字或字母
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkIdCard(String idCard) {
        String regex = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";
        return Pattern.matches(regex,idCard);
    }

    // 判断email格式是否正确
    public static boolean isEmail(String email) {
        Matcher m = PATTERN_EMAIL.matcher(email);
        return m.matches();
    }

    // 判断用户名格式是否正确
    public static boolean isUsernameValid(String uname) {
        Matcher matcher = PATTERN_UNAME.matcher(uname);
        return matcher.matches();
    }

    // 判断密码是否符合规定格式
    public static boolean isPasswordValid(String pwd) {
        Matcher matcher = PATTERN_PWD.matcher(pwd);
        return matcher.matches();
    }

    // ----------------------- Toast统一管理 -------------------------

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showShort(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, CharSequence message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param context
     * @param message
     */
    public static void showLong(Context context, int message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, CharSequence message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param message
     * @param duration
     */
    public static void show(Context context, int message, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    // -----------------------------------------------------------------------

    /**
     * 判断网络请求返回数据是否成功, 如果没有成功返回错误信息
     * @param response 网络请求JSON格式的响应数据
     *                 { data : { ok : 1 }, status : { succeed : 1 } }
     * @return 二元组, 第一个元素为Boolean类型的成功标识; 第二个元素为错误信息, 成功时为null
     */
    public static TwoTuple<Boolean, String> networkStatus(JSONObject response) {
        try {
            JSONObject statusObj = response.getJSONObject("status");
            int succeed = (int) statusObj.get("succeed");
            if (succeed == 1) {
                return TwoTuple.tuple(true, null);
            }
            return TwoTuple.tuple(false, statusObj.get("error_desc").toString());
        } catch (JSONException e) {
            return TwoTuple.tuple(false, e.getMessage());
        }
    }

}
