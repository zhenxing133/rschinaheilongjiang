package com.chinars.mapapi;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;

class TmsProjection implements Projection {
	private int OriginX=-180,OriginY=90,TWidth=256,THeight=256;
	public  static final double CIRCUMFERENCE_IN_METERS = 40075160.0;
	private double RZ;//第N层分辨率
	private double R;//地图分辨率
	private RectF bounds;
	private int tileOffsetY;
	private  MapView mapView;
	
	 public Point geoPointToPoint(GeoPoint gp) {
		 Point ret = new Point();
		 ret.x=(int)( Math.abs(OriginX-gp.getLongitude())/R);
		 ret.y=(int)((Math.abs(OriginY-gp.getLatitude()))/R);
		 return ret;
	 }
	
	 public GeoPoint pointToGeoPoint(Point p) {
		 double lon=p.x*R-Math.abs(OriginX);
		 double lat=OriginY-p.y*R;
		 return new GeoPoint(lon,lat);
	 }
	
	 public TmsProjection(MapView mapView){
		 this.mapView=mapView;
		 R=mapView.getRatio();
		 RZ=R*mapView.getTileZoom();
		 tileOffsetY=(int)Math.floor( Math.abs(OriginY-bounds.bottom)/RZ/TWidth);
	 }
	 
	 
	@Override
	public GeoPoint fromPixels(int x, int y) {
		Point c = geoPointToPoint(mapView.getMapCenter());
		  c.x = c.x - (mapView.getWidth() / 2) + x;
          c.y = c.y - (mapView.getHeight() / 2) + y;
		return pointToGeoPoint(c);
	}

	@Override
	public float metersToEquatorPixels(float meters) {
		return (float)(meters*360/R/CIRCUMFERENCE_IN_METERS);
	}

	@Override
	public Point toPixels(GeoPoint in) {
        Point   out = new Point();
        Point p = geoPointToPoint(in);
        Point c = geoPointToPoint(mapView.getMapCenter());
        out.set(p.x - c.x + (mapView.getWidth() / 2), p.y - c.y + (mapView.getHeight() / 2));
        return out;
	}

	TileCoords getTileCoords(double lat, double lon){
		int x=(int) Math.floor( Math.abs(OriginX-lon)/RZ/THeight );
		int y=(int)(tileOffsetY-Math.floor( Math.abs(OriginY-lat)/RZ/THeight) );
		return new TileCoords(x, y, mapView.getZoomLevel());
	}
	
	 
	public Rect getBoundingBox(RectF bounds){
		 int maxX=(int) Math.floor( Math.abs(OriginX-bounds.right)/RZ/THeight );
		 int minX=(int) Math.floor( Math.abs(OriginX-bounds.left)/RZ/THeight );
		 int maxY=(int)(Math.floor( Math.abs(OriginY-bounds.bottom)/RZ/THeight ) - Math.floor( Math.abs(OriginY-bounds.top)/RZ/TWidth));
		 return new Rect(minX,maxY, maxX, 0);
	 }

	
	public TileCoords getTileCoords(GeoPoint gp) {
		return getTileCoords(gp.getLatitude(), gp.getLongitude());
	}
	
}
