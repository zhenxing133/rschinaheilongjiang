package com.chinars.mapapi.offline;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
 









import com.chinars.mapapi.utils.LogUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.location.GpsStatus.Listener;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
 
public class ZipExtractor extends Thread {
    private final String TAG = "ZipExtractorTask";
    private  File mInput;
    private  File mOutput;
    private int mProgress = 0;
    private ZipExtractorProgressListener listener;
    private static Handler handler=new Handler(Looper.getMainLooper());
    private  File indexFile;
    public ZipExtractor(OfflineMapDownloadInfo downInfo){
    	if(downInfo.status!=OfflineMapDownloadInfo.FINISHED){
    		LogUtils.e("Not FINISHED");
    		return;
    	}
        mInput = new File(RSOfflineMap.getTempDir(null)+"/"+downInfo.mapName+"-"+downInfo.cityName+".zip");
        LogUtils.i(mInput.getPath());
        if(!mInput.exists()){
        	mInput=null;
        	LogUtils.e("File not Find");
        	return;
        }
        mOutput = new File(RSOfflineMap.getDataRoot(null));
        if(!mOutput.exists()){
            if(!mOutput.mkdirs()){
                Log.e(TAG, "Failed to make directories:"+mOutput.getAbsolutePath());
            }
        }
        indexFile=new File(RSOfflineMap.getDataRoot(null),downInfo.cityID+".idx");
    }
  
    public void setZipExtractorProgressListener(ZipExtractorProgressListener listener){
    	this.listener=listener;
    }
   
    @Override
    public void run() {
    	try {
			sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	unzip();
    }
    
    @SuppressWarnings("unchecked")
	private long unzip(){
        long extractedSize = 0L;
        if(mInput==null){
        	return 0;
        }
        Enumeration<ZipEntry> entries;
        ZipFile zip = null;
        try {
            zip = new ZipFile(mInput);
            long originSize=getOriginalSize(zip);
            entries = (Enumeration<ZipEntry>) zip.entries();
            BufferedWriter idxOut=new BufferedWriter(new FileWriter(indexFile));
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(entry.isDirectory()){
                    continue;
                }
                File destination = new File(mOutput, entry.getName());
                if(!destination.getParentFile().exists()){
                    Log.e(TAG, "make="+destination.getParentFile().getAbsolutePath());
                    destination.getParentFile().mkdirs();
                }
                if(destination.exists()){
                	if(destination.length()<entry.getSize()){
                		destination.delete();
                	}else{
                		extractedSize+=destination.length();
                		continue;
                	}
                }
                OutputStream outStream = new FileOutputStream(destination);
                idxOut.write(destination.getAbsolutePath());
                idxOut.newLine();
                extractedSize+=copy(zip.getInputStream(entry),outStream);
                destination.getAbsolutePath();
                outStream.close();
                mProgress=(int) (extractedSize*100/originSize);
                if(listener!=null){
                	 handler.post(new Runnable() {
						@Override
						public void run() {
							listener.onProgressUpdate(mProgress);
						}
					});
                }
            }
            idxOut.flush();
            idxOut.close();
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                zip.close();
                mInput.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(listener!=null){
        	handler.post(new Runnable() {
        		@Override
        		public void run() {
        			listener.onFinish();
        		}
        	});
        }
        return extractedSize;
    }
 
    public long getOriginalSize(ZipFile file){
        Enumeration<ZipEntry> entries = (Enumeration<ZipEntry>) file.entries();
        long originalSize = 0l;
        while(entries.hasMoreElements()){
            ZipEntry entry = entries.nextElement();
            if(entry.getSize()>=0){
                originalSize+=entry.getSize();
            }
        }
        return originalSize;
    }
     
    private int copy(InputStream input, OutputStream output){
        byte[] buffer = new byte[1024*8];
        BufferedInputStream in = new BufferedInputStream(input, 1024*8);
        BufferedOutputStream out  = new BufferedOutputStream(output, 1024*8);
        int count =0,n=0;
        try {
            while((n=in.read(buffer, 0, 1024*8))!=-1){
                out.write(buffer, 0, n);
                count+=n;
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return count;
    }
     
    
  public  interface ZipExtractorProgressListener {
	  	void onProgressUpdate(int progress);
    	void onFinish();
    }
}
