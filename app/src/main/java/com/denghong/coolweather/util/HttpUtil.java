package com.denghong.coolweather.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by denghong on 2017/9/19.
 */

public class HttpUtil {

    private static final int READ_TIMEOUT = 5 * 1000;
    private static final int CONN_TIMEOUT = 8 * 1000;

    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(address);
                    conn = (HttpURLConnection) url.openConnection();
                    // 连接参数设置
                    conn.setRequestMethod("GET");
                    conn.setReadTimeout(READ_TIMEOUT);
                    conn.setConnectTimeout(CONN_TIMEOUT);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = null;
                    StringBuilder response = new StringBuilder();
                    if ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        // 回调onFinish方法将从服务器读取到的数据传出去
                        listener.onFinish(response.toString());
                    }

                } catch (Exception e) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                    Log.e("CoolWeather", e.toString());
                } finally {
                    conn.disconnect();
                }
            }
        }).start();
    }
}
