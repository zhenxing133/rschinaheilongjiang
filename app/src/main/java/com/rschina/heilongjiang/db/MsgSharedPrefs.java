package com.rschina.heilongjiang.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 123 on 2016/9/22.
 */
public class MsgSharedPrefs {

    private SharedPreferences smp;
    private SharedPreferences.Editor editor;
    public MsgSharedPrefs(Context context) {
        smp = context.getSharedPreferences("shareMsg", Context.MODE_PRIVATE);
    }

    public void removeCookie(){
        editor.remove("set_Cookie");
    }
    public void openEditor() {
        editor = smp.edit();
    }

    public void closeEditor() {
        editor.commit();
    }


    // 设置系统是否运行过
    public void setAppHasRunned(boolean flag) {
        editor.putBoolean("hasrunned", flag);
    }

    public boolean getAppHasRunned() {
        return smp.getBoolean("hasrunned", false);
    }


    //设置更新下载ID
    public Long getDownloadId() {
        return smp.getLong("updateId", 0);
    }

    public void setDownloadId(Long updateId) {
        editor.putLong("updateId", updateId);
    }

    //设置删除更新下载ID
    public Long getDeldateId() {
        return smp.getLong("deldateId", 0);
    }

    public void setDeldateId(Long deldateId) {
        editor.putLong("deldateId", deldateId);
    }

    public void doClear(){
        editor.clear();
    }

}
