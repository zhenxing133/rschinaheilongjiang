package com.chinars.mapapi;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.chinars.mapapi.utils.IntSet;
import com.chinars.mapapi.utils.LogUtils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

public class OfflineMapFileManager {
	
	private String root;
	private Bitmap blanktile;
	private Bitmap placeholder;
	private SparseArray<Bitmap> data=new SparseArray<Bitmap>(32);
	private SparseArray<String> fileSet=new SparseArray<String>(128);
	private IntSet keys=new IntSet();
	private MapView mapView;
	Vector<Integer> loadTask=new Vector<Integer>();
	private BitmapFactory.Options options;
	private Resources res=null;
	private final int[][] indTable=new int[4][16];
	private int foldNum=0;
	private boolean available=false;
	private boolean isDone=true;
	private static int workNum=0;
//	static{
//		System.loadLibrary("rscloudsmap");
//	}
	
	
	public OfflineMapFileManager(String aRoot,MapView mapView,Bitmap holder){
		this.root=aRoot;
		this.mapView=mapView;
		this.res=mapView.getResources();
		this.placeholder=holder;
		options=new BitmapFactory.Options();
		options.inPreferredConfig=Config.RGB_565;
		File file=new File(root);
		if(!file.exists()){
			LogUtils.i(aRoot+" not exists");
			available=false;
		}else{
			File[] dirs=file.listFiles();
			if(dirs.length==0){
				available=false;
			}else{
				available=true;
			}
 		}
		blanktile=Bitmap.createBitmap(256, 256, Config.ALPHA_8);
		for(int i=0;i<4;i++){
 			for(int j=0;j<16;j++){
 				int x=i%2*4+j%4;
 				int y=i/2*4+j/4;
 				indTable[i][j]=y*8+x;
 			}
 		}
	}
	
//	public  static void testJNI(){
//		int x=0,y=0,z=0;
//		int urid=0;
//		IntSet set=new IntSet();
//		for(z=0;z<19;z++){
//			for(x=0;x<4000;x++){
//				for(y=0;y<4000;y++){
//					urid=getTileNumURI(x, y, z);
//					if(!set.add(urid/100)){
//						throw new RuntimeException("exists file");
//					}
//				}
//			}
//		}
//	
//	}
	
	public boolean isAvailable(){
		return available;
	}
	
	public void setRootDir(String root){
		this.root=root;
	}
	
	public String getRootDir(){
		return root;
	}
	
	public void clearData(){
		data.clear();
	}
	public static boolean isDone(){
	  return workNum==0;
	}

	public Bitmap getDrawable(int urid){
		Bitmap result=data.get(urid);
		if(result==null){
			int id=(urid/100)*10+((urid%100)>>2&1)+((urid%100)>>4&2);
			if(keys.add(id)){
				loadTask.add(id);
				if(isDone&&!loadTask.isEmpty()){
					//new LoadTask().execute();
					new LoadThread().start();
						workNum++;
						isDone=false;
				}
			}else if(fileSet.get(id,"unknown").length()<1){
				return null;
			}
			return placeholder;
		}else if(result==placeholder){
			return null;
		}
		return result;
	}
	
	public Bitmap getBitmapSync(int urid){
		Bitmap result=data.get(urid);
		if(result==null){
			int id=(urid/100)*10+((urid%100)>>2&1)+((urid%100)>>4&2);
			if(keys.add(id)){
				loadTask.add(id);
				if(data.size()>64){
					data.clear();
					LogUtils.d("full");
				}
				try{
					File file;
					String fileName=fileSet.get(id);
					if(fileName==null){
						int foldId=id/1000000;
						int fileId=id%1000000;
						file=new File(root+"/"+foldId,fileId/10+".dat");
						if(file.exists()){
							fileSet.put(id, file.getPath());
						}else{
							fileSet.put(id, "");
							return null;
						}
					}else{
						file=new File(fileName);
					}
					int index=id%10;
					RandomAccessFile din=new RandomAccessFile(file, "r");
					long pos=0,fileLen=din.length();
					for(int i=0;i<index&&pos<fileLen;i++){  /** 寻位**/
						int lineLen=din.readInt();
						if(lineLen>0){
							pos=pos+lineLen+4;
							din.seek(pos);
						}else{
							pos=pos+4;
						}
					}
					long readLen=din.readInt();
					pos=pos+4;
					if(pos+readLen>fileLen){
						LogUtils.d("文件错误");
						din.close();
						return null;
					}else if(readLen==0){
						fileSet.put(id, "");
					}
					readLen=pos+readLen;
					boolean[] hasData=new boolean[16];
					int loaded=0;
					while(pos<readLen){
						int mark=din.readInt();
						int tileInd=mark/1000000;
						int tileLen=mark%1000000;
						if(tileLen>0){
							hasData[(tileInd>>1&12)+(tileInd&3)]=true;
							byte[] drawable=new byte[tileLen];
							din.readFully(drawable);
							Bitmap bm=BitmapFactory.decodeByteArray(drawable, 0, tileLen,options);
							data.put((id/10)*100+tileInd, bm);
							loaded++;
						}else{
							data.put((id/10)*100+tileInd, blanktile);
						}
						pos=pos+tileLen+4;
					}
					if(loaded<16){
						for(int i=0;i<16;i++){
							if(!hasData[i]){
								data.put((id/10)*100+indTable[index][i], placeholder);
							}
						}
					}
					keys.remove(id);
					din.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				return data.get(urid);
			}else if(fileSet.get(id,"unknown").length()<1){
				return null;
			}
		}
		return result;
	}
	
	
 class	LoadThread	extends Thread{
	 @Override
	public void run() {
		// LogUtils.d("start");
		 try{
				if(data.size()>64){
					data.clear();
					LogUtils.d("full");
				}
				while(!loadTask.isEmpty()){
					Integer id=loadTask.lastElement();
					loadTask.remove(id);
					File file;
					String fileName=fileSet.get(id);
					if(fileName==null){
						int foldId=id/1000000;
						int fileId=id%1000000;
						file=new File(root+"/"+foldId,fileId/10+".dat");
						if(file.exists()){
							fileSet.put(id, file.getPath());
						}else{
							fileSet.put(id, "");
							continue;
						}
					}else{
						file=new File(fileName);
					}
					LogUtils.d(file.getPath());
					int index=id%10;
					RandomAccessFile din=new RandomAccessFile(file, "r");
					long pos=0,fileLen=din.length();
					for(int i=0;i<index&&pos<fileLen;i++){  /** 寻位**/
						int lineLen=din.readInt();
						if(lineLen>0){
							pos=pos+lineLen+4;
							din.seek(pos);
						}else{
							pos=pos+4;
						}
					}
					long readLen=din.readInt();
					pos=pos+4;
					if(pos+readLen>fileLen){
						LogUtils.d("文件错误");
						din.close();
						return ;
					}else if(readLen==0){
						fileSet.put(id, "");
						continue;
					}
					readLen=pos+readLen;
					boolean[] hasData=new boolean[16];
					int loaded=0;
					while(pos<readLen){
						int mark=din.readInt();
						int tileInd=mark/1000000;
						int tileLen=mark%1000000;
						if(tileLen>0){
							hasData[(tileInd>>1&12)+(tileInd&3)]=true;
							byte[] drawable=new byte[tileLen];
							din.readFully(drawable);
							Bitmap bm=BitmapFactory.decodeByteArray(drawable, 0, tileLen,options);
							data.put((id/10)*100+tileInd, bm);
							loaded++;
						}else{
							data.put((id/10)*100+tileInd, blanktile);
						}
						pos=pos+tileLen+4;
					}
					if(loaded<16){
						for(int i=0;i<16;i++){
							if(!hasData[i]){
								data.put((id/10)*100+indTable[index][i], placeholder);
							}
						}
					}
					mapView.refresh();
					keys.remove(id);
					din.close();
				}
				mapView.refresh();
			}catch (Exception e) {
				e.printStackTrace();
			}
			isDone=true;
			workNum--;
			return;
	}
 }
	
//	public static native int getTileNumURI(int x,int y,int z);
	
	class LoadTask extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected Void doInBackground(Void... params) {
			try{
				if(data.size()>48){
					data.clear();
					LogUtils.d("full");
				}
				while(!loadTask.isEmpty()){
					Integer id=loadTask.lastElement();
					LogUtils.d(root+":"+id);
					loadTask.remove(id);
					File file;
					if(id/10==28841){
						LogUtils.d("dd");
					}
					String fileName=fileSet.get(id);
					if(fileName==null){
						int foldId=id/1000000;
						int fileId=id%1000000;
						file=new File(root+"/"+foldId,fileId/10+".dat");
						if(file.exists()){
							fileSet.put(id, file.getPath());
						}else{
							fileSet.put(id, "");
							continue;
						}
					}else{
						file=new File(fileName);
					}
					int index=id%10;
					RandomAccessFile din=new RandomAccessFile(file, "r");
					long pos=0,fileLen=din.length();
					for(int i=0;i<index&&pos<fileLen;i++){  /** 寻位**/
						int lineLen=din.readInt();
						if(lineLen>0){
							pos=pos+lineLen+4;
							din.seek(pos);
						}else{
							pos=pos+4;
						}
					}
					long readLen=din.readInt();
					pos=pos+4;
					if(pos+readLen>fileLen){
						LogUtils.d("文件错误");
						return null;
					}else if(readLen==0){
						fileSet.put(id, "");
						continue;
					}
					readLen=pos+readLen;
					boolean[] hasData=new boolean[16];
					int loaded=0;
					while(pos<readLen){
						int mark=din.readInt();
						int tileInd=mark/1000000;
						int tileLen=mark%1000000;
						if(tileLen>0){
							hasData[(tileInd>>1&12)+(tileInd&3)]=true;
							byte[] drawable=new byte[tileLen];
							din.readFully(drawable);
							Bitmap bm=BitmapFactory.decodeByteArray(drawable, 0, tileLen,options);
							data.put((id/10)*100+tileInd, bm);
							loaded++;
						}else{
							data.put((id/10)*100+tileInd, blanktile);
						}
						pos=pos+tileLen+4;
					}
					if(loaded<16){
						for(int i=0;i<16;i++){
							if(!hasData[i]){
								data.put((id/10)*100+indTable[index][i], placeholder);
							}
						}
					}
					mapView.refresh();
					keys.remove(id);
					din.close();
				}
				mapView.refresh();
			}catch (Exception e) {
				e.printStackTrace();
			}
			isDone=true;
			workNum--;
			return null;
		}
	}
}
