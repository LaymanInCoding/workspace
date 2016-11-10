package com.witmoon.xmb.model;

/**
 * 县级行政单位实体类
 * Created by zhyh on 2015/1/22.
 */
public class Country {
    private String code;
    private String name;
    private String cityCode;

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
