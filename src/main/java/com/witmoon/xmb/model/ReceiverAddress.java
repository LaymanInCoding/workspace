package com.witmoon.xmb.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 收货地址
 * Created by zhyh on 2015/6/2.
 */
public class ReceiverAddress extends BaseBean {
    private String id;
    private String name;
    private String address;
    private String provinceId;
    private String cityId;
    private String districtId;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String telephone;
    private String email;
    private String is_black;
    private String real_name;

    public String getIdentity_card() {
        return identity_card;
    }

    public void setIdentity_card(String identity_card) {
        this.identity_card = identity_card;
    }

    public String getReal_name() {
        return real_name;
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getIs_black() {
        return is_black;
    }

    public void setIs_black(String is_black) {
        this.is_black = is_black;
    }

    private String identity_card;
    private boolean isDefault;

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIsDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public String getCityId() {
        return cityId;
    }

    public String getDistrictId() {
        return districtId;
    }

    // 解析方法
    public static ReceiverAddress parse(JSONObject addressObj,Boolean is_) throws JSONException {
        ReceiverAddress address = new ReceiverAddress();
        if(addressObj.has("address_id")) {
            address.setId(addressObj.getString("address_id"));
        }else if(addressObj.has("id")){
            address.setId(addressObj.getString("id"));
        }
        address.setName(addressObj.getString("consignee"));
        address.setProvinceName(addressObj.getString("province_name"));
        address.setCityName(addressObj.getString("city_name"));
        address.setDistrictName(addressObj.getString("district_name"));
        address.setAddress(addressObj.getString("address"));
        address.setTelephone(addressObj.getString("mobile"));
        if(addressObj.has("is_default")) {
            address.setIsDefault(addressObj.getString("is_default").equals("1"));
        }else if(addressObj.has("default_address")){
            address.setIsDefault(addressObj.getInt("default_address") == 1);
        }
        return address;
    }
}
