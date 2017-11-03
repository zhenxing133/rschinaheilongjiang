package com.rschina.heilongjiang.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

/**
 * 调用相册或相机工具
 * 
 * @author wangxiaojie
 */
public class PickPhoto {

	// 选择图片
	public static final int PICK_PHOTO = 3021;

	// 照相
	public static final int TAKE_PHOTO = 3022;

	// 裁剪图片
	public static final int CROP_PHOTO = 3023;

	// 裁剪图片
	public static final int CROP_TAKE = 3024;

	// 裁剪输出宽度和高度
	public static final int OUTPUTWIDTH = 200;

	public static final int OUTPUTHEIGHT = 200;

	/* 表示从相册裁剪图片的activity */
	public static final int PHOTO_CROP_PHOTO = 3020;

	private static File restFile; // 图片文件

	public static File getRestFile() {
		return restFile;
	}

	/**
	 * 调用拍照
	 */
	public static void takePhoto(Activity activity, String output) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(output)));
		activity.startActivityForResult(intent, TAKE_PHOTO);
	}

	/**
	 * 调用相册选择图片并裁剪
	 */
	public static void pickPhotoAndCrop(Activity activity, String output) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		intent.putExtra("output", Uri.fromFile(new File(output)));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", OUTPUTWIDTH);
		intent.putExtra("outputY", OUTPUTHEIGHT);
		activity.startActivityForResult(intent, CROP_PHOTO);
	}

	/**
	 * 调用相册选择图片并裁剪
	 */
	public static void pickPhotoAndCrop(Activity activity, String output, int width, int height) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		intent.setType("image/*");
		intent.putExtra("output", Uri.fromFile(new File(output)));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", height);
		activity.startActivityForResult(intent, CROP_PHOTO);
	}

	/** 拍照后剪切 */
	public static void getCropIntent(Activity activity, String path, int width, int height) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(Uri.fromFile(new File(path)), "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", height);
		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, CROP_TAKE);
	}

	/**
	 * 调用相册选择图片并返回
	 */
	public static void pickPhoto(Activity activity) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		activity.startActivityForResult(intent, PICK_PHOTO);
	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 读取图片的旋转角度
	 * 
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		if (!TextUtils.isEmpty(path)) {
			try {
				ExifInterface exif = new ExifInterface(path);
				int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
				switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return degree;
	}

	/**
	 * 从相册裁剪图片
	 */
	public static void cropRestPhotos(Activity activity, File photoUri,String OUTPUTFILEPATH) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(Uri.fromFile(photoUri), "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", OUTPUTWIDTH);
		intent.putExtra("outputY", OUTPUTHEIGHT);
		intent.putExtra("return-data", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, new File(OUTPUTFILEPATH));
		// setIntentExtra(intent);
		activity.startActivityForResult(intent, PHOTO_CROP_PHOTO);
	}

	public static void cropRestPhoto(Activity activity) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(Uri.fromFile(getRestFile()), "image/*");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getRestFile()));
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", OUTPUTWIDTH);
		intent.putExtra("outputY", OUTPUTHEIGHT);
		intent.putExtra("return-data", true);
		activity.startActivityForResult(intent, CROP_PHOTO);
	}

	public static void setRestFile() {
		File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
		long time = System.currentTimeMillis();
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG_'yyyyMMdd");
		String name = String.valueOf(time);
		name = dateFormat.format(new Date(time)) + "_" + name.substring(name.length() - 6, name.length()) + ".jpg";
		try {
			restFile = new File(file, name);
			if (!restFile.exists())
				restFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用拍照
	 */
	public static void callPhoto(Activity activity, String output) {
		setRestFile();
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getRestFile()));
		activity.startActivityForResult(intent, TAKE_PHOTO);
	}
}
