package com.witmoon.xmb.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by de on 2016/3/8.
 */
public class Talkattentionlist extends BaseBean {
    private String position;
    private String content;
    private String cat_id;
    private String addtime;
    private ArrayList<String> imglist;
    private String user_id;
    private String praise;
    private String tid;
    private String comment;
    private String userName;

    public String getIs_attention() {
        return is_attention;
    }

    public void setIs_attention(String is_attention) {
        this.is_attention = is_attention;
    }

    public String  getIs_praise() {
        return is_praise;
    }

    public void setIs_praise(String is_praise) {
        this.is_praise = is_praise;
    }

    private String is_praise;
    private String is_attention;

    public ArrayList<Map<String, String>> getPraise_list() {
        return praise_list;
    }

    public void setPraise_list(ArrayList<Map<String, String>> praise_list) {
        this.praise_list = praise_list;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private ArrayList<Map<String,String>> praise_list;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    private String avatar;

    public ArrayList<String> getImglist() {
        return imglist;
    }

    public void setImglist(ArrayList<String> imglist) {
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

    public static Talkattentionlist parse(JSONObject object) throws JSONException {
        Talkattentionlist mCircle = new Talkattentionlist();
        mCircle.setPosition(object.getString("position"));
        mCircle.setContent(object.getString("content"));
        mCircle.setCat_id(object.getString("cat_id"));
        mCircle.setAddtime(object.getString("addtime"));
        mCircle.setUser_id(object.getString("user_id"));
        mCircle.setPraise(object.getString("praise"));
        mCircle.setTid(object.getString("tid"));
        mCircle.setComment(object.getString("comment"));
        mCircle.setAvatar(object.getString("avatar"));
        if(!object.isNull("username"))
            mCircle.setUserName(object.getString("username"));
        if(!object.isNull("is_attention"))
        mCircle.setIs_attention(object.getString("is_attention"));
        Log.e("is_praise====",object.getString("is_praise"));
        mCircle.setIs_praise(object.getString("is_praise"));
        if (!object.isNull("praise_list"))
        {
            JSONArray praise_list = object.getJSONArray("praise_list");
            ArrayList<Map<String,String>> list = new ArrayList<>();
            for (int i =0;i<praise_list.length();i++)
            {
                Map<String,String> map = new HashMap<>();
                map.put("praise_avatar", praise_list.getJSONObject(i).getString("praise_avatar"));
                list.add(map);
            }
            mCircle.setPraise_list(list);
        }
        JSONArray imglist = object.getJSONArray("imglist");
        ArrayList<String> list = new ArrayList<>();
        if (imglist.length()!=0)
        {
            for (int i=0;i<imglist.length();i++)
            {
                list.add(imglist.getJSONObject(i).getString("thumb"));
            }
        }
        mCircle.setImglist(list);
        return mCircle;
    }
}
