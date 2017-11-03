package com.rschina.heilongjiang.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chinars.mapapi.GeoPoint;
import com.chinars.mapapi.MapLayer;
import com.chinars.mapapi.SphericalMercatorLayer;

import org.json.JSONObject;

import static com.rschina.heilongjiang.model.MapInfo.mercator2wgsY;

/**
 * Created by Administrator on 2017/9/16.
 */

public class BaseIdDes implements Parcelable,JsonParser{

    public String bounds;
    public String format;
    public String layers;
    public String name;
    public String service_url;
    public String type;

    public int maxZoom=30;
    public int minZoom=0;


    public  final  static  Creator<MonitorInfo> CREATOR=new Creator<MonitorInfo>() {
        @Override
        public MonitorInfo createFromParcel(Parcel source) {
            MonitorInfo monitor=new MonitorInfo();
            monitor.name=source.readString();
            monitor.service_url=source.readString();
            monitor.bounds=source.readString();
            monitor.type=source.readString();
            monitor.format=source.readString();
            monitor.layers=source.readString();

            return monitor;
        }

        @Override
        public MonitorInfo[] newArray(int size) {
            return new MonitorInfo[size];
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
        dest.writeString(layers);
    }

    public MapLayer buildMapLayer(){
        MapLayer mapLayer=null;
        if("wms".equals(type)){
            String[] coords=bounds.split(",");
            mapLayer=new SphericalMercatorLayer(layers,service_url,format
                    , layers,0,15
                    ,new GeoPoint(mercator2wgsX(Double.parseDouble(coords[0])),mercator2wgsY(Double.parseDouble(coords[3])))
                    ,new GeoPoint(mercator2wgsX(Double.parseDouble(coords[2])),mercator2wgsY(Double.parseDouble(coords[1]))),null);
        }
        return  mapLayer;
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

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static double mercator2wgsX(double mercatorX) {
        // web墨卡托 转 WGS-84，主要用于将坐标单位为米的值转为单位为度的值
        mercatorX  = mercatorX * 180 / 20037508.34;
        return mercatorX;
    }
}
