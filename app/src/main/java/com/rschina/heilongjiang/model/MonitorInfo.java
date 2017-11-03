package com.rschina.heilongjiang.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.chinars.mapapi.GeoPoint;
import com.chinars.mapapi.MapLayer;
import com.chinars.mapapi.SphericalMercatorLayer;
import com.rschina.heilongjiang.utils.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static com.rschina.heilongjiang.model.MapInfo.mercator2wgsY;

/**
 * Created by Administrator on 2017/9/16.
 */

public class MonitorInfo implements Parcelable,JsonParser{

    public String model_data_id;
    public String pclsid;

    public String relationstr;
    public String reportlink;

    public String mapinfoid;
    public String showname;

    public String bounds;
    public long createtime;
    public long startTime;
    public long endTime;
    public String format;
    public String layers;
    public String monitorid;
    public String name;
    public String res;
    public String service_url;
    public String type;
    public long updatetime;
    public String timeString;
    public int status;
    public String legend;
    public String pclsname;

    public String doc_downloadurl;

    public int maxZoom=30;
    public int minZoom=0;



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




    public  final  static  Creator<MonitorInfo> CREATOR=new Creator<MonitorInfo>() {
        @Override
        public MonitorInfo createFromParcel(Parcel source) {
            MonitorInfo monitor=new MonitorInfo();
            monitor.name=source.readString();
            monitor.service_url=source.readString();
            monitor.bounds=source.readString();
            monitor.type=source.readString();
            monitor.format=source.readString();
            monitor.startTime=source.readLong();
            monitor.endTime=source.readLong();
            monitor.createtime=source.readLong();
            monitor.updatetime=source.readLong();
            monitor.res=source.readString();
            monitor.monitorid=source.readString();
            monitor.model_data_id=source.readString();
            monitor.pclsid=source.readString();
            monitor.relationstr=source.readString();
            monitor.mapinfoid=source.readString();
            monitor.showname=source.readString();
            monitor.status=source.readInt();
            monitor.legend=source.readString();
            monitor.pclsname=source.readString();

            monitor.reportlink=source.readString();
            monitor.doc_downloadurl=source.readString();
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
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeLong(createtime);
        dest.writeLong(updatetime);
        dest.writeString(res);
        dest.writeString(monitorid);
        dest.writeString(model_data_id);
        dest.writeString(pclsid);
        dest.writeString(relationstr);
        dest.writeString(mapinfoid);
        dest.writeString(showname);
        dest.writeString(legend);
        dest.writeString(pclsname);
        dest.writeInt(status);

        dest.writeString(reportlink);
        dest.writeString(doc_downloadurl);

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

    public void fromIdJson(JSONObject json) {
        try {
            model_data_id = json.getString("model_data_id");
            relationstr = json.getString("relationstr");
            pclsname = json.getString("pclsname");
            pclsid = json.getString("pclsid");
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
            res=json.optString("resname");
            //monitorid = json.getString("mosaicid");
            monitorid = json.getString("monitorid");
           // relationstr = json.getString("relationstr");
            legend = json.getString("legend");
            status = json.getInt("status");
            reportlink = json.getString("reportlink");
            doc_downloadurl = json.getString("doc_downloadurl");
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
}
