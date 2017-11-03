package com.rschina.heilongjiang.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chinars.mapapi.GeoPoint;
import com.chinars.mapapi.MapLayer;
import com.chinars.mapapi.SphericalMercatorLayer;
import com.rschina.heilongjiang.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/9/12.
 */
public class MapInfo implements Parcelable,JsonParser{
    public String bounds;
    public long createtime;
    public long startTime;
    public long endTime;
    public String format;
    public String layers;
    public String mosaicid;
    public String name;
    public String res;
    public String service_url;
    public String type;
    public long updatetime;
    public  String timeString;
    public String clsid;
    public int maxZoom=30;
    public int minZoom=0;


    public String model_data_id;
    public int startYear = 0;
    public int endYear = 0;

    /*public String id;
    public String url;
    public String describe;
    public int maxZoom;
    public int minZoom;
    public String reportLink;
    public String doc_downloadUrl;
    public  String timeString;
    public String legend;
    public  String baseMaps;*/




    public  final  static  Creator<MapInfo> CREATOR=new Creator<MapInfo>() {
        @Override
        public MapInfo createFromParcel(Parcel source) {
            MapInfo mapInfo=new MapInfo();
            mapInfo.name=source.readString();
            mapInfo.service_url=source.readString();
            mapInfo.bounds=source.readString();
            mapInfo.type=source.readString();
            mapInfo.format=source.readString();
            mapInfo.startTime=source.readLong();
            mapInfo.endTime=source.readLong();
            mapInfo.createtime=source.readLong();
            mapInfo.updatetime=source.readLong();
            mapInfo.res=source.readString();
            mapInfo.mosaicid=source.readString();
            mapInfo.model_data_id=source.readString();
            mapInfo.clsid=source.readString();
            return mapInfo;
    }

        @Override
        public MapInfo[] newArray(int size) {
            return new MapInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(service_url);
        dest.writeString(name);
        dest.writeString(bounds);
        dest.writeString(type);
        dest.writeString(format);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeLong(createtime);
        dest.writeLong(updatetime);
        dest.writeString(res);
        dest.writeString(mosaicid);
        dest.writeString(model_data_id);
        dest.writeString(clsid);

    }

    public MapLayer buildMapLayer(){
        MapLayer mapLayer=null;
        if("wms".equals(type)){
            String[] coords=bounds.split(",");
            //(mercator2wgsX(Double.parseDouble(coords[0])),mercator2wgsX(Double.parseDouble(coords[3])))
            mapLayer=new SphericalMercatorLayer(layers,service_url,format
                    , layers,0,20
                    ,new GeoPoint(mercator2wgsX(Double.parseDouble(coords[0])),mercator2wgsY(Double.parseDouble(coords[3])))
                    ,new GeoPoint(mercator2wgsX(Double.parseDouble(coords[2])),mercator2wgsY(Double.parseDouble(coords[1]))),null);
//                    ,new GeoPoint((mercator2wgsX(Double.parseDouble(coords[0]))+mercator2wgsY(Double.parseDouble(coords[3])))/2,(mercator2wgsX(Double.parseDouble(coords[0]))+mercator2wgsY(Double.parseDouble(coords[3])))/2)
//                    ,new GeoPoint((mercator2wgsX(Double.parseDouble(coords[2]))+mercator2wgsY(Double.parseDouble(coords[1])))/2,(mercator2wgsX(Double.parseDouble(coords[2]))+mercator2wgsY(Double.parseDouble(coords[1])))/2),null);
        }
        return  mapLayer;
    }

    public void fromIdJson(JSONObject json) {
        try {
            model_data_id = json.getString("model_data_id");
            clsid = json.getString("clsid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void fromJson(JSONObject json) {
        try {
            service_url=json.getString("service_url");
            layers=json.getString("layers");
            name=json.getString("name");
            type=json.getString("type");
            bounds=json.getString("bounds");

            format=json.getString("format").replace("/","%2F");
            startTime=json.getLong("starttime");
            endTime=json.getLong("endtime");
            createtime = json.getLong("createtime");
            updatetime = json.getLong("updatetime");
            res=json.optString("res");
            mosaicid = json.getString("mosaicid");


            String stime= DateUtil.toSlashString(startTime);
            String etime=DateUtil.toSlashString(endTime);
            if(stime.equals(etime)){
                timeString=stime;
            }else {
                timeString=stime+"-"+etime;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static double mercator2wgsX(double mercatorX) {
        // web墨卡托 转 WGS-84，主要用于将坐标单位为米的值转为单位为度的值
           mercatorX  = mercatorX * 180 / 20037508.34;
        return mercatorX;
    }
    public static double mercator2wgsY(double mercatorY) {
        // web墨卡托 转 WGS-84，主要用于将坐标单位为米的值转为单位为度的值
        mercatorY = 180 / Math.PI * (2 * Math.atan(Math.exp((mercatorY / 20037508.34) * Math.PI)) - Math.PI / 2);
        return mercatorY;
    }

}
