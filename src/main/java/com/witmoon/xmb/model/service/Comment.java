package com.witmoon.xmb.model.service;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Comment {
    private int comment_id;
    private int user_id;
    private String comment_content;
    private ArrayList<String> comment_imgs;
    private ArrayList<String> comment_thumb_imgs;
    private String comment_date;
    private int shop_id;

    public int getComment_cnt() {
        return comment_cnt;
    }

    public void setComment_cnt(int comment_cnt) {
        this.comment_cnt = comment_cnt;
    }

    private int comment_cnt;
    private String comment_username;

    public String getComment_header_img() {
        return comment_header_img;
    }

    public void setComment_header_img(String comment_header_img) {
        this.comment_header_img = comment_header_img;
    }

    public String getComment_username() {
        return comment_username;
    }

    public void setComment_username(String comment_username) {
        this.comment_username = comment_username;
    }

    private String comment_header_img;

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
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

    public int getShop_id() {
        return shop_id;
    }

    public void setShop_id(int shop_id) {
        this.shop_id = shop_id;
    }

    public static Comment parse(JSONObject commentObject){
        Comment comment = new Comment();
        try {
            comment.setComment_id(commentObject.getInt("comment_id"));
            comment.setUser_id(commentObject.getInt("user_id"));
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
            comment.setComment_header_img(commentObject.getString("header_img"));
            comment.setComment_username(commentObject.getString("user_name"));
            comment.setShop_id(commentObject.getInt("shop_id"));
            comment.setComment_cnt(commentObject.getInt("comment_total"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return comment;
    }
}

