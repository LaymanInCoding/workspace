package com.witmoon.xmb.api;

import android.util.Log;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.model.User;
import com.witmoon.xmb.util.TDevice;
import com.witmoon.xmb.util.TwoTuple;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 接口帮助类
 * Created by zhyh on 2015/7/1.
 */
public class ApiHelper {
    public static final int PAGE_SIZE = TDevice.getPageSize();
//
    public static final String API_URL = "https://api.xiaomabao.com/mobile/?url=%s";
    public static final String MBQZ_API_URL = "https://api.xiaomabao.com/mobile/?url=%s";
    public static final String BASE_URL = "https://api.xiaomabao.com/";
//    public static final String BASE_URL = "http://192.168.10.230/";
    public static final String HOME_URL = "http://www.xiaomabao.com/";
    public static final String GOODS_LINK_MODE = "https://www.xiaomabao.com/goods-%s.html";
    public static final String MARKET_LINK_MODE = "https://www.xiaomabao.com/topic.php?topic_id=%s";
//
//    public static final String API_URL = "http://192.168.10.202/?url=%s";
//    public static final String MBQZ_API_URL = "http://192.168.10.202/?url=%s";
//    public static final String BASE_URL = "http://192.168.10.202/";
//    public static final String GOODS_LINK_MODE = "http://192.168.10.202/goods-%s.html";
//    public static final String MARKET_LINK_MODE = "http://192.168.10.202/topic.php?topic_id=%s";
//
//    public static final String API_URL = "http://192.168.11.36/mobile/?url=%s";
//    public static final String MBQZ_API_URL = "http://192.168.11.36/mobile/?url=%s";
//    public static final String BASE_URL = "http://192.168.11.36/";
//    public static final String GOODS_LINK_MODE = "http://192.168.11.36/goods-%s.html";
//    public static final String MARKET_LINK_MODE = "http://192.168.11.36/topic.php?topic_id=%s";

    //    public static final String API_URL = "http://172.16.1.122/mobile/?url=%s";
//    public static final String MBQZ_API_URL = "http://172.16.1.122/mobile?url=%s";
//    public static final String BASE_URL = "http://172.16.1.122/";
    public static String mSessionID;

    public static void setSessionID(String sessionID) {
        mSessionID = sessionID;
    }

    public static void clearSession() {
        mSessionID = "";
    }

    public static String getGoodsShareLinkUrl(String id) {
        return String.format(GOODS_LINK_MODE, id);
    }

    public static String getMarketShareLinkUrl(String id) {
        return String.format(MARKET_LINK_MODE, id);
    }

    /**
     * 创建附带用户登录信息的HTTP请求参数对象JSONObject
     *
     * @param pm 其它参数Map<String, String>
     * @return JSONObject
     */
    public static JSONObject getParamObj(Map<String, String> pm) {
//
        JSONObject paramObj = pm == null ? new JSONObject() : new JSONObject(pm);
        JSONObject sessionObj = new JSONObject();
        try {
            sessionObj.put("uid", AppContext.getLoginUid());
            sessionObj.put("sid", AppContext.getLoginInfo().getSid());
            paramObj.put("session", sessionObj);
            paramObj.put("version", AppContext.geVerSion());
            paramObj.put("channel", AppContext.getChannels());
            paramObj.put("device", "android");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return paramObj;
    }

    /**
     * 创建附带用户登录信息的HTTP请求参数对象Map<String,String>
     *
     * @param pm 其它参数Map<String, String>
     * @return Map<String,String>
     */
    public static Map<String, String> getParamMap(Map<String, String> pm) {
//
        Map<String, String> paramObj = pm == null ? new HashMap<>() : pm;
        paramObj.put("session[uid]", AppContext.getLoginUid() + "");
        paramObj.put("session[sid]", AppContext.getLoginInfo().getSid());
        paramObj.put("version", AppContext.geVerSion());
        paramObj.put("channel", AppContext.getChannels());
        paramObj.put("device", "android");
        return paramObj;
    }

    /**
     * 获取分页信息的JSONObject
     *
     * @param p  页码
     * @param jo 原始参数JSONObject
     * @return 附带分页信息的参数JSONObject
     */
    public static JSONObject getPaginationParamObj(int p, JSONObject jo) {
        return getPaginationParamObj(p, PAGE_SIZE, jo);
    }

    public static JSONObject getPaginationParamObj(int p, Map<String, String> pm) {
        pm.put("version", AppContext.geVerSion());
        pm.put("channel", AppContext.getChannels());
        pm.put("device", "android");
        return getPaginationParamObj(p, new JSONObject(pm));
    }

    public static JSONObject getPaginationParamObj(int p, int ps, JSONObject jo) {
        JSONObject pageObj = new JSONObject();
        try {
            pageObj.put("page", p);
            pageObj.put("count", ps);
            jo.put("pagination", pageObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jo;
    }

    // 获取服务器接口的绝对URL
    public static String getAbsoluteApiUrl(String url) {
        return String.format(API_URL, url);
    }

    // 获取麻包圈子接口绝对URL
    public static String getMbqzApiUrl(String url) {
        return String.format(MBQZ_API_URL, url);
    }

    public static String getAbsoluteUrl(String url) {
        return BASE_URL + url;
    }

    /**
     * 判断网络请求返回数据是否成功, 如果没有成功返回错误信息
     *
     * @param response 网络请求JSON格式的响应数据
     *                 { data : { ok : 1 }, status : { succeed : 1 } }
     * @return 二元组, 第一个元素为Boolean类型的成功标识; 第二个元素为错误信息, 成功时为null
     */
    public static TwoTuple<Boolean, String> parseResponseStatus(JSONObject response) {
        try {
            JSONObject statusObj = response.getJSONObject("status");
            int succeed = (int) statusObj.get("succeed");
            if (succeed == 1) {
                return TwoTuple.tuple(true, null);
            }
            return TwoTuple.tuple(false, statusObj.get("error_desc").toString());

        } catch (JSONException e) {
            Log.e("error", e.getMessage());
            return TwoTuple.tuple(false, e.getMessage());
        }
    }

    public static TwoTuple<Boolean, String> parseRetrieveStatus(JSONObject response) {
        try {
            JSONObject statusObj = response.getJSONObject("status");
            int succeed = (int) statusObj.get("succeed");
            if (succeed == 1) {
                return TwoTuple.tuple(true, null);
            } else {
                return TwoTuple.tuple(false, response.getJSONObject("data").getString("info"));
            }
        } catch (JSONException e) {
            Log.e("error", e.getMessage());
            return TwoTuple.tuple(false, e.getMessage());
        }
    }
}
