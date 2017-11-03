package com.chinars.mapapi.search;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.io.Serializable;

import com.chinars.mapapi.GeoPoint;
import com.chinars.mapapi.PoiOverlay;
/**
 * POI数据类
 * @author rsclouds 中科遥感
 *
 */
public class PoiInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public GeoPoint geoPoint;
	public String name;			// 名称
	public String py;				// 名称拼音
	public String admincode;		// 所属行政区划代码
	public String vadmincode;		// 较大影响力的行政区划代码
	public String zipcode;			// 邮政编码
	public String telephone;		// 电话号码，多个用“|”分隔，区号和号码用    // “-”分隔
	public String province;			// 省直辖市
	public String city;				// 地级市
	public String district;			// 区县
	public String poiType;			//poi类别
	public String addr;				// 街道和门牌号
	public Drawable marker;           //显示图标

	@Override
	public String toString() {
		return "{'name':"+name+",'lon':"+geoPoint.getLongitude()+",'lat':"+geoPoint.getLatitude()+"}";
	}
	
	public PoiInfo() {
	}
	public PoiInfo(GeoPoint loc,Drawable marker){
		this(loc);
		this.marker=marker;
	}
	
	public PoiInfo(GeoPoint p){
		this.geoPoint=p;
	}
	
	public GeoPoint getGeoPoint(){
		return  geoPoint;
	}
	
}
