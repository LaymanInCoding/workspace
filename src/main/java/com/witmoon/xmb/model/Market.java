package com.witmoon.xmb.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 卖场实体类
 * Created by zhyh on 2015/7/21.
 */
public class Market {
    private String id;
    private String name;
    private Long startTime;
    private Long endTime;
    private Long activeRemainderTime;
    private String image;
    private String favourable;

    public void setFavourable(String favourable) {
        this.favourable = favourable;
    }

    public String getFavourable() {
        return favourable;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getStartTime() {
        return startTime;
    }

    public Long getActiveRemainderTime() {
        return activeRemainderTime;
    }

    public void setActiveRemainderTime(Long activeRemainderTime) {
        this.activeRemainderTime = activeRemainderTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public String getImage() {
        return image;
    }

    public String getTimeDesc() {
        long temp = activeRemainderTime;
        int day = (int) (temp / (60 * 60 * 24));
        if (day > 0) {
            temp = temp - (60 * 60 * 24) * day;
        }
        int hour = (int) (temp / (60 * 60));
        return String.format("剩余%d天%d小时", day, hour);
    }

    public static Market parse(JSONObject marketObj) throws JSONException {
        Market market = new Market();

        market.setId(marketObj.getString("act_id"));
        market.setName(marketObj.getString("act_name"));
        market.setStartTime((long) Integer.parseInt(marketObj.getString("start_time")));
        market.setEndTime((long) Integer.parseInt(marketObj.getString("end_time")));
        market.setImage(marketObj.getString("act_img"));
        market.setActiveRemainderTime(Long.valueOf(marketObj.getString("active_remainder_time")));
        if (marketObj.has("favourable_name")) {
            market.setFavourable(marketObj.getString("favourable_name"));
        }

        return market;
    }
}
