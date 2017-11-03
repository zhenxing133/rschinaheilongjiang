package com.chinars.mapapi.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.os.Environment;
import android.os.StatFs;

public class CommonUtils {


    /**  
     * 获取外置SD卡路径  
     *   
     * @return  
     */  
    public static String getSDCard2() {  
        File sdfile = Environment.getExternalStorageDirectory();
        File parentFile = sdfile.getParentFile();
      //列出该父目录下的所有路径
        File[] listFiles = parentFile.listFiles();
        long size=getAllorFreeSize(sdfile.getPath(), true);
        for (int i = 0; i < listFiles.length; i++) 
        {
        	if (listFiles[i].canWrite())
        	{
        		 String path=listFiles[i].toString();
        		 if(getAllorFreeSize(path, true)!=size){
        			 return path;
        		 }
        	}
        } 
        return null;  
    } 
    
    /**
	    * 使用文件通道的方式复制文件
	    * 
	    * @param s
	    *            源文件
	    * @param t
	    *            复制到的新文件
	    */

	public static void fileCopy(File s, File t) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		BufferedInputStream bin=null;
		BufferedOutputStream bout=null;
		try {
			fi = new FileInputStream(s);
			fo = new FileOutputStream(t);
			bin=new BufferedInputStream(fi);
			bout=new BufferedOutputStream(fo);
			byte[] buffer=new byte[8192];
			int len;
			while((len=bin.read(buffer))!=-1){
				bout.write(buffer, 0, len);
			}
			bout.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				bin.close();
				bout.close();
				fi.close();
				fo.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String sizeConvert(long bytes) {
		float temp = bytes;
		String[] units = {"B", "KB", "MB", "GB"};
		int i = 0;
		while(temp/1024 > 1) {
			temp = temp/1024;
			i++;
		}
		String str = String.valueOf(temp);
		String[] strs = {String.valueOf((int) temp), String.valueOf(temp-(int)temp)};
		if(strs[0].length()>2) {
			str = strs[0]+" ";
		} else if(strs[0].length()<3 && str.length()>3) {
			str = str.substring(0, 4);
		}
		return str+units[i];
	}
	
	/**
	 * SDCard剩余空间
	 * @return
	 */
	public static long getAllorFreeSize(String sdPath,boolean all) {
		StatFs sf = new StatFs(sdPath);
		// 获取单个数据块大小(Byte)
		long blockSize = sf.getBlockSize();
		if(all){
			long blockCount = sf.getBlockCount();
			return (blockCount * blockSize);
		}else{
			long freeBlocks = sf.getAvailableBlocks();
			return (freeBlocks * blockSize);
		}
	} 
	
	public static long getDirSize(File dir) {
		if (dir == null) {
			return 0;
		}
		if (!dir.isDirectory()) {
			return 0;
		}
		long dirSize = 0;
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isFile()) {
				dirSize += file.length();
			} else if (file.isDirectory()) {
				dirSize += getDirSize(file); // 如果遇到目录则通过递归调用继续统计
			}
		}
		return dirSize;
	}
	
	public static void copyFolder(String oldPath, String newPath){
		try{
			(new File(newPath)).mkdirs(); //如果文件夹不存在 则建立新文件夹
			File olderDir=new File(oldPath);
			String[] files=olderDir.list();
			for (int i = 0; i < files.length; i++) {
				File in=new File(olderDir, files[i]);
				if(in.isFile()){ 
					File out=new File(newPath,files[i]);
					fileCopy(in,out);
				}
				if(in.isDirectory()){//如果是子文件夹
					copyFolder(oldPath+"/"+files[i],newPath+"/"+files[i]);
				} 
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	
	
    /**
     * 删除目录（文件夹）以及目录下的文件
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        //删除文件夹下的所有文件(包括子目录)
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文件
            if (files[i].isFile()) {
               files[i].delete();
            } //删除子目录
            else {
                deleteDirectory(files[i].getAbsolutePath());
            }
        }
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }
	
	/** 
	* 移动文件 
	* @param srcFileName 	源文件完整路径
	* @param destDirName 	目的目录完整路径
	* @return 文件移动成功返回true，否则返回false 
	*/  
	public static boolean moveFile(String srcFileName, String destDirName) {
		
		File srcFile = new File(srcFileName);
		if(!srcFile.exists() || !srcFile.isFile()) 
		    return false;
		
		File destDir = new File(destDirName);
		if (!destDir.exists())
			destDir.mkdirs();
		File distFile=new File(destDirName, srcFile.getName());
		fileCopy(srcFile, distFile);
		return srcFile.delete();
	}
	
	/** 
	* 移动目录 
	* @param srcDirName 	源目录完整路径
	* @param destDirName 	目的目录完整路径
	* @return 目录移动成功返回true，否则返回false 
	*/  
	public static boolean moveDirectory(String srcDirName, String destDirName) {
		
		File srcDir = new File(srcDirName);
		if(!srcDir.exists() || !srcDir.isDirectory())  
			return false;  
	   
	   File destDir = new File(destDirName);
	   if(!destDir.exists())
		   destDir.mkdirs();
	   
	   /**
	    * 如果是文件则移动，否则递归移动文件夹。删除最终的空源文件夹
	    * 注意移动文件夹时保持文件夹的树状结构
	    */
	   File[] sourceFiles = srcDir.listFiles();
	   for (File sourceFile : sourceFiles) {
		   if (sourceFile.isFile())
			   moveFile(sourceFile.getAbsolutePath(), destDir.getAbsolutePath());
		   else if (sourceFile.isDirectory())
			   moveDirectory(sourceFile.getAbsolutePath(), 
					   destDir.getAbsolutePath() + File.separator + sourceFile.getName());
		   else
			   ;
	   }
	   return srcDir.delete();
	}
	
}
