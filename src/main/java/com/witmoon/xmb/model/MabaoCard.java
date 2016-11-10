package com.witmoon.xmb.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/6/23.
 */
public class MabaoCard {
    public int getIs_checked() {
        return is_checked;
    }

    public void setIs_checked(int is_checked) {
        this.is_checked = is_checked;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getCard_money() {
        return card_money;
    }

    public void setCard_money(String card_money) {
        this.card_money = card_money;
    }

    public String getCard_surplus_money() {
        return card_surplus_money;
    }

    public void setCard_surplus_money(String card_surplus_money) {
        this.card_surplus_money = card_surplus_money;
    }

    public String getCard_use_end_time() {
        return card_use_end_time;
    }

    public void setCard_use_end_time(String card_use_end_time) {
        this.card_use_end_time = card_use_end_time;
    }

    private String card_no;
    private String card_money;
    private String card_surplus_money;
    private String card_use_end_time;
    private int is_checked;

    public Boolean getOver_date() {
        return over_date;
    }

    public void setOver_date(Boolean over_date) {
        this.over_date = over_date;
    }

    private Boolean over_date;

    public static MabaoCard parse(JSONObject jsonObject){
        MabaoCard mabaoCard = new MabaoCard();
        try {
            mabaoCard.setCard_no(jsonObject.getString("card_no"));
            mabaoCard.setCard_money(jsonObject.getString("card_money"));
            mabaoCard.setCard_surplus_money(jsonObject.getString("card_surplus_money"));
            mabaoCard.setCard_use_end_time(jsonObject.getString("use_end_time"));
            mabaoCard.setIs_checked(0);
            mabaoCard.setOver_date(jsonObject.getBoolean("over_date"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mabaoCard;
    }

}
