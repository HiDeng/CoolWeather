package com.denghong.coolweather.util;

/**
 * Created by denghong on 2017/9/19.
 */

public interface HttpCallbackListener {
    void onFinish(String reponse);
    void onError(Exception e);
}
