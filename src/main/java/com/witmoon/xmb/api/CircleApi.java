package com.witmoon.xmb.api;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.api.alipay.NormalGetJSONRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class CircleApi {

    // 获取所有圈子分类信息
    public static void get_all_cat(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "circle/get_all_cat", listener));
    }

    //获取推荐圈子信息
    public static void get_recommend_cat(Listener<JSONObject> listener){
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "circle/get_recommend_cat", listener));
    }

    //获取用户关注圈子
    public static void get_user_circle(Listener<JSONObject> listener){
        HashMap<String,String> pm = new HashMap<>();
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "UserCircle/get_user_circle",
                ApiHelper.getParamObj(pm), listener));
    }

    //获取睡前故事
    public static void get_mb_story(Listener<JSONObject> listener){
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "discovery/story_detail/1", listener));
    }

    //获取推荐圈子信息
    public static void get_circle_ads(Listener<JSONObject> listener){
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "circle/get_circle_ads", listener));
    }

    //获取圈子大家都在搜关键字
    public static void get_hot_search_keyword(Listener<JSONObject> listener){
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "circle/get_hot_search_words", listener));
    }

    //获取热帖信息
    public static void get_circle_hot(int page,Listener<JSONObject> listener){
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "circle/get_circle_hot/" + page, listener));
    }

    // 搜索圈子
    public static void search_circle(String keyword,int user_id, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);
        paramMap.put("user_id", user_id+"");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "circle/search_circle",
                paramMap, listener));
    }

    // 搜索帖子
    public static void search_post(String keyword,int page, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);
        paramMap.put("page", page+"");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "circle/search_post",
                paramMap, listener));
    }

    // 加入圈子
    public static void join_circle(int circle_id, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("circle_id", circle_id + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "UserCircle/join_circle",
                ApiHelper.getParamObj(paramMap), listener));
    }

    //圈子详情及post列表
    public static void circle_post_list(int circle_id,int page,Listener<JSONObject>listener){
        Map<String,String> map = new HashMap<>();
        map.put("circle_id",circle_id+"");
        map.put("page",page+"");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "circle/get_circle_info",
                map, listener));
    }

    //获取帖子详情及评论信息
    public static void circle_detail(int post_id,int page,int type,int user_id,int comment_id,Listener<JSONObject>listener){
        Map<String,String> map = new HashMap<>();
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "circle/get_post_detail/"+post_id+"/"+page+"/"+type+"/"+user_id+"/"+comment_id,
                map, listener));
    }

    //收藏帖子
    public static void collect_post(int post_id,Listener<JSONObject>listener){
        Map<String,String> map = new HashMap<>();
        map.put("post_id",post_id+"");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "UserCircle/collect_post/",
                ApiHelper.getParamObj(map), listener));
    }

    //获取我的收藏列表
    public static void get_my_collect(int page,Listener<JSONObject>listener){
        Map<String,String> map = new HashMap<>();
        map.put("page",page+"");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "UserCircle/get_collect_post",
                ApiHelper.getParamObj(map), listener));
    }

    //获取我的收藏列表
    public static void get_message_number(Listener<JSONObject>listener){
        Map<String,String> map = new HashMap<>();
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "UserCircle/get_message_number",
                ApiHelper.getParamObj(map), listener));
    }

    //判断是否已收藏
    public static void check_is_collect(int post_id,Listener<JSONObject>listener){
        Map<String,String> map = new HashMap<>();
        map.put("post_id",post_id+"");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "UserCircle/check_is_collect",
                ApiHelper.getParamObj(map), listener));
    }

    //获取消息列表
    public static void get_notification(int page,Listener<JSONObject>listener){
        Map<String,String> map = new HashMap<>();
        map.put("page",page+"");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "UserCircle/get_notification",
                ApiHelper.getParamObj(map), listener));
    }

}
