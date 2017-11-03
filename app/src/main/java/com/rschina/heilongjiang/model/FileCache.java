package com.rschina.heilongjiang.model;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.content.Context;
import android.os.Environment;

public class FileCache {

	static DataOutputStream dos;

	private File cacheDir;

	private File fileCacheDir;

	private File audioCacheDir;

	private File imageCacheDir;

	private File chatImageCacheDir;

	// 缓存目录
	public static final String DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/rsnews/";

	public File getCacheDir() {
		return cacheDir;
	}

	public File getFileCacheDir() {
		fileCacheDir = new File(cacheDir, "/file/");
		if (!fileCacheDir.exists())
			fileCacheDir.mkdirs();
		return fileCacheDir;
	}

	public File getAudioCacheDir() {
		audioCacheDir = new File(cacheDir, "/audio/");
		if (!audioCacheDir.exists())
			audioCacheDir.mkdirs();
		return audioCacheDir;
	}

	public File getImageCacheDir() {
		imageCacheDir = new File(cacheDir, "/image/");
		if (!imageCacheDir.exists())
			imageCacheDir.mkdirs();
		return imageCacheDir;
	}

	public File getImageCacheDirPath() {
		chatImageCacheDir = new File(cacheDir, "/image/chat/");
		if (!chatImageCacheDir.exists())
			chatImageCacheDir.mkdirs();
		return chatImageCacheDir;
	}

	public FileCache(Context context) {

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

			cacheDir = new File(DIR, "/cache");
		} else {

			cacheDir = context.getCacheDir();
		}
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

	public File getFile(String url) {
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		for (File f : files)
			f.delete();
	}

	public static long getAmrDuration(File file) throws IOException {
		long duration = -1;
		int[] packedSize = { 12, 13, 15, 17, 19, 20, 26, 31, 5, 0, 0, 0, 0, 0, 0, 0 };
		RandomAccessFile randomAccessFile = null;
		try {
			randomAccessFile = new RandomAccessFile(file, "rw");
			long length = file.length();// 文件的长度
			int pos = 6;// 设置初始位置
			int frameCount = 0;// 初始帧数
			int packedPos = -1;
			// ///////////////////////////////////////////////////
			byte[] datas = new byte[1];// 初始数据值
			while (pos <= length) {
				randomAccessFile.seek(pos);
				if (randomAccessFile.read(datas, 0, 1) != 1) {
					duration = length > 0 ? ((length - 6) / 650) : 0;
					break;
				}
				packedPos = (datas[0] >> 3) & 0x0F;
				pos += packedSize[packedPos] + 1;
				frameCount++;
			}
			// ///////////////////////////////////////////////////
			duration += frameCount * 20;// 帧数*20
		} finally {
			if (randomAccessFile != null) {
				randomAccessFile.close();
			}
		}
		return duration;
	}
}