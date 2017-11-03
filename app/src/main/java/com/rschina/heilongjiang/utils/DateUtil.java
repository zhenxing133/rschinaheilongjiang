package com.rschina.heilongjiang.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2016/9/27.
 */
public class DateUtil {

    private static SimpleDateFormat  slashTimeFormat=new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private  static SimpleDateFormat slashFormat=new SimpleDateFormat("yyyy/MM/dd");
    /**
     * @return 时间格式 yyyy/MM/dd
     */
    public static String toSlashString(long time){
        return slashFormat.format(new Date(time));
    }
}
