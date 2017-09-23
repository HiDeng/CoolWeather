package com.denghong.coolweather.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by denghong on 2017/9/19.
 */

public class HttpUtil {

    private static final int READ_TIMEOUT = 5 * 1000;
    private static final int CONN_TIMEOUT = 8 * 1000;

    /**
     * 通过HTTPURLConnection向服务器发送请求数据
     * @param address
     * @param listener
     */
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

    /**
     * 通过okHttp发送请求数据
     * @param address
     * @param callback
     */
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback); // 异步方法，execute是同步方法
    }
}
