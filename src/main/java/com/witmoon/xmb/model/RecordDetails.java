package com.witmoon.xmb.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by de on 2016/1/28.
 */
public class RecordDetails extends BaseBean {
    private String id;
    private String content;
    private String year;
    private String month;
    private String day;
    private String week;
    private String addtime;
    private String mood;
    private int is_first;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    private String group;

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getIs_first() {
        return is_first;
    }

    public void setIs_first(int is_first) {
        this.is_first = is_first;
    }

    private String position;

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    private String weather;
    private ArrayList<ImageInfo> photo,photo_thumb;

    public ArrayList<ImageInfo> getPhoto() {
        return photo;
    }

    public ArrayList<ImageInfo> getPhoto_thumb() {
        return photo_thumb;
    }

    public void setPhoto(ArrayList<ImageInfo> photo) {
        this.photo = photo;
    }

    public void setPhoto_thumb(ArrayList<ImageInfo> photo_thumb) {
        this.photo_thumb = photo_thumb;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public static RecordDetails parse(JSONObject reObj,int is_first) throws JSONException {
        RecordDetails recordDetails = new RecordDetails();
        recordDetails.setId(reObj.getString("id"));
        recordDetails.setIs_first(is_first);
        recordDetails.setContent(reObj.getString("content"));
        recordDetails.setYear(reObj.getString("year"));
        recordDetails.setMonth(reObj.getString("month"));
        recordDetails.setDay(reObj.getString("day"));
        recordDetails.setWeek(reObj.getString("week"));
        recordDetails.setAddtime(reObj.getString("addtime"));
        recordDetails.setMood(reObj.getString("mood"));
        recordDetails.setWeather(reObj.getString("weather"));
        recordDetails.setPosition(reObj.getString("position"));
        recordDetails.setGroup(reObj.getString("group"));
        JSONArray reArray = reObj.getJSONArray("photo");
        ArrayList<ImageInfo> photoImg = new ArrayList<>();
        for (int i = 0; i < reArray.length(); i++) {
            ImageInfo info = new ImageInfo();
            info.url = reArray.getString(i);
            info.height = 0;
            info.width = 0;
            photoImg.add(info);
        }
        recordDetails.setPhoto(photoImg);

        try{
            JSONArray rethumbArray = reObj.getJSONArray("photo_thumb");
            ArrayList<ImageInfo> photoThumbImg = new ArrayList<>();
            for (int i = 0; i < rethumbArray.length(); i++) {
                ImageInfo info = new ImageInfo();
                info.url = rethumbArray.getString(i);
                info.height = 0;
                info.width = 0;
                photoThumbImg.add(info);
            }
            recordDetails.setPhoto_thumb(photoThumbImg);
        }catch (Exception e){
            recordDetails.setPhoto_thumb(photoImg);
        }
        return recordDetails;
    }
}
