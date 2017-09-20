package com.denghong.coolweather.util;

/**
 * Created by denghong on 2017/9/19.
 */

public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
