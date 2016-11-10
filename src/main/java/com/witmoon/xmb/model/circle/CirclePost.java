package com.witmoon.xmb.model.circle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CirclePost {
    public String getAuthor_userhead() {
        return author_userhead;
    }

    public void setAuthor_userhead(String author_userhead) {
        this.author_userhead = author_userhead;
    }

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    public int getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(int author_id) {
        this.author_id = author_id;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public int getReply_cnt() {
        return reply_cnt;
    }

    public void setReply_cnt(int reply_cnt) {
        this.reply_cnt = reply_cnt;
    }

    public ArrayList<String> getPost_imgs() {
        return post_imgs;
    }

    public void setPost_imgs(ArrayList<String> post_imgs) {
        this.post_imgs = post_imgs;
    }

    public int getView_cnt() {
        return view_cnt;
    }

    public void setView_cnt(int view_cnt) {
        this.view_cnt = view_cnt;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public int getCircle_id() {
        return circle_id;
    }

    public void setCircle_id(int circle_id) {
        this.circle_id = circle_id;
    }

    public String getCircle_name() {
        return circle_name;
    }

    public void setCircle_name(String circle_name) {
        this.circle_name = circle_name;
    }

    private int post_id;
    private int author_id;
    private String post_time;
    private String post_content;
    private int reply_cnt;
    private ArrayList<String> post_imgs;
    private int view_cnt;
    private String author_name;
    private String post_title;
    private int circle_id;
    private String circle_name;
    private String author_userhead;

    public static CirclePost parse(JSONObject jsonObject){
        CirclePost post = new CirclePost();
        try {
            post.setPost_id(jsonObject.getInt("post_id"));
            post.setAuthor_id(jsonObject.getInt("author_id"));
            post.setPost_time(jsonObject.getString("post_time"));
            post.setPost_content(jsonObject.getString("post_content"));
            post.setReply_cnt(jsonObject.getInt("reply_cnt"));
            JSONArray imgsJsonArray = jsonObject.getJSONArray("post_imgs");
            ArrayList<String> imgsArray = new ArrayList<>();
            for(int i = 0; i < imgsJsonArray.length(); i++){
                imgsArray.add(imgsJsonArray.getString(i));
            }
            post.setPost_imgs(imgsArray);
            post.setView_cnt(jsonObject.getInt("view_cnt"));
            post.setAuthor_name(jsonObject.getString("author_name"));
            post.setPost_title(jsonObject.getString("post_title"));
            post.setCircle_id(jsonObject.getInt("circle_id"));
            post.setAuthor_userhead(jsonObject.getString("author_userhead"));
            post.setCircle_name(jsonObject.getString("circle_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return post;
    }
}
