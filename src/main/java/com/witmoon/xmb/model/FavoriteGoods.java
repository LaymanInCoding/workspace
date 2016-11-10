package com.witmoon.xmb.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 商品收藏数据类
 * Created by zhyh on 2015/5/4.
 */
public class FavoriteGoods extends BaseBean {
    private String id;
    private String goodsId;
    private String photo;
    private String title;
    private String price;
    private String marketPrice;
    private String discount;

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getId() {
        return id;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public static FavoriteGoods parse(JSONObject jsonObject) throws JSONException {
        FavoriteGoods goods = new FavoriteGoods();

        goods.setTitle(jsonObject.getString("goods_name"));
        goods.setPhoto(jsonObject.getString("goods_thumb"));
        goods.setId(jsonObject.getString("rec_id"));
        goods.setMarketPrice(jsonObject.getString("market_price_formatted"));
        goods.setPrice(jsonObject.getString("shop_price_formatted"));
        goods.setGoodsId(jsonObject.getString("goods_id"));

        return goods;
    }
}
