package com.rschina.heilongjiang.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.graphics.Matrix;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import android.graphics.BitmapFactory.Options;
import com.android.volley.toolbox.ImageLoader;

public class BitmapUtil {
	
	public static Bitmap  getBitmap(Context context,int resId){
		Options options=new Options();
		options.inPreferredConfig=Config.RGB_565;
		options.inDensity=DisplayMetrics.DENSITY_DEFAULT;
		options.inTargetDensity=DisplayMetrics.DENSITY_DEFAULT;
		Bitmap bitmap=BitmapFactory.decodeResource(context.getResources(), resId,options);
		return bitmap;
	}

	public static ImageLoader.ImageListener getImageListener(ImageView imageView,int drawableId){
		return  ImageLoader.getImageListener(imageView,drawableId,drawableId);
	}

	/***
	 * 图片的缩放方法
	 *
	 * @param bgimage
	 *            ：源图片资源
	 * @param newWidth
	 *            ：缩放后宽度
	 * @param newHeight
	 *            ：缩放后高度
	 * @return
	 */
	public static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
		// 获取这个图片的宽和高
		float width = bgimage.getWidth();
		float height = bgimage.getHeight();
		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();
		// 计算宽高缩放率
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width, (int) height, matrix, true);
		return bitmap;
	}

	public final static Bitmap returnBitMap(String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
			HttpURLConnection conn;
			conn = (HttpURLConnection) myFileUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			bitmap = BitmapFactory.decodeStream(is);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 从指定路径 按指定宽高 保持纵横比收缩加载图片
	 *
	 * @param filePath
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmap(String filePath, int width, int height) {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, opts);
		int x = opts.outWidth / width;
		int y = opts.outHeight / height;
		int scale = x > y ? x : y;
		return getBitmap(filePath, scale);
	}

	/**
	 * 从指定路径 按规定收缩比例加载图片
	 *
	 * @param filePath
	 * @param scale
	 * @return
	 */
	public static Bitmap getBitmap(String filePath, int scale) {
		Options opts = new Options();
		opts.inSampleSize = scale;
		return BitmapFactory.decodeFile(filePath, opts);
	}

}
