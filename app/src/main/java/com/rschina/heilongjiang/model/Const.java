package com.rschina.heilongjiang.model;

import android.os.Environment;

import com.rschina.heilongjiang.BuildConfig;


/**
 * Created by Administrator on 2016/9/14.
 */
public interface Const {

    int ACTION_CANCEL_COMPARE=1001;
    String FILE_ROOT= Environment.getExternalStorageDirectory() + "/HeiLongJiang/";
    String APP_NAME= BuildConfig.FLAVOR;
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    //String HOST = "http://192.168.101.203:8189/";
   // String TYPE = HOST+"getDataClsInfoList?";
   // String TYPE_DATA=HOST+"getMonitorDataInfoList?admincode=230000&keyword=&dataClsId=";
   // String IMAGEMAP =HOST+ "getMosaicDataInfoList?admincode=230000&getAttrsTag=1";
    //http://localhost:8189/userLogin?contact=1&password=84d5f8ec365b42a04d2b0dcb585ed1ed&type=2
    //String REGIST_URL = "http://192.168.101.203:8189/userRegister?";
   // String LOGIN_URL = "http://192.168.101.203:8189/userLogin?";

    //测试环境
    //String TEST_URL = "http://10.0.82.136:8080/hlj/";
    //String TEST_IMAGEMAP =HOST+"getMosaicDataInfoList?admincode=230000&getAttrsTag=1";
    //String test_type = TEST_URL+"";
    //String LOG_OUT = TEST_URL+"/userLogout";

    //正式环境
    //端口
    String FORMAL_HOST = "http://210.77.87.227:8092/hlj/";
    //卫星影像
    String FORMAL_IMAGEMAP = FORMAL_HOST+"getMosaicDataInfoList?admincode=230000&keyword=&startTime=&endTime=&getAttrsTag=1";
    //分类
    String FORMAT_TYPE =FORMAL_HOST+"getDataClsInfoList?";
    //注册
    String REGIST_URL = FORMAL_HOST+"userRegister?";
    //登陆
    String LOGIN_URL =FORMAL_HOST+"userLogin?";
    //注销退出
    String LOG_OUT = FORMAL_HOST+"userLogout";

}
