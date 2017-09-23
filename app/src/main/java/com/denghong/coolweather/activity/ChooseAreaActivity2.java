package com.denghong.coolweather.activity;

import android.app.Activity;
import android.os.Bundle;

import com.denghong.coolweather.R;
import com.denghong.coolweather.util.LogUtil;

import static com.denghong.coolweather.util.LogUtil.TAG;

/**
 * Created by denghong on 2017/9/23.
 */

public class ChooseAreaActivity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.d(TAG, "onCreate start");
        setContentView(R.layout.activity_main);
    }
}
