package com.witmoon.xmb.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单实体类
 * Created by zhyh on 2015/7/17.
 */
public class Order extends BaseBean {
    private String id;
    private String orderType;
    private String serialNo;
    private String time;

    public String getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
    }

    private String order_amount;
    private String shipping_name;
    private String invoice_no;
    private String totalFee;
    private String subject;
    private String description;
    private List<Map<String, String>> mGoodsList;

    private int isCrossBorder;

    private int isSplitOrder;

    private JSONArray child_orders;

    public String getInvoice_no() {
        return invoice_no;
    }

    public void setInvoice_no(String invoice_no) {
        this.invoice_no = invoice_no;
    }

    public String getShipping_name() {
        return shipping_name;
    }

    public void setShipping_name(String shipping_name) {
        this.shipping_name = shipping_name;
    }


    public int getIsCrossBorder() {
        return isCrossBorder;
    }

    public void setIsCrossBorder(int isCrossBorder) {
        this.isCrossBorder = isCrossBorder;
    }

    public int getIsSplitOrder() {
        return isSplitOrder;
    }

    public void setIsSplitOrder(int isSplitOrder) {
        this.isSplitOrder = isSplitOrder;
    }

    public JSONArray getChild_orders() {
        return child_orders;
    }

    public void setChild_orders(JSONArray child_orders) {
        this.child_orders = child_orders;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public void setGoodsList(List<Map<String, String>> goodsList) {
        mGoodsList = goodsList;
    }

    public String getId() {
        return id;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public String getTime() {
        return time;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public List<Map<String, String>> getGoodsList() {
        return mGoodsList;
    }

    public static Order parse(JSONObject orderObj) {
        Order order = new Order();
        try {
//            order.setId(orderObj.getString("order_id"));
            order.setSerialNo(orderObj.getString("parent_order_sn"));
            order.setId(orderObj.getString("order_id"));
            order.setTime(orderObj.getString("order_time"));
            order.setOrderType(orderObj.getString("order_status"));
            Log.e("TOTAL_FEE",orderObj.getString("order_status"));
            order.setTotalFee(orderObj.getString("totalFee"));
            order.setIsCrossBorder(orderObj.getInt("is_cross_border"));
            order.setShipping_name(orderObj.getString("shipping_name"));
            order.setInvoice_no(orderObj.getString("invoice_no"));
            order.setOrder_amount(orderObj.getString("order_amount"));
            JSONArray childOrders = orderObj.getJSONArray("child_orders");
            if (childOrders.length() == 0) {
                order.setIsSplitOrder(0);
            } else {
                order.setIsSplitOrder(1);
                order.setChild_orders(childOrders);
            }

            if (orderObj.has("subject")) {
                order.setSubject(orderObj.getString("subject"));
            }
            if (orderObj.has("desc")) {
                order.setDescription(orderObj.getString("desc"));
            }

            JSONArray goodsArray = orderObj.getJSONArray("goods_list");
            List<Map<String, String>> goodsList = new ArrayList<>();
            for (int i = 0; i < goodsArray.length(); i++) {
                JSONObject goodsObj = goodsArray.getJSONObject(i);
                Map<String, String> tmap = new HashMap<>();
                tmap.put("goods_id", goodsObj.getString("goods_id"));
                tmap.put("goods_name", goodsObj.getString("name"));
                tmap.put("count", goodsObj.getString("goods_number"));
                tmap.put("is_comment", goodsObj.getString("is_comment"));
                tmap.put("goods_img", goodsObj.getString("img"));
                tmap.put("goods_price", goodsObj.getString("shop_price_formatted"));
                goodsList.add(tmap);
            }
            order.setGoodsList(goodsList);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return order;
    }
}
