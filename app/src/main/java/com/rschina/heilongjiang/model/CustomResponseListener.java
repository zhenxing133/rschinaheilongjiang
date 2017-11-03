package com.rschina.heilongjiang.model;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.chinars.mapapi.utils.LogUtils;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/1/27.
 */
public class CustomResponseListener implements Response.Listener<JSONObject>,Response.ErrorListener{
    @Override
    public void onResponse(JSONObject response) {
        LogUtils.d(response.toString());
    }
    @Override
    public void onErrorResponse(VolleyError error) {
        if(error.networkResponse==null){
            if(error.getNetworkTimeMs()>=5000){
                LogUtils.d("网络超时");
            }else{
                LogUtils.d("no network connect");
            }
        }else{
            int code=error.networkResponse.statusCode;

        }
    }
}
