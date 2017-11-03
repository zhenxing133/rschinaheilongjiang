package com.rschina.heilongjiang.utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Created by Administrator on 2016/4/1.
 */
public class DebugUtil implements Thread.UncaughtExceptionHandler{
    private final static String DIR_NAME="2debug";
    private static Toast mToast;
    private static Handler mHandler = new Handler(Looper.getMainLooper());
    private static Context mContext;
    public static boolean allowD = true;
    public static boolean allowToast=true;
    public static boolean allowRun=false;
    public static File logFile;
    private static SimpleDateFormat myLogSdf = new SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss");// 日志的输出格式
    private static Thread.UncaughtExceptionHandler mDefaultHandler;
    private static long lastLogTime=0;


    public  static void init(Context context){
        mContext=context;
        mDefaultHandler=Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new DebugUtil());
        File dir=new File(Environment.getExternalStorageDirectory(),DIR_NAME);
        if(!dir.exists()){
            dir.mkdir();
        }
        logFile=new File(dir,mContext.getPackageName()+" "+myLogSdf.format(new Date()).split(" ")[0]+".txt");
    }

    private static Runnable showAgain = new Runnable() {
        public void run() {
            if(mToast!=null){
                mToast.show();
            }
        }
    };

    private static Runnable hide=new Runnable() {
        @Override
        public void run() {
            if (mToast!=null){
                mToast.cancel();
                mToast = null;//toast隐藏后，将其置为null
            }
        }
    };

    public static void logTrace(){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String trace="logTrace \n ";
        for(int i=3;i<stackTraceElements.length&&i<6;i++){
            trace=trace+generateTag(stackTraceElements[i])+"\n ";
        }
        saveToFile(trace);
    }

    /**
     * 在循环内使用，1秒钟内只打印一个,避免重复打印
     * @param o
     */
    public static void logOne(Object o){
        if (!allowD) return;
        if(System.currentTimeMillis()-lastLogTime>1000){
            lastLogTime=System.currentTimeMillis();
            StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
            String result=" logOne \n "+generateTag(caller)+" : "+objectToString(o);
            saveToFile(result);
        }
    }

    public static void logIf(boolean condition,Object o){
        if (!allowD) return;
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String result=" logIf  "+condition+"\n";
        if(condition){
            result+=generateTag(caller)+objectToString(o);
        }
        saveToFile(result);
    }


    public static void toast(Object o){
        if(!allowToast){
            return;
        }
        String message=null;
        if(o instanceof String){
            message=(String)o;
        }else{
            message=objectToString(o);
        }
        if(!allowToast||mContext==null){
            return;
        }
        if(mToast==null){
            final String msg=message;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mToast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
                    mHandler.postDelayed(showAgain, 3000);
                    mHandler.postDelayed(hide, 5000);
                    mToast.show();
                }
            });
        }else{
            mToast.setText(message);
        }
        saveToFile(message);
    }

    private static   String objectToString(Object o){
        if(o==null){
            return  "object is null";
        }else if(o instanceof String||o instanceof Integer||o instanceof Double||o instanceof Boolean||o instanceof Float) {
            return ""+o;
        }else if(o instanceof Throwable){
            return  extractInfo((Throwable)o) ;
        }
        Class clazz=o.getClass();
        String clazzName=clazz.getSimpleName();
        Field[] fields=clazz.getFields();
        StringBuilder builder=new StringBuilder(clazzName+":{");
        for(int i=0;i<fields.length;i++){
            Field field=fields[i];
            try {
                if(field.getType()==String.class){
                    builder.append(field.getName()+": "+field.get(o)+" , ");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Method[] methods=clazz.getMethods();
        for(int i=0;i<methods.length;i++){
            Method method=methods[i];
            try {
                if(method.getReturnType()==String.class&&method.getTypeParameters().length==0){
                    if(method.getName().equals("toString")){
                        continue;
                    }
                    builder.append(method.getName()+": "+method.invoke(o,(Object[]) null)+" , ");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        builder.append("}");
        return  builder.toString();
    }

    private static String generateTag(StackTraceElement caller) {
        String tag = "%s[%s, %d]";
        String callerClazzName = caller.getClassName();
        callerClazzName = callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1);
        tag = String.format(tag, callerClazzName, caller.getMethodName(), caller.getLineNumber());
        return tag;
    }


    public static void d(Object o){
        if (!allowD) return;
        String message=null;
        if(o instanceof String){
            message=(String)o;
        }else {
            message=objectToString(o);
        }
        saveToFile(message);
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String tag = generateTag(caller);
        Log.d(tag, message);
    }

    public static void d(String content, Throwable tr) {
        if (!allowD) return;
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        String tag = generateTag(caller);
        Log.d(tag, content, tr);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handleException(ex);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Log.e("DebugUtil", "error : ", e);
        }finally {
            mDefaultHandler.uncaughtException(thread,ex);
        }
    }

    /**
     * 自定义错误处理，收集错误信息，发送错误报告等操作均在此完成
     *
     * @param ex
     * @return true：如果处理了该异常信息；否则返回 false
     */
    private static boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        final String message=extractInfo(ex);
        if(!allowToast){
            return false;
        }
        // 使用 Toast 来显示异常信息
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext,message , Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        //保存到文件
        saveToFile(message);
        return false;
    }

    private synchronized static  void saveToFile(String message){
        try {
            FileWriter filerWriter = new FileWriter(logFile, true);//后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(myLogSdf.format(new Date())+" : "+message);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void test(Runnable runnable){
        if(allowRun){
            runnable.run();
        }
    }

    public static String extractInfo(Throwable ex){
        StringBuilder builder=new StringBuilder("Exception \n ");
        StringWriter sw=new StringWriter();
        PrintWriter printWriter=new PrintWriter(sw);
        ex.printStackTrace(printWriter);
        Scanner scanner=new Scanner(sw.toString());
        while (scanner.hasNextLine()){
            String line=scanner.nextLine();
            if(line.contains("android")||line.length()<20||line.contains("reflect")||line.contains("dalvik")){
                continue;
            }
            builder.append(line+"\n");
        }
        return  builder.toString();
    }
}
