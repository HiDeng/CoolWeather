package com.denghong.coolweather.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.denghong.coolweather.module.City;
import com.denghong.coolweather.module.County;
import com.denghong.coolweather.module.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by denghong on 2017/9/19.
 */

public class CoolWeatherDB {

    /**
     * 数据库名
     */
    public static final String DB_NAME = "cool_weather";

    private static final String TABLE_PROVINCE = "Province";
    private static final String TABLE_CITY = "City";
    private static final String TABLE_COUNTY = "County";

    private static final String KEY_PROVINCE_NAME = "province_name";
    private static final String KEY_PROVINCE_CODE = "province_code";
    private static final String KEY_CITY_NAME = "city_name";
    private static final String KEY_CITY_CODE = "city_code";
    private static final String KEY_PROVINCE_ID = "province_id";
    private static final String KEY_COUNTY_NAME = "county_name";
    private static final String KEY_COUNTY_CODE = "county_code";
    private static final String KEY_CITY_ID = "city_id";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private CoolWeatherDB mCoolWeatherDB;

    private SQLiteDatabase mDB;

    /**
     * 将构造函数私有化
     * @param context
     */
    private CoolWeatherDB(Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION);
        // 获取到一个可写的数据库
        mDB = dbHelper.getWritableDatabase();
    }

    /**
     * 获取CoolWeather实例
     * @param context
     * @return
     */
    public synchronized CoolWeatherDB getInstance(Context context) {
        if (mCoolWeatherDB == null) {
            mCoolWeatherDB = new CoolWeatherDB(context);
        }
        return mCoolWeatherDB;
    }

    /**
     * 将Province实例保存到数据库中
     * @param province
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put(KEY_PROVINCE_NAME, province.getProvinceName());
            values.put(KEY_PROVINCE_CODE, province.getProvinceCode());
            mDB.insert(TABLE_PROVINCE, null, values);
        }
    }

    /**
     * 从数据库中读取全国所有省份
     * @return
     */
    public List<Province> loadProvince() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = mDB.query(TABLE_PROVINCE, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex(KEY_PROVINCE_NAME)));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex(KEY_PROVINCE_CODE)));
                list.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    // 将City实例保存到数据库中
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put(KEY_CITY_NAME, city.getCityName());
            values.put(KEY_CITY_CODE, city.getCityCode());
            values.put(KEY_PROVINCE_ID, city.getProvinceId());
            mDB.insert(TABLE_CITY, null, values);
        }
    }

    // 从数据库中读取某省下得所有城市信息
    public List<City> loadCity(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = mDB.query(TABLE_CITY, null, "province_id = ?",
                new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex(KEY_CITY_NAME)));
                city.setCityCode(cursor.getString(cursor.getColumnIndex(KEY_CITY_CODE)));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    // 将County实例保存到数据库中
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put(KEY_COUNTY_NAME, county.getCountyName());
            values.put(KEY_COUNTY_CODE, county.getCountyCode());
            values.put(KEY_CITY_ID, county.getCityId());
            mDB.insert(TABLE_COUNTY, null, values);
        }
    }

    // 从数据库中读取某市的所有县的信息
    public List<County> loadCounty(int cityId) {
        List<County> list = new ArrayList<County>();
        Cursor cursor = mDB.query(TABLE_COUNTY, null, "city_id = ?",
                new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex(KEY_COUNTY_NAME)));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex(KEY_COUNTY_CODE)));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
