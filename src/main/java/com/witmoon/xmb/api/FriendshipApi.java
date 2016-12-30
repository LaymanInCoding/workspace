package com.witmoon.xmb.api;

import android.util.Log;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.api.alipay.NormalGetJSONRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 麻包圈子相关接口
 * Created by zhyh on 2015/8/1.
 */
public class FriendshipApi {

    // 组合分页参数Map
    public static Map<String, String> getPaginationMap(int page) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("page", String.valueOf(page));
        return paramMap;
    }

    // 查看帖子列表       暫時沒用到
    public static void articleList(int page, String type, Listener<JSONObject> listener) {
        Map<String, String> paramMap = getPaginationMap(page);
        paramMap.put("type", type);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getMbqzApiUrl("/mbqz/post/list"),
                paramMap, listener));
    }

    // 关注的人帖子列表   暫時沒用到
    public static void attentionArticleList(int page, String type, Listener<JSONObject> listener) {
        Map<String, String> paramMap = getPaginationMap(page);
        paramMap.put("type", type);
        paramMap.put("user_id", String.valueOf(AppContext.getLoginUid()));
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getMbqzApiUrl("/mbqz/post/list"),
                paramMap, listener));
    }

    // 个人帖子列表    暫時沒用到
    public static void personalArticleList(String uid, int page, Listener<JSONObject> listener) {
        Map<String, String> paramMap = getPaginationMap(page);
        paramMap.put("user_id", uid);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getMbqzApiUrl("/mbqz/post/my_list"),
                paramMap, listener));
    }

    // 帖子详情         暫時沒用到
    public static void articleDetail(String id, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("post_id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getMbqzApiUrl("/mbqz/post/detail"),
                paramMap, listener));
    }

    // 发表评论
    public static void comment(String id, String content, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("post_id", id);
        paramMap.put("comment_content", content);
        paramMap.put("user_id", String.valueOf(AppContext.getLoginUid()));
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getMbqzApiUrl("/mbqz/comment/add"),
                paramMap, listener));
    }

    // 点赞或取消点赞
    public static void praiseOrNot(String pid, boolean isPraise, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("type", isPraise ? "1" : "0");
        paramMap.put("post_id", pid);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getMbqzApiUrl("/mbqz/praise/set"),
                paramMap, listener));
    }

    // 加关注
    public static void focusOnOrNot(String targetUid, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("user_id", String.valueOf(AppContext.getLoginUid()));
        paramMap.put("follow_id", targetUid);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getMbqzApiUrl("/mbqz/follow/add"),
                paramMap, listener));
    }

    // 活动列表
    public static void activeList(int page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getMbqzApiUrl("/mbqz/act/list"),
                getPaginationMap(page), listener));
    }

    // 用户统计
    public static void userTotal(String uid, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("user_id", uid);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/circle/member_count"),
                paramMap, listener));
    }

    //麻包圈广告图
    public static void communicate_index(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest("https://api.xiaomabao.com/communicate/index",
                ApiHelper.getParamObj(new HashMap<String, String>()), listener));
    }

    //tiezi
    public static void talkcatlist(String cat_id, String pager, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("cat_id", cat_id);
        map.put("page", pager);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/talkcatlist"),
                ApiHelper.getParamObj(map), listener));
    }

    //自己的帖子
    public static void talklist(String pager, String user_id, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("page", pager);
        map.put("user_id", user_id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/talklist"),
                ApiHelper.getParamObj(map), listener));
    }

    //获取用户麻包圈的基本信息
    //麻包圈广告图
    public static void getusermbqinfo(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/getusermbqinfo"),
                ApiHelper.getParamObj(new HashMap<String, String>()), listener));
    }

    //说说详情
    public static void talkdetail(String tid, String page, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("tid", tid);
        map.put("page", page);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/talkdetail"),
                ApiHelper.getParamObj(map), listener));
    }

    //删除说说
    public static void talkdel(String tid, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", tid);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/talkdel"),
                ApiHelper.getParamObj(map), listener));
    }

    //分类信息
    public static void categoryinfo(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/categoryinfo"),
                ApiHelper.getParamObj(new HashMap<String, String>()), listener));
    }

    //赞说说
    public static void praise(String talk_id, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talk_id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/praise"),
                ApiHelper.getParamObj(map), listener));
    }

    //发表评论
    public static void talkcomment(String talk_id, String content, String pid, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("talk_id", talk_id);
        map.put("content", content);
        map.put("pid", pid);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/talkcomment"),
                ApiHelper.getParamObj(map), listener));
    }

    public static void talkattentionlist(String page, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/talkattentionlist"),
                ApiHelper.getParamObj(map), listener));
    }

    //关注列表
    public static void attentionlist(String page, String user_id, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page);
        map.put("user_id", user_id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/attentionlist"),
                ApiHelper.getParamObj(map), listener));
    }

    //粉丝列表
    public static void fanlist(String page, String user_id, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page);
        map.put("user_id", user_id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/fanlist"),
                ApiHelper.getParamObj(map), listener));
    }

    //关注http://172.16.1.122/communicate/payattention
    public static void payattention(String user_id, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/payattention"),
                ApiHelper.getParamObj(map), listener));
    }

    //头部信息http://172.16.1.122/communicate/getusermbqinfo
    public static void getusermbqinfo(String user_id, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("user_id", user_id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/getusermbqinfo"),
                ApiHelper.getParamObj(map), listener));
    }

    //未读消息数量http://172.16.1.122/communicate/usertips
    public static void usertips(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/usertips"),
                ApiHelper.getParamObj(new HashMap<String, String>()), listener));
    }

    //未读消息列表
    public static void newmessagetip(String page, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/newmessagetip"),
                ApiHelper.getParamObj(map), listener));
    }

    //清空消息列表
    public static void clearct(Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteUrl("/communicate/clearct"),
                ApiHelper.getParamObj(map), listener));
    }


    //实惠星球接口
    public static void affordablePlanet(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("/AffordablePlanet/index"), listener));
    }

    //实惠星球接口分页
    public static void affordablePlanets(String url, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("AffordablePlanet/recommend_goods/") + url, listener));
    }

    //免税
    public static void taxfreeStore(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("/TaxfreeStore/index"), listener));
    }

    //子分类
    public static void child_category_index(String cat_id, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("/AffordablePlanet/child_category_index2/") + cat_id, listener));
    }

    //子分类- 分页
    public static void child_category_index(String cat_id, String page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("/AffordablePlanet/child_category_index2/") + cat_id + "/" + page, listener));
    }

    //子分类- 分页- 筛选
    public static void child_category_index(String cat_id, String page, String asc_or_desc, String type, String is_type, Listener<JSONObject> listener) {
        if (asc_or_desc.equals("")) {
            if (is_type.equals("0")) {
                Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("/AffordablePlanet/get_category_goods/") + cat_id + "/" + page + "/" + type, listener));
            } else {
                Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("/taxfreeStore/goods_list/") + cat_id + "/" + page + "/" + type, listener));
            }
        }else{
            if (is_type.equals("0")) {
                Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("/AffordablePlanet/get_category_goods/") + cat_id + "/" + page + "/" + type + "?sort=" + asc_or_desc
                        + "&channel=" + AppContext.getChannels() + "&device=android" + "&version=" + AppContext.geVerSion(), listener));
            } else {
                Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("/taxfreeStore/goods_list/") + cat_id + "/" + page + "/" + type + "?sort=" + asc_or_desc
                        + "&channel=" + AppContext.getChannels() + "&device=android" + "&version=" + AppContext.geVerSion(), listener));
            }
        }
    }

//    //子分类- 分页- 筛选 - 价格
//    public static void child_category_index_price(String cat_id, String page, String asc_or_desc, String is_type, String type, Listener<JSONObject> listener) {
//        if (is_type.equals("0")) {
//            Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("/AffordablePlanet/get_category_goods/") + cat_id + "/" + page + "/" + type + "?sort=" + asc_or_desc
//                    + "&channel=" + AppContext.getChannels() + "&device=android" + "&version=" + AppContext.geVerSion(), listener));
//        } else {
//            Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("/taxfreeStore/goods_list/") + cat_id + "/" + page + "/" + type + "?sort=" + asc_or_desc
//                    + "&channel=" + AppContext.getChannels() + "&device=android" + "&version=" + AppContext.geVerSion(), listener));
//        }
//    }

    //专场
    public static void marketplace(String cat_id, String page, String type, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("topic/info/") + cat_id + "/" + page + "/" + type, listener));
    }

    //价格高低
    public static void marketplace_price(String cat_id, String page, String asc_or_desc, String type, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.getAbsoluteUrl("topic/info/") + cat_id + "/" + page + "/" + type + "?sort=" + asc_or_desc
                + "&channel=" + AppContext.getChannels() + "&device=android" + "&version=" + AppContext.geVerSion(), listener));
        Log.e("API", ApiHelper.getAbsoluteUrl("topic/info/") + cat_id + "/" + page + "/" + type + "?s=" + asc_or_desc
                + "&channel=" + AppContext.getChannels() + "&device=android" + "&version=" + AppContext.geVerSion());

    }
}
