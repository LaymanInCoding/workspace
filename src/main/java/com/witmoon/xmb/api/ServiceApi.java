package com.witmoon.xmb.api;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.api.alipay.NormalGetJSONRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ServiceApi {

    public static String AWAIT_PAY = "await_pay";
    public static String AWAIT_USE = "await_use";
    public static String AWAIT_COMMENT = "await_comment";
    public static String AFTER_SALES = "after_sales";

    //商铺列表
    public static void comment_shopList(int page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "service/shop_list/" + page, listener));
    }

    //商铺列表
    public static void shopList(int page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "service/index/" + page, listener));
    }

    //一级目录
    public static void subShopList(int page, String cat_id, Listener<JSONObject> listener) {
        HashMap<String, String> pm = new HashMap<>();
        pm.put("pid", cat_id);
        pm.put("page", page + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "/service/c_index", pm, listener));
    }

    //二级目录
    public static void subSubShopList(int page, String cat_id, Listener<JSONObject> listener) {
        HashMap<String, String> pm = new HashMap<>();
        pm.put("pid", cat_id);
        pm.put("page", page + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "service/product_list", pm, listener));
    }

    //搜索服务
    public static void searchService(int page, String search_key, String sort,Listener<JSONObject> listener) {
        HashMap<String, String> pm = new HashMap<>();
        pm.put("search_key", search_key);
        pm.put("page", page + "");
        pm.put("sort",sort);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "service/search", pm, listener));
    }

    //商铺详情
    public static void shopDetail(int shop_id, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "/service/shop_detail_info/" + shop_id, listener));
    }

    //商铺详情
    public static void shopComments(int shop_id, int page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "/service/shop_comments/" + shop_id + "/" + page, listener));
    }

    //商铺详情
    public static void userComments(int user_id, int page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalGetJSONRequest(ApiHelper.BASE_URL + "/service/user_comments/" + user_id + "/" + page, listener));
    }

    //商铺详情
    public static void productCart(int product_id, int product_num, ArrayList<String> mSelectedCard, Listener<JSONObject> listener) {
        HashMap<String, String> pm = new HashMap<>();
        pm.put("product_id", product_id + "");
        pm.put("product_num", product_num + "");
        String cards = "";
        for (int i = 0; i < mSelectedCard.size(); i++) {
            if (i == mSelectedCard.size() - 1) {
                cards += mSelectedCard.get(i);
            } else {
                cards += mSelectedCard.get(i) + ",";
            }
        }
        pm.put("cards", cards);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "/service/checkout", pm, listener));
    }

    //提交订单
    public static void submitOrder(HashMap<String, String> pm, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "/service/submit_order", pm, listener));
    }

    //提交退款订单
    public static void submitReturnOrder(HashMap<String, String> pm, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "/service/submit_return_order", pm, listener));
    }

    //获取订单列表
    public static void my_order(String type, int page, Listener<JSONObject> listener) {
        HashMap<String, String> pm = new HashMap<>();
        pm.put("type", type);
        pm.put("page", page + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "/service/my_product", pm, listener));
    }

    //获取订单列表
    public static void view_ticket(int order_id, Listener<JSONObject> listener) {
        HashMap<String, String> pm = new HashMap<>();
        pm.put("order_id", order_id + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "/service/view_ticket", pm, listener));
    }

    //获取订单列表
    public static void order_detail(int order_id, Listener<JSONObject> listener) {
        HashMap<String, String> pm = new HashMap<>();
        pm.put("order_id", order_id + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "/service/order_info", pm, listener));
    }

    //获取退款订单信息
    public static void return_order_detail(int order_id, Listener<JSONObject> listener) {
        HashMap<String, String> pm = new HashMap<>();
        pm.put("order_id", order_id + "");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "/service/return_order_info", pm, listener));
    }
}
