package com.denghong.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.denghong.coolweather.R;
import com.denghong.coolweather.database.CoolWeatherDB;
import com.denghong.coolweather.module.City;
import com.denghong.coolweather.module.County;
import com.denghong.coolweather.module.Province;
import com.denghong.coolweather.util.HttpCallbackListener;
import com.denghong.coolweather.util.HttpUtil;
import com.denghong.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by denghong on 2017/9/20.
 */

public class ChooseAreaActivity extends Activity {

    public static final int LEVEL_PROViNCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog mProgressDialog;
    private TextView mTitleText;
    private ListView mListView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<String>();
    private CoolWeatherDB mCoolWeatherDB;

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this); //TODO
        if (prefs.getBoolean(Utility.KEY_CITYSELECTED, false)) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.choose_area);

        mListView = (ListView) findViewById(R.id.list_view);
        mTitleText = (TextView) findViewById(R.id.title_text);

        adapter = new ArrayAdapter<String>(ChooseAreaActivity.this, android.R.layout.simple_list_item_1, dataList);
        mListView.setAdapter(adapter);
        mCoolWeatherDB = CoolWeatherDB.getInstance(ChooseAreaActivity.this);

        // 根据点击的级别不同，加载不同的点击数据
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_PROViNCE) {
                    selectedProvince = provinceList.get(i);
                    queryCities(); // 加载市级数据
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(i);
                    queryCounties(); // 加载县级数据
                } else if (currentLevel == LEVEL_COUNTY) {
                    String countyCode = countyList.get(i).getCountyCode();
                    Intent intent = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);
//                    finish();
                }
            }
        });
        queryProvinces(); //加载省级数据
    }

    /**
     * 查询全国所有的省份，优先从数据库查询，如果没有查询到，就从服务器上去查询
     */
    private void queryProvinces() {
        provinceList = mCoolWeatherDB.loadProvinces(); // 从数据库中查询全国所有省份
        if (provinceList.size() > 0) {
            dataList.clear(); //TODO
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged(); // TODO
            mListView.setSelection(0);
            mTitleText.setText("中国");
            currentLevel = LEVEL_PROViNCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    // 查询选中的省内所有的城市，优先从数据库查询，如果没有查询到，就从服务器上去查询
    private void queryCities() {
        cityList = mCoolWeatherDB.loadCities(selectedProvince.getId()); // 从
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询选中市内所有的县，优先从数据库查询，如果没有查询到，就从服务器去查询
     */
    private void queryCounties() {
        countyList = mCoolWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mTitleText.setText(selectedCity.getCityName());
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }
    }

    /**
     * 根据传入的代号和类型，从服务器上查询省市县的数据
     * @param code
     * @param type
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(mCoolWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(mCoolWeatherDB, response, selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiedResponse(mCoolWeatherDB, response, selectedCity.getId());
                }

                if (result) {
                    // 通过runOnUiThreas()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() { //TODO
                        @Override
                        public void run() {
                            closeProgressDialog();

                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(ChooseAreaActivity.this);
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else {
            finish();
        }
    }
}
