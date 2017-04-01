package com.witmoon.xmb.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ming on 2017/3/30.
 */
public class InviteCoupon extends BaseBean {

    private String user_name;
    private String status;
    private String coupon_type;
    private String header_img;

    public String getHeader_img() {
        return header_img;
    }

    public void setHeader_img(String header_img) {
        this.header_img = header_img;
    }

    public String getCoupon_type() {
        return coupon_type;
    }

    public void setCoupon_type(String coupon_type) {
        this.coupon_type = coupon_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public static InviteCoupon parse(JSONObject jsonObject){
        InviteCoupon coupon = new InviteCoupon();
        try {
            coupon.setUser_name(jsonObject.getString("user_name"));
            coupon.setStatus(jsonObject.getString("status"));
            coupon.setCoupon_type(jsonObject.getString("coupon_type"));
            coupon.setHeader_img(jsonObject.getString("header_img"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return coupon;
    }
}
