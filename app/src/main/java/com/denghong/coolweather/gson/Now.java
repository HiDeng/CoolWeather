package com.denghong.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by denghong on 2017/9/23.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
