package com.witmoon.xmb.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 帖子实体类
 * Created by zhyh on 2015/8/2.
 */
public class Article {
    private String id;
    private String title;
    private String content;

    private String userId;  // 作者id
    private String nickName;
    private String mAvatar;
    private int praiseNumber;
    private int mUserRank;
    private String commentNumber;
    private String[] imageArray;
    private List<Comment> mComments;
    private long publishTime;

    public void setUserRank(int userRank) {
        mUserRank = userRank;
    }

    public int getUserRank() {
        return mUserRank;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setComments(List<Comment> comments) {
        mComments = comments;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPraiseNumber(int praiseNumber) {
        this.praiseNumber = praiseNumber;
    }

    public void setCommentNumber(String commentNumber) {
        this.commentNumber = commentNumber;
    }

    public String[] getImageArray() {
        return imageArray;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getPraiseNumber() {
        return praiseNumber;
    }

    public String getCommentNumber() {
        return commentNumber;
    }

    public void setImageArray(String[] imageArray) {
        this.imageArray = imageArray;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public static Article parse(JSONObject articleObj) throws JSONException {
        Article article = new Article();

        article.setId(articleObj.getString("post_id"));
        article.setTitle(articleObj.getString("title"));
        article.setContent(articleObj.getString("content"));
        article.setCommentNumber(articleObj.getString("comment_num"));
        article.setNickName(articleObj.getString("nick_name"));
        article.setUserId(articleObj.getString("user_id"));
        article.setPraiseNumber(Integer.parseInt(articleObj.getString("praise_num")));
        article.setPublishTime(Long.parseLong(articleObj.getString("publish_time")));
        article.setUserRank(Integer.parseInt(articleObj.getString("user_rank")));

        if (articleObj.has("header_img")) {
            article.setAvatar(articleObj.getString("header_img"));
        }

        if (articleObj.has("imgs")) {
            article.setImageArray(articleObj.getString("imgs").split(","));
        }

        if (articleObj.has("comments")) {
            JSONArray commentArray = articleObj.getJSONArray("comments");
            List<Comment> comments = new ArrayList<>(commentArray.length());
            for (int i = 0; i < commentArray.length(); i++) {
                Comment comment = Comment.parse(commentArray.getJSONObject(i));
                comments.add(comment);
            }
            article.setComments(comments);
        }

        return article;
    }
}
