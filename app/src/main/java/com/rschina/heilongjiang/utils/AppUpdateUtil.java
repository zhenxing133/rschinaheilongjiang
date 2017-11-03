package com.rschina.heilongjiang.utils;

import android.util.Log;

import com.android.volley.Response;
import com.rschina.heilongjiang.BuildConfig;
import com.rschina.heilongjiang.MyApplication;
import com.rschina.heilongjiang.model.Const;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/29.
 */
public class AppUpdateUtil {
    private static final String TAG = "AppUpdateUtil";
    private String Check_URL="http://oe97palbo.bkt.clouddn.com/update.json?="+System.currentTimeMillis()/10000;
    private boolean hasUpdate;
    private int versionCode;
    private String versionName;
    private String updateTime;
    private String downloadUrl;
    private String updateLog;
    private  long lastCheckTime=0;


     public  void checkUpdate(final Runnable onFinish){
         if(System.currentTimeMillis()-lastCheckTime>1000*3600){
             CommonRequest commonRequest=CommonRequest.getInstance();
             commonRequest.requestJson(Check_URL, new Response.Listener<JSONObject>() {
                 @Override
                 public void onResponse(JSONObject response) {
                     Log.d(TAG, "onResponse: "+response);
                     try {
                         versionCode=response.getInt("version_code");
                         versionName=response.getString("version_name");
                         downloadUrl=response.getString("download_url");
                         updateLog=response.getString("update_log");
                         updateTime=response.getString("update_time");
                         if(versionCode> MyApplication.getInstance().getCurVersionCode()){
                             hasUpdate=true;
                         }else {
                             hasUpdate=false;
                         }
                         onFinish.run();
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }
                 }
             });
         }else {
             onFinish.run();
         }
     }

    public boolean isHasUpdate() {
        return hasUpdate;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getUpdateLog() {
        return updateLog;
    }
}
