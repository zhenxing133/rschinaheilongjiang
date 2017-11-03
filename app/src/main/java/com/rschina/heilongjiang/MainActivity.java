package com.rschina.heilongjiang;

import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rschina.heilongjiang.activity.CompareChooseActivity;
import com.rschina.heilongjiang.activity.FristChooseActivity;
import com.rschina.heilongjiang.activity.HelpActivity;
import com.rschina.heilongjiang.activity.MarkActivity;
import com.rschina.heilongjiang.activity.ReportActivity;
import com.rschina.heilongjiang.db.MarkInfo;
import com.rschina.heilongjiang.db.MsgSharedPrefs;
import com.rschina.heilongjiang.db.RsService;
import com.rschina.heilongjiang.fragment.CollectionFragment;
import com.rschina.heilongjiang.fragment.MapFragment;
import com.rschina.heilongjiang.fragment.UserCenterFragment;
import com.rschina.heilongjiang.fragment.UserFragment;
import com.rschina.heilongjiang.model.BaseIdDes;
import com.rschina.heilongjiang.model.MonitorInfo;
import com.rschina.heilongjiang.utils.AppUpdateUtil;
import com.rschina.heilongjiang.model.Const;
import com.rschina.heilongjiang.model.Event;
import com.rschina.heilongjiang.model.MapInfo;
import com.rschina.heilongjiang.utils.DialogHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;

import cn.aigestudio.downloader.bizs.DLManager;
import cn.aigestudio.downloader.interfaces.SimpleDListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Event.ActionListener {
    private static final String TAG = "MainActivity";
    public final static int REQUEST_BASE = 1;
    public final static int REQUEST_OVER = 2;
    public final static int REQUEST_USER= 3;
    public final static int REQUEST_MARK = 4;

    @ViewInject(R.id.tv_btn_left)
    TextView tv_btn_left;
    @ViewInject(R.id.tv_btn_right)
    TextView tv_btn_right;
    @ViewInject(R.id.iv_back)
    ImageView iv_back;

    @ViewInject(R.id.iv_xianqqian)
    ImageView iv_xianqqian;
    @ViewInject(R.id.iv_monitor)
    ImageView iv_monitor;
    @ViewInject(R.id.iv_user)
    ImageView iv_user;

    @ViewInject(R.id.ll_sate_map)
    LinearLayout ll_sate_map;
    @ViewInject(R.id.ll_user)
    LinearLayout ll_user;

    @ViewInject(R.id.ll_monitor)
    LinearLayout ll_monitor;
    @ViewInject(R.id.headerBar)
    RelativeLayout headerBar;
    @ViewInject(R.id.ll_navbar)
    LinearLayout ll_navbar;
    @ViewInject(R.id.ll_navbar_tow)
    LinearLayout ll_navbar_tow;
    @ViewInject(R.id.text1)
    TextView text1;
    @ViewInject(R.id.text2)
    TextView text2;
    @ViewInject(R.id.text3)
    TextView text3;
    @ViewInject(R.id.tv_ok)
    TextView tv_ok;
    @ViewInject(R.id.tv_cance)
    TextView tv_cance;

    MapFragment mapFragment;
    UserFragment userFragment;
    double displayRatio = 1;
    private List<MonitorInfo> monitorInfos = null;
    private List<MapInfo> imageMaps = null;
    private List<MapInfo> imageMap = null;
    private List<BaseIdDes> baseIdDes = null;
    private int baseIndex, overIndex, baseIndex2 = -1;
    private int baseMapIdIndex = 0;
    AppUpdateUtil appUpdateUtil;
    DLManager dlManager;
    MsgSharedPrefs msp;
    static Handler handler;
    int fLength, nProgress;
    int tabIndex = 0;
    NotificationManager notificationManager;
    RsService rsService;
    private MapInfo selectMap;
    private int moo = 0;//加载底图框是否显示
    private int linkindex = 0;//报告索引
    private boolean hasLogin = false;
    private List<List<BaseIdDes>> baseIdDesList;
    //经纬度信息
    static double latitude = 0.0;
    static double longitude= 0.0;
    private int number;
    private double longi;
    private double lati;
    private String name;
    private String des;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        x.view().inject(this);
        rsService = RsService.getInstance();
        mapFragment = new MapFragment();

        setView();
        setListeners();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, mapFragment).commit();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mapFragment.setText();
                mapFragment.removeOverLayer();
                mapFragment.setMapInfo(imageMaps.get(number),true);
                mapFragment.showOverlay(lati,longi,name,des,baseIndex);
            }
        };
        checkUpdate();
        ll_sate_map.setBackgroundColor(getResources().getColor(R.color.colorPressed));
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        setTabIndex(tabIndex);
        //request();

    }



    public void setTabIndex(int tabIndex) {
        if (tabIndex == 0) {
            tabIndex = 0;
            ll_sate_map.setBackgroundColor(getResources().getColor(R.color.colorPressed));
            mapFragment.setMoo(moo);
            tv_btn_left.setText("选择影像");
            tv_btn_right.setVisibility(View.INVISIBLE);
        } else if (tabIndex == 1) {
            tabIndex = 1;
            moo = 1;
            mapFragment.setMoo(moo);
            tv_btn_left.setText("选择应用");
            tv_btn_right.setText("查看报告");
            tv_btn_right.setVisibility(View.VISIBLE);
        } else if (tabIndex == 2) {
            tabIndex = 2;
            moo = 2;

        }
    }

    private void checkUpdate() {
        appUpdateUtil = new AppUpdateUtil();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                appUpdateUtil.checkUpdate(new Runnable() {
                    @Override
                    public void run() {
                        final String fileName = Const.APP_NAME + appUpdateUtil.getVersionName() + ".apk";
                        File file = new File(Const.FILE_ROOT, fileName);
                        if (appUpdateUtil.isHasUpdate()) {
                            if (file.exists()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(file),
                                        "application/vnd.android.package-archive");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                return;
                            }
                            DialogHelper.Confirm(MainActivity.this, getText(R.string.update_title), appUpdateUtil.getUpdateLog()
                                    , "更新", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(MainActivity.this, "已开始下载", Toast.LENGTH_SHORT).show();
                                            downloadAndInstallApk(fileName);
                                        }
                                    }, "下次再说", null);

                        }
                    }
                });
            }
        }, 5000);
    }

    private void downloadAndInstallApk(final String fileName) {
        if (dlManager == null) {
            dlManager = DLManager.getInstance(this);
        }
        dlManager.dlStart(appUpdateUtil.getDownloadUrl(), Const.FILE_ROOT, fileName, new SimpleDListener() {
            @Override
            public void onStart(String fileName, String realUrl, int fileLength) {
                fLength = fileLength;
            }

            @Override
            public void onProgress(int progress) {
                Log.d(TAG, "onProgress: " + progress);
                nProgress = progress * 100 / fLength;
                NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this);
                builder.setProgress(100, nProgress, false).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
                builder.setSmallIcon(R.drawable.ic_launcher).setContentTitle("正在下载" + nProgress + "%");
                builder.setContentIntent(PendingIntent.getActivity(MainActivity.this, 1, new Intent(MainActivity.this, MainActivity.class), PendingIntent.FLAG_NO_CREATE));
                notificationManager.notify(1001, builder.build());
            }

            @Override
            public void onFinish(File file) {
                notificationManager.cancel(1001);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onError(int status, String error) {
                Log.d(TAG, "onError: " + status + "  " + error);
            }
        });


    }


    private void setView() {
        tv_btn_left.setText("切换底图");
        tv_btn_right.setText("选择对比图层");
        iv_back.setVisibility(View.GONE);

    }

    private void setListeners() {
        iv_back.setOnClickListener(this);
        tv_btn_left.setOnClickListener(this);
        tv_btn_right.setOnClickListener(this);
        ll_monitor.setOnClickListener(this);
        ll_sate_map.setOnClickListener(this);
        ll_user.setOnClickListener(this);
        tv_ok.setOnClickListener(this);
        tv_cance.setOnClickListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAction(Event.Action action) {
        if (action.equals(Event.Finish)) {
            imageMaps = rsService.getImageMaps();
            selectMap = imageMaps.get(imageMaps.size()-1);
            mapFragment.setMapInfo(selectMap, true);
            mapFragment.setMapCenter();
            baseIndex = imageMaps.indexOf(selectMap);
            mapFragment.setText();
        }

        if (action.withCode(1001)) {
            monitorInfos = rsService.getMonitorInfos();
            //baseIdDes = rsService.getBaseIdDes();
            baseIdDesList = rsService.getBaseIdDesList();
        }

        if (action.withCode(Const.ACTION_CANCEL_COMPARE)) {
            Log.d(TAG, "onAction: " + Const.ACTION_CANCEL_COMPARE);
            baseIndex2 = -1;
        }
    }

    @Override
    public void onClick(View v) {
        iv_xianqqian.setImageResource(R.drawable.xiangqian_normal);
        iv_monitor.setImageResource(R.drawable.monitor_normal);
        iv_user.setImageResource(R.drawable.personal_center_normal);
        ll_sate_map.setBackgroundColor(Color.WHITE);
        ll_monitor.setBackgroundColor(Color.WHITE);
        ll_user.setBackgroundColor(Color.WHITE);
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_back:
                if (displayRatio > 0) {
                    displayRatio -= 0.05;
                    mapFragment.setDisplayRatio(displayRatio);
                }
                break;
            case R.id.tv_btn_right:
                tabIndex = 1;
                moo = 1;
                mapFragment.setMoo(moo);
                ll_monitor.setBackgroundColor(getResources().getColor(R.color.colorPressed));

                Intent reportIntent = new Intent(getApplicationContext(), ReportActivity.class);
                reportIntent.putExtra("monitorid", monitorInfos.get(linkindex).monitorid);
                reportIntent.putExtra("downUrl", monitorInfos.get(linkindex).doc_downloadurl);
                //Log.e("yzx", monitorInfos.get(0).doc_downloadurl);
                startActivity(reportIntent);
                break;
            case R.id.tv_btn_left:

                if (tabIndex == 0) {
                    tabIndex = 0;
                    moo = 0;
                    mapFragment.setMoo(moo);
                    intent = new Intent(this, FristChooseActivity.class);
                    intent.putExtra("selectedIndex", baseIndex);
                    startActivityForResult(intent, REQUEST_BASE);
                } else {
                    tabIndex = 1;
                    ll_monitor.setBackgroundColor(getResources().getColor(R.color.colorPressed));
                    moo = 1;
                    mapFragment.setMoo(moo);
                    mapFragment.setLengerText();
                    intent = new Intent(this, CompareChooseActivity.class);
                    intent.putExtra("selectedIndex", overIndex);
                    startActivityForResult(intent, REQUEST_OVER);

                    break;
                }
            case R.id.ll_sate_map:
                iv_xianqqian.setImageResource(R.drawable.xiangqian_pressed);
                mapFragment.removeOverLayer();
                if (imageMaps != null) {
                    mapFragment.setMapInfo(imageMaps.get(baseIndex), true);
                    //mapFragment.removeOverLayer();
                    tabIndex = 0;
                    setTabIndex(tabIndex);
                    moo = 0;
                    mapFragment.setMoo(moo);
                    headerBar.setVisibility(View.VISIBLE);
                    ll_sate_map.setBackgroundColor(getResources().getColor(R.color.colorPressed));
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, mapFragment).commit();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mapFragment.setText();
                        }
                    }, 200);
                }


                break;
            case R.id.ll_monitor:
                tabIndex = 1;
                moo = 1;
                setTabIndex(tabIndex);
                mapFragment.setMoo(moo);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment, mapFragment).commit();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mapFragment.setMonitorText();
                        mapFragment.setMoo(moo);
                        if (monitorInfos != null ) {
                            headerBar.setVisibility(View.VISIBLE);
                            ll_monitor.setBackgroundColor(getResources().getColor(R.color.colorPressed));
                            mapFragment.setBaseMapIdIndex(baseMapIdIndex);
                            if (baseIdDesList.get(overIndex).size() > 0) {
                                mapFragment.setBaseIds(baseIdDesList.get(overIndex).get(0), true);
                                mapFragment.removeOverLayer();
                                mapFragment.setMonitorInfo(monitorInfos.get(overIndex), false);
                            }

                        }
                    }
                }, 500);

                break;
            case R.id.ll_user:
                if (hasLogin) {
                    tabIndex = 2;
                    UserCenterFragment userCenterFragment = new UserCenterFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment,userCenterFragment)
                            .commit();
                    headerBar.setVisibility(View.GONE);
                    ll_user.setBackgroundColor(getResources().getColor(R.color.colorPressed));
                    iv_user.setImageResource(R.drawable.personal_center_pressed);
                } else {
                    tabIndex = 2;
                    userFragment = new UserFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment, userFragment).commit();
                    headerBar.setVisibility(View.GONE);
                    ll_user.setBackgroundColor(getResources().getColor(R.color.colorPressed));
                    iv_user.setImageResource(R.drawable.personal_center_pressed);
                }


                break;
            case R.id.tv_ok:
                Intent intent2 = new Intent(MainActivity.this, MarkActivity.class);
                //intent2.putExtra("latitude", latitude);
                //intent2.putExtra("longitude", longitude);
                //intent2.putExtra("id", baseIndex);
                showBootom();
                setTabIndex(0);
                setVisible();
                mapFragment.setcancel();
                //startActivity(intent2);
                startActivityForResult(intent2,REQUEST_MARK);
                //finish();
                break;
            case R.id.tv_cance:
                mapFragment.setcancel();
                tabIndex = 0;
                setTabIndex(tabIndex);
                setVisible();
                ll_navbar.setVisibility(View.VISIBLE);
                ll_navbar_tow.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_BASE) {
                baseIndex = data.getIntExtra("selectedIndex", 0);
                mapFragment.setMapInfo(imageMaps.get(baseIndex), true);
                //mapFragment.removeOverLayer();
                mapFragment.setText();
            } else if (requestCode == REQUEST_OVER) {
                overIndex = data.getIntExtra("selectedIndex", 0);
                linkindex = overIndex;
                baseMapIdIndex = overIndex;
                mapFragment.setBaseMapIdIndex(baseMapIdIndex);
                mapFragment.removeOverLayer();
                mapFragment.setMonitorInfo(monitorInfos.get(overIndex), false);
                //mapFragment.setBaseIds(baseIdDes.get(baseMapIdIndex), true);
                if (baseIdDesList.get(baseMapIdIndex).size() > 0) {

                    mapFragment.setBaseIds(baseIdDesList.get(baseMapIdIndex).get(0), true);
                }
                //Log.e("yzx", baseIdDesList.get(baseMapIdIndex).get(0) + "mainactivity");
                mapFragment.setMonitorText();
            } else if (requestCode == REQUEST_MARK) {
                //Log.e("yzx", "这个方法调用了");
                String name = data.getStringExtra("name");
                String des = data.getStringExtra("des");
                mapFragment.showOverlay(latitude,longitude,name,des,baseIndex);


            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 展示与隐藏底部栏
     */
    public void showBootom() {
        ll_navbar.setVisibility(View.VISIBLE);
        ll_navbar_tow.setVisibility(View.GONE);

    }


    public void dismissBootom() {
        ll_navbar.setVisibility(View.GONE);
        ll_navbar_tow.setVisibility(View.VISIBLE);

    }

    /**
     * 设置经纬度
     */
    public void setInfo(double la,double lo) {
        this.latitude = la ;
        this.longitude = lo;
    }

    /**
     * 从userfragment切换UserCenterFragment
     * @param contact
     */
    public void gotoUserCenterFragment(String contact) {
        hasLogin = true;
        mapFragment.setHasLogon();
        //UserCenterFragment userCenterFragment = new UserCenterFragment();
        //userCenterFragment.newInstance(contact);
        UserCenterFragment userCenterFragment = UserCenterFragment.newInstance(contact);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment,userCenterFragment)
                .commit();
    }

    /**
     * 从UserCenterFragment切换userfragment
     */
    public void gotoUserFragment() {
        UserFragment userFragment = new UserFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment,userFragment)
                .commit();
        headerBar.setVisibility(View.GONE);
        ll_user.setBackgroundColor(getResources().getColor(R.color.colorPressed));
        iv_user.setImageResource(R.drawable.personal_center_pressed);
        ll_sate_map.setBackgroundColor(getResources().getColor(R.color.headbar_textcolor));
        iv_xianqqian.setImageResource(R.drawable.xiangqian_normal);
    }

    /**
     * 从个人中心fragment切换到收藏fragment
     */
    public void gotoCollectionFragment(){
        CollectionFragment collectionFragment = new CollectionFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment,collectionFragment)
                .commit();
    }
    /**
     * 从标注MarkActivity切换mapFragment
     */
    public void gotoMapFragment(int i) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment, mapFragment).commit();
        List<MarkInfo> all = DataSupport.findAll(MarkInfo.class);
        this.number = all.get(i).getNumber();
        this.longi = all.get(i).getLongitude();
        this.lati = all.get(i).getLatitude();
        this.name = all.get(i).getName();
        this.des = all.get(i).getDes();
        Log.e("yzx", "number:=" + number);
        handler.sendEmptyMessageDelayed(0, 100);
        ll_user.setBackgroundColor(Color.WHITE);
        iv_xianqqian.setImageResource(R.drawable.xiangqian_pressed);
        iv_user.setImageResource(R.drawable.personal_center_normal);
        if (imageMaps != null) {
            tabIndex = 0;
            setTabIndex(tabIndex);
            moo = 0;
            mapFragment.setMoo(moo);
            headerBar.setVisibility(View.VISIBLE);
            ll_sate_map.setBackgroundColor(getResources().getColor(R.color.colorPressed));
        }
        //mapFragment.showOverlay(lati,longi,name,des,);

    }

    /**
     * 解决点击标注不能显示map
     */
    public void setVisible(){
        mapFragment.setMapInfo(imageMaps.get(baseIndex), true);
    }



}
