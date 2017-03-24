package com.witmoon.xmb.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ming on 2017/3/23.
 */
public class CustomElecCard extends BaseBean {
    private int card_id;
    private String card_money;
    private String card_max_money;

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }

    public String getCard_money() {
        return card_money;
    }

    public void setCard_money(String card_money) {
        this.card_money = card_money;
    }

    public String getCard_max_money() {
        return card_max_money;
    }

    public void setCard_max_money(String card_max_money) {
        this.card_max_money = card_max_money;
    }

    public static CustomElecCard parse(JSONObject jsonObject){
        CustomElecCard bean = new CustomElecCard();
        try {
            bean.setCard_id(jsonObject.getInt("card_id"));
            bean.setCard_max_money(jsonObject.getString("card_max_money"));
            bean.setCard_money(jsonObject.getString("card_money"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }
}
