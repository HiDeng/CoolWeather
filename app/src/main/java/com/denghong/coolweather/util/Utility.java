package com.denghong.coolweather.util;

import android.text.TextUtils;

import com.denghong.coolweather.database.CoolWeatherDB;
import com.denghong.coolweather.module.City;
import com.denghong.coolweather.module.County;
import com.denghong.coolweather.module.Province;

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
}
