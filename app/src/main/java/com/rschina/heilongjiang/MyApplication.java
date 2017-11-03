package com.rschina.heilongjiang;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;

import com.rschina.heilongjiang.MyEventBusIndex;
import com.rschina.heilongjiang.utils.DebugUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePalApplication;
import org.litepal.tablemanager.Connector;
import  org.xutils.x;

import timber.log.Timber;

/**
 * Created by Administrator on 2016/9/7.
 */
public class MyApplication extends LitePalApplication {

    public static MyApplication instance;
    private  int curVersionCode=-1;

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;

        x.Ext.init(this);
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        DebugUtil.init(this);
        EventBus.builder().addIndex(new MyEventBusIndex()).installDefaultEventBus();

    }

    public int getCurVersionCode(){
        if(curVersionCode==-1){
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(
                        getPackageName(), 0);
                curVersionCode = pInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return curVersionCode;
    }


    public static MyApplication getInstance(){
        return instance;
    }

}
