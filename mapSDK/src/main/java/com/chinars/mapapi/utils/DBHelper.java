package com.chinars.mapapi.utils;

import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.List;

import com.chinars.mapapi.offline.OfflineMapInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	public  static final String DATABASE_NAME="DB_OFFLINEMAP";
	
	private static final String TABLE_OFFLINEMAP_INFO="offlinemap_info";
	private static final String TABLE_OFFLINEMAP_DOWNLOADINFO="offlinemap_download_info";
	private static final String TABLE_SDK_PROPERTIES="sdk_properties";
	
	public static final String KEY_INTERNAL_SDCARD_PATH="internal_sdcard_path";
	public static final String KEY_SDK_VERSION="sdk_version";
	public static final String KEY_EXTERNAL_SDCARD_PATH="external_sdk_path";
	public static final String KEY_OFFLINEMAP_ROOTDIR="offlinemap_rootdir";
	
	private String CREATE_TABLE_OFFLINEMAP_INFO="CREATE TABLE IF NOT EXISTS 'offlinemap_info' ('cityId' INTEGER PRIMARY KEY  NOT NULL , "
			+ "'downUrl' TEXT, 'mapName' TEXT, 'size' INTEGER, 'cityName' TEXT, 'updateTime' NUMERIC, 'centLat' DOUBLE,"
			+ " 'centLng' DOUBLE, 'minZoom' INTEGER, 'parentid' INTEGER, 'ishot' BOOL)";
	
	private String CREATE_TABLE_SDK_PROPERTIES="CREATE TABLE IF NOT EXISTS 'sdk_properties' ('key' TEXT PRIMARY  KEY  NOT NULL UNIQUE , 'value' TEXT)";
	private String CREATE_TABLE_OFFLINEMAP_DOWNLOADINFO="CREATE TABLE IF NOT EXISTS 'offlinemap_download_info' ('cityID' INTEGER PRIMARY KEY  NOT NULL ,"
			+ " 'cityName' TEXT,'mapName' TEXT, 'downURL' TEXT,'status' INTEGER, 'totalSize' INTEGER, 'downloadedSize' INTEGER, 'updateTime' INTEGER, 'ratio' INTEGER)";
	SQLiteDatabase  db;
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_OFFLINEMAP_INFO);
		db.execSQL(CREATE_TABLE_OFFLINEMAP_DOWNLOADINFO);
		db.execSQL(CREATE_TABLE_SDK_PROPERTIES);
		this.db=db;
		initTableData();
	}
	

	private void initTableData(){
		putProperty(KEY_EXTERNAL_SDCARD_PATH, "");
		putProperty(KEY_INTERNAL_SDCARD_PATH, "");
		putProperty(KEY_OFFLINEMAP_ROOTDIR, "");
		putProperty(KEY_SDK_VERSION, "1.0");
	}
	
	public void closeDB(){
		db.close();
	}
	
	public String getProperty(String key){
		if(db==null||!db.isOpen()){
			db=getWritableDatabase();
		}
		Cursor cur=db.rawQuery("select value from "+TABLE_SDK_PROPERTIES+" where key =?", new String[]{key} );
		if(cur.moveToFirst()){
			return cur.getString(0);
		}
		return null;
	}
	
	public void putProperty(String key,String value){
		if(db==null||!db.isOpen()){
			db=getWritableDatabase();
		}
		db.beginTransaction();
		db.execSQL("INSERT OR REPLACE INTO " + TABLE_SDK_PROPERTIES + " VALUES(?, ?)",new Object[]{key,value});
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	
	public void addOfflineMapInfo(List<OfflineMapInfo> infos){
		if(db==null||!db.isOpen()){
			db=getWritableDatabase();
		}
		db.beginTransaction();
		for(OfflineMapInfo info : infos){
			db.execSQL("INSERT OR REPLACE INTO " + TABLE_OFFLINEMAP_INFO + " VALUES(?, ?, ?, ? ,?, ? ,?, ?, ? ,?, ?)", 
					new Object[]{info.cityId,info.downUrl,info.mapName,info.size,info.cityName,info.updateTime,
					info.centLat,info.centLng,info.minZoom,info.parentid,String.valueOf(info.ishot)});
		}
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	
}
