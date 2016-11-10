package com.witmoon.xmb.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by de on 2016/3/7.
 */
public class Circle extends BaseBean {
    private String position;
    private String content;
    private String cat_id;
    private String addtime;
    private String imglist;
    private String user_id;
    private String praise;
    private String tid;
    private String comment;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBaby_age() {
        return baby_age;
    }

    public void setBaby_age(String baby_age) {
        this.baby_age = baby_age;
    }

    private String username;
    private String baby_age;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private String avatar;

    public String getImglist() {
        return imglist;
    }

    public void setImglist(String imglist) {
        this.imglist = imglist;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPraise() {
        return praise;
    }

    public void setPraise(String praise) {
        this.praise = praise;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static Circle parse(JSONObject object) throws JSONException {
        Circle mCircle = new Circle();
        mCircle.setPosition(object.getString("position"));
        mCircle.setContent(object.getString("content"));
        mCircle.setCat_id(object.getString("cat_id"));
        mCircle.setAddtime(object.getString("addtime"));
        mCircle.setUser_id(object.getString("user_id"));
        mCircle.setPraise(object.getString("praise"));
        mCircle.setTid(object.getString("tid"));
        mCircle.setComment(object.getString("comment"));
        mCircle.setAvatar(object.getString("avatar"));
        mCircle.setUsername(object.getString("username"));
        mCircle.setBaby_age(object.getString("baby_age"));
        JSONArray imglist = object.getJSONArray("imglist");
        if (imglist.length()!=0)
        {
            mCircle.setImglist(imglist.getJSONObject(0).getString("thumb"));
        }
        return mCircle;
    }
}
