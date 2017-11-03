package com.chinars.mapapi;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

abstract class AbstractProjection implements Projection {
	public  static final double CIRCUMFERENCE_IN_METERS = 40075160.0;//赤道周长
	protected MapView mapView;
	
	public AbstractProjection(MapView mapView){
		this.mapView=mapView;
	}
	
	@Override
	public GeoPoint fromPixels(int x, int y) {
		  Point c = geoPointToPoint(mapView.getMapCenter());
	         c.x = c.x - halfWidth() + x;
	         c.y = c.y - halfHeigth() + y;
	         return pointToGeoPoint(c);
	}

	@Override
	public float metersToEquatorPixels(float meters) {
		return (float)(meters*360/getRatio()/CIRCUMFERENCE_IN_METERS);
	}
	
	@Override
	public Point toPixels(GeoPoint in) {
	         Point   out = new Point();
	         Point p = geoPointToPoint(in);
	         Point c = geoPointToPoint(mapView.getMapCenter());
	         out.set(p.x - c.x + halfWidth(), p.y - c.y + halfHeigth());
	         return out;
	}
	
	/**
	 * @return 是否为缓冲画布上的投影，否则为当前MapView上的投影
	 */
	public boolean isBufferProjection(){
		return false;
	}
	
	/**
	 * @return 半倍宽度
	 */
	public abstract int halfWidth();
	
	/**
	 * @return 半倍高度
	 */
	public abstract int halfHeigth();
	
	/**
	 * @return 返回分辨率（每像素代表的经纬度），度/像素
	 */
   public abstract  double getRatio();

   /**
    * @param p 瓦片世界坐标
    * @return  经纬度坐标
    */
   public abstract GeoPoint pointToGeoPoint(Point p);
	
   /**
    * @return 该经纬度所在瓦片的行列号
    */
   public abstract TileCoords getTileCoords(GeoPoint pt);
   
   /**
    * @return  经纬度范围所在的瓦片范围
    */
   public abstract Rect getBoundingBox(RectF bounds);
   
   /**
    * @param tileX 瓦片列
    * @param tileY 瓦片行
    * @return 当前状态下，瓦片对应的BBox,(BBox表示的经纬度范围)
    */
   public abstract RectF getBBox(int tileX,int tileY);
	
}
