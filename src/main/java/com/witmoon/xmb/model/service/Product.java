package com.witmoon.xmb.model.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/4/5.
 */
public class Product {
    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_img() {
        return product_img;
    }

    public void setProduct_img(String product_img) {
        this.product_img = product_img;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_shop_price() {
        return product_shop_price;
    }

    public void setProduct_shop_price(String product_shop_price) {
        this.product_shop_price = product_shop_price;
    }

    public String getProduct_market_price() {
        return product_market_price;
    }

    public void setProduct_market_price(String product_market_price) {
        this.product_market_price = product_market_price;
    }

    public static Product parse(JSONObject productObject){
        Product product = new Product();
        try {
            product.setProduct_id(productObject.getInt("product_id"));
            product.setProduct_img(productObject.getString("product_img"));
            product.setProduct_name(productObject.getString("product_name"));
            product.setProduct_shop_price(productObject.getString("product_shop_price"));
            product.setProduct_market_price(productObject.getString("product_market_price"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return product;
    }

    private int product_id;
    private String product_img;
    private String product_name;
    private String product_shop_price;
    private String product_market_price;
}
