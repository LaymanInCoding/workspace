package com.witmoon.xmb.model.circle;


import org.json.JSONException;
import org.json.JSONObject;

public class CircleCategory {


    private int circle_id;

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

    public String getCircle_logo() {
        return circle_logo;
    }

    public void setCircle_logo(String circle_logo) {
        this.circle_logo = circle_logo;
    }

    public String getCircle_desc() {
        return circle_desc;
    }

    public void setCircle_desc(String circle_desc) {
        this.circle_desc = circle_desc;
    }

    public int getCircle_user_cnt() {
        return circle_user_cnt;
    }

    public void setCircle_user_cnt(int circle_user_cnt) {
        this.circle_user_cnt = circle_user_cnt;
    }

    public int getCircle_post_cnt() {
        return circle_post_cnt;
    }

    public void setCircle_post_cnt(int circle_post_cnt) {
        this.circle_post_cnt = circle_post_cnt;
    }

    private String circle_name;
    private String circle_logo;
    private String circle_desc;
    private int circle_user_cnt;
    private int circle_post_cnt;

    public Boolean getUser_is_join() {
        return user_is_join;
    }

    public void setUser_is_join(Boolean user_is_join) {
        this.user_is_join = user_is_join;
    }

    private Boolean user_is_join;

    public static CircleCategory parse(JSONObject jsonObject) {
        CircleCategory circleCategory = new CircleCategory();
        try {
            circleCategory.setCircle_id(jsonObject.getInt("circle_id"));
            circleCategory.setCircle_name(jsonObject.getString("circle_name"));
            circleCategory.setCircle_logo(jsonObject.getString("circle_logo"));
            circleCategory.setCircle_desc(jsonObject.getString("circle_desc"));
            circleCategory.setCircle_user_cnt(jsonObject.getInt("circle_user_cnt"));
            circleCategory.setCircle_post_cnt(jsonObject.getInt("circle_post_cnt"));
            circleCategory.setUser_is_join(jsonObject.getBoolean("is_join"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return circleCategory;
    }
}

