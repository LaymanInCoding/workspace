package com.witmoon.xmb.api;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.api.alipay.NormalGetJSONRequest;
import com.witmoon.xmb.services.ArticleService;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ShoppingApi {
    // 获取实惠星球数据
    public static void get_affordable(Listener<JSONObject> listener){
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "AffordablePlanet/index2", listener));
    }

    // 获取全球闪购数据
    public static void get_tax(Listener<JSONObject> listener){
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "TaxfreeStore/index2", listener));
    }

    //获取麻包特色数据
    public static void get_feature(Listener<JSONObject> listener){
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL+"feature/index",listener));
    }

    //获取特色品牌数据
    public static void get_brand_feature(String id,String type,String page,Listener<JSONObject> listener){
        Map<String,String> map = new HashMap<>();
        map.put("id",id);
        map.put("type",type);
        map.put("page",page);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"feature/detail",ApiHelper.getParamObj(map),listener));
    }
}
