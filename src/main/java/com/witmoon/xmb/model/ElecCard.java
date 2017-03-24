package com.witmoon.xmb.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ming on 2017/3/23.
 */
public class ElecCard extends BaseBean {
    private int card_id;
    private String card_money;
    private String card_max_money;
    private String card_money_format;
    private int delete_index = -1;
    private int card_num = 1;

    public void setCard_num(int number) {
        this.card_num = number;
    }

    public int getCard_num() {
        return card_num;
    }

    public void setDelete_index(int index) {
        this.delete_index = index;
    }

    public int getDelete_index() {
        return delete_index;
    }

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

    public String getCard_money_format() {
        return card_money_format;
    }

    public void setCard_money_format(String card_money_format) {
        this.card_money_format = card_money_format;
    }

    public static ElecCard parse(JSONObject jsonObject) {
        ElecCard bean = new ElecCard();
        try {
            bean.setCard_id(jsonObject.getInt("card_id"));
            String card_money = jsonObject.getString("card_money");
            String[] tmp = card_money.split("\\.");
            bean.setCard_money(tmp[0]);
            bean.setCard_max_money(jsonObject.getString("card_max_money"));
            bean.setCard_money_format(jsonObject.getString("card_money_format"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }
}
