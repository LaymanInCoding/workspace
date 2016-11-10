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

    // 首页轮播广告
    public static void carouselAdvertisement(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl("/index/advert")
                , new HashMap<String, String>(), listener));
    }

    // 首页中部促销广告
    public static void promotionAdvertisement(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/index/middle_advert"), new HashMap<String, String>(), listener));
    }

    // 品牌分类
    public static void brandCategory(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/home/categoryBrand"), new HashMap<String, String>(), listener));
    }

    // 商品分类
    public static void category(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/home/category"), new HashMap<String, String>(), listener));
    }

    // 加载功能大分类(上新、母婴、寝居等)
    public static void functionClass(String type, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("menu_type", type);
//        pm.put("version",AppContext.geVerSion());
//        pm.put("channel", AppContext.getChannels());
//        pm.put("device",AppContext.getDevice());
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/home/getFavourableMenu"), pm, listener));
    }

    // 获取上新首页的“最新特卖和即将推出”专场列表接口
    public static void homeMarket(String type, int page, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("type", type);
//        pm.put("version",AppContext.geVerSion());
//        pm.put("channel",AppContext.getChannels());
//        pm.put("device",AppContext.getDevice());
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/home/shouYe"), ApiHelper.getPaginationParamObj(page, pm), listener));
    }

    // 团购首页
    public static void group_buy(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/index/group_buy/"), listener));
    }


    // 获取功能大分类列表(上新、母婴、寝居等)
    //免税页面的实现
    public static void functionList(String typeId, int page, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("menu_id", typeId);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/home/getFavourableList"), ApiHelper.getPaginationParamObj(page, pm), listener));
    }

    public static void getTopic(String typeId, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("topic_id", typeId);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/group/topic&topic_id=" + typeId), listener));
    }

    // 签到信息
    public static void signinData(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"promote/get_sign_days", ApiHelper.getParamObj(null), listener));
    }

    // 签到
    public static void signin(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"promote/sign",
                ApiHelper.getParamObj(null), listener));
    }

    // 摇一摇次数
    public static void shakeCount(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"promote/get_remain_swing", ApiHelper.getParamObj(null), listener));
    }

    // 摇一摇最新中奖信息
    public static void newestPrize(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"promote/get_remote_reward", ApiHelper.getParamObj(null), listener));
    }

    // 摇一摇
    public static void shake(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"promote/swing",
                ApiHelper.getParamObj(null), listener));
    }

    //获取萌宝信息
    public static void mblisener(int page, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<String, String>();
//        map.put("session[uid]", AppContext.getLoginUid() + "");
//        map.put("session[sid]", ApiHelper.mSessionID);
        map.put("page", page + "");
//        ApiHelper.getAbsoluteApiUrl("/home/getFavourableList")
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("athena/diarylist"), ApiHelper.getParamObj(map), listener));
    }

    //获取日志信息
    public static void diaarylisener(int page, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("page", page + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("athena/diarylistng"), ApiHelper.getParamObj(map), listener));
    }

    public static void anxintip(Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("longitude", MainActivity.longitude + "");
        map.put("latitude",MainActivity.latitude + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("athena/tips"), ApiHelper.getParamObj(map), listener));
    }

    public static void diarydel(String id, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("athena/diarydel"), ApiHelper.getParamObj(map), listener));
    }
}
