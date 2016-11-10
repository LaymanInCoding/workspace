package com.witmoon.xmb.model;

import android.util.Log;

import com.duowan.mobile.netroid.request.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by de on 2015/12/3.
 */
public class Out_ extends BaseBean {
    public int getIs_() {
        return is_;
    }

    public void setIs_(int is_) {
        this.is_ = is_;
    }

    private int is_;
    public int getPosion() {
        return posion;
    }

    public void setPosion(int posion) {
        this.posion = posion;
    }

    private int posion;
    private String order_sn;

    public String getBack_tax() {
        return back_tax;
    }

    public void setBack_tax(String back_tax) {
        this.back_tax = back_tax;
    }

    private String back_tax;
    private String order_id;
    private String add_time;
    private String order_total_money;
    private String refund_status;

    private List<Map<String, String>> mGoodsList;

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getOrder_total_money() {
        return order_total_money;
    }

    public void setOrder_total_money(String order_total_money) {
        this.order_total_money = order_total_money;
    }

    public String getRefund_status() {
        return refund_status;
    }

    public void setRefund_status(String refund_status) {
        this.refund_status = refund_status;
    }

    public List<Map<String, String>> getmGoodsList() {
        return mGoodsList;
    }

    public void setmGoodsList(List<Map<String, String>> mGoodsList) {
        this.mGoodsList = mGoodsList;
    }

    public static Out_ parse(JSONObject orderObj) throws JSONException {
        Out_ order = new Out_();
        order.setOrder_id(orderObj.getInt("order_id") + "");
        order.setOrder_sn(orderObj.getString("order_sn"));
        order.setAdd_time(orderObj.getString("add_time"));
        order.setOrder_total_money(orderObj.getDouble("order_total_money") + "");
        order.setRefund_status(orderObj.getInt("refund_status") + "");
        order.setBack_tax(orderObj.getString("back_tax") + "");
        JSONArray childOrders = orderObj.getJSONArray("goods_detail");
        List<Map<String, String>> goodsList = new ArrayList<Map<String, String>>(childOrders.length());
        for (int i = 0; i < childOrders.length(); i++) {
            JSONObject goodsObj = childOrders.getJSONObject(i);
            Map<String, String> tmap = new HashMap<>();
            tmap.put("goods_id", goodsObj.getInt("goods_id")+"");
            tmap.put("goods_name", goodsObj.getString("goods_name"));
            tmap.put("goods_price", goodsObj.getString("goods_price"));
            tmap.put("goods_number", goodsObj.getInt("goods_number")+"");
            tmap.put("goods_img", goodsObj.getString("goods_thumb"));
            goodsList.add(tmap);
        }
        order.setmGoodsList(goodsList);
        return order;
    }
}
