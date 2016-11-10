package com.witmoon.xmb.model;

/**
 * 省级行政单位实体类
 * Created by zhyh on 2015/1/22.
 */
public class Province {
    private String code;    // 省级行政单位代码, 为主键
    private String name;    // 省级行政单位名称

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
