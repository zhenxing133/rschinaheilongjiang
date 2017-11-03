package com.chinars.mapapi;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

import com.chinars.mapapi.utils.LogUtils;
import com.chinars.mapsdk.R;

/**
 * web墨卡托投影，将地球投影成一个正方形
 * @author liudanfeng
 * @since 2015-8-26
 */
public class SphericalMeterMercatorProjection extends AbstractProjection{
    private static final double CIRCUMFERENCE_IN_METERS = 40075016.685574;//赤道周长
    private static final double OriginX = -20037508.342787;
    private static final double OriginY = 20037508.342787;
    private static final double Radius = 20037508.342787;
    private static final double R0 = 156543.033928/2;
    private int tileSize = 256;//瓦片大小
    private int tiles = 1;//一行或者一列的瓦片数量
    private int halfWidth, halfHeight;
    private boolean isBuffer = false;//在
    private static final double degreeToRadian = Math.PI / 180.0;
    private int dx;
    private int dy;
    private double dpiRatio;
    private double ratioTimes=111319.4916838170374572 ;

    public SphericalMeterMercatorProjection(MapView mapView) {
        super(mapView);
        tiles=1<<mapView.getZoomLevel();
        halfWidth=mapView.getWidth()/2;
        halfHeight=mapView.getHeight()/2;
        dpiRatio=mapView.getDpiRatio();
    }

    public SphericalMeterMercatorProjection(MapView mapView, int width, int height){
        super(mapView);
        tiles=1<<mapView.getZoomLevel();
        halfWidth=width/2;
        halfHeight=height/2;
        isBuffer=true;
        dpiRatio=mapView.getDpiRatio();
    }

     public Point geoPointToPoint(GeoPoint pt) {
         double x=pt.getLongitude()* Radius/180;
         double y= Math.log(Math.tan((90 + pt.getLatitude()) * Math.PI / 360)) / Math.PI * Radius;
         return geoPointToPoint(x, y);
     }

    public Point geoPointToPoint(double x,double y){
        Point ret = new Point();
        double RZ = getRatio()*ratioTimes;
        ret.x=(int)((x-OriginX) / RZ);
        ret.y=(int)((OriginY-y )/ RZ);
        return  ret;
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
        if(isBuffer){
            return mapView.getRatio()*mapView.getTileZoom();
        }else{
            return mapView.getRatio();
        }
    }

    public  TileCoords getTileCords(double x,double y){
        int zoomLevel=mapView.getZoomLevel();
        double RZ=R0/Math.pow(2, zoomLevel);
        int xtile =(int)(Math.abs(OriginX - x) / tileSize / RZ);
        int ytile = (int)(Math.abs(OriginY - y) / tileSize / RZ);
        return new TileCoords(xtile, ytile, zoomLevel);
    }

    @Override
    public TileCoords getTileCoords(GeoPoint pt) {
        double x=pt.getLongitude()* Radius/180;
        double y= Math.log(Math.tan((90 + pt.getLatitude()) * Math.PI / 360)) / Math.PI * Radius;
        return getTileCords(x,y);
    }

    @Override
    public Rect getBoundingBox(RectF bounds) {
        double RZ = R0 / Math.pow(2, mapView.getZoomLevel());
        double tilez=RZ*tileSize;
        double minLon=bounds.left* Radius/180;
        double maxLon=bounds.right* Radius/180;
        double minLat= Math.log(Math.tan((90 + bounds.top) * Math.PI / 360)) / Math.PI * Radius;
        double maxLat= Math.log(Math.tan((90 + bounds.bottom) * Math.PI / 360)) / Math.PI * Radius;
        int maxX=(int) Math.floor( Math.abs(OriginX-maxLon)/tilez);
        int minX=(int) Math.floor( Math.abs(OriginX-minLon)/tilez );
        int maxY=(int)(Math.floor( Math.abs(OriginY-maxLat)/tilez));
        int minY=(int)(Math.floor( Math.abs(OriginY-minLat)/tilez));
//        LogUtils.d("maxX:"+maxX+" minX:"+minX+" maxY:"+maxY+" minY:"+minY);
        return new Rect(minX,minY, maxX,maxY );
    }

    @Override
    public RectF getBBox(int tileX, int tileY) {
        return null;
    }


    public GeoPoint pointToGeoPoint(Point p) {
        double RZ = getRatio()*ratioTimes;;
        double x = OriginX + p.x * RZ;
        double y = OriginY-p.y * RZ;
        double longitude = 180 * x / Radius;
        double latitude = 180 / Math.PI * (2 * Math.atan(Math.exp((y / Radius) * Math.PI)) - Math.PI / 2);
//        LogUtils.d("longitude:"+longitude+" latitude:"+latitude);
        return new GeoPoint(longitude, latitude);
     }

}
