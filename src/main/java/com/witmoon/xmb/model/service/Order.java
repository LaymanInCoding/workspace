package com.witmoon.xmb.model.service;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Order {
    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_sn() {
        return product_sn;
    }

    public void setProduct_sn(String product_sn) {
        this.product_sn = product_sn;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public double getProduct_shop_price() {
        return product_shop_price;
    }

    public void setProduct_shop_price(double product_shop_price) {
        this.product_shop_price = product_shop_price;
    }

    public double getProduct_market_price() {
        return product_market_price;
    }

    public void setProduct_market_price(double product_market_price) {
        this.product_market_price = product_market_price;
    }

    public int getProduct_number() {
        return product_number;
    }

    public void setProduct_number(int product_number) {
        this.product_number = product_number;
    }

    public double getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(double order_amount) {
        this.order_amount = order_amount;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_logo() {
        return shop_logo;
    }

    public void setShop_logo(String shop_logo) {
        this.shop_logo = shop_logo;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }

    private int order_id;
    private int product_id;
    private String product_sn;
    private String product_name;
    private double product_shop_price;
    private double product_market_price;
    private int product_number;
    private double order_amount;
    private String shop_name;
    private String shop_logo;
    private int shop_id;
    private String product_img;

    public String getOrder_status() {
        return order_status;
    }

    public void setOrder_status(String order_status) {
        this.order_status = order_status;
    }

    private String order_status;

    public static Order parse(JSONObject orderObject){
        Order order = new Order();
        try {
            order.setOrder_id(orderObject.getInt("order_id"));
            order.setProduct_sn(orderObject.getString("product_sn"));
            order.setProduct_id(orderObject.getInt("product_id"));
            order.setProduct_name(orderObject.getString("product_name"));
            order.setProduct_shop_price(orderObject.getDouble("product_shop_price"));
            order.setProduct_market_price(orderObject.getDouble("product_market_price"));
            order.setProduct_number(orderObject.getInt("product_number"));
            order.setOrder_amount(orderObject.getDouble("order_amount"));
            order.setShop_name(orderObject.getString("shop_name"));
            order.setShop_logo(orderObject.getString("shop_logo"));
            order.setShop_id(orderObject.getInt("shop_id"));
            order.setProduct_img(orderObject.getString("product_img"));
            order.setOrder_status(orderObject.getString("order_status"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return order;
    }
}

