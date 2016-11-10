package com.witmoon.xmb.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 商品实体类
 * Created by zhyh on 2015-07-11.
 */
public class Goods extends BaseBean {
    private String id;
    private String name;
    private String brief;
    private String thumb;
    private String brandId;     // 所属品牌ID
    private String cateId;      // 所属分类ID
    private int rank;           // 评分
    private int inventory;      // 商品库存
    private String discount;    // 折扣
    private boolean isNoPostage; // 是否包邮
    private boolean isPromote;  // 是否限时促销
    private String preferentialInfo;   // 优惠信息
    private String goodsFreight;   // 运费
    private Long activeRemainderTime;//活动剩余时间
    private boolean isCollected;    // 是否已被收藏
    private String marketPriceDesc;
    private float shopPrice;
    private String shopPriceDesc;
    private int salesSum;
    private String[] gallery;
    private String[] detailGallery;

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public int getInventory() {
        return inventory;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrief() {
        return brief;
    }

    public String getThumb() {
        return thumb;
    }

    public void setMarketPriceDesc(String marketPriceDesc) {
        this.marketPriceDesc = marketPriceDesc;
    }

    public void setShopPriceDesc(String shopPriceDesc) {
        this.shopPriceDesc = shopPriceDesc;
    }

    public String getMarketPriceDesc() {
        if (marketPriceDesc.endsWith("元")) {
            return marketPriceDesc.substring(0, marketPriceDesc.length() - 1);
        }
        return marketPriceDesc;
    }

    public String getShopPriceDesc() {
        if (shopPriceDesc.endsWith("元")) {
            return shopPriceDesc.substring(0, shopPriceDesc.length() - 1);
        }
        return shopPriceDesc;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public void setShopPrice(float shopPrice) {
        this.shopPrice = shopPrice;
    }

    public void setSalesSum(int salesSum) {
        this.salesSum = salesSum;
    }

    public void setGallery(String[] gallery) {
        this.gallery = gallery;
    }

    public String getBrandId() {
        return brandId;
    }

    public String getCateId() {
        return cateId;
    }

    public int getRank() {
        return rank;
    }

    public float getShopPrice() {
        return shopPrice;
    }

    public int getSalesSum() {
        return salesSum;
    }

    public String[] getGallery() {
        return gallery;
    }

    public void setDetailGallery(String[] detailGallery) {
        this.detailGallery = detailGallery;
    }

    public String[] getDetailGallery() {
        return detailGallery;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public boolean isNoPostage() {
        return isNoPostage;
    }

    public String getGoodsFreight() {
        return goodsFreight;
    }

    public void setGoodsFreight(String goodsFreight) {
        this.goodsFreight = goodsFreight;
    }

    public boolean isPromote() {
        return isPromote;
    }

    public void setIsPromote(boolean isPromote) {
        this.isPromote = isPromote;
    }

    public String getPreferentialInfo() {
        return preferentialInfo;
    }

    public void setPreferentialInfo(String preferentialInfo) {
        this.preferentialInfo = preferentialInfo;
    }

    public Long getActiveRemainderTime() {
        return activeRemainderTime;
    }

    public void setIsNoPostage(boolean isNoPostage) {
        this.isNoPostage = isNoPostage;
    }

    public void setActiveRemainderTime(Long activeRemainderTime) {
        this.activeRemainderTime = activeRemainderTime;
    }

    public void setIsCollected(boolean isCollected) {
        this.isCollected = isCollected;
    }

    public boolean isCollected() {
        return isCollected;
    }

    // 解析Json
    public static Goods parse(JSONObject jsonObject) throws JSONException {
        Goods goods = new Goods();

        goods.setId(jsonObject.getString("goods_id"));
        goods.setName(jsonObject.getString("goods_name"));
        if (jsonObject.has("goods_brief")) {
            goods.setBrief(jsonObject.getString("goods_brief"));
        }
        if (jsonObject.has("goods_thumb")) {
            goods.setThumb(jsonObject.getString("goods_thumb"));
        }
        if (jsonObject.has("salesnum")) {
            goods.setSalesSum(jsonObject.getInt("salesnum"));
        }
        if (jsonObject.has("zhekou")) {
            goods.setDiscount(jsonObject.getString("zhekou"));
        }
        if (jsonObject.has("is_shipping")) {
            goods.setIsNoPostage("1".equals(jsonObject.getString("is_shipping")));
        }
        if (jsonObject.has("is_promote")) {
            goods.setIsPromote("1".equals(jsonObject.getString("is_promote")));
        }
        if (jsonObject.has("active_remainder_time")) {
            try {
                goods.setActiveRemainderTime(Long.valueOf(jsonObject.getString
                        ("active_remainder_time")));
            } catch (Exception ignored) {
            }
        }
        if (jsonObject.has("is_collect")) {
            goods.setIsCollected(jsonObject.getInt("is_collect") == 1);
        }
        if (jsonObject.has("preferential_info")) {
            goods.setPreferentialInfo(jsonObject.getString("preferential_info"));
        }
        if (jsonObject.has("carriage_fee")) {
            goods.setGoodsFreight(jsonObject.getString("carriage_fee"));
        }
        goods.setMarketPriceDesc(jsonObject.getString("market_price_formatted"));
        goods.setShopPriceDesc(jsonObject.getString("shop_price_formatted"));
        if (jsonObject.has("shop_price")) {
            goods.setShopPrice(Float.parseFloat(jsonObject.getString("shop_price")));
        }
        if (jsonObject.has("goods_number")) {
            goods.setInventory(Integer.parseInt(jsonObject.getString("goods_number")));
        }

        if (jsonObject.has("goods_gallery")) {
            JSONArray imgUrlArray = jsonObject.getJSONArray("goods_gallery");
            String[] tmp = new String[imgUrlArray.length()];
            for (int i = 0; i < imgUrlArray.length(); i++) {
                tmp[i] = imgUrlArray.getString(i);
            }
            goods.setGallery(tmp);
        }

        if (jsonObject.has("goods_desc")) {
            JSONArray imgUrlArray = jsonObject.getJSONArray("goods_desc");
            String[] tmp = new String[imgUrlArray.length()];
            for (int i = 0; i < imgUrlArray.length(); i++) {
                tmp[i] = imgUrlArray.getString(i);
            }
            goods.setDetailGallery(tmp);
        }

        return goods;
    }
}
