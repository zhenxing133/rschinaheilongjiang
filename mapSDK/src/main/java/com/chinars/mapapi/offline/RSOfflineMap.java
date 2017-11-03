package com.chinars.mapapi.offline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.chinars.mapapi.utils.CommonUtils;
import com.chinars.mapapi.utils.DBHelper;
import com.chinars.mapapi.utils.LogUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;

public class RSOfflineMap {
	
	static int TYPE_NEW_OFFLINE;		//新安装离线地图事件类型
	static int TYPE_DOWNLOAD_UPDATE;	//离线地图下载更新事件类型
	static int TYPE_VAR_UPDATE;			//离线地图数据版本更新事件类型
	public static final String KEY_UPDATE_TIME="offline_list_update_time";
	public static final String KEY_INIT_DATA_READY="init_data_ready";
	public static final String KEY_STORAGE_KYPE="storage_type";
	public static final String KEY_OFFLINE_MAP_DIR="offline_map_dir";
	
	public static int PHONE=0;
	public static int SDCARD=1;
	public static int NONE=3;
	
	private static int storageType=NONE;
	public static String PHONE_PATH=null;
	public static String SDCARD_PATH=null;
	private long lastUpdateTime=0;
	
	private  Context mContext;
	private OfflineMapListener offlineMapListener;
	private static DBHelper dbHelper;
	private  SQLiteDatabase db;
	private static Handler handler=new Handler(Looper.getMainLooper());
	private boolean stop=false;
	
	SparseArray<Downloader> downloadTasks=new SparseArray<Downloader>();
	
	public RSOfflineMap(Context context){
		this.mContext=context;
		if(PHONE_PATH==null){
			init(mContext);
		}
		db=dbHelper.getWritableDatabase();
		lastUpdateTime=Long.parseLong(dbHelper.getProperty(KEY_UPDATE_TIME));
		if(System.currentTimeMillis()-lastUpdateTime>60*60*1000){
			LogUtils.d("update offlinemap info");
			new FetchCityListThread().start();;
			}
		LogUtils.d(dbHelper.getProperty(DBHelper.KEY_SDK_VERSION)+"");
	}
	
	public void setStopThread(boolean stop){
		this.stop=stop;
	}
	public static void init(Context context){
		dbHelper=new DBHelper(context);
		String ready=dbHelper.getProperty(KEY_INIT_DATA_READY);
		if(ready==null||ready.equals("false")){
			File sdcard=Environment.getExternalStorageDirectory();
			PHONE_PATH=sdcard.toString()+"/ChinarsMap";
			if(CommonUtils.getSDCard2()!=null){
				SDCARD_PATH=CommonUtils.getSDCard2()+"/ChinarsMap";
			}
			
			File dataDir=new File(PHONE_PATH+"/data");
			if(dataDir.exists()){
				storageType=PHONE;
				File[] files=dataDir.listFiles();
				for(File file:files){
					if(file.getName().endsWith(".idx")){
						
					}
				}
			}
			if(storageType==NONE){
				File data2=new File(SDCARD_PATH+"/data");
				if(data2.exists()){
					storageType=SDCARD;
				}
			}
			if(storageType==NONE){
				storageType=0;
				dataDir.mkdirs();
				File tempDir=new File(PHONE_PATH+"/temp");
				if(!tempDir.exists()){
					tempDir.mkdirs();
				}
			}
			
			dbHelper.putProperty(KEY_STORAGE_KYPE, storageType+"");
			dbHelper.putProperty(DBHelper.KEY_EXTERNAL_SDCARD_PATH,SDCARD_PATH+"");
			dbHelper.putProperty(DBHelper.KEY_INTERNAL_SDCARD_PATH,PHONE_PATH);
			if(storageType==1){
				dbHelper.putProperty(KEY_OFFLINE_MAP_DIR,SDCARD_PATH+"/data");
			}else{
				dbHelper.putProperty(KEY_OFFLINE_MAP_DIR,PHONE_PATH+"/data");
			}
			dbHelper.putProperty(KEY_UPDATE_TIME, "0");
			dbHelper.putProperty(KEY_INIT_DATA_READY, "true");
		}else{
			SDCARD_PATH=dbHelper.getProperty(DBHelper.KEY_EXTERNAL_SDCARD_PATH);
			PHONE_PATH=dbHelper.getProperty(DBHelper.KEY_INTERNAL_SDCARD_PATH);
			storageType=Integer.valueOf(dbHelper.getProperty(KEY_STORAGE_KYPE));
			if(CommonUtils.getSDCard2()==null){
				storageType=0;
				File dataDir=new File(PHONE_PATH+"/data");
				if(!dataDir.exists()){
					dataDir.mkdirs();
					File tempDir=new File(PHONE_PATH+"/temp");
					if(!tempDir.exists()){
						tempDir.mkdirs();
					}
				}
				SDCARD_PATH=null;
			}
		}
		dbHelper.closeDB();
		
	}
	
	
	
	public static String getDataRoot(Context context){
		if(PHONE_PATH==null){
			init(context);
		}
		if(storageType==PHONE){
			return PHONE_PATH+"/data";
		}else if(storageType==SDCARD){
			return SDCARD_PATH+"/data";
		}else {
			return null;
		}
	}
	
	public static String getTempDir(Context context){
		if(PHONE_PATH==null){
			init(context);
		}
		if(storageType==PHONE){
			return PHONE_PATH+"/temp";
		}else{
			return SDCARD_PATH+"/temp";
		}
	}
	
	public int  getStorageType(){
	 return storageType;	
	}
	
	public boolean setStorageType(int type,ProgressDialog dialog){
		if(type==storageType){
			return true;
		}else{
			if(type==PHONE){
				new MoveFolderThread(SDCARD_PATH, PHONE_PATH,dialog).start();;
			}else if(type==SDCARD){
				new MoveFolderThread(PHONE_PATH, SDCARD_PATH,dialog).start();
			}
		}
		return false;
	}
	
	
	/**
	 * 销毁离线地图管理模块，不用时调用 
	 */
	public void destroy(){
		for(int i=0;i<downloadTasks.size();i++){
			Downloader downloader=downloadTasks.valueAt(i);
			downloader.pause();
		}
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				db.close();
			}
		},500);
		
	}

	
	public void setOfflineMapListener(OfflineMapListener listener){
		this.offlineMapListener=listener;
		Downloader.setDownloadListener(offlineMapListener);
	}
	
	/**
	 * 返回指定城市ID离线地图更新信息
	 * @param cityID 指定的城市ID
	 * @return
	 */
	public OfflineMapDownloadInfo getUpdateInfo(int cityID){
	
		
		return null;
	}
	
	
	public List<OfflineMapDownloadInfo> getOfflineMapDownloadInfoList(){
		List<OfflineMapDownloadInfo> result=new ArrayList<OfflineMapDownloadInfo>();
		Cursor cur=db.rawQuery("SELECT * FROM offlinemap_download_info", null);
		while(cur.moveToNext()){
			OfflineMapDownloadInfo info=OfflineMapDownloadInfo.fromDBCursor(cur);
			result.add(info);
		}
		return result;
	}
	
	public OfflineMapDownloadInfo getOfflineMapDownloadInfoById(int cityId){
		OfflineMapDownloadInfo info=null;
		Cursor cur=db.rawQuery("SELECT * FROM offlinemap_download_info where cityID=?", new String[]{cityId+""});
		if(cur.moveToFirst()){
			info=OfflineMapDownloadInfo.fromDBCursor(cur);
		}
		return info;
	}
	
	public OfflineMapInfo getOfflineMapInfoById(int cityId){
		OfflineMapInfo info=null;
		Cursor cur=db.rawQuery("SELECT * FROM offlinemap_info where cityID=?", new String[]{cityId+""});
		if(cur.moveToFirst()){
			info=OfflineMapInfo.fromDBCursor(cur);
		}
		return info;
	}
	
	
	
	
	/**
	 * 返回热门城市列表 
	 * @return
	 */
	public List<OfflineMapInfo> getHotCityList(){
		
		Cursor cur=db.rawQuery("SELECT * FROM offlinemap_info where ishot='true'", null);
		List<OfflineMapInfo> result=new ArrayList<OfflineMapInfo>();
		while(cur.moveToNext()){
			OfflineMapInfo info=OfflineMapInfo.fromDBCursor(cur);
			result.add(info);
		}
		return result;
	}
	
	
	/**
	 * 返回支持离线地图城市列表 
	 * @return
	 */
	public List<OfflineMapInfo> getOfflineCityList(){
		Cursor cur=db.rawQuery("SELECT * FROM offlinemap_info", null);
		List<OfflineMapInfo> result=new ArrayList<OfflineMapInfo>();
		while(cur.moveToNext()){
			OfflineMapInfo info=OfflineMapInfo.fromDBCursor(cur);
			result.add(info);
		}
		return result;
	}
	
	

	public List<OfflineMapInfo> searchOfflineInfoByName(String condition){
		List<OfflineMapInfo> infos=new ArrayList<OfflineMapInfo>();
		Cursor cur=db.rawQuery("SELECT * FROM offlinemap_info where cityName like ?", new String[]{condition});
		while(cur.moveToNext()){
			OfflineMapInfo info=OfflineMapInfo.fromDBCursor(cur);
			infos.add(info);
		}
		return infos;
	}
	
	/**
	 * 导入离线地图包 
	 * @return 	添加的离线包个数
	 */
	public int scan(){
		
		return 0;
	}
	
	
	/**
	 * 暂停下载指定城市ID的离线地图 
	 * @param cityID
	 * @return
	 */
	public boolean pause(int cityID){
		Downloader downloader=downloadTasks.get(cityID);
		if(downloader==null){
			return false;
		}else{
			return downloader.pause();
		}
	}
	
	
	/**
	 * 删除指定城市ID的离线地图 
	 * @param cityID
	 * @return
	 */
	public boolean remove(final int cityID){
		pause(cityID);
		final OfflineMapDownloadInfo downInfo=getOfflineMapDownloadInfoById(cityID);
		if(downInfo!=null){
			if(downInfo.status==OfflineMapDownloadInfo.FINISHED){
				File saveFile = new File(getTempDir(null),downInfo.mapName+"-"+downInfo.cityName+".zip");
				if(saveFile.exists()){
					saveFile.delete();
				}
				//删除文件
				new AsyncTask<Void, Void, Void>(){
					@Override
					protected Void doInBackground(Void... params) {
						try{
							File idxFile=new File(getDataRoot(null),downInfo.mapName+"-"+downInfo.cityName+".idx");
							if(!idxFile.exists()){
								return null;
							}
							BufferedReader reader=new BufferedReader(new FileReader(idxFile));
							while(reader.ready()){
								String filePath=reader.readLine();
								File file=new File(filePath);
								if(file.exists()){
									file.delete();
								}
							}
							reader.close();
							idxFile.delete();
						}catch(Exception e){
							e.printStackTrace();
						}

						return null;
					}
				}.execute();
			}else{
				File tempFile = new File(getTempDir(null),downInfo.mapName+"-"+downInfo.cityName+".tmp");
				if(tempFile.exists()){
					tempFile.delete();
				}
			}
			db.beginTransaction();
			db.execSQL("DELETE FROM offlinemap_download_info where cityID=?",new String[]{cityID+""});
			db.setTransactionSuccessful();
			db.endTransaction();
			return true;
		}
		return false;
	}
	


	/**
	 * 启动下载指定城市ID的离线地图
	 * @param cityID 指定的城市ID
	 * @return 下载状态
	 */
	public int start(int cityID){
		Downloader downloader=downloadTasks.get(cityID);
		if(downloader==null){
			OfflineMapDownloadInfo downInfo=getOfflineMapDownloadInfoById(cityID);
			if(downInfo!=null){
				downloader=new Downloader(downInfo,dbHelper);
			}else{
				OfflineMapInfo  mapInfo=getOfflineMapInfoById(cityID);
				downloader=new Downloader(new OfflineMapDownloadInfo(mapInfo),dbHelper);
			}
			downloadTasks.put(cityID, downloader);
		}
		return downloader.start();
	}
	
	class FetchCityListThread extends Thread {
		@Override
		public void run() {
			HttpClient client = new DefaultHttpClient();
			StringBuilder builder = new StringBuilder();
			List<OfflineMapInfo> mapInfos=new ArrayList<OfflineMapInfo>();
			HttpGet get = new HttpGet("http://192.168.1.80:8080/mapsdk/apk/getCityList.action"); 
			try {  
				HttpResponse response = client.execute(get);  
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));  
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {  
					builder.append(s);  
				}  
				String result=builder.toString();
				JSONObject json=new JSONObject(result);
				JSONArray jArray=json.getJSONArray("list");
				for(int i=0;i<jArray.length();i++){
					OfflineMapInfo mapInfo=OfflineMapInfo.fromJson(jArray.getJSONObject(i));
					mapInfos.add(mapInfo);
				}
				dbHelper.addOfflineMapInfo(mapInfos);
				if(offlineMapListener!=null){
					offlineMapListener.onCityListUpdate(mapInfos);
				}
				dbHelper.putProperty(KEY_UPDATE_TIME, System.currentTimeMillis()+"");
				LogUtils.d("update success");  
			}catch(Exception e){
				LogUtils.wtf(e);
			}
		}
	}
	
	class MoveFolderThread extends Thread {
		private String srcDir;
		private String distDir;
		private int progress=0;
		private ProgressDialog dialog; 
		public MoveFolderThread(String src,String dist,ProgressDialog dialog){
			this.srcDir=src;
			this.distDir=dist;
			this.dialog=dialog;
		}
		@Override
		public void run() {
			File src=new File(srcDir);
			if(!src.exists()){
				return;
			}
			File dist=new File(distDir);
			if(!dist.exists()){
				dist.mkdirs();
			}
			final long totalSize=CommonUtils.getDirSize(src)-CommonUtils.getDirSize(dist);
			long freeSize=CommonUtils.getAllorFreeSize(distDir, false);
			if(totalSize>freeSize){
				dialog.dismiss();
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					dialog.setMessage("总大小："+CommonUtils.sizeConvert(totalSize));
				}
			});
			if(totalSize==0){
				return;
			}
			long copySize=0;
			Vector<File> vector=new Vector<File>();
			File[] topFiles=src.listFiles();
			for(File topFile :topFiles){
				vector.add(topFile);
			}
			stop=false;
			while(!vector.isEmpty()&&!stop){
				File srcFile=vector.remove(0);
				if(srcFile.isDirectory()){
					new File(distDir,srcFile.getPath().replace(srcDir+"/", "")).mkdirs();
					File[] files=srcFile.listFiles();
					for(File childFile:files){
						vector.add(childFile);
					}
				}else{
					File distFile=new File(distDir,srcFile.getPath().replace(srcDir+"/", ""));
					if(!distFile.exists()){
						CommonUtils.fileCopy(srcFile, distFile);
						copySize+=srcFile.length();
					}
				}
				if(progress<(int) (copySize*100/totalSize)){
					progress=(int) (copySize*100/totalSize);
					LogUtils.d("progress:"+progress);
					handler.post(new Runnable() {
						@Override
						public void run() {
							dialog.setProgress(progress);
						}
					});
				}
			}
			if(progress==100){
				CommonUtils.deleteDirectory(srcDir);
				if(storageType==PHONE){
					storageType=SDCARD;
				}else{
					storageType=PHONE;
				}
				dbHelper.putProperty(KEY_STORAGE_KYPE, storageType+"");
			}
			handler.post(new Runnable() {
				@Override
				public void run() {
					dialog.dismiss();
				}
			});
		}
	}
	
}
