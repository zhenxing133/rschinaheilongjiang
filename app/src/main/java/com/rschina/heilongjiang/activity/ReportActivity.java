package com.rschina.heilongjiang.activity;
import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.lzyzsd.circleprogress.CircleProgress;
import com.rschina.heilongjiang.MainActivity;
import com.rschina.heilongjiang.R;
import com.rschina.heilongjiang.model.Const;
import com.rschina.heilongjiang.views.DialogDownload;


import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.net.URLEncoder;
import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.SimpleDListener;

public class ReportActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "ReportActivity";
    @ViewInject(R.id.tv_btn_left)
    TextView tv_btn_left;
    @ViewInject(R.id.iv_btn_right)
    ImageView iv_btn_right;
    @ViewInject(R.id.iv_back)
    ImageView iv_back;
    @ViewInject(R.id.webview)
    WebView webview;

    @ViewInject(R.id.circle_progress)
    CircleProgress circleProgress;

    String monitorid;
    String downUrl;
    String fileName;
    boolean downloaded=false;
    int progressCent=0,fLength;
    String FILE_ROOT= Environment.getExternalStorageDirectory() + "/TodayLuohu/";
    Context mContext;
    DialogDownload dialogDownload;
    DLManager dlManager;
    NotificationManager manager;
    NotificationCompat.Builder notificationBuilder;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        mContext=this;
        dlManager=DLManager.getInstance(this);
        manager=(NotificationManager)mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder=new NotificationCompat.Builder(mContext);
        x.view().inject(this);
        setView();
        setListeners();
        webview.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("goMap")){
                    Intent intent=new Intent(ReportActivity.this, MainActivity.class);
                    intent.putExtra("url",url);
                    startActivity(intent);
                }else {
                    view.loadUrl(url);
                }
                return true;
            }
        });
        monitorid=getIntent().getStringExtra("monitorid");
        downUrl=getIntent().getStringExtra("downUrl");
        String url = Const.FORMAL_HOST+"mobileReport?monitorId="+monitorid;
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webview.loadUrl(url);//+"&isApp=1"
        String[] temp=downUrl.split("=");
        fileName=temp[temp.length-1];
        try{
            downUrl=temp[0]+"="+URLEncoder.encode(fileName,"utf-8");
            System.out.println(downUrl);
        }catch (Exception e){
            e.printStackTrace();
        }
        File downFile=new File(FILE_ROOT,fileName);
        if(downFile.exists()){
            downloaded=true;
            iv_btn_right.setImageResource(R.drawable.ic_word);
        }
    }

    private void setView(){
        tv_btn_left.setText("专题报告");
    }

    public static void verifyStoragePermissions(Activity activity) {
// Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }


    private  void setListeners(){
        iv_back.setOnClickListener(this);
        iv_btn_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_btn_right:
                if(downloaded){
                    openWord(Uri.fromFile(new File(FILE_ROOT,fileName)));
                    break;
                }
                if(dialogDownload==null){
                    dialogDownload=new DialogDownload(mContext);
                    dialogDownload.setOnConfirmClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(ReportActivity.this, "已开始下载", Toast.LENGTH_SHORT).show();
                            verifyStoragePermissions(ReportActivity.this);
                            circleProgress.setVisibility(View.VISIBLE);
                            iv_btn_right.setVisibility(View.INVISIBLE);
                            circleProgress.setProgress(0);
                            dlManager.dlStart(downUrl, FILE_ROOT,fileName
                                    , new SimpleDListener(){

                                        @Override
                                        public void onFinish(final File file) {
                                            Log.d(TAG, "onFinish: "+file.length());
                                            downloaded=true;
                                            circleProgress.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    circleProgress.setVisibility(View.GONE);
                                                    iv_btn_right.setVisibility(View.VISIBLE);
                                                    iv_btn_right.setImageResource(R.drawable.ic_word);
                                                    openWord(Uri.fromFile(file));
                                                }
                                            });

                                            try {
                                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                                intent.setType("application/msword");
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.setData(Uri.fromFile(file));
                                                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                                                notificationBuilder.setContentText("下载完成");
                                                notificationBuilder.setTicker(fileName+"下载完成");
                                                notificationBuilder.setProgress(0, 0, false);
                                                notificationBuilder.setOngoing(false);
                                                notificationBuilder.setContentIntent(pendingIntent);
                                                manager.notify(fLength, notificationBuilder.build());
                                            } catch (Exception e) {
                                                Toast.makeText(ReportActivity.this, "没有安装word软件", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onProgress(int progress) {

                                            notificationBuilder.setContentTitle(fileName);
                                            notificationBuilder.setContentText("下载进度  "+progress*100/fLength+"/100");
                                            notificationBuilder.setSmallIcon(R.drawable.heilongjiang);
                                            notificationBuilder.setProgress(100,progress*100/fLength,false);
                                            notificationBuilder.setWhen(0);
                                            notificationBuilder.setTicker("开始下载"+fileName);
                                            notificationBuilder.setOngoing(true);
                                            manager.notify(fLength,notificationBuilder.build());

                                            Log.d(TAG, "onProgress: "+progress);

                                            if(progress*100/fLength>progressCent){
                                                progressCent=progress*100/fLength;
                                                circleProgress.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        circleProgress.setProgress(progressCent);
                                                    }
                                                });
                                            }
                                        }
                                        @Override
                                        public void onStart(String fileName, String realUrl, int fileLength) {
                                            Log.d(TAG, "onStart: "+fileName +"  "+ fileLength);
                                            fLength=fileLength;
                                        }
                                    });
                            dialogDownload.dismiss();
                        }
                    });
                }
                dialogDownload.show();
                break;

            default:
                break;
        }
    }


    private void openWord(Uri downUri) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setType("application/msword");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(downUri);
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(ReportActivity.this, "没有安装word软件", Toast.LENGTH_SHORT).show();
        }
    }

}
