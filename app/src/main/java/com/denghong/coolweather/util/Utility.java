package com.denghong.coolweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.denghong.coolweather.database.CoolWeatherDB;
import com.denghong.coolweather.module.City;
import com.denghong.coolweather.module.County;
import com.denghong.coolweather.module.Province;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by denghong on 2017/9/19.
 */

public class Utility {

    /**
     * 解析并处理服务器返回的省级数据
     * @param coolWeatherDB
     * @param response
     * @return
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] arr = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(arr[0]);
                    province.setProvinceName(arr[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析并处理服务器返回的市级数据
     * @param coolWeatherDB
     * @param response
     * @param provinceId
     * @return
     */
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] arr = c.split("\\|");
                    City city = new City();
                    city.setCityCode(arr[0]);
                    city.setCityName(arr[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    // 解析并处理服务器返回的县级数据
    public synchronized static boolean handleCountiedResponse(CoolWeatherDB coolWeatherDB, String response, int cityId) {
        if(!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] arr = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(arr[0]);
                    county.setCountyName(arr[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON数据，并将解析出来的数据保存到本地，返回的数据格式：
     * {"weatherinfo":
     *     {"city":"昆山","cityid":"101190404","temp1":"21℃","temp2":"9℃","weather":"多转小雨","img1":"d1.gif","img2":"n7.gif","ptime":"11:00"}
     * }
     * @param context
     * @param response
     */
    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            // 将解析出的数据保存到本地文件中
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String KEY_CITYSELECTED = "city_selected";
    public static final String KEY_CITYNAME = "city_name";
    public static final String KEY_WEATHERCODE = "weather_code";
    public static final String KEY_TEMP1 = "temp1";
    public static final String KEY_TEMP2 = "temp2";
    public static final String KEY_WEATHERDESP = "weather_desp";
    public static final String KEY_PUBLISHTIME = "publish_time";
    public static final String KEY_CURRENTDATE = "current_date";

    /**
     * 将服务器返回的天气信息存储到SharedPreferences文件中
     * @param context
     * @param cityName
     * @param weatherCode
     * @param temp1
     * @param temp2
     * @param weatherDesp
     * @param publishTime
     */
    public static void saveWeatherInfo(Context context, String cityName,
                                       String weatherCode, String temp1, String temp2, String weatherDesp, String publishTime) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        editor.putBoolean(KEY_CITYSELECTED, true);
        editor.putString(KEY_CITYNAME, cityName);
        editor.putString(KEY_WEATHERCODE, weatherCode);
        editor.putString(KEY_TEMP1, temp1);
        editor.putString(KEY_TEMP2, temp2);
        editor.putString(KEY_WEATHERDESP, weatherDesp);
        editor.putString(KEY_PUBLISHTIME, publishTime);
        editor.putString(KEY_CURRENTDATE, sdf.format(new Date()));
        editor.commit();
    }
}
