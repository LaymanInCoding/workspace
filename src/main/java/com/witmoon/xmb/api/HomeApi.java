package com.witmoon.xmb.api;

import android.util.Log;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.MainActivity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页API
 * Created by zhyh on 2015-07-08.
 */
public class HomeApi {


    // 团购首页
    public static void group_buy(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL +
                "/group/index/", listener));
    }


    public static void getTopic(String typeId, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("topic_id", typeId);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL +
                "/group/topic", ApiHelper.getParamMap(pm), listener));
    }

    // 签到信息
    public static void signinData(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "promote/get_sign_days", ApiHelper.getParamObj(null), listener));
    }

    // 签到
    public static void signin(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "promote/sign",
                ApiHelper.getParamObj(null), listener));
    }

    // 摇一摇次数
    public static void shakeCount(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "promote/get_remain_swing", ApiHelper.getParamObj(null), listener));
    }

    // 摇一摇最新中奖信息
    public static void newestPrize(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "promote/get_remote_reward", ApiHelper.getParamObj(null), listener));
    }

    // 摇一摇
    public static void shake(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "promote/swing",
                ApiHelper.getParamObj(null), listener));
    }

    //获取萌宝信息
    public static void mblisener(int page, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("page", page + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("athena/diarylist"), map, listener));
    }

    //获取日志信息
    public static void diaarylisener(int page, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("page", page + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("athena/diarylistng"), map, listener));
    }

    public static void anxintip(Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("longitude", MainActivity.longitude + "");
        map.put("latitude", MainActivity.latitude + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("athena/tips"), map, listener));
    }

    public static void diarydel(String id, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("athena/diarydel"), map, listener));
    }
}
