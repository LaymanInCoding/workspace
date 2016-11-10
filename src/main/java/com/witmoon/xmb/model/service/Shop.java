package com.witmoon.xmb.model.service;


import org.json.JSONException;
import org.json.JSONObject;

public class Shop {
    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public String getShop_name() {
        return shop_name;
    }

    public void setShop_name(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShop_desc() {
        return shop_desc;
    }

    public void setShop_desc(String shop_desc) {
        this.shop_desc = shop_desc;
    }

    public String getShop_logo() {
        return shop_logo;
    }

    public void setShop_logo(String shop_logo) {
        this.shop_logo = shop_logo;
    }

    public String getShop_address() {
        return shop_address;
    }

    public void setShop_address(String shop_address) {
        this.shop_address = shop_address;
    }

    public String getShop_phone() {
        return shop_phone;
    }

    public void setShop_phone(String shop_phone) {
        this.shop_phone = shop_phone;
    }

    public String getShop_nearby_subway() {
        return shop_nearby_subway;
    }

    public void setShop_nearby_subway(String shop_nearby_subway) {
        this.shop_nearby_subway = shop_nearby_subway;
    }

    public int getShop_comment_cnt() {
        return shop_comment_cnt;
    }

    public void setShop_comment_cnt(int shop_comment_cnt) {
        this.shop_comment_cnt = shop_comment_cnt;
    }

    public int getShop_products_num() {
        return shop_products_num;
    }

    public void setShop_products_num(int shop_products_num) {
        this.shop_products_num = shop_products_num;
    }

    public static Shop parse(JSONObject shopObject){
        Shop shop = new Shop();
        try {
            shop.setShop_id(shopObject.getInt("shop_id"));
            shop.setShop_index(shopObject.getInt("index"));
            shop.setShop_name(shopObject.getString("shop_name"));
            shop.setShop_desc(shopObject.getString("shop_desc"));
            shop.setShop_logo(shopObject.getString("shop_logo"));
            shop.setShop_address(shopObject.getString("shop_address"));
            shop.setShop_phone(shopObject.getString("shop_phone"));
            shop.setShop_nearby_subway(shopObject.getString("shop_nearby_subway"));
            shop.setShop_comment_cnt(shopObject.getInt("shop_comment_cnt"));
            shop.setShop_products_num(shopObject.getInt("shop_products_num"));
            shop.setCity(shopObject.getString("shop_city"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return shop;
    }

    private int shop_id;

    public int getShop_index() {
        return shop_index;
    }

    public void setShop_index(int shop_index) {
        this.shop_index = shop_index;
    }

    private int shop_index;
    private String shop_name;
    private String shop_desc;
    private String shop_logo;
    private String shop_address;
    private String shop_phone;
    private String shop_nearby_subway;
    private int shop_comment_cnt;
    private int shop_products_num;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String city;


}
