package com.chinars.mapapi.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.AsyncTask;

import com.chinars.mapapi.GeoPoint;
import com.chinars.mapapi.MapView;
/**
 * Poi搜索类
 * @author rsclouds
 *
 */
public class PoiSearch {
	//private final static String server_addr="http://202.104.180.14:6010/mapsrv/services/rest/";
	private final static String server_addr="http://113.105.131.192:8081/sslpoisvc/services/rest/";
	private String url;
	private int pagesize=15;
	private int pagenum=0;
	private PoiSearchListener mPoiSearchListener;
	private MapView mapView;
	private GeoPoint myLocation=new GeoPoint(22.93061, 113.88177);
	
	public PoiSearch(MapView mapView){
		this.mapView=mapView;
	}
	
	public PoiSearch(){
		
	}
	public static final int  TYPE_POI_LIST=1;
	public static final int  TYPE_AREA_MULTI_POI_LIST=2;
	public static final int  TYPE_DETAIL_SEARCH=3;
	public static final int  TYPE_GEOCODE=4;
	public static final int  TYPE_REVERSE_GOECODE=5;
	public static final int TYPE_SEARCH_ALL=6;
	static List<AsyncTask<Object, Void, String>> tasks=new ArrayList<AsyncTask<Object,Void,String>>();
	
	public void cancelAllTask(){
		for(AsyncTask<Object, Void, String> task:tasks){
			if(task!=null){
				task.cancel(true);
			}
		}
		tasks.clear();
	}

	public int poiSearchInbounds(String key, GeoPoint ptLB, GeoPoint ptRT) {
		url=server_addr+"placeInBounds?keys="+key+"&bounds="+ptLB.getLongitude()+
				","+ptLB.getLatitude()+","+ptRT.getLongitude()+","+ptRT.getLatitude()+getPageParams();
		new PoiSearchTask().execute(url,TYPE_POI_LIST,mPoiSearchListener);
		return 0;
	}

	private String getPageParams(){
		String params="&page_size="+pagesize+"&page_num="+pagenum+"&_type=json";
		if(mapView==null||mapView.getMyLocation()==null){
			params="&lonlat="+myLocation.getLongitude()+","+myLocation.getLatitude()+params;
		}else{
			params="&lonlat="+mapView.getMyLocation().getLongitude()+","+mapView.getMyLocation().getLatitude()+params;
		}
		return params;
	}
	
	public int poiDetailSearch(String uid) {
		return 0;
	}

	public int poiSearchInCity(String city, String key) {
		url=server_addr+"placeInAdminArea?keys="+key+"&area="+city+getPageParams();
		new PoiSearchTask().execute(url,TYPE_POI_LIST,mPoiSearchListener);
		return 0;
	}

	/**
	 * 使用POI类别名称搜索，默认返回10条结果
	 * @param kindName 类别名称
	 * @return
	 */
	public int poiSearchByKindName(String kindName){
		url=server_addr+"getInfosByKindName?kind_name="+kindName+"&page_size="+20+"&page_num="+0+"&_type=json";
		if(mapView!=null&&mapView.getMyLocation()!=null){
			url=url+"&lonlat="+mapView.getMyLocation().getLongitude()+","+mapView.getMyLocation().getLatitude();
		}else{
			url=url+"&lonlat="+myLocation.getLongitude()+","+myLocation.getLatitude();
		}
		new PoiSearchTask().execute(url,TYPE_SEARCH_ALL,mPoiSearchListener);
		return 0;
	}
	
	/**
	 * 使用POI类别编号称搜索，默认返回10条结果
	 * @param kindCode 类别编号
	 * @return
	 */
	public int poiSearchByKindCode(int kindCode){
		url=server_addr+"getInfosByKindCode?kind_code="+Integer.toHexString(kindCode).toUpperCase()+getPageParams();
		new PoiSearchTask().execute(url,TYPE_POI_LIST,mPoiSearchListener);
		return 0;
	}
	
	
	public int poiSearchAll(int kindCode){
		url=server_addr+"getInfosByKindCode?kind_code="+Integer.toHexString(kindCode).toUpperCase()+"&page_size="+500+"&page_num="+0+"&_type=json";
		if(mapView!=null&&mapView.getMyLocation()!=null){
			url=url+"&lonlat="+mapView.getMyLocation().getLongitude()+","+mapView.getMyLocation().getLatitude();
		}else{
			url=url+"&lonlat="+myLocation.getLongitude()+","+myLocation.getLatitude();
		}
		new PoiSearchTask().execute(url,TYPE_SEARCH_ALL,mPoiSearchListener);
		return 0;
	}
	
	public int poiMultiSearchInbounds(String[] keys, GeoPoint ptLB,
			GeoPoint ptRT) {
		String combkey="";
		for(int i=0;i<keys.length;i++){
			combkey=combkey+keys[i];
		}
		combkey.trim();
		return poiSearchInbounds(combkey,ptLB,ptRT);
	}

	public int poiSearchNearBy(String key, GeoPoint pt, int radius) {
		url=server_addr+"placeInCircle?keys="+key+"&centerXY="+pt.getLongitude()+","+pt.getLatitude()+
				"&radius="+radius+getPageParams();
		new PoiSearchTask().execute(url,TYPE_POI_LIST,mPoiSearchListener);
		return 0;
	}

	public int poiMultiSearchNearBy(String[] keys, GeoPoint pt,
			int radius) {
		String combkey="";
		for(int i=0;i<keys.length;i++){
			combkey=combkey+keys[i];
		}
		combkey.trim();
		return poiSearchNearBy(combkey,pt,radius);
	}

	public int reverseGeocode(GeoPoint pt) {
		url=server_addr+"locationByLonLat?lon="+pt.getLongitude()+"&lat="+pt.getLatitude()+getPageParams();
		new PoiSearchTask().execute(url,TYPE_REVERSE_GOECODE,mPoiSearchListener);
		return 0;
	}

	public void setmPoiSearchListener(PoiSearchListener mPoiSearchListener) {
		this.mPoiSearchListener = mPoiSearchListener;
	}
	
	public int geocode(String strAddr, String city) {
		return 0;
	}
	
	public void setPoiPageSize(int pagesize){
		this.pagesize=pagesize;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getPagenum() {
		return pagenum;
	}

	public void setPagenum(int pagenum) {
		this.pagenum = pagenum;
	}

	public GeoPoint getMyLocation() {
		return myLocation;
	}

	public void setMyLocation(GeoPoint myLocation) {
		this.myLocation = myLocation;
	}
	
	
}
