package com.witmoon.xmb.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 广告图片实体类
 * Created by zhyh on 2015-07-10.
 */
public class Advertisement extends BaseBean {
    private String id;
    private String title;
    private String link;
    private String picture;
    private String position;
    private String endtime;

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getPicture() {
        return picture;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public static Advertisement parse(JSONObject jsonObject) throws JSONException {
        Advertisement advertisement = new Advertisement();

        advertisement.setId(jsonObject.getString("id"));
        advertisement.setTitle(jsonObject.getString("title"));
        advertisement.setEndtime(jsonObject.getString("end_time"));
        advertisement.setPicture(jsonObject.getString("ad_code"));
        if (jsonObject.has("position_id")) {
            advertisement.setPosition(jsonObject.getString("position_id"));
        }

        return advertisement;
    }

    public static List<Advertisement> parse(JSONArray jsonArray) {
        List<Advertisement> advertisements = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                advertisements.add(parse(jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return advertisements;
    }

    public static Map<String, Advertisement> parsePosition(JSONArray jsonArray) {
        Map<String, Advertisement> tmap = new HashMap<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                tmap.put(jsonObject.getString("position_id"), parse(jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tmap;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }
}
