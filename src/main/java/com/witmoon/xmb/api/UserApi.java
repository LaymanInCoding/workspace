package com.witmoon.xmb.api;

import android.util.Log;

import com.duowan.mobile.netroid.Listener;
import com.nostra13.universalimageloader.utils.L;
import com.witmoon.xmb.AppContext;
import com.witmoon.xmb.model.ReceiverAddress;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关Api接口
 * Created by zhyh on 2015/6/16.
 */
public class UserApi {

    // 初始化参数HashMap
    private static Map<String, String> initParamMap() {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("type", "1");
        return paramMap;
    }

    // 验证手机号
    public static void verifyPhone(String phone, Listener<JSONObject> listener) {
        Map<String, String> paramMap = initParamMap();
        paramMap.put("name", phone);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl("/user/valid"),
                paramMap, listener));
    }

    // 发送手机短信验证码
    public static void phoneCode(String phone,String type, Listener<JSONObject> listener) {
        Map<String, String> paramMap = initParamMap();
        paramMap.put("name", phone);
        paramMap.put("requesttype",type);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"users/phonecode", paramMap, listener));
    }

    // 验证手机验证码短信
    @Deprecated
    public static void verifyPhoneCode(String phone, String code, Listener<JSONObject> listener) {
        Map<String, String> paramMap = initParamMap();
        paramMap.put("name", phone);
        paramMap.put("phoneCode", code);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/user/phoneCodeValid"), paramMap, listener));
    }

    // 注册接口
    public static void signup(String code ,String email, String phone, String pwd, Listener<JSONObject> listener) {
        Map<String, String> paramMap = initParamMap();
        paramMap.put("phoneCode", code);
        paramMap.put("name", phone);
        paramMap.put("password", pwd);
        paramMap.put("email",email);
        Log.e("signup_name", phone);
        Log.e("singup_password", pwd);
        Log.e("PARAM_MAP_SIGN",paramMap.toString());
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+("/users/register"),
                paramMap, listener));
    }

    // 找回密码, 检测手机号是否已注册
    public static void signed(String phone, Listener<JSONObject> listener) {
        Map<String, String> paramMap = initParamMap();
        paramMap.put("name", phone);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+("/users/checkuser"),
                paramMap, listener));
    }

    // 重置密码接口
    public static void resetPassword(String phone, String pwd, String phonecode, Listener<JSONObject> listener) {
        Map<String, String> paramMap = initParamMap();
        paramMap.put("name", phone);
        paramMap.put("password", pwd);
        paramMap.put("phoneCode", phonecode);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+("/users/modpassword"),
                paramMap, listener));
    }

    // 第三方登录
    public static void login(String type, String account, String avatar, String username,
                             Listener<JSONObject> listener) {
        Map<String, String> paramMap = initParamMap();
        paramMap.put("sign_type", type);
        paramMap.put("name", account);
        paramMap.put("header_img", avatar);
        paramMap.put("nick_name", username);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"users/login",
                paramMap, listener));
    }

    // 登录
    public static void login1(String account, String pwd, Listener<JSONObject> listener) {
        Map<String, String> paramMap = initParamMap();
        paramMap.put("sign_type", "ecshop");
        paramMap.put("name", account);
        paramMap.put("password", pwd);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"users/login",
                paramMap, listener));
    }

    // 登录
    public static void login(String account, String pwd, String type, Listener<JSONObject> listener) {
        Map<String, String> paramMap = initParamMap();
        paramMap.put("sign_type", type);
        paramMap.put("name", account);
        paramMap.put("password", pwd);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"users/register",
                paramMap, listener));
    }

    // --------------------------- 用户设置 ---------------------------

    // 修改密码
    public static void modifyPwd(String opwd, String pwd, Listener<JSONObject> listener) {
        Map<String, String> paramMap = initParamMap();
        paramMap.put("old_password", opwd);
        paramMap.put("new_password", pwd);
        paramMap.put("name",AppContext.getLoginInfo().getMobile_phone());
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"users/modpassword", ApiHelper.getParamObj(paramMap), listener));
    }

    // 加载用户资料信息
    public static void userInfo(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/user/info"), ApiHelper.getParamObj(null), listener));
    }

    // ----------------------------------------------------------------------

    // 收货地址列表
    public static void addressList(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"address/address_list", ApiHelper.getParamObj(null), listener));
    }

    // 新建收货地址
    public static void newAddress(ReceiverAddress address, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"address/address_add", buildAddressParamObj(address, null), listener));
    }

    // 设置默认收货地址
    public static void setDefaultAddr(String addrId, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("address_id", addrId);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"address/set_default_address", ApiHelper.getParamObj(paramMap), listener));
    }

    // 删除收货地址
    public static void deleteAddress(String id, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("address_id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"address/address_delete", ApiHelper.getParamObj(paramMap), listener));
    }

    // 获取地址信息
    public static void fetchAddress(String id, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("address_id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"address/address_info", ApiHelper.getParamObj(paramMap), listener));
    }

    // 更新地址
    public static void updateAddress(String id, ReceiverAddress addr, Listener<JSONObject> l) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("address_id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"address/address_edit", buildAddressParamObj(addr, paramMap), l));
    }

    private static JSONObject buildAddressParamObj(ReceiverAddress address,
                                                   Map<String, String> paramMap) {
        JSONObject paramObj = ApiHelper.getParamObj(paramMap);
        JSONObject addrObj = new JSONObject();
        try {
            addrObj.put("consignee", address.getName());
            addrObj.put("province", address.getProvinceId());
            addrObj.put("city", address.getCityId());
            addrObj.put("district", address.getDistrictId());
            addrObj.put("mobile", address.getTelephone());
            addrObj.put("address", address.getAddress());
            addrObj.put("default", address.isDefault() ? "1" : "0");
            paramObj.put("address", addrObj);
        } catch (Exception ignored) {
        }
        return paramObj;
    }

    // ----------------------------------------------------------------------

    // 收藏商品
    public static void collectGoods(String id, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("goods_id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"collect/collect_goods", ApiHelper.getParamObj(paramMap), listener));
    }

    // 取消收藏商品
    public static void cancelCollect(String id, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("rec_id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"collect/del_goods", ApiHelper.getParamObj(paramMap), listener));
    }

    // 商品收藏列表
    public static void collectionList(int page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"collect/goods_list", ApiHelper.getPaginationParamObj(page, ApiHelper
                .getParamObj(null)), listener));
    }
    // 收藏品牌
    public static void collectBrand(String id, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("brand_id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/user/collectBrands/create"), ApiHelper.getParamObj(paramMap), listener));
    }

    // 取消品牌收藏
    public static void cancelBrandCollection(String id, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("rec_id", id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/user/collectBrands/delete"), ApiHelper.getParamObj(paramMap), listener));
    }

    // 品牌收藏列表
    public static void brandCollectionList(int page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/user/collectBrands/list"), ApiHelper.getPaginationParamObj(page, ApiHelper
                .getParamObj(null)), listener));
    }

    // 订单列表
    public static void orderList(String type, int page, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("type", type);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"order/order_list_new",
                ApiHelper.getPaginationParamObj(page, ApiHelper.getParamObj(paramMap)), listener));
    }

    // 确认收货affirm_received
    public static void affirm_received(String order_id, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("order_id", order_id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"order/order_receive",
                ApiHelper.getParamObj(paramMap), listener));
    }

    // 全部订单列表
    public static void all_orderList(int page, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("session[sid]", AppContext.getLoginUid()+"");
        paramMap.put("session[uid]", ApiHelper.mSessionID);
        paramMap.put("page",page+"");
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl("/refund/list"),
                ApiHelper.getParamObj(paramMap), listener));
    }
    // 已申请接口
    public static void applyList(String page, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("session[sid]", AppContext.getLoginUid()+"");
        paramMap.put("session[uid]", ApiHelper.mSessionID);
        paramMap.put("page",page);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl("refund/applyList"),
                ApiHelper.getParamObj(paramMap), listener));
    }

    // 已申请退款信息
    public static void search_(String page, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("session[sid]", AppContext.getLoginUid()+"");
        paramMap.put("session[uid]", ApiHelper.mSessionID);
        paramMap.put("order_sn",page);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl("/refund/search"),
                ApiHelper.getParamObj(paramMap), listener));
    }

    // 已申请退款信息
    public static void out_addss(String page, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("session[sid]", AppContext.getLoginUid()+"");
        paramMap.put("session[uid]", ApiHelper.mSessionID);
        paramMap.put("order_id",page);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl("/refund/info"),
                ApiHelper.getParamObj(paramMap), listener));
    }

    //
    public static void fill_waybill(String order_id,String logistics_no,String logistics_cost, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("session[sid]", AppContext.getLoginUid()+"");
        paramMap.put("session[uid]", ApiHelper.mSessionID);
        paramMap.put("order_id",order_id);
        paramMap.put("logistics_no",logistics_no);
        paramMap.put("logistics_cost",logistics_cost);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl("/refund/submitLogistics"),
                ApiHelper.getParamObj(paramMap), listener));
    }

    //
    public static void check_proo(String order_id, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("session[sid]", AppContext.getLoginUid()+"");
        paramMap.put("session[uid]", ApiHelper.mSessionID);
        paramMap.put("order_id",order_id);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl("/refund/process"),
                ApiHelper.getParamObj(paramMap), listener));
    }

    // 订单详情
    public static void orderDetail(String id ,String sn,Listener<JSONObject> listener) {
        String api = "order/order_detail_new";
        //如果传了order_sn，走下面接口
        Map<String, String> paramMap = new HashMap<>();
        if (id != null && !id.equals(""))
        {
            api = "order/order_detail";
            paramMap.put("order_id",id);
        }else{
            paramMap.put("order_sn",sn);
        }
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+api,
                ApiHelper.getParamObj(paramMap), listener));
    }

    // -----------------------------------------------------------------------------------

    // 我的优惠券
    public static void cashCoupon(int page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"discount/get_user_coupon", ApiHelper.getPaginationParamObj(page, ApiHelper
                .getParamObj(null)), listener));
    }

    // 我的优惠券(可用优惠券)
    public static void enabledCashCoupon(int page, String money, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("order_money", money);
//        paramMap.put("version",AppContext.geVerSion());
//        paramMap.put("channel",AppContext.getChannels());
//        paramMap.put("device",AppContext.getDevice());
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL+"discount/get_coupon_enable", ApiHelper.getPaginationParamObj(page, ApiHelper
                .getParamObj(paramMap)), listener));
    }

    // 用户浏览记录
    public static void browseHistory(int page, Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL +
                ("users/history"), ApiHelper.getPaginationParamObj(page, ApiHelper
                .getParamObj(null)), listener));
    }

    // 清除用户浏览记录
    public static void cleanBrowseHistory(Listener<JSONObject> listener) {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.BASE_URL +
                ("users/chistory"), ApiHelper.getParamObj(null), listener));
    }

    // 用户反馈
    public static void evaluate(String evaluate, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("msg_content", evaluate);
//        paramMap.put("version",AppContext.geVerSion());
//        paramMap.put("channel",AppContext.getChannels());
//        paramMap.put("device",AppContext.getDevice());
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl
                ("/user/feedback"), ApiHelper.getParamObj(paramMap), listener));
    }
    //版本更新
    public static void Upversion(String device, Listener<JSONObject> listener) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("device",device);
        Netroid.addRequest(new NormalPostJSONRequest("http://api.xiaomabao.com/common/update",
                paramMap, listener));
    }

    public static void  CheckIdCard(String identity_card,String uid,String sid,String real_name, Listener<JSONObject> listener){
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("session[uid]",uid);
        paramMap.put("session[sid]",sid);
        paramMap.put("real_name",real_name);
        paramMap.put("identity_card",identity_card);
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl("/user/realname"),
                ApiHelper.getParamObj(paramMap), listener));
    }

    public static void  is_Gnore(Listener<JSONObject> listener){
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl("/common/ignore"),listener));
    }

    public static void vaccine_record(Listener<JSONObject> listener)
    {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl("/discovery/vaccine_record"),
                ApiHelper.getParamObj(new HashMap<String, String>()), listener));
    }

    public static void pregnancy_record(Listener<JSONObject> listener)
    {
        Netroid.addRequest(new NormalPostJSONRequest(ApiHelper.getAbsoluteApiUrl("/discovery/pregnancy_record"),
                ApiHelper.getParamObj(new HashMap<String, String>()), listener));
    }
}