package com.rschina.heilongjiang.model;

/**
 * Created by Administrator on 2017/9/20.
 */

public class WgsMercator {

    public static void wgs2mercator(double wgsLong,double wgsLat) {

        wgsLong =  wgsLong * 20037508.34 / 180;
        wgsLat = Math.log(Math.tan((90 + wgsLat) * Math.PI / 360)) / Math.PI * 20037508.34;
        wgsLat = Math.max(-20037508.34, Math.min(wgsLat, 20037508.34));
        // var y = Math.log(Math.tan((90 + wgsLat) * Math.PI / 360)) / (Math.PI / 180);
        // y = y * 20037508.34 / 180;
        //return (wgsLong,wgsLat);
    }

   /* public static double mercator2wgs(double mercatorX,double mercatorY) {
        // web墨卡托 转 WGS-84，主要用于将坐标单位为米的值转为单位为度的值
           mercatorX  = mercatorX * 180 / 20037508.34;
           mercatorY = 180 / Math.PI * (2 * Math.atan(Math.exp((mercatorY / 20037508.34) * Math.PI)) - Math.PI / 2);
            return (mercatorX,mercatorY);
    }*/
}
