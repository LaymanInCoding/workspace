package com.witmoon.xmb.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.witmoon.xmb.AppContext;

/**
 * 数据库操作帮助类
 * Created by zhyh on 2015/1/22.
 */
public class XmbOpenHelper extends SQLiteOpenHelper {
    // Province表建表语句
    public static final String CREATE_PROVINCE = "create table province (province_code text " +
            "primary key, province_name text)";
    // city表建表语句
    public static final String CREATE_CITY = "create table city (city_code text primary key, " +
            "city_name text, province_code text)";
    // country表建表语句
    public static final String CREATE_COUNTRY = "create table country (country_code text primary " +
            "key, country_name text, city_code text)";
    // region表建表语句
    public static final String CREATE_REGION = "create table region (id text primary key, " +
            "name text, type text, agency_id text, parent_id text)";

    public XmbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                         int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTRY);
        db.execSQL(CREATE_REGION);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("SQLiteDatabase_UP", "数据库升级！");
    }
}
