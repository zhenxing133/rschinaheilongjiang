package com.rschina.heilongjiang.utils;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/9/19.
 */

public class GsonUtils {
    /**
     * @author yuanzhenxing
     */


    /**
     * 解析json数据
     *
     * @param <T>
     */
    public static <T> T java2Bean(String json, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(json, clazz);
    }

}
