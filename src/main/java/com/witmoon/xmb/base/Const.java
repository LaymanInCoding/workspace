package com.witmoon.xmb.base;

import com.witmoon.xmb.R;

/**
 * 常量类
 * Created by zhyh on 2015/4/29.
 */
public class Const {
    // http parameters
    public static final int HTTP_MEMORY_CACHE_SIZE = 100 * 1024 * 1024; // 100MB
    public static final int HTTP_DISK_CACHE_SIZE = 200 * 1024 * 1024; // 200MB
    public static final String HTTP_DISK_CACHE_DIR_NAME = "netroid";
    public static final String HTTP_CACHE_DIR = "retrofit";
    public static final String USER_AGENT = "netroid.cn";
    public static final String MY_JOIN_CIRCLE_KEY = "my_join_circle_array";
    public static final String MBQ_SEARCH_HOT_KEYWORD = "mbq_search_hot_keywords";
    public static final String MBQ_MESSAGE_TIP = "mbq_message_tip";
    public static final String MBQ_MESSAGE_PUSH = "mbq_message_push";

    public static final long CACHE_EXPIRE_OND_DAY = 3600000;//一天
    public static final String INTENT_ACTION_REFRESH_NUM = "com.witmoon.xmb.NUM";
    public static final String INTENT_ACTION_REFRESH_SERVICE_ORDER = "com.witmoon.xmb.refreshServiceOrder";
    public static final String INTENT_ACTION_REFRESH = "com.witmoon.xmb.REFRESH";
    public static final String INTENT_WEB_REFRESH = "com.witmoon.xmb.WEB_REFRESH";
    public static final String INTENT_ACTION_REFRESH_FEATURE = "com.witmoon.xmb.REFRESH_FEATURE";
    public static final String INTENT_ACTION_REFRESH_BRAND_GOOD = "com.witmoon.xmb.REFRESH_BRAND_GOOD";
    public static final String INTENT_ACTION_REFRESH_MY_MBQ = "com.witmoon.xmb.REFRESH_MY_MBQ";
    public static final String INTENT_ACTION_REFRESH_CIRCLE = "com.witmoon.xmb.REFRESH_MY_CIRCLE";
    public static final String INTENT_ACTION_REFRESH_POST = "com.witmoon.xmb.REFRESH_MY_POST";
    public static final String INTENT_ACTION_REFRESH_MY_MBQ_SEARCH = "com.witmoon.xmb.REFRESH_MY__SEARCH";
    public static final String INTENT_ACTION_TUI = "com.witmoon.xmb.YUI";
    public static final String INTENT_ACTION_LOGOUT = "com.witmoon.xmb.LOGOUT";
    public static final String INTENT_ACTION_LOGIN = "com.witmoon.xmb.LOGIN";
    public static final String INTENT_ACTION_REG_PUSH = "com.witmoon.xmb.REG_PUSH";
    public static final String INTENT_ACTION_BABY = "com.witmoon.xmb.baby";
    public static final String INTENT_ACTION_EXIT_APP = "com.witmoon.xbm.EXIT_APP";
    public static final String INTENT_ACTION_UPDATA_CAR = "com.witmoon.xbm.updata.CAR";
    public static final String INTENT_ACTION_MAIN_TAB = "com.witmoon.xmb.TAB_CHANGE";
    public static final String WX_CALLBACK = "com.witmoon.xmb.WX_CALLBACK";
    public static final String FINISH_CHILD_STATUS = "com.witmoon.xmb.FINISH_CHILD_STATUS";
    public static final String REFRESH_TOOLKIT = "com.witmoon.xmb.REFRESH_TOOLKIT";
    public static final String REFRESH_MENGBAO = "com.witmoon.xmb.REFRESH_MENGBAO";
    public static final String BIND_MABAO_CARD_SUCCESS = "com.witmoon.xmb.BIND_MABAO_CARD_SUCCESS";

    public static final String INTENT_ACTION_REFRESH_BEAN = "send_bean_refresh";

//    public static final String INTENT_ACTION_SERVICE_REFRESH = "service_refresh";


    public static final String BABY_SEX = "baby_sex";


    public static String mood_desc_text[] = {"开心", "窃喜", "萌笑", "害羞", "困了", "花痴", "加油", "委屈", "伤心", "棒棒"};
    public static int mood_img[] = {R.mipmap.mood0, R.mipmap.mood1, R.mipmap.mood2, R.mipmap.mood3, R.mipmap.mood4, R.mipmap.mood5, R.mipmap.mood6, R.mipmap.mood7, R.mipmap.mood8, R.mipmap.mood9};
    public static int mood_active_img[] = {R.mipmap.mood0_image, R.mipmap.mood1_image, R.mipmap.mood2_image, R.mipmap.mood3_image, R.mipmap.mood4_image, R.mipmap.mood5_image, R.mipmap.mood6_image,
            R.mipmap.mood7_image, R.mipmap.mood8_image, R.mipmap.mood9_image};
    public static String weather_desc_text[] = {"晴天", "冰雹", "多云", "雨水", "雷电", "多云转晴", "雾霾", "下雪"};
    public static int weather_img[] = {R.mipmap.mweather0, R.mipmap.mweather1, R.mipmap.mweather2, R.mipmap.mweather3, R.mipmap.mweather4, R.mipmap.mweather5, R.mipmap.mweather6, R.mipmap.mweather7};
    public static int weather_active_img[] = {R.mipmap.mweather0_image, R.mipmap.mweather1_image, R.mipmap.mweather2_image, R.mipmap.mweather3_image, R.mipmap.mweather4_image, R.mipmap.mweather5_image
            , R.mipmap.mweather6_image, R.mipmap.mweather7_image};
    public static int weather_icon_img[] = {R.mipmap.weather_icon1, R.mipmap.weather_icon2, R.mipmap.weather_icon3, R.mipmap.weather_icon4, R.mipmap.weather_icon5, R.mipmap.weather_icon6
            , R.mipmap.weather_icon7, R.mipmap.weather_icon8};
}
