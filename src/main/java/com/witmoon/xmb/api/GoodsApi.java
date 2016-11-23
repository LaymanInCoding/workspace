package com.witmoon.xmb.api;

import android.util.Log;

import com.duowan.mobile.netroid.Listener;
import com.witmoon.xmb.AppContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 商品相关Api接口
 * Created by zhyh on 2015/7/11.
 */
public class GoodsApi {

    // 分类商品列表
    public static void goodsListByCategory(String cateId, int page, String sortColumn,
                                           String sortType, boolean filterInStore,
                                           Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("cat_id", cateId);
        pm.put("sort", sortColumn);
        pm.put("order", sortType);
        pm.put("having_goods", String.valueOf(filterInStore));
        pm.put("page_now", String.valueOf(page));
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/home/listGoodsByCId"), pm, listener));
    }

    // 品牌商品列表
    public static void goodsListByBrand(String brandId, int page, String sortColumn,
                                        String sortType, boolean filterInStore,
                                        Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("brand_id", brandId);
        pm.put("sort", sortColumn);
        pm.put("order", sortType);
        pm.put("having_goods", String.valueOf(filterInStore));
        pm.put("page_now", String.valueOf(page));
        pm.put("version",AppContext.geVerSion());
        pm.put("channel",AppContext.getChannels());
        pm.put("device","android");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/home/listGoodsByBId"), ApiHelper.getParamObj(pm), listener));
    }

    // 商品搜索
    public static void search(String keyword, int page, String sortColumn, String sortType,
                              boolean filterInStore, Listener<JSONObject> listener) {
        JSONObject paramObject = new JSONObject();
        try {
            JSONObject filterObj = new JSONObject();
            filterObj.put("sort", sortColumn);
            filterObj.put("order", sortType);
            filterObj.put("keywords", keyword);
            filterObj.put("having_goods", String.valueOf(filterInStore));
            paramObject.put("filter", filterObj);
            JSONObject pageObj = new JSONObject();
            pageObj.put("page", page);
            pageObj.put("count", ApiHelper.PAGE_SIZE);
            paramObject.put("pagination", pageObj);
        } catch (JSONException ignored) {
        }
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + ("search/index"),
                paramObject, listener));
    }

    // 加载专卖场
    public static void market(String id, int p, String sortColumn, String sortType,
                              boolean filterInStore, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("act_id", id);
        pm.put("sort", sortColumn);
        pm.put("order", sortType);
        pm.put("having_goods", String.valueOf(filterInStore));
//        pm.put("version",AppContext.geVerSion());
//        pm.put("channel",AppContext.getChannels());
//        pm.put("device","android");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/home/getGoodsListByActId"), ApiHelper.getPaginationParamObj(p, pm), listener));
    }

    // 加载免税
    public static void market1(int page,Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/home/getCrossBorder&&page=" + page),listener));
    }

    // 商品详情, 如果是专场商品, 把其ID传进来
    public static void goodsDetail(String id, String actId, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("goods_id", id);
        pm.put("act_id", actId);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+
                ("goods/getgoodsinfo"), ApiHelper.getParamObj(pm), listener));
    }

    // 商品规格参数接口
    public static void goodsProperties(String id, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("goods_id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+
                ("goods/getgoodsproperty"), pm, listener));
    }

    // 获取商品评价
    public static void goodsComments(String id, int p, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("goods_id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+
                ("goods/comments"),
                ApiHelper.getPaginationParamObj(p, pm), listener));
    }

    // 商品规格信息
    public static void goodsSpecification(String id, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("goods_id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+
                ("goods/getgoodsspecs"), pm, listener));
    }

    // 评价商品
    public static void commentGoods(String gid, String oid, String rating, String content,
                                    Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("goods_id", gid);
        pm.put("order_id", oid);
        pm.put("comment_rank", rating);
        pm.put("comment_content", content);
        pm.put("user_id", String.valueOf(AppContext.getLoginUid()));
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/comment_goods"), pm, listener));
    }

    // -------------------------   购物车流程   --------------------------

    // 加入购物车
    public static void addToCart(String id, String s, int count, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("goods_id", id);
        pm.put("spec", s);
        pm.put("number", String.valueOf(count));

        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+("flow/addtocart"),
                ApiHelper.getParamObj(pm), listener));
    }

    // 选择规格
    public static void getGoodsSpecificationInfo(String id, String attr, int count, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("goods_id", id);
        pm.put("attr", attr);
        pm.put("number", String.valueOf(count));
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+("goods/getgoodsspecinfos"),
                pm, listener));
    }

    // 购物车列表
    public static void cartList(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "flow/cart",
                ApiHelper.getParamObj(null), listener));
    }

    // 购物车商品数量
    public static void cartGoodsCount(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+
                ("flow/list_count"), ApiHelper.getParamObj(null), listener));
    }

    // 从购物车中删除商品
    public static void removeFromCart(String id, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("rec_id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "flow/del_cart",
                ApiHelper.getParamObj(pm), listener));
    }

    // 更新购物车
    public static void updateCart(String id, boolean isChecked, int num, Listener<JSONObject> l) {
        Map<String, String> pm = new HashMap<>();
        pm.put("rec_id", id);
        pm.put("flow_order", isChecked ? "1" : "0");
        pm.put("new_number", String.valueOf(num));
//        pm.put("version",AppContext.geVerSion());
//        pm.put("channel",AppContext.getChannels());
//        pm.put("device","android");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "flow/update_cart",
                ApiHelper.getParamObj(pm), l));
    }

    // 购物车全选及取消
    public static void selectAllInCart(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/cart/select_all_or_zero"), ApiHelper.getParamObj(null), listener));
    }

    // 确认订单
    public static void checkOrder(String bonusId, String couponId, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("bonus_id", bonusId);
        pm.put("coupon_id", couponId);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "flow/checkout", ApiHelper.getParamObj(pm), listener));
    }

    // 确认订单,麻包卡支付情况
    public static void checkOrder(String bonusId, String couponId,ArrayList<String> mabaoCardArray, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("bonus_id", bonusId);
        pm.put("coupon_id", couponId);
        String cards = "";
        for (int i = 0; i < mabaoCardArray.size();i++){
            if (i == mabaoCardArray.size() - 1){
                cards += mabaoCardArray.get(i);
            }else{
                cards += mabaoCardArray.get(i) + ",";
            }
        }
        pm.put("cards",cards);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "flow/checkout", ApiHelper.getParamObj(pm), listener));
    }

    public static void checkOrderV2(String bonusId, String couponId,String id,ArrayList<String> mabaoCardArray, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("bonus_id", bonusId);
        pm.put("coupon_id", couponId);
        pm.put("address_id", id);
        String cards = "";
        for (int i = 0; i < mabaoCardArray.size();i++){
            if (i == mabaoCardArray.size() - 1){
                cards += mabaoCardArray.get(i);
            }else{
                cards += mabaoCardArray.get(i) + ",";
            }
        }
        pm.put("cards",cards);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "flow/checkout", ApiHelper.getParamObj(pm), listener));
    }

    // 提交订单
    public static void submitOrder(String addrId, String couponId, String bonusId,String real_name,String identity_card,String postscript,ArrayList<String> mabaoCardArray,
                                   Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("pay_id", "3");
        pm.put("shipping_id", "4");
        pm.put("real_name", real_name);
        pm.put("identity_card", identity_card);
        pm.put("address_id", addrId);
        pm.put("postscript", postscript);
        pm.put("coupon_id", couponId);
        pm.put("bonus_id", bonusId);
        String cards = "";
        for (int i = 0; i < mabaoCardArray.size();i++){
            if (i == mabaoCardArray.size() - 1){
                cards += mabaoCardArray.get(i);
            }else{
                cards += mabaoCardArray.get(i) + ",";
            }
        }
        pm.put("cards",cards);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "flow/done_new", ApiHelper.getParamObj(pm), listener));
    }

    // 验证身份
    public static void check_idcard(String real_name, String identity_card,
                                   Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("real_name", real_name);
        pm.put("identity_card", identity_card);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "idcard/add", ApiHelper.getParamObj(pm), listener));
    }


    // 搜索红包或代金券
    public static void searchBouns(String serialNo, String money, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("bonus_sn", serialNo);
        pm.put("order_money", money);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL + "discount/get_bonus_info", ApiHelper.getParamObj(pm), listener));
    }

    // ------------------------------ 付款部分 ---------------------------------

    // 订单参数RSA签名
    // 为了保护私钥, 此处采用服务器端签名方式
    public static void sign(String orderId, Listener<JSONObject> listener) {
        Map<String, String> pm = new HashMap<>();
        pm.put("order_id", orderId);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/payment/alipay/request"), ApiHelper.getParamObj(pm), listener));
    }
}