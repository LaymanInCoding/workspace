package com.witmoon.xmb.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by de on 2016/3/8.
 */
public class comments extends BaseBean {
    private String comment_addtime;
    private String comment_id;
    private String comment_parent_name;
    private String comment_is_read;
    private String comment_user_id;
    private String comment_content;

    public String getComment_username() {
        return comment_username;
    }

    public void setComment_username(String comment_username) {
        this.comment_username = comment_username;
    }

    public String getComment_addtime() {
        return comment_addtime;
    }

    public void setComment_addtime(String comment_addtime) {
        this.comment_addtime = comment_addtime;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getComment_parent_name() {
        return comment_parent_name;
    }

    public void setComment_parent_name(String comment_parent_name) {
        this.comment_parent_name = comment_parent_name;
    }

    public String getComment_is_read() {
        return comment_is_read;
    }

    public void setComment_is_read(String comment_is_read) {
        this.comment_is_read = comment_is_read;
    }

    public String getComment_user_id() {
        return comment_user_id;
    }

    public void setComment_user_id(String comment_user_id) {
        this.comment_user_id = comment_user_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getComment_avatar() {
        return comment_avatar;
    }

    public void setComment_avatar(String comment_avatar) {
        this.comment_avatar = comment_avatar;
    }

    public String getComment_talk_id() {
        return comment_talk_id;
    }

    public void setComment_talk_id(String comment_talk_id) {
        this.comment_talk_id = comment_talk_id;
    }

    private String comment_username;
    private String comment_avatar;
    private String comment_talk_id;

    public static comments parse(JSONObject jsonObject) throws JSONException {
        comments com = new comments();
        if (!jsonObject.isNull("comment_addtime"))
        com.setComment_addtime(jsonObject.getString("comment_addtime"));
        com.setComment_avatar(jsonObject.getString("comment_avatar"));
        com.setComment_content(jsonObject.getString("comment_content"));

        com.setComment_user_id(jsonObject.getString("comment_user_id"));
        if (!jsonObject.isNull("comment_is_read"))
        com.setComment_is_read(jsonObject.getString("comment_is_read"));
        com.setComment_id(jsonObject.getString("comment_id"));
        com.setComment_talk_id(jsonObject.getString("comment_talk_id"));
        com.setComment_parent_name(jsonObject.getString("comment_parent_name"));
        com.setComment_username(jsonObject.getString("comment_username"));
        return com;
    }
}
