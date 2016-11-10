package com.witmoon.xmb.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 帖子评论实体
 * Created by zhyh on 2015/8/2.
 */
public class Comment extends BaseBean {
    private String id;
    private String content;
    private Long time;
    private String userid;
    private String articleId;
    private String nickName;
    private String avatar;
    private String rank;

    public void setId(String id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getTime() {
        return time;
    }

    public String getUserid() {
        return userid;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getNickName() {
        return nickName;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getRank() {
        return rank;
    }

    public static Comment parse(JSONObject commentObj) throws JSONException {
        Comment comment = new Comment();

        comment.setId(commentObj.getString("comment_id"));
        comment.setContent(commentObj.getString("comment_content"));
        comment.setArticleId(commentObj.getString("post_id"));
        comment.setTime(Long.parseLong(commentObj.getString("comment_time")));
        comment.setUserid(commentObj.getString("user_id"));
        comment.setNickName(commentObj.getString("nick_name"));
        comment.setAvatar(commentObj.getString("header_img"));

        return comment;
    }
}
