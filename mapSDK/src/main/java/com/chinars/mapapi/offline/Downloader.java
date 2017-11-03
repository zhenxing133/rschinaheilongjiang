package com.chinars.mapapi.offline;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Vector;

import com.chinars.mapapi.utils.DBHelper;
import com.chinars.mapapi.utils.LogUtils;

import android.R.string;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class Downloader implements Runnable{
	private final static String TAG = "BreakpointDownloader";
	
	private final static int  MAX_THREAD_NUM=3;
	private static Vector<Downloader> threadQueue=new Vector<Downloader>();
	
	private static int thread_num=0;
	public static final int MSG_PROGRESS_UPDATE=1;
	public static final int MSG_FINISH=2;
	public static final int MSG_FAILURE=3;
	
	private String downUrl;
	private long totalSize=0;
	private long downloadedSize;
	private int status=OfflineMapDownloadInfo.UNDEFINED;
	public int cityId;
	public String cityName;
	public String mapName;
	public int ratio=0;
	public long updateTime;
	private static OfflineMapListener listener;
	private String savePath=RSOfflineMap.getTempDir(null);
	private DBHelper dbHelper;

	private static Handler handler=new Handler(Looper.getMainLooper()){
		@Override
		public void handleMessage(Message msg) {
			//LogUtils.d("handle msg");
			switch (msg.what) {
			case MSG_PROGRESS_UPDATE:
				if(listener!=null){
					listener.onProgressUpdate(msg.arg1, msg.arg2);
				}
				break;
			case MSG_FINISH:
				if(listener!=null){
					listener.onDownloadFinish(msg.arg1);
				}
				break;
				
			case MSG_FAILURE:
				Bundle bundle=(Bundle)msg.obj;
				listener.onFailure(bundle.getInt("cityId"), bundle.getString("msg"));
				break;
			}
		}
	};
	
	
	public  Downloader(OfflineMapDownloadInfo offlineMapDownloadInfo,DBHelper dbHelper){
		if(offlineMapDownloadInfo==null){
			throw new NullPointerException();
		}
		this.cityName=offlineMapDownloadInfo.cityName;
		this.downUrl=offlineMapDownloadInfo.downUrl;
		this.cityId=offlineMapDownloadInfo.cityID;
		this.totalSize=offlineMapDownloadInfo.totalSize;
		this.mapName=offlineMapDownloadInfo.mapName;
		this.updateTime=offlineMapDownloadInfo.updateTime;
		this.downloadedSize=offlineMapDownloadInfo.downloadedSize;
		this.status=offlineMapDownloadInfo.status;
		this.ratio=offlineMapDownloadInfo.ratio;
		this.dbHelper=dbHelper;
	}
	
	public int getStatus(){
		return status;
	}
	
	public void saveInfoToDB(){
		int tempStatus=status;
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		if(status!=OfflineMapDownloadInfo.FINISHED){
			tempStatus=OfflineMapDownloadInfo.SUSPENDED;
		}
		db.beginTransaction();
		db.execSQL("INSERT OR REPLACE INTO offlinemap_download_info VALUES(?, ?, ?, ? ,?, ? ,?, ?, ? )", 
				new Object[]{cityId,cityName,mapName,downUrl,tempStatus,totalSize,downloadedSize,
				updateTime,ratio});
		db.setTransactionSuccessful();
		db.endTransaction();
	}
	
	
	public static void setDownloadListener(OfflineMapListener listener){
		Downloader.listener=listener;
	}
	
	
	public synchronized boolean pause(){
		if(this.status==OfflineMapDownloadInfo.DOWNLOADING||status==OfflineMapDownloadInfo.WAITING){
			this.status=OfflineMapDownloadInfo.SUSPENDED;
			thread_num--;
			if(!threadQueue.isEmpty()){
			 Downloader downloader=threadQueue.firstElement();
			 downloader.start();
			}
			return true;
		}else{
			return false;
		}
	}
	
	
	public synchronized  int start(){
		if(this.status==OfflineMapDownloadInfo.FINISHED){
			LogUtils.i("已经下载完成");
		}
		if(status==OfflineMapDownloadInfo.WAITING||status==OfflineMapDownloadInfo.SUSPENDED){
			if(thread_num<=MAX_THREAD_NUM){
				thread_num++;
				status=OfflineMapDownloadInfo.DOWNLOADING;
				threadQueue.remove(this);
				new Thread(this).start();
			}else{
				status=OfflineMapDownloadInfo.WAITING;
				if(!threadQueue.contains(this)){
					threadQueue.add(this);
				}
			}
		}
		return status;
	}
	
	@Override
	public void run() {
		File saveFile = null;
		try {
			saveFile = new File(savePath,mapName + "-" + cityName+ ".tmp");
			if (!saveFile.exists()) {
				saveFile.createNewFile();
				Log.i(TAG, "Create file!");
				downloadedSize = 0;
			}
			else
				downloadedSize = saveFile.length();
			
			long startPos = downloadedSize; 
			Log.i(TAG, "startPos: " + startPos);
			long endPos = totalSize -1; 
			Log.i(TAG, "endPos: " + endPos);
			saveInfoToDB();
			URL url = new URL(this.downUrl);
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setConnectTimeout(5 * 1000);
			http.setRequestMethod("GET");
			http.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
			http.setRequestProperty("Accept-Language", "zh-CN");
			http.setRequestProperty("Referer", downUrl); 
			http.setRequestProperty("Charset", "UTF-8");
			
			http.setRequestProperty("Range", "bytes=" + startPos + "-"+ endPos);
			http.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
			http.setRequestProperty("Connection", "Keep-Alive");

			byte[] buffer = new byte[8*1024];
			BufferedInputStream in = new BufferedInputStream(http.getInputStream());
			int offset = 0;
			FileOutputStream fileOut=new FileOutputStream(saveFile, true);
			BufferedOutputStream bout=new BufferedOutputStream(fileOut);
			while ((offset = in.read(buffer, 0, buffer.length)) != -1) {
				bout.write(buffer, 0, offset);
				downloadedSize += offset;
				if((int) (downloadedSize*100/totalSize)>ratio){
					ratio=(int) (downloadedSize*100/totalSize);
					handler.obtainMessage(MSG_PROGRESS_UPDATE, cityId,ratio).sendToTarget();
				}
				if (status!=OfflineMapDownloadInfo.DOWNLOADING) {
					bout.close();
					saveInfoToDB();
					return ;
				}
			}
			bout.close();
			saveFile.renameTo(new File(savePath + File.separator + mapName + "-" + cityName+".zip"));
			status=OfflineMapDownloadInfo.FINISHED;
			handler.obtainMessage(MSG_FINISH, cityId, 0).sendToTarget();
			saveInfoToDB();
			LogUtils.i("下载完成:"+mapName);
			thread_num--;
			if(!threadQueue.isEmpty()){
				Downloader downloader=threadQueue.firstElement();
				downloader.start();
			}
			return ;
		} catch (Exception e) {
			e.printStackTrace();
			Bundle bundle=new Bundle();
			bundle.putInt("cityId", cityId);
			bundle.putString("msg", e.getMessage());
			handler.obtainMessage(MSG_FAILURE,bundle).sendToTarget();
		} 
		return ;
	}
	
}
