package com.chinars.mapapi;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import com.chinars.mapapi.utils.LogUtils;

 class WebImageCache {
	private static String TAG = "WebImageCache";
    private static final long ONE_DAY_IN_MILLIS = 1000 * 60 * 60 * 24 ;
	private static HashMap<String , String> prefixMap=new HashMap<String, String>();
	private boolean isRead=false;
	private Bitmap blanktile;//空白瓦片
	private MapView mapView;
	private Context context;
	private BitmapFactory.Options options;
	private int maxSize =101;
	private File cacheRoot = null;
	private int minCacheSize=20;//最小20M
	private int maxCacheSize=100;//最大100M
	private int maxCacheDays=16;//缓存最大保存天数
	private int threadNum=0;
	private boolean netAvailable=true;
	private long lastTime=0;
	private Vector<TaskJob> taskJobs=new Vector<TaskJob>();
	private Vector<String> cacheJobs=new Vector<String>();
	private long lastRefreshTime=0;
	 private int count=0;
	private Set<String> keySet=Collections.synchronizedSet(new HashSet<String>(48));
	private Set<String> blankSet=Collections.synchronizedSet(new HashSet<String>(48));
	private Map<String, Bitmap> cache = Collections
			.synchronizedMap(new LinkedHashMap<String, Bitmap>(101, .75F, true) {
				private static final long serialVersionUID = 1L;

				protected boolean removeEldestEntry(Map.Entry<String, Bitmap> eldest) {
					if(size()>maxSize){
						Bitmap bitmap=eldest.getValue();
						bitmap.recycle();
						bitmap=null;
						return true;
					}else{
						return false;
					}
				}
				
			});

	public static void putUrlPrefix(String name,String prefix){
		prefixMap.put(name, prefix);
		//Log.e("yzx", prefix);
	}
		
	public void refreshMapView(){
		if(System.currentTimeMillis()-lastRefreshTime>360||(cacheJobs.isEmpty()&&taskJobs.isEmpty())){
			mapView.refresh();
			lastRefreshTime=System.currentTimeMillis();
		}
	}
	
	public void setMaxCacheSize(int size){
		this.maxSize=size;
	}
	
	/**
	 * 从内存缓存中获取瓦片，没有则返回null
	 * @param key
	 * @return
	 */
	public  Bitmap get(String key){
		return cache.get(key);
	}
	
	public void destroy(){
		taskJobs.clear();
		cacheJobs.clear();
		synchronized (cache) {
			for(Iterator<Bitmap> itr=cache.values().iterator();itr.hasNext();){
				Bitmap bitmap=itr.next();
				if(bitmap!=null){
					bitmap.recycle();
				}
			}
		}
	}
	
	public WebImageCache( MapView mapView, int maxSize) {
		this.context=mapView.getContext();
		this.mapView=mapView;
		this.cacheRoot =new File(context.getCacheDir(),"tiles");
		if(!cacheRoot.exists()){
			cacheRoot.mkdir();
		}
		this.maxSize = maxSize;
		blanktile=Bitmap.createBitmap(256, 256, Config.ALPHA_8);
		if (cacheRoot != null) {
			new CacheCleanThread().start();
		}
		options=new BitmapFactory.Options();
		options.inPreferredConfig=Config.RGB_565;
	}
	
	public boolean isDone() {
		return cacheJobs.isEmpty()&&keySet.isEmpty();
	}


	public Bitmap  get(String key,int weight) {
		if(key==null){
			return blanktile;
		}
		Bitmap result=cache.get(key);
		if(result!=null){
			return result;
		}else if(blankSet.contains(key)){
			return blanktile;
		}
		if(keySet.contains(key)||cacheJobs.contains(key)){
			return null;
		}
		File file=new File(cacheRoot, key);
		if(file.exists()){
			cacheJobs.add(key);
		}else{
			if(keySet.add(key)){
				TaskJob job=new TaskJob(key, weight);
				synchronized (taskJobs) {
					int i=taskJobs.size()-1;
					for(;i>-1;i--){
						if(taskJobs.get(i).weigth>job.weigth){
							continue;
						}else{
							break;
						}
					}
					taskJobs.add(i+1, job);
				}
				if(taskJobs.size()>48){
					keySet.remove(taskJobs.firstElement().url);
					taskJobs.remove(taskJobs.firstElement());
				}
			}
		}
		return null;
	} 

	@SuppressLint("NewApi")
	private void readLast(){
		try{
		String	key=cacheJobs.lastElement();
		File cacheFile=new File(cacheRoot, key);
		cacheFile.setLastModified(System.currentTimeMillis());
		Bitmap bitmap=BitmapFactory.decodeFile(cacheFile.getAbsolutePath(), options);
		if(bitmap!=null){
		  cache.put(key,bitmap);
		}else{
		  cache.put(key, blanktile);
		}
		cacheJobs.remove(key);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public synchronized void startWork(){
		if(!cacheJobs.isEmpty()){
			if(isRead==false&&cacheJobs.size()>=3){
				isRead=true;
				new ReadImageThread().start();
			}
			readLast();
			refreshMapView();
//			LogUtils.d("refresh");
		}
		//LogUtils.d("taskSize"+taskJobs.size()+"thread num:"+threadNum);
		if(threadNum<5){
			synchronized (taskJobs) {
				while(threadNum<5&&!taskJobs.isEmpty()){
					TaskJob job=taskJobs.lastElement();
					taskJobs.remove(job);
					threadNum++;
					new FectchThread(job.url).start();
				}
			}
		}
	}

	class ReadImageThread extends Thread{
		@Override
		public void run() {
			while(!cacheJobs.isEmpty()&&cache.get(cacheJobs.lastElement())==null){
				readLast();
			}
			isRead=false;
			refreshMapView();
			startWork();
		}
	}
	
	class FectchThread extends Thread {
		String url;
		public FectchThread(String url) {
			this.url=url;
		}
		@SuppressLint("NewApi")
		@Override
		public void run() {
			try {
				if(System.currentTimeMillis()-lastTime>15000){
					lastTime=System.currentTimeMillis();
					ConnectivityManager mConnectivityManager = (ConnectivityManager) context  
							.getSystemService(Context.CONNECTIVITY_SERVICE);  
					NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
					if (mNetworkInfo != null&&mNetworkInfo.isAvailable()) {  
						netAvailable=true;
					}else{
						netAvailable=false;
					}
				}
				if(netAvailable){
					String[] ss=url.split("@");
					String a_url=prefixMap.get(ss[0]).replace("#",String.valueOf(System.currentTimeMillis()%7))+ss[1].replace("%", "/");
					if(++count%20==0){
						LogUtils.d(count+"Fetch tile:"+a_url);
					}
					URLConnection connection = new URL(a_url).openConnection();
					connection.setConnectTimeout(3600);
					InputStream stream = connection.getInputStream();
					BufferedInputStream in = new BufferedInputStream(stream);
					ByteArrayOutputStream out;
					if(connection.getContentLength()>0){
						out=new ByteArrayOutputStream(connection.getContentLength());
					}else {
						out= new ByteArrayOutputStream();
					}
					int read,contentLength=0;
					byte[] b = new byte[4096];
					while ((read = in.read(b)) != -1) {
						out.write(b, 0, read);
						contentLength+=read;
					}
					out.flush();
					out.close();
					byte[] raw = out.toByteArray();
					if(raw.length>128){
						Bitmap	bitmap=BitmapFactory.decodeByteArray(raw, 0, raw.length,options);
						if(bitmap!=null){
							cache.put(url,bitmap);
							File file=new File(cacheRoot,url);
							FileOutputStream fo = new FileOutputStream(file);
							fo.write(raw);
							fo.flush();
							fo.close();
						}else{
							blankSet.add(url);
						}
					}else {
						LogUtils.d("invalidate data:"+url);
						blankSet.add(url);
					}
				}else{
					LogUtils.d("net not available");
					sleep(5000); //不能联网，等待
				}
			} catch (FileNotFoundException  e) {
				LogUtils.d("FileNotFound:"+url);
				blankSet.add(url);
			}catch (InterruptedException e) {
				Log.d(TAG, "thread sleep failed");
			} catch (SocketTimeoutException  e) {
				LogUtils.d("tile time out");
			}catch (Exception e) {
				LogUtils.d(url);
				e.printStackTrace();
			}finally{
				threadNum--;
			}
			keySet.remove(url);
			refreshMapView();
			startWork();
		}
	}
	
	class TaskJob {
		public final String url;
		public final int weigth;
		public TaskJob(String url,int weight ) {
			this.url=url;
			this.weigth=weight;
		}
	}
	class CacheCleanThread extends Thread{
		@Override
		public void run() {
			int[] daySizes=new int[maxCacheDays];
			int totalSize=0;
			try {
				String[] tiles = cacheRoot.list();
				if(tiles.length<500){
					return;
				}
				sleep(60*1000);
				long minTime=System.currentTimeMillis()-maxCacheDays*ONE_DAY_IN_MILLIS;
				for (int i = 0; i < tiles.length; i++) {
					File file=new File(cacheRoot, tiles[i]);
					long modifyTime=file.lastModified();
					if(modifyTime<minTime){
						file.delete();
					}else{
						daySizes[(int) ((modifyTime-minTime)/ONE_DAY_IN_MILLIS)]+=file.length();
					}
				}
				for(int i=0;i<maxCacheDays;i++){
						totalSize+=daySizes[i];
				}
				if(totalSize<maxCacheSize*1024*1024){
					LogUtils.i("cache size:"+totalSize/1024/1024+"M");
					return;
				}
				int minSize=minCacheSize*1024*1024;
				for(int i=0;i<maxCacheDays;i++){
					totalSize-=daySizes[i];
					if(totalSize<minSize){
						if(i==maxCacheDays-1){
							minTime=System.currentTimeMillis()-ONE_DAY_IN_MILLIS/12;//保留最后2小时的数据
						}else{
							minTime=System.currentTimeMillis()-(maxCacheDays-i-1)*ONE_DAY_IN_MILLIS;
							LogUtils.i("cache size:"+totalSize/1024/1024+"M");
						}
					}
				}
				for (int i = 0; i < tiles.length; i++) {
					File file=new File(cacheRoot, tiles[i]);
					long modifyTime=file.lastModified();
					if(modifyTime<minTime){
						file.delete();
					}
				}
				LogUtils.i("cache clear finished");
			} catch (Throwable t) {
				Log.e(TAG, "Exception cleaning cache", t);
			}
		}
	}
}