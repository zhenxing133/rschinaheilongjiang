package com.rschina.heilongjiang.db;

import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.rschina.heilongjiang.MyApplication;
import com.rschina.heilongjiang.model.BaseId;
import com.rschina.heilongjiang.model.BaseIdDes;
import com.rschina.heilongjiang.model.Const;
import com.rschina.heilongjiang.model.Event;
import com.rschina.heilongjiang.model.MapInfo;
import com.rschina.heilongjiang.model.MonitorInfo;
import com.rschina.heilongjiang.model.TypeInfo;
import com.rschina.heilongjiang.utils.CommonRequest;
import com.rschina.heilongjiang.utils.GsonUtils;
import com.rschina.heilongjiang.utils.UserSharedPrefs;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2016/11/7.
 * email :
 * usages :
 */

public class RsService {
    public final static long ONE_YEAR = (long) (1000 * 3600 * 24 * 365.242199);
    private static RsService instance;
    private String host;
    private CommonRequest commonRequest;
    private List<MonitorInfo> monitorInfos;//监测
    private List<MonitorInfo> monitorIds;//监测id分类
    private List<MapInfo> imageMaps;//影像
    private List<MapInfo> imageMap;//影像
    private List<TypeInfo> typeInfos;//分类

    private Set<Integer> infoYears;
    private Set<Integer> imageYears;
    private Set<String> infoTypes;
    private Set<String> allRes;
    boolean requesting = false;
    private long lastUpdateTime = 0;
    Comparator<String> comparator;
    Comparator<String> resComparator;
    Calendar calendar = Calendar.getInstance();
    private List<String> monitors = new ArrayList<>();
    private List<String> pclIds = new ArrayList<>();
    private List<String> baseIds = new ArrayList<>();
    private List<String> intIds;
    private List<String> desList;
    private BaseId baseId;
    private List<BaseIdDes> baseIdDess;
    private List<List<BaseIdDes>> baseIdDesList;
    private List<List<String>> baseIdDes;
    private List<List<String>> listid;
    public String clsid;
    public boolean isread = false;
    private RsService() {
        //host = Const.RELEASE_SERVER;
        commonRequest = CommonRequest.getInstance();
        monitorInfos = new ArrayList<>();
        monitorIds = new ArrayList<>();
        baseIdDesList = new ArrayList<>();
        baseIdDes = new ArrayList<>();
        listid = new ArrayList<>();
        imageMaps = new ArrayList<>();
        imageMap = new ArrayList<>();
        typeInfos = new ArrayList<>();

        infoYears = new HashSet<>();
        imageYears = new HashSet<>();
        infoTypes = new HashSet<>();
        allRes = new HashSet<>();
        comparator = new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return rhs.compareTo(lhs);
            }
        };
        resComparator = new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                float lv = Float.valueOf(lhs.split("m")[0]);
                float rv = Float.valueOf(rhs.split("m")[0]);
                return lv - rv > 0 ? 1 : -1;
            }
        };
    }

    public static RsService getInstance() {
        if (instance == null) {
            instance = new RsService();
            instance.fetchImage();
        }
        return instance;
    }

    /**
     * 卫星影像
     */
    public void fetchImage() {
        monitorInfos.clear();
        monitorIds.clear();
        baseIdDesList.clear();
        baseIdDes.clear();
        listid.clear();
        imageMaps.clear();
        imageMap.clear();
        typeInfos.clear();
        infoYears.clear();
        imageYears.clear();
        infoTypes.clear();
        allRes.clear();
        //Log.e("yzx", "请求----");
        final List<String> modelIds = new ArrayList<>();
        requesting = true;
        commonRequest.requestJson(Const.FORMAL_IMAGEMAP, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                imageMaps.clear();

                //Log.e("yzx-影像", response.toString());
                try {
                    if (Integer.parseInt(response.getString("code")) == 1) {
                        JSONArray dataArray = response.getJSONArray("dataInfoList");
                        for (int i = 0; i < dataArray.length(); i++) {
                            MapInfo mapInfo = new MapInfo();
                            mapInfo.fromIdJson(dataArray.getJSONObject(i));
                            modelIds.add(mapInfo.model_data_id);

                        }
                        JSONObject mosicObj = response.getJSONObject("mapServiceInfoList");
                        for (int i = 0; i < modelIds.size(); i++) {
                            JSONObject mosicJSONObject = mosicObj.getJSONObject(modelIds.get(i));
                            MapInfo mapInfo = new MapInfo();
                            mapInfo.fromJson(mosicJSONObject);
                            imageMaps.add(mapInfo);
                        }

                        fetchInfoType();
                        EventBus.getDefault().post(Event.Finish);

                    } else {
                        Toast.makeText(MyApplication.getInstance(), "出错：" + response.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                requesting = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requesting = false;
            }
        });
    }
    /**
     * 卫星影像
     */
    public void fetch() {
        imageMap.clear();
        //Log.e("yzx", "请求----");
        final List<String> modelIds = new ArrayList<>();
        requesting = true;
        commonRequest.requestJson(Const.FORMAL_IMAGEMAP, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                //Log.e("yzx-影像", response.toString());
                try {
                    if (Integer.parseInt(response.getString("code")) == 1) {
                        JSONArray dataArray = response.getJSONArray("dataInfoList");
                        for (int i = 0; i < dataArray.length(); i++) {
                            MapInfo mapInfo = new MapInfo();
                            mapInfo.fromIdJson(dataArray.getJSONObject(i));
                            modelIds.add(mapInfo.model_data_id);

                        }
                        JSONObject mosicObj = response.getJSONObject("mapServiceInfoList");
                        for (int i = 0; i < modelIds.size(); i++) {
                            JSONObject mosicJSONObject = mosicObj.getJSONObject(modelIds.get(i));
                            MapInfo mapInfo = new MapInfo();
                            mapInfo.fromJson(mosicJSONObject);
                            imageMap.add(mapInfo);
                        }


                        EventBus.getDefault().post(Event.Finish);

                    } else {
                        Toast.makeText(MyApplication.getInstance(), "出错：" + response.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                requesting = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                requesting = false;
            }
        });
    }

    /**
     * 监控分类
     */
    public void fetchInfoType() {
        requesting = true;
        commonRequest.requestJson(Const.FORMAT_TYPE, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                //Log.e("yzx", "分类："+response.toString());
                try {
                    if (Integer.parseInt(response.getString("code")) == 1) {
                        typeInfos.clear();
                        JSONArray dataArray = response.getJSONArray("list");
                        for (int i = 0; i < dataArray.length(); i++) {
                            TypeInfo typeInfo = new TypeInfo();
                            typeInfo.fromJson(dataArray.getJSONObject(i));
                            typeInfos.add(typeInfo);
                        }
                        fetchInfoTypeData();
                    } else {
                        Toast.makeText(MyApplication.getInstance(), "出错：" + response.getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                requesting = false;
            }
        });
    }

    /**
     * 根据分类id解析数据
     */
    public void fetchInfoTypeData() {
        requesting = true;
        //monitors.clear();
        monitorInfos.clear();
        pclIds.clear();
        listid.clear();
        baseIdDes.clear();
     //   baseIdDess.clear();
        for (int n = 0; n < typeInfos.size(); n++) {
            /**
             * 根据分类dataclsid解析对应数据
             *
             */
            String url = Const.FORMAL_HOST+"getMonitorDataInfoList?admincode=230000&keyword=&dataClsId=" + typeInfos.get(n).dataclsid + "&getAttrsTag=1";
            //String url = "http://192.168.101.203:8189/getMonitorDataInfoList?admincode=230000&keyword=&dataClsId="+typeInfos.get(n).dataclsid+"&getAttrsTag=1";
            commonRequest.requestJson(url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (Integer.parseInt(response.getString("code")) == 1) {
                            monitors.clear();


                            JSONArray datainfos = response.getJSONArray("dataInfoList");
                            for (int o = 0; o < datainfos.length(); o++) {
                                MonitorInfo monitorId = new MonitorInfo();
                                monitorId.fromIdJson(datainfos.getJSONObject(o));
                                monitors.add(monitorId.model_data_id);
                                pclIds.add(monitorId.pclsid);
                                baseIds.add(monitorId.relationstr);
                                JSONObject mosicObject = response.getJSONObject("mapServiceInfoList");
                                JSONObject mosicJSONObject = mosicObject.getJSONObject(monitorId.model_data_id);
                                MonitorInfo monitorInfo = new MonitorInfo();
                                monitorInfo.fromJson(mosicJSONObject);
                                monitorInfos.add(monitorInfo);

                            }
                            JSONObject basemap = response.getJSONObject("mapServiceInfoList");
                            for (int z = 0; z < baseIds.size(); z++) {
                                baseId = GsonUtils.java2Bean(baseIds.get(z), BaseId.class);
                                intIds = new ArrayList<>();
                                desList = new ArrayList<>();
                                baseIdDess = new ArrayList<>();
                                for (int p = 0; p < baseId.getBasemapinfos().size(); p++) {
                                    JSONObject jsonObject = basemap.getJSONObject(baseId.getBasemapinfos().get(p).getMapinfoid());
                                    MonitorInfo monitorInfo = new MonitorInfo();
                                    monitorInfo.fromJson(jsonObject);
                                    intIds.add(baseId.getBasemapinfos().get(p).getMapinfoid());
                                    //Log.e("yzx", baseId.getBasemapinfos().get(p).getMapinfoid()+"----");
                                    monitorIds.add(monitorInfo);

                                    BaseIdDes des = new BaseIdDes();
                                    des.fromJson(jsonObject);
                                    baseIdDess.add(des);
                                    desList.add(des.name);
                                }
                                listid.add(intIds);
                                baseIdDes.add(desList);
                                baseIdDesList.add(baseIdDess);
                            }

                            Log.e("yzx", listid.size() + "listid");
                            Log.e("yzx", baseIdDess.size() + "baseiddess");
                            //Log.e("yzx", baseIdDes.size() + "baseiddes");
                            baseIds.clear();
                            EventBus.getDefault().post(new Event.Action(1001));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    requesting = false;
                }
            });
        }
        requesting = false;


    }



    //获取影像
    public List<MapInfo> getImageMaps() {
        if (imageMaps.isEmpty() && !requesting) {
            fetchImage();
        }
        return imageMaps;
    }
    //获取影像
    public List<MapInfo> getImageMap() {
        if (imageMap.isEmpty() && !requesting) {
            fetch();
        }
        return imageMap;
    }

    //获取监测应用
    public List<MonitorInfo> getMonitorInfos() {
        if (monitorInfos.isEmpty() && !requesting) {
            fetchInfoTypeData();
        }
        return monitorInfos;
    }

    //获取监测应用分类id
    public List<String> getMonitorsId() {
        if (pclIds.isEmpty() && !requesting) {
            fetchInfoTypeData();
        }
        return pclIds;
    }


    //获取basemapid
    public List<List<String>> getBaseMapId() {
        if (listid.isEmpty() && !requesting) {
            fetchInfoTypeData();
        }
        return listid;
    }

    //获取basemapid对应数据
    public List<BaseIdDes> getBaseIdDes() {
        if (baseIdDess.isEmpty() && !requesting) {
            fetchInfoTypeData();
        }
        return baseIdDess;
    }

    //获取basemapid对应数据
    public List<List<String>> getBaseIdDess() {
        if (baseIdDes.isEmpty() && !requesting) {
            fetchInfoTypeData();
        }
        return baseIdDes;
    }
    //获取basemapid对应数据
    public List<List<BaseIdDes>> getBaseIdDesList() {
        if (baseIdDesList.isEmpty() && !requesting) {
            fetchInfoTypeData();
        }
        return baseIdDesList;
    }


    /**
     * 获取所有有数据的年份
     *
     * @param isImageMap 是底图还是专题图
     * @return
     */
    public List<String> getAllYears(boolean isImageMap) {
        List<String> ret = new ArrayList<>();
        if (isImageMap) {
            for (Integer year : imageYears) {
                ret.add(year + "");
            }
        } else {
            for (Integer year : infoYears) {
                ret.add(year + "");
            }
        }
        Collections.sort(ret, comparator);
        ret.add(0, "所有年份");
        return ret;
    }

    /**
     * 获取所有专题类型
     *
     * @return
     */
    public List<String> getAllInfoTypes() {
        List<String> ret = new ArrayList<>();
        ret.add("所有产品类型");
        for (String s : infoTypes) {
            ret.add(s);
        }
        return ret;
    }

    /**
     * 获取所有分辨率
     *
     * @return
     */
    public List<String> getAllRes() {
        List<String> ret = new ArrayList<>();
        ret.addAll(allRes);
        Collections.sort(ret, resComparator);
        ret.add(0, "所有分辨率");
        return ret;
    }

    /**
     * 影像产品年份排序
     */
    public List<MapInfo> filterImageMapByRes(String res, List<String> years) {
        List<MapInfo> result = new ArrayList<>();
        Set<Integer> iy = new HashSet<>();
        for (MapInfo mapInfo : imageMaps) {
            result.add(mapInfo);
            iy.add(mapInfo.startYear);
            iy.add(mapInfo.endYear);
        }
        for (Integer i : iy) {
            years.add(i + "");
        }
        Collections.sort(years, comparator);
        years.add(0, "所有年份");
        return result;
    }

    public static List<String> removeDuplicate(List list) {
        HashSet h = new HashSet(list);
        list.clear();
        list.addAll(h);
        return list;

    }


}
