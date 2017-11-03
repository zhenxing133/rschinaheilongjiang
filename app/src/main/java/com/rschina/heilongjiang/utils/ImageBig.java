package com.rschina.heilongjiang.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;

import com.rschina.heilongjiang.model.FileCache;

/**
 * 压缩图片的工具
 * 
 */
public class ImageBig {

	/**
	 * 压缩图片
	 * 
	 * @param context
	 * @param filename
	 * @param maxWidth
	 * @param maxHeight
	 * @return
	 */
	public static String scalePicture(Context context, String filename, int maxWidth, int maxHeight) {

		String url = "";
		Bitmap bitmap = null;
		Bitmap bitmap2 = null;
		try {
			Options opts = new Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filename, opts);
			int srcWidth = opts.outWidth;
			int srcHeight = opts.outHeight;
			if (srcWidth <= maxWidth && srcHeight <= maxHeight) {
				return filename;
			}
			int desWidth = 0;
			int desHeight = 0;
			double ratio = 0.0;
			if (srcWidth > srcHeight) {
				ratio = srcWidth / maxWidth;
				desWidth = maxWidth;
				desHeight = (int) (srcHeight / ratio);
			} else {
				ratio = srcHeight / maxHeight;
				desHeight = maxHeight;
				desWidth = (int) (srcWidth / ratio);
			}
			Options newOpts = new Options();
			newOpts.inSampleSize = (int) (ratio) + 1;
			newOpts.inJustDecodeBounds = false;
			newOpts.outWidth = desWidth;
			newOpts.outHeight = desHeight;
			bitmap = BitmapFactory.decodeFile(filename, newOpts);
			// 读取图片旋转角度
			final int angle = PickPhoto.readPictureDegree(filename);
			bitmap2 = PickPhoto.rotaingImageView(angle, bitmap);
			url = new FileCache(context).getImageCacheDir().getAbsolutePath() + "/M" + System.currentTimeMillis() + ".jpg";
			FileOutputStream out = new FileOutputStream(url);
			if (bitmap2.compress(Bitmap.CompressFormat.JPEG, 70, out)) {
				out.flush();
				out.close();
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
			}
			if (bitmap2 != null && !bitmap2.isRecycled()) {
				bitmap2.recycle();
			}
		}
		return url;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	public final static Bitmap lessenUriImage(String path) {
		Options options = new Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options); // 此时返回 bm 为空
		options.inJustDecodeBounds = false; // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = (int) (options.outHeight / (float) 320);
		if (be <= 0)
			be = 1;
		options.inSampleSize = be; // 重新读入图片，注意此时已经把 options.inJustDecodeBounds
									// 设回 false 了
		bitmap = BitmapFactory.decodeFile(path, options);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		System.out.println(w + " " + h); // after zoom
		return bitmap;
	}

	public static Bitmap getimage(String srcPath) {
		Options newOpts = new Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 1280f;// 这里设置高度为800f
		float ww = 720f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	private static Bitmap compressImage(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	public static Bitmap stringtoBitmap(String string) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}
	
	/**
	 * 图片质量压缩 把图片压缩到100kb
	 * 
	 * @param image
	 * @return
	 */
	public static String compressImageToBase64(String imageUrl) {
		if (TextUtils.isEmpty(imageUrl)) {
			return null;
		}
		// 按照宽高比 480*800的比例，从sdcard上取的图片
		Bitmap bm = BitmapUtil.getBitmap(imageUrl, 480, 800);

		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
			bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			int options = 90;
			// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			while (baos.toByteArray().length / 1024 > 100) {
				options -= 10;// 每次都减少10

				baos.reset();// 重置baos即清空baos

				// 这里压缩options%，把压缩后的数据存放到baos中
				bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
			}
			// 把压缩后的数据baos存放到ByteArrayInputStream中
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());
			// 把ByteArrayInputStream数据生成图片
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);

//			savaBitmap("temp.jpg", bitmap);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				baos.flush();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		String uploadBuff = new String(Base64.encode(baos.toByteArray(),Base64.DEFAULT));

		return uploadBuff;

	}

	
	/**
	 * 图片质量压缩 把图片压缩到100kb
	 * 
	 * @param image
	 * @return
	 */
	public static String compressImageToBase64(Context context,
			List<String> imageUrls) {
		if (imageUrls == null || imageUrls.isEmpty()) {
			return "";
		}

		String uploadBuff = "";
		for (String imageUrl : imageUrls) {
			uploadBuff += compressImageToBase64(imageUrl) + ",";
		}

		return uploadBuff;

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

	/**
	 * 把Bitmap转Byte
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		return baos.toByteArray();
	}

}
