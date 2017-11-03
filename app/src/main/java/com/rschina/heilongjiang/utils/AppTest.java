package com.rschina.heilongjiang.utils;

import com.rschina.heilongjiang.model.MapInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/9/12.
 */
public class AppTest {

    public  static List<MapInfo> getMapInfos(){
        List<MapInfo> mapInfos=new ArrayList<>();
        for (int i = 0; i < 5 ; i++) {
            MapInfo mapInfo=new MapInfo();
            mapInfo.name="map"+1;
            //mapInfo.describe="2015/6/28-2016/6/28 建设用地专题图"+i;
            mapInfos.add(mapInfo);
        }
        return mapInfos;
    }

}
