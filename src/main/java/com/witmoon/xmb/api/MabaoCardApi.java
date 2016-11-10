package com.witmoon.xmb.api;


import com.duowan.mobile.netroid.Listener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MabaoCardApi {
    //获取麻包卡列表信息
    public static void get_card_list(int page,Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("page",page+"");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "mcard/mlist",
                ApiHelper.getParamObj(map), listener));
    }

    //绑定麻包卡
    public static void bind_card(String card_pass,Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("card_pass",card_pass+"");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "mcard/madd",
                ApiHelper.getParamObj(map), listener));
    }
}
