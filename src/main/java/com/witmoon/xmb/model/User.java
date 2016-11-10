package com.witmoon.xmb.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 登录用户实体类
 * Created by zhyh on 2015-07-08.
 */
public class User extends BaseBean {
    private int uid;

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    private String sid;
    private String name;
    private String account;
    private String password;
    private String avatar;
    private String childSex;
    private String parentSex;
    private String babyBirthday;
    private String dueDay;          // 预产期

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    private String mobile_phone;
    private String rankName;
    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", name='" + name + '\'' +
                ", sid='" + sid + '\'' +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", avatar='" + avatar + '\'' +
                ", childSex='" + childSex + '\'' +
                ", parentSex='" + parentSex + '\'' +
                ", babyBirthday='" + babyBirthday + '\'' +
                ", dueDay='" + dueDay + '\'' +
                ", rankName='" + rankName + '\'' +
                ", baby_id='" + baby_id + '\'' +
                ", baby_birthday='" + baby_birthday + '\'' +
                ", baby_gender='" + baby_gender + '\'' +
                ", baby_photo='" + baby_photo + '\'' +
                ", baby_nickname='" + baby_nickname + '\'' +
                ", rankLevel=" + rankLevel +
                ", is_baby_add='" + is_baby_add + '\'' +
                ", is_Or='" + is_Or + '\'' +
                ", type='" + type + '\'' +
                ", sin_name='" + sin_name + '\'' +
                ", mobile_phone='" + mobile_phone + '\'' +
                ", identity_card='" + identity_card + '\'' +
                '}';
    }

    private String baby_id;
    private String baby_birthday;
    private String baby_gender;
    private String baby_photo;
    private String baby_nickname;
    private int rankLevel;
    private String is_baby_add;

    public String getIs_baby_add() {
        return is_baby_add;
    }

    public void setIs_baby_add(String is_baby_add) {
        this.is_baby_add = is_baby_add;
    }

    public String getBaby_birthday() {
        return baby_birthday;
    }

    public void setBaby_birthday(String baby_birthday) {
        this.baby_birthday = baby_birthday;
    }

    public String getBaby_id() {
        return baby_id;
    }

    public void setBaby_id(String baby_id) {
        this.baby_id = baby_id;
    }

    public String getBaby_gender() {
        return baby_gender;
    }

    public void setBaby_gender(String baby_gender) {
        this.baby_gender = baby_gender;
    }

    public String getBaby_photo() {
        return baby_photo;
    }

    public void setBaby_photo(String baby_photo) {
        this.baby_photo = baby_photo;
    }

    public String getBaby_nickname() {
        return baby_nickname;
    }

    public void setBaby_nickname(String baby_nickname) {
        this.baby_nickname = baby_nickname;
    }

    public String getIs_Or() {
        return is_Or;
    }

    public void setIs_Or(String is_Or) {
        this.is_Or = is_Or;
    }

    private String is_Or;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;

    public String getSin_name() {
        return sin_name;
    }

    public void setSin_name(String sin_name) {
        this.sin_name = sin_name;
    }

    private String sin_name;
    private String identity_card = "";

    public String getIdentity_card() {
        return identity_card;
    }

    public void setIdentity_card(String identity_card) {
        this.identity_card = identity_card;
    }

    public void setRankLevel(int rankLevel) {
        this.rankLevel = rankLevel;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public String getRankName() {
        return rankName;
    }

    public int getRankLevel() {
        return rankLevel;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setParentSex(String parentSex) {
        this.parentSex = parentSex;
    }

    public void setChildSex(String childSex) {
        this.childSex = childSex;
    }

    public String getParentSex() {
        return parentSex;
    }

    public String getChildSex() {
        return childSex;
    }

    public void setBabyBirthday(String babyBirthday) {
        this.babyBirthday = babyBirthday;
    }

    public void setDueDay(String dueDay) {
        this.dueDay = dueDay;
    }

    public String getBabyBirthday() {
        return babyBirthday;
    }

    public String getDueDay() {
        return dueDay;
    }

    // 解析登录返回结果
    public static User parse(JSONObject userObj, String type, String sin_name, String is_or) throws JSONException {
        User user = new User();
        try {
            user.setUid(userObj.getInt("id"));
            user.setAccount(userObj.getString("user_name"));
            user.setPassword(userObj.getString("password"));
            user.setName(userObj.getString("nick_name"));
            user.setRankName(userObj.getString("rank_name"));
            user.setRankLevel(userObj.getInt("rank_level"));
            user.setAvatar(userObj.getString("header_img"));
            user.setChildSex(userObj.getString("child_sex"));
            user.setParentSex(userObj.getString("parent_sex"));
            user.setBabyBirthday(userObj.getString("child_birthday"));
            user.setDueDay(userObj.getString("expected_date"));
            user.setIdentity_card(userObj.getString("identity_card"));
            user.setIs_baby_add(userObj.getString("is_baby_add"));
            user.setType(type);
            user.setSin_name(sin_name);
            user.setIs_Or(is_or);
            user.setMobile_phone(userObj.getString("mobile_phone"));
            if (userObj.isNull("user_baby_info")){
                return user;
            }
            JSONObject user_Baby = userObj.getJSONObject("user_baby_info");
            user.setBaby_id(user_Baby.getString("id"));
            user.setBaby_birthday(user_Baby.getString("birthday"));
            user.setBaby_gender(user_Baby.getString("gender"));
            user.setBaby_photo(user_Baby.getString("photo"));
            user.setBaby_nickname(user_Baby.getString("nickname"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
