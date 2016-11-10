package com.witmoon.xmb.model.service;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UserComment {
    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
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

    public ArrayList<String> getComment_imgs() {
        return comment_imgs;
    }

    public void setComment_imgs(ArrayList<String> comment_imgs) {
        this.comment_imgs = comment_imgs;
    }

    public ArrayList<String> getComment_thumb_imgs() {
        return comment_thumb_imgs;
    }

    public void setComment_thumb_imgs(ArrayList<String> comment_thumb_imgs) {
        this.comment_thumb_imgs = comment_thumb_imgs;
    }

    public String getComment_date() {
        return comment_date;
    }

    public void setComment_date(String comment_date) {
        this.comment_date = comment_date;
    }

    private int comment_id;
    private int shop_id;

    public int getComment_cnt() {
        return comment_cnt;
    }

    public void setComment_cnt(int comment_cnt) {
        this.comment_cnt = comment_cnt;
    }

    public int getProducts_num() {
        return products_num;
    }

    public void setProducts_num(int products_num) {
        this.products_num = products_num;
    }

    private int comment_cnt;
    private int products_num;
    private String comment_content;
    private String shop_name;
    private String shop_logo;
    private ArrayList<String> comment_imgs;
    private ArrayList<String> comment_thumb_imgs;
    private String comment_date;

    public static UserComment parse(JSONObject commentObject){
        UserComment comment = new UserComment();
        try {
            comment.setComment_id(commentObject.getInt("comment_id"));
            comment.setShop_id(commentObject.getInt("shop_id"));
            comment.setComment_cnt(commentObject.getInt("shop_comment_cnt"));
            comment.setProducts_num(commentObject.getInt("shop_products_num"));
            comment.setComment_content(commentObject.getString("comment_content"));
            JSONArray comment_img_json_array = commentObject.getJSONArray("comment_imgs");
            JSONArray comment_imgthumb_json_array = commentObject.getJSONArray("comment_thumb_imgs");
            ArrayList<String> arr1 = new ArrayList<>();
            for (int i = 0; i< comment_img_json_array.length(); i++){
                arr1.add(i,comment_img_json_array.getString(i));
            }
            ArrayList<String> arr2 = new ArrayList<>();
            for (int i = 0; i< comment_imgthumb_json_array.length(); i++){
                arr2.add(i,comment_imgthumb_json_array.getString(i));
            }
            comment.setComment_imgs(arr1);
            comment.setComment_thumb_imgs(arr2);
            comment.setComment_date(commentObject.getString("comment_date"));
            comment.setShop_name(commentObject.getString("shop_name"));
            comment.setShop_logo(commentObject.getString("shop_logo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return comment;
    }
}

