//package com.rschina.heilongjiang.model;
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.NetworkResponse;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//import com.chinars.mapapi.GeoPoint;
//import com.chinars.mapapi.utils.LogUtils;
//import com.chinars.todaychina.MyApplication;
//import com.chinars.todaychina.activity.LoginActivity;
//import com.chinars.todaychina.config.Const;
//import com.chinars.todaychina.config.HttpCode;
//import com.chinars.todaychina.db.UserSharedPrefs;
//import com.chinars.todaychina.model.Constants;
//import com.chinars.todaychina.util.BBoxPos;
//import com.chinars.todaychina.util.DebugUtil;
//import com.chinars.todaychina.util.MercatorUtil;
//import com.lidroid.xutils.HttpUtils;
//import com.lidroid.xutils.exception.HttpException;
//import com.lidroid.xutils.http.RequestParams;
//import com.lidroid.xutils.http.ResponseInfo;
//import com.lidroid.xutils.http.callback.RequestCallBack;
//import com.lidroid.xutils.http.client.HttpRequest;
//import com.lidroid.xutils.http.client.multipart.MIME;
//
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import cn.jpush.android.api.JPushInterface;
//import cn.jpush.android.api.TagAliasCallback;
//
///**
// * Created by Administrator on 2016/1/21.
// */
//public class DiscoveryServiceImpl implements DiscoveryService {
//
//    Context mContext;
//    RequestQueue mQueue;
//    String host;
//    CustomResponseListener defaultListener;
//    long currentTime;//当前时间,以秒为单位，5秒钟更新一次
//
//    String TGT;
//    Map<String, String> tickets = new HashMap<>();
//    String XUserAgent;
//    String sessionId;
//    Map<String, String> headers = new HashMap<>();
//    boolean isLogin;
//    private int oldRow = 0;
//    private int oldCol = 0;
//    public static DiscoveryServiceImpl instance;
//
//
//    private DiscoveryServiceImpl(Context context) {
//        mContext = context;
//        mQueue = Volley.newRequestQueue(mContext);
//        defaultListener = new CustomResponseListener();
//        host = Constants.API_SERVER_HOST;
//        userSharedPrefs = new UserSharedPrefs(mContext);
//        XUserAgent = ((MyApplication) mContext.getApplicationContext()).get("XUserAgent");
//        headers.put("XUserAgent", XUserAgent);
//        TGT = userSharedPrefs.getTGT();
//        checkLogin();
//        instance = this;
//    }
//
//    public static DiscoveryServiceImpl getInstance() {
//        if (instance == null) {
//            return new DiscoveryServiceImpl(MyApplication.getInstance());
//        }
//        return instance;
//    }
//
//    @Override
//    public void getLastTopics(CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void getMapMessage(CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void getTenTopicsBefore(long time, CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void getLastComments(String topicId, CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void getCommentsBefore(String topicId, long time, CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void addComment(String topicId, String content, CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void setLike(String topicId, boolean isLike, CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void modifyNickname(String nickName, CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void modifySex(int sex, CustomResponseListener listener) {
//
//    }
//
//
//    @Override
//    public void modifyAvatar(final File bitmapFile,final CustomResponseListener listener) {
//        final  String apiName=MODIFY_AVATAR;
//        String url=host+apiName;
//        if(isLogin()){
//            String ticket=tickets.get(MODIFY_AVATAR);
//            if(ticket!=null){
//                HttpUtils httpUtils=new HttpUtils();
//                RequestParams params=new RequestParams();
//                params.addHeader("XUserAgent",XUserAgent);
//                params.addQueryStringParameter("ticket", ticket);
//                LogUtils.d(ticket);
//                LogUtils.d(url);
//                params.addBodyParameter("upload",bitmapFile);
//                httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
//                    @Override
//                    public void onSuccess(ResponseInfo<String> responseInfo) {
//                        try {
//                            LogUtils.d(responseInfo.result);
//                            listener.onResponse(new JSONObject(responseInfo.result));
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                    }
//                    @Override
//                    public void onFailure(HttpException e, String s) {
//                        e.printStackTrace();
//                        LogUtils.d(e.getMessage() + " " + s + " code:" + e.getExceptionCode());
//                        listener.onErrorResponse(new VolleyError(s));
//                    }
//                });
//
//            }else{
//                getST(MODIFY_AVATAR, new Runnable() {
//                    @Override
//                    public void run() {
//                        modifyAvatar(bitmapFile,listener);
//                    }
//                });
//                return;
//            }
//        }else{
//            startLoginActivity();
//        }
//    }
//
//    @Override
//    public boolean getTimeAndRes(int zoom, GeoPoint center, CustomResponseListener listener) {
//        return false;
//    }
//
//    @Override
//    public void getFiveWeatherTime(String type, CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void getAqiOfPoint(GeoPoint point, int zoom, int sIndex, CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void addSubscription(String name, GeoPoint center, int zoom, double spanX, double spanY, CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void deleteSubscription(String id, CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void listSubscription(CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void getSubscriptionDetail(String id, CustomResponseListener listener) {
//
//    }
//
//    @Override
//    public void markSubscription(String id, CustomResponseListener listener) {
//
//    }
//}
//
//
//
//
