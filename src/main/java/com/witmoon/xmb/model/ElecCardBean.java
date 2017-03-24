package com.witmoon.xmb.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ming on 2017/3/22.
 */
public class ElecCardBean extends BaseBean {
    private String order_sn;

    private String add_time;

    private String order_amount;

    private String order_status_code;

    private String card_amount;

    public String getCard_amount() {
        return card_amount;
    }

    public void setCard_amount(String card_amount) {
        this.card_amount = card_amount;
    }

    public String getOrder_sn() {
        return order_sn;
    }

    public void setOrder_sn(String order_sn) {
        this.order_sn = order_sn;
    }

    public String getAdd_time() {
        return add_time;
    }

    public void setAdd_time(String add_time) {
        this.add_time = add_time;
    }

    public String getOrder_amount() {
        return order_amount;
    }

    public void setOrder_amount(String order_amount) {
        this.order_amount = order_amount;
    }

    public String getOrder_status_code() {
        return order_status_code;
    }

    public void setOrder_status_code(String order_status_code) {
        this.order_status_code = order_status_code;
    }

    public List<CardBean> getOrder_cards_list() {
        return order_cards_list;
    }

    public void setOrder_cards_list(List<CardBean> order_cards_list) {
        this.order_cards_list = order_cards_list;
    }

    private List<CardBean> order_cards_list;


    public static class CardBean extends BaseBean {
        private String card_img;
        private String card_name;
        private String card_money;

        public String getCard_name() {
            return card_name;
        }

        public void setCard_name(String card_name) {
            this.card_name = card_name;
        }

        public String getCard_img() {
            return card_img;
        }

        public void setCard_img(String card_img) {
            this.card_img = card_img;
        }

        public int getCard_number() {
            return card_number;
        }

        public void setCard_number(int card_number) {
            this.card_number = card_number;
        }

        public String getCard_money() {
            return card_money;
        }

        public void setCard_money(String card_money) {
            this.card_money = card_money;
        }

        private int card_number;
    }

    public static ElecCardBean parse(JSONObject orderObj) throws JSONException {
        ElecCardBean order = new ElecCardBean();
        order.setOrder_sn(orderObj.getString("order_sn"));
        order.setAdd_time(orderObj.getString("add_time"));
        order.setOrder_amount(orderObj.getString("order_amount"));
        order.setCard_amount(orderObj.getString("card_amount"));
        order.setOrder_status_code(orderObj.getString("order_status_code"));
        JSONArray orderCardList = orderObj.getJSONArray("order_cards_list");
        List<CardBean> goodsList = new ArrayList<>(orderCardList.length());
        for (int i = 0; i < orderCardList.length(); i++) {
            JSONObject goodsObj = orderCardList.getJSONObject(i);
            CardBean bean = new CardBean();
            bean.setCard_img(goodsObj.getString("card_img"));
            bean.setCard_money(goodsObj.getString("card_money"));
            bean.setCard_name(goodsObj.getString("card_name"));
            bean.setCard_number(goodsObj.getInt("card_number"));
            goodsList.add(bean);
        }
        order.setOrder_cards_list(goodsList);
        return order;
    }
}
