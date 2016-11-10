package com.witmoon.xmb.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by de on 2016/3/11.
 */
public class All_Comments extends BaseBean {
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;
    private String id;
    private String user_name;
    private String time;
    private String img;

    public String getComment_parent_id() {
        return comment_parent_id;
    }

    public void setComment_parent_id(String comment_parent_id) {
        this.comment_parent_id = comment_parent_id;
    }

    private String comment_parent_id;

    public String getTalk_id() {
        return talk_id;
    }

    public void setTalk_id(String talk_id) {
        this.talk_id = talk_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String talk_id;
    private String user_id;
    private String avatar;
    private String type;

    public static All_Comments parse(JSONObject jsonObject) throws JSONException {
        All_Comments comments = new All_Comments();
//        comments.setId(jsonObject.getString("id"));
        comments.setUser_name(jsonObject.getString("user_name"));
        comments.setTime(jsonObject.getString("time"));
        comments.setImg(jsonObject.getString("img"));
        comments.setTalk_id(jsonObject.getString("talk_id"));
        comments.setUser_id(jsonObject.getString("user_id"));
        comments.setAvatar(jsonObject.getString("avatar"));
        comments.setComment_parent_id(jsonObject.getString("comment_id"));
        if (!jsonObject.isNull("content")) {
            comments.setContent(jsonObject.getString("content"));
        } else {
            comments.setContent("");
        }
        comments.setType(jsonObject.getString("type"));
        return comments;
    }

    public static All_Comments unparse(JSONObject jsonObject) throws JSONException {
        All_Comments comments = new All_Comments();
//        comments.setId(jsonObject.getString("id"));
        comments.setUser_name(jsonObject.getString("user_name"));
        comments.setTime(jsonObject.getString("time"));
        comments.setImg(jsonObject.getString("img"));
        comments.setTalk_id(jsonObject.getString("talk_id"));
        comments.setUser_id(jsonObject.getString("user_id"));
        comments.setAvatar(jsonObject.getString("avatar"));
        comments.setComment_parent_id(jsonObject.getString("comment_id"));
        if (!jsonObject.isNull("content")) {
            comments.setContent(jsonObject.getString("content"));
        } else {
            comments.setContent("");
        }
        comments.setType(jsonObject.getString("type"));
        return comments;
    }
}
