package com.witmoon.xmb.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by de on 2016/1/20.
 */
public class TimeUtill {
     public static List<Map<String, String>> byeLit = new ArrayList<>();
    /**
     * 用来存储倒计时的值  单品团
     */
    public static List<Map<String, String>> list = new ArrayList<>();

    /**
     * 用来存储倒计时的值  品牌团
     */
    public static List<Map<String, String>> mlist = new ArrayList<>();
    //单品团
    public static String get(int position, String key) {
        return list.get(position).get(key);
    }

    public static void add(Map<String, String> map) {
        list.add(map);
    }

    public static void removeAll() {
        if (null != list)
            list.clear();
    }

    //    品牌团
    public static String mGet(int position, String key) {
        return mlist.get(position).get(key);
    }

    public static void mAdd(Map<String, String> map) {
        mlist.add(map);
    }

    public static void mRemoveAll() {
        if (null != mlist)
            mlist.clear();
    }

    //    第二个页面需要的值
    public static String byeGet(int position, String key) {
        return byeLit.get(position).get(key);
    }

    public static void byeAdd(Map<String, String> map) {
        byeLit.add(map);
    }

    public static void byeRemoveAll() {
        if (null != byeLit)
            byeLit.clear();
    }
}
