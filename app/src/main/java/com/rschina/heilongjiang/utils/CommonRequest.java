package com.rschina.heilongjiang.utils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rschina.heilongjiang.MyApplication;

import org.json.JSONObject;

/**
 * Created by Administrator on 2016/8/26.
 */
public class CommonRequest {
    private static CommonRequest ourInstance = new CommonRequest();
    RequestQueue mQueue;
    Response.ErrorListener errorListener;

    public static CommonRequest getInstance() {
        return ourInstance;
    }

    private CommonRequest() {
        mQueue= Volley.newRequestQueue(MyApplication.getInstance());
        errorListener= new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error:"+" . reason:"+error.getMessage() + "  "+ error.networkResponse);
            }
        };
    }

    public void  requestString(String url, Response.Listener<String> listener){
        StringRequest stringRequest=new StringRequest(url, listener,errorListener);
        mQueue.add(stringRequest);
    }

    public  void requestJson(String url, Response.Listener<JSONObject> listener){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(url,listener,errorListener);
        mQueue.add(jsonObjectRequest);
    }

    public  void requestJson(String url, Response.Listener<JSONObject> listener,Response.ErrorListener errorListener){
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(url,listener,errorListener);
        mQueue.add(jsonObjectRequest);
    }

    public  void addRequest(Request request){
        mQueue.add(request);
    }

    public  void  setErrorListener(Response.ErrorListener errorListener){
        this.errorListener=errorListener;
    }
}
