package com.witmoon.xmb.api;


import com.duowan.mobile.netroid.Listener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MengbaoApi {
    //获取萌宝基本信息
    public static void getMengbaoInfo(String date_str,Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("current_date",date_str);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "mengbao/get_index_info",
                map, listener));
    }

    //获取个人工具信息
    public static void getToolkits(String dateStr,Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("current_date",dateStr);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "mengbao/get_user_toolkit_v2",
                                                     map, listener));
    }

    //获取个人照片墙
    public static void getPicWall(Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "mengbao/get_pic_wall",
                ApiHelper.getParamObj(map), listener));
    }

    //保存个人信息
    public static void saveinfo(HashMap<String,String> map,Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "athena/set_mengbao_info",
                ApiHelper.getParamObj(map), listener));
    }
}
