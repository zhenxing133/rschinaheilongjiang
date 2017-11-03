package com.chinars.mapapi;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * web墨卡托投影，将地球投影成一个正方形
 * @author liudanfeng
 * @since 2015-8-26
 */
public class SphericalMercatorProjection extends AbstractProjection{
	 private static final double CIRCUMFERENCE_IN_METERS = 40075160.0;//赤道周长
	 private int tileSize=256;//瓦片大小
     private int tiles = 1 ;//一行或者一列的瓦片数量
     private double circumference;//总边长
     private double radius ;
    private int halfWidth,halfHeight;
    private boolean isBuffer=false;//在
    private static final double degreeToRadian=Math.PI / 180.0;
    private int dx;
    private int dy;
    private double ratio;
    private double dpiRatio;

    public SphericalMercatorProjection(MapView mapView) {
        super(mapView);
        tiles=1<<mapView.getZoomLevel();
        circumference=tileSize * tiles;
        radius = circumference / (2.0 * Math.PI);
        halfWidth=mapView.getWidth()/2;
        halfHeight=mapView.getHeight()/2;
        dpiRatio=mapView.getDpiRatio();
    }

    public  SphericalMercatorProjection(MapView mapView,int width,int height){
        super(mapView);
        tiles=1<<mapView.getZoomLevel();
        circumference=tileSize * tiles;
        radius = circumference / (2.0 * Math.PI);
        halfWidth=width/2;
        halfHeight=height/2;
        isBuffer=true;
    }


    /**
      * 放大层级改变，需要重新计算相关参数
      */
     public void notifyZoomChanged(){
    	 tiles=1<<mapView.getZoomLevel();
    	 circumference=tileSize * tiles;
    	 radius = circumference / (2.0 * Math.PI);
     }
     
     public Point geoPointToPoint(GeoPoint gp) {
       Point ret = new Point();
       double longRadians = gp.getLongitude()* degreeToRadian;
       ret.x = (int)(radius * longRadians + (circumference / 2.0));
       double latRadians =gp.getLatitude()* degreeToRadian;
       ret.y = (int)((circumference / 2.0) - 
               (radius / 2.0 * Math.log((1.0 + Math.sin(latRadians)) / (1.0 - Math.sin(latRadians)))));
       return ret;
     }

    @Override
    public int halfWidth() {
        return halfWidth;
    }

    @Override
    public int halfHeigth() {
        return halfHeight;
    }

    @Override
    public double getRatio() {
        return 0;
    }

    @Override
    public TileCoords getTileCoords(GeoPoint pt) {
        int zoomLevel=mapView.getZoomLevel();
        int xtile = (int)Math.floor( (pt.getLongitude() + 180.0) / 360.0 * tiles) ;
        int ytile = (int)Math.floor( (1.0 - Math.log(Math.tan(pt.getLatitude() *degreeToRadian) +
                1.0 / Math.cos(pt.getLatitude() * degreeToRadian)) / Math.PI) / 2 * tiles) ;
        return new TileCoords(xtile, ytile, zoomLevel);
    }

    @Override
    public Rect getBoundingBox(RectF bounds) {
        return null;
    }

    @Override
    public RectF getBBox(int tileX, int tileY) {
        return null;
    }

    public GeoPoint pointToGeoPoint(Point p) {
       double longRadians = (p.x - (circumference / 2.0)) / radius;
       double longitude = longRadians * 180.0 / Math.PI;

       double latRadians =  (Math.PI / 2.0) - (2.0 * Math.atan(Math.exp(/* -1.0 * */(p.y - (circumference / 2.0)) / radius)));
       double latitude = latRadians * 180.0 / Math.PI ;

       return new GeoPoint(longitude, latitude);
     }

     public float metersToEquatorPixels(float meters) {
         return (float)(meters * circumference / CIRCUMFERENCE_IN_METERS);
     }

}
