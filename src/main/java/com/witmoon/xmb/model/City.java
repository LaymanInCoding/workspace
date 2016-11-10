package com.witmoon.xmb.model;

/**
 * 市级行政单位实体类
 * Created by zhyh on 2015/1/22.
 */
public class City {
    private String code;
    private String name;
    private String provinceCode;

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceCode() {
        return provinceCode;
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
