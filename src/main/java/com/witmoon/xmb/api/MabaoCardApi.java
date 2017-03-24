package com.witmoon.xmb.api;


import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.api.alipay.NormalGetJSONRequest;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MabaoCardApi {
    //获取麻包卡列表信息
    public static void get_card_list(int page, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "mcard/mlist",
                ApiHelper.getParamObj(map), listener));
    }

    //绑定麻包卡
    public static void bind_card(String card_pass, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("card_pass", card_pass + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "mcard/madd",
                ApiHelper.getParamObj(map), listener));
    }

    //获取实体卡列表
    public static void get_entity_card(int page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "giftcard/entity_card/" + page, listener));
    }

    //获取电子卡列表
    public static void get_electronic_card(int page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "giftcard/electronic_card/" + page, listener));
    }

    //获取福利卡列表
    public static void get_welfare_card(int page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "giftcard/welfare_card/" + page, listener));
    }

    //获取福利卡列表
    public static void get_card_order_info(int page, Listener<JSONObject> listener) {
        Map<String, String> map = new HashMap<>();
        map.put("page", page + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "giftcard/welfare_card/", ApiHelper.getParamMap(map), listener));
    }

    public static void checkOrder(String inv_type, String inv_payee, String inv_content,String remarks,
                                  ArrayList<String> mabaoCardArray, ArrayList<String> cardList, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("inv_type", inv_type);
        pm.put("inv_payee", inv_payee);
        pm.put("inv_content", inv_content);
        pm.put("remarks",remarks);
        for (int i = 0; i < cardList.size(); i++) {
            pm.put("card[" + i + "]", cardList.get(i));
        }
        String cards = "";
        for (int i = 0; i < mabaoCardArray.size(); i++) {
            if (i == mabaoCardArray.size() - 1) {
                cards += mabaoCardArray.get(i);
            } else {
                cards += mabaoCardArray.get(i) + "|";
            }
        }
        pm.put("mabao_card", cards);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "giftflow/checkout", ApiHelper.getParamMap(pm), listener));
    }

    //提交订单
    public static void submitOrder(String inv_type, String inv_payee, String inv_content,String remarks,
                                   ArrayList<String> mabaoCardArray, ArrayList<String> cardList, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("inv_type", inv_type);
        pm.put("inv_payee", inv_payee);
        pm.put("inv_content", inv_content);
        pm.put("remarks",remarks);
        for (int i = 0; i < cardList.size(); i++) {
            pm.put("card[" + i + "]", cardList.get(i));
        }
        String cards = "";
        for (int i = 0; i < mabaoCardArray.size(); i++) {
            if (i == mabaoCardArray.size() - 1) {
                cards += mabaoCardArray.get(i);
            } else {
                cards += mabaoCardArray.get(i) + "|";
            }
        }
        pm.put("mabao_card", cards);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "giftflow/done", ApiHelper.getParamMap(pm), listener));
    }

    //获取电子卡订单详情
    public static void order_detail(String order_sn, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("order_sn", order_sn);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "order/gift_order_detail", ApiHelper.getParamMap(pm), listener));
    }
}
