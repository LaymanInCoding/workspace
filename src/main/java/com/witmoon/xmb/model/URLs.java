package com.witmoon.xmb.model;

/**
 * URL接口
 * Created by zhyh on 2015/5/5.
 */
public class URLs {
    public final static String HOST = "http://115.28.9.8:8083/mobile/?url=/";
    public final static String HTTP = "http://";
    public final static String HTTPS = "https://";

    // 注册
    public static final String SIGNUP = HOST + "user/signup";
    // 用户名验证
    public static final String VALID = HOST + "user/valid";
    // 发送手机校验码
    public static final String PHONE_CODE = HOST + "user/phoneCode";
    // 登陆
    public static final String SIGNIN = HOST + "user/signin";
}
