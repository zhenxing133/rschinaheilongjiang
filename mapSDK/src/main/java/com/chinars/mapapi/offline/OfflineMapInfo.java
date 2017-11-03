package com.chinars.mapapi.offline;

import org.json.JSONObject;

import android.database.Cursor;

/**
 * OfflineMapInfo 记录离线地图本身的一些属性，如城市名、地图类型，离线包大小等等。
 * @author rsClouds
 *
 */
public class OfflineMapInfo {
	public int cityId;
	public String downUrl;
	public String mapName;
	public long size;
	public String cityName;
	public long updateTime;
	public double centLat;
	public double centLng;
	public int minZoom;
	public int parentid;
	public boolean ishot;
	
	public  static OfflineMapInfo fromJson(JSONObject json){
		OfflineMapInfo offlineMapInfo=new OfflineMapInfo();
		offlineMapInfo.cityId=json.optInt("cityId");
		offlineMapInfo.downUrl=json.optString("downUrl");
		offlineMapInfo.mapName=json.optString("mapName");
		offlineMapInfo.size=json.optLong("size");
		offlineMapInfo.cityName=json.optString("cityName");
		offlineMapInfo.updateTime=json.optLong("updateTime");
		offlineMapInfo.centLat=json.optDouble("centLat");
		offlineMapInfo.centLng=json.optDouble("centLng");
		offlineMapInfo.minZoom=json.optInt("minZoom");
		offlineMapInfo.parentid=json.optInt("parentid");
		offlineMapInfo.ishot=json.optBoolean("ishot");
		return offlineMapInfo;
	}
	
	public static OfflineMapInfo fromDBCursor(Cursor c){
		OfflineMapInfo offlineMapInfo=new OfflineMapInfo();
		offlineMapInfo.cityId=c.getInt(c.getColumnIndex("cityId"));
		offlineMapInfo.downUrl=c.getString(c.getColumnIndex("downUrl"));
		offlineMapInfo.mapName=c.getString(c.getColumnIndex("mapName"));
		offlineMapInfo.size=c.getInt(c.getColumnIndex("size"));
		offlineMapInfo.cityName=c.getString(c.getColumnIndex("cityName"));
		offlineMapInfo.updateTime=c.getInt(c.getColumnIndex("updateTime"));
		offlineMapInfo.centLat=c.getDouble(c.getColumnIndex("centLat"));
		offlineMapInfo.centLng=c.getDouble(c.getColumnIndex("centLng"));
		offlineMapInfo.minZoom=c.getInt(c.getColumnIndex("minZoom"));
		offlineMapInfo.parentid=c.getInt(c.getColumnIndex("parentid"));
		offlineMapInfo.ishot=Boolean.parseBoolean(c.getString(c.getColumnIndex("ishot")));
		return offlineMapInfo;
	}
}
