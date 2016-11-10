package com.witmoon.xmb.model;

/**
 * 浏览记录数据实体类
 * Created by zhyh on 2015/5/3.
 */
public class BrowseHistory extends BaseBean {
    private String goodsId;
    private String photo;
    private String goodsName;
    private String browseTime;

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getBrowseTime() {
        return browseTime;
    }

    public void setBrowseTime(String browseTime) {
        this.browseTime = browseTime;
    }
}
