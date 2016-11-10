package com.witmoon.xmb.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.witmoon.xmb.model.City;
import com.witmoon.xmb.model.Country;
import com.witmoon.xmb.model.Province;
import com.witmoon.xmb.model.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作工具类
 * Created by zhyh on 2015/1/22.
 */
public class XmbDB {
    // 数据库名称
    public static final String DB_NAME = "city.db";
    // 数据库版本
    public static final int DB_VERSION = 1;

    private static XmbDB instance;

    private String dbFilePath;
    private SQLiteDatabase db;

    private XmbDB(Context context, String dbFilePath) {
        if (TextUtils.isEmpty(dbFilePath)) {
            XmbOpenHelper dbHelper = new XmbOpenHelper(context, DB_NAME, null, DB_VERSION);
            db = dbHelper.getWritableDatabase();
        } else {
            db = context.openOrCreateDatabase(dbFilePath, Context.MODE_PRIVATE, null);
        }
    }

    public static XmbDB getInstance(Context context, String dbFilePath) {
        if (instance == null || (dbFilePath != null && !dbFilePath.equals(instance.dbFilePath))) {
            instance = new XmbDB(context, dbFilePath);
            instance.dbFilePath = dbFilePath;
        }
        return instance;
    }

    public void clear() {
        db.execSQL("delete from province;");
        db.execSQL("delete from city;");
        db.execSQL("delete from country;");
        db.execSQL("delete from region;");
        db.execSQL("delete from search;");
    }

    /**
     * 将Province保存到数据库
     *
     * @param province 要保存的Province实体
     */
    public void saveProvince(Province province) {
        if (null != province) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getName());
            values.put("province_code", province.getCode());
            db.insert("province", null, values);
        }
    }

    /**
     * 加载省级行政单位
     *
     * @return 所有省级行政单位
     */
    public List<Province> loadProvinces() {
        List<Province> provinceList = new ArrayList<Province>();

        Cursor cursor = db.query("province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setCode(cursor.getString(cursor.getColumnIndex("province_code")));
                provinceList.add(province);
            } while (cursor.moveToNext());
        }

        return provinceList;
    }

    /**
     * 保存市级行政单位
     *
     * @param city City实体
     */
    public void saveCity(City city) {
        if (null != city) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getName());
            values.put("city_code", city.getCode());
            values.put("province_code", city.getProvinceCode());
            db.insert("city", null, values);
        }
    }

    /**
     * 加载指定省级行政单位下的所有市级行政单位
     *
     * @return 给定省级行政单位下的所有市级行政单位
     */
    public List<City> loadCities(String provinceCode) {
        List<City> cityList = new ArrayList<City>();

        Cursor cursor = db.query("city", null, "province_code=?", new String[]{provinceCode},
                null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceCode(provinceCode);
                cityList.add(city);
            } while (cursor.moveToNext());
        }

        return cityList;
    }

    /**
     * 保存县级行政单位
     *
     * @param country Country实体
     */
    public void saveCountry(Country country) {
        if (null != country) {
            ContentValues values = new ContentValues();
            values.put("country_name", country.getName());
            values.put("country_code", country.getCode());
            values.put("city_code", country.getCityCode());
            db.insert("country", null, values);
        }
    }

    /**
     * 加载指定市级行政单位下的所有县级行政单位
     *
     * @return 给定市级行政单位下的所有县级行政单位
     */
    public List<Country> loadCountries(String cityCode) {
        List<Country> countryList = new ArrayList<Country>();

        Cursor cursor = db.query("country", null, "city_code=?", new String[]{cityCode}, null,
                null, null);
        if (cursor.moveToFirst()) {
            do {
                Country country = new Country();
                country.setName(cursor.getString(cursor.getColumnIndex("country_name")));
                country.setCode(cursor.getString(cursor.getColumnIndex("country_code")));
                country.setCityCode(cityCode);
                countryList.add(country);
            } while (cursor.moveToNext());
        }

        return countryList;
    }

    public City loadCity(String cityName) {
        Cursor cursor = db.query("city", null, "city_name=?", new String[]{cityName}, null, null,
                null);
        if (cursor.moveToFirst()) {
            City city = new City();
            city.setName(cursor.getString(cursor.getColumnIndex("city_name")));
            city.setCode(cursor.getString(cursor.getColumnIndex("city_code")));
            city.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
            return city;
        }
        return null;
    }

    public List<Region> loadRegions(String pid) {
        List<Region> regions = new ArrayList<>();

        Cursor cursor = db.query("region", null, "parent_id=?", new String[]{pid}, null,
                null, null);

        if (cursor.moveToFirst()) {
            do {
                Region region = new Region();

                region.setParentId(pid);
                region.setName(cursor.getString(cursor.getColumnIndex("name")));
                region.setId(cursor.getString(cursor.getColumnIndex("id")));
                region.setType(cursor.getString(cursor.getColumnIndex("type")));

                regions.add(region);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return regions;
    }

    public List<Region> loadRegions() {
        List<Region> regions = new ArrayList<>();

        Cursor cursor = db.query("region", null, null, null, null,
                null, null);

        if (cursor.moveToFirst()) {
            do {
                Region region = new Region();
                region.setName(cursor.getString(cursor.getColumnIndex("name")));
                region.setId(cursor.getString(cursor.getColumnIndex("id")));
                region.setType(cursor.getString(cursor.getColumnIndex("type")));
                region.setParentId(cursor.getString(cursor.getColumnIndex("parent_id")));
                regions.add(region);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return regions;
    }

    public String loadCao(String pid) {
        String ms = null;
        Cursor cursor = db.query("region", null, "id=?", new String[]{pid}, null,
                null, null);
        if (cursor.moveToFirst()) {
            do {
                ms = cursor.getString(cursor.getColumnIndex("name"));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return ms;
    }

    public void search_insret(String name)
    {
        name = name.trim();
        if (null!=name)
        {
            //保持唯一 0-0
            Cursor cursor = db.rawQuery("select * from search where search_name = ?", new String[]{name});
            if (cursor.getCount()<=0)
            {
                ContentValues values = new ContentValues();
                values.put("search_name", name);
                db.insert("search", null,values);
            }
        }
    }

    //查询所有搜索记录
    public List<String> search_name(){
        List<String> search = new ArrayList<String>();
        Cursor cursor =  db.query("search", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                search.add(cursor.getString(cursor.getColumnIndex("search_name")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return search;
    }
    //查询所有搜索记录
    public void  search_delete(){
        List<String> mList =  search_name();
        for (int i =0;i<mList.size();i++)
        {
            db.delete("search","search_name = ?",new String[]{mList.get(i).toString()});
        }
    }

    /**
     * 判断某张表是否存在
     * @param tabName 表名
     * @return
     */
    public boolean tabIsExist(String tabName){
        boolean result = false;
        if(tabName == null){
            return false;
        }
        Cursor cursor = null;
        try {
            Log.e("输出当前数据库的版本号",db.getVersion()+"");
            String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tabName.trim()+"' ";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
        }
        return result;
    }
    public void addTable()
    {
        // search表建表语句
        String CREATE_SEARCH = "create table search (id text primary key, " +
                "search_name text)";
        db.execSQL(CREATE_SEARCH);
    }
}
