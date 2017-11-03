package com.chinars.mapapi.offline;

import android.database.Cursor;
import android.util.Log;


public class OfflineMapDownloadInfo {

	public static final int UNDEFINED=0; //未加入下载
	public static final int DOWNLOADING=1;//正在下载
	public static final int WAITING=2; // 等待分配线程下载
	public static final int SUSPENDED=3; // 已下载一部分，暂停中
	public static final int FINISHED=4; // 已下载完成
	
	
	public int cityID;
	public String cityName;
	public String mapName;
	public String downUrl;
	public  int status;
	public int totalSize;
	public int downloadedSize;
	public long updateTime;
	public int ratio;
	
	public OfflineMapDownloadInfo(){
		
	}
	
	public OfflineMapDownloadInfo(OfflineMapInfo mapInfo){
		this.cityID=mapInfo.cityId;
		this.cityName=mapInfo.cityName;
		this.mapName=mapInfo.mapName;
		this.downUrl=mapInfo.downUrl;
		this.status=SUSPENDED;
		this.totalSize=(int) mapInfo.size;
		this.downloadedSize=0;
		this.updateTime=mapInfo.updateTime;
		this.ratio=0;
	}
	
	public static OfflineMapDownloadInfo fromDBCursor(Cursor cur){
		OfflineMapDownloadInfo info=new OfflineMapDownloadInfo();
		info.cityID=cur.getInt(cur.getColumnIndex("cityID"));
		info.cityName=cur.getString(cur.getColumnIndex("cityName"));
		info.downUrl=cur.getString(cur.getColumnIndex("downURL"));
		Log.e("qwe", info.downUrl);
		info.mapName=cur.getString(cur.getColumnIndex("mapName"));
		info.status=cur.getInt(cur.getColumnIndex("status"));
		info.totalSize=cur.getInt(cur.getColumnIndex("totalSize"));
		info.downloadedSize=cur.getInt(cur.getColumnIndex("downloadedSize"));
		info.updateTime=cur.getInt(cur.getColumnIndex("updateTime"));
		info.ratio=cur.getInt(cur.getColumnIndex("ratio"));
		return info;
	}
	
	public static String getStatusString(int status){
		switch(status){
		case UNDEFINED:
			return "";
		case DOWNLOADING:
			return "(正在下载)";
		case WAITING:
			return "(等待下载)";
		case SUSPENDED:
			return "(暂停下载)";
		case FINISHED:
			return "(已下载)";
		 default:
			 return "";
		}
	}
}
