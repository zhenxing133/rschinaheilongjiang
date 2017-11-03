package com.chinars.mapapi.utils;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ResourseUtil {
	/** 
	 * 根据资源的名字获取其ID值 
	 * @author mining 
	 * 
	 */ 
	private static BitmapFactory.Options options;
	static{
		options=new BitmapFactory.Options();
	    options.inPreferredConfig=Config.RGB_565;
		options.inDensity=DisplayMetrics.DENSITY_XHIGH;
		options.inTargetDensity=DisplayMetrics.DENSITY_DEFAULT;;
	}
	 public static int getIdByName(Context context, String className, String name) {  
	        String packageName = context.getPackageName();  
	        Class r = null;  
	        int id = 0;  
	        try {  
	            r = Class.forName(packageName + ".R");  
	            Class[] classes = r.getClasses();  
	            Class desireClass = null;  
	  
	            for (int i = 0; i < classes.length; ++i) {  
	                if (classes[i].getName().split("\\$")[1].equals(className)) {  
	                    desireClass = classes[i];  
	                    break;  
	                }  
	            }  
	  
	            if (desireClass != null)  
	                id = desireClass.getField(name).getInt(desireClass);  
	        } catch (ClassNotFoundException e) {  
	            e.printStackTrace();  
	        } catch (IllegalArgumentException e) {  
	            e.printStackTrace();  
	        } catch (SecurityException e) {  
	            e.printStackTrace();  
	        } catch (IllegalAccessException e) {  
	            e.printStackTrace();  
	        } catch (NoSuchFieldException e) {  
	            e.printStackTrace();  
	        }  
	        return id;  
	    }  
	
	 
	 
	 
	 
	    /** 
	     * 读取指定asset目录中的图片文件为 Drawable 
	     * 
	     * @param context 
	     * @param imageFileName 
	     * @return null if exception happened. 
	     */  
	    public static Drawable getDrawableFromAssets(Context context,  
	            String imageFileName) {  
	        Drawable result = null;  
	        AssetManager assetManager = context.getAssets();  
	        InputStream is = null;  
	        try {  
	            is = assetManager.open(imageFileName);  
	            Bitmap bitmap=BitmapFactory.decodeStream(is);
	            if(bitmap==null){
	            	LogUtils.d("bitmap can't be created");
	            }else{
	              result = new BitmapDrawable(context.getResources(), bitmap);
	              result.setBounds(0,0,result.getIntrinsicWidth(),result.getIntrinsicHeight());
	            }
	            is.close();  
	            is = null;
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        return result;  
	    }  
	    
	    
	    /** 
	     * 读取指定asset目录中的图片文件为 Drawable 
	     * 
	     * @param context 
	     * @param imageFileName 
	     * @return null if exception happened. 
	     */  
	    public static Drawable getDrawableFromAssetsForDp(Context context,  
	            String imageFileName) {  
	        Drawable result = null;  
	        AssetManager assetManager = context.getAssets();  
	        InputStream is = null;  
	        try {  
	            is = assetManager.open(imageFileName);
	            Bitmap bitmap=BitmapFactory.decodeStream(is, null, options);
	            if(bitmap==null){
	            	LogUtils.d("bitmap can't be created");
	            }else{
	            	result = new BitmapDrawable(context.getResources(), bitmap);
	            	result.setBounds(0,0,result.getIntrinsicWidth(),result.getIntrinsicHeight());
	            }
	            is.close();  
	            is = null;
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        return result;  
	    }  
	    
	    
	    /** 
	     * 读取指定asset目录中的图片文件为 Bitmap 
	     * 
	     * @param context 
	     * @param imageFileName 
	     * @return null if exception happened. 
	     */  
	    public static Bitmap getBitmapFromAssets(Context context,  
	            String imageFileName) {  
	    	Bitmap result = null;  
	        AssetManager assetManager = context.getAssets();  
	        InputStream is = null;  
	        try {  
	            is = assetManager.open(imageFileName);  
	            result=BitmapFactory.decodeStream(is);
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        return result;  
	    }  
	    
	    public static int dip2px(Context context, float dipValue)

		{
			float m=context.getResources().getDisplayMetrics().density ;
			return (int)(dipValue * m + 0.5f) ;
		}



		public static int px2dip(Context context, float pxValue)

		{
			float m=context.getResources().getDisplayMetrics().density ;
			return (int)(pxValue / m + 0.5f) ;
		}
}
