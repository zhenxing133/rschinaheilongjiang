package com.chinars.mapapi;

import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
class WmtsProjection extends AbstractProjection{
	private double OriginX=-180,OriginY=90,TWidth=256,THeight=256;
	private int halfWidth,halfHeight;
	private boolean isBuffer=false;//在
	private int dx;
	private int dy;
	private double ratio;
	 public Point geoPointToPoint(GeoPoint gp) {
		 Point ret = new Point();
		 double R=getRatio();
		 ret.x=(int)( (gp.getLongitude()-OriginX)/R);
		 ret.y=(int)((OriginY-gp.getLatitude())/R);
		 return ret;
	 }
	
	 public GeoPoint pointToGeoPoint(Point p) {
		 double R=getRatio();
		 double lon=p.x*R-Math.abs(OriginX);
		 double lat=OriginY-p.y*R;
		 return new GeoPoint(lon,lat);
	 }
	
	 /**
	  * 按当前缩放比例投影到MapView上，考虑瓦片的缩放
	  * @param mapView
	  */
	 public WmtsProjection(MapView mapView){
		 super(mapView);
		 halfWidth=mapView.getWidth()/2;
		 halfHeight=mapView.getHeight()/2;
	 }
	 
	 /**
	  * 按地图当前的瓦片层级所在的比例投影到缓冲画布上，不考虑瓦片的缩放
	  * @param mapView
	  * @param width
	  * @param height
	  */
	 public WmtsProjection(MapView mapView,int width,int height){
		 super(mapView);
		 halfWidth=width/2;
		 halfHeight=height/2;
		 isBuffer=true;
	 }
	
	 /**
	  * @return 是否为缓冲画布上的投影，否则为当前
	  */
	public boolean isBufferProjection(){
		return isBuffer;
	}
	
	public void setOrigin(GeoPoint origin){
		OriginX=origin.getLongitude();
		OriginY=origin.getLatitude();
	}
	 
	public double getRatio(){
		if(isBuffer){
			return mapView.getRatio()*mapView.getTileZoom();
		}else{
			return mapView.getRatio();
		}
	}

	

	public WmtsProjection update(){
		Point p=geoPointToPoint(mapView.getMapCenter());
		dx=-p.x+halfWidth;
		dy=-p.y+halfHeight;
		ratio=getRatio();
		return this;
	}
	/**
	 * 处理大量数据时调用方法，调用此方法前先调用一次update
	 * @param in
	 * @return
	 */
	public void fastToPixels(GeoPoint gp,Point out) {
		out.x=(int)( (gp.longitude-OriginX)/ratio)+dx;
		out.y=(int)((OriginY-gp.latitude)/ratio)+dy;
	}
	
	@Override
	public Rect getBoundingBox(RectF bounds){
		 double RZ=getRatio();
		 if(!isBuffer){
			 RZ=RZ*mapView.getTileZoom();
		 }
		 int maxX=(int) Math.floor( Math.abs(OriginX-bounds.right)/RZ/TWidth );
		 int minX=(int) Math.floor( Math.abs(OriginX-bounds.left)/RZ/TWidth );
		 int maxY=(int)(Math.floor( Math.abs(OriginY-bounds.bottom)/RZ/THeight));
		 int minY=(int)(Math.floor( Math.abs(OriginY-bounds.top)/RZ/THeight));
		 return new Rect(minX,minY, maxX,maxY );
	 }

	public TileCoords getTileCoords(GeoPoint gp) {
		double RZ=getRatio();
		if(!isBuffer){
			RZ=RZ*mapView.getTileZoom();
		}
		int x=(int) Math.floor( (gp.getLongitude()-OriginX)/RZ/THeight );
		int y=(int) Math.floor( (OriginY-gp.getLatitude())/RZ/THeight);
		return new TileCoords(x, y, mapView.getZoomLevel());
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
	public RectF getBBox(int tileX, int tileY) {
		double RZ=getRatio();
		if(!isBuffer){
			RZ=RZ*mapView.getTileZoom();
		}
		float left=(float) (OriginX+tileX*RZ*TWidth);
		float top=(float) (OriginY-tileY*RZ*THeight);
		float right=(float) (OriginX+(tileX+1)*RZ*TWidth);
		float bottom=(float) (OriginY-(tileY+1)*RZ*THeight);
		return new RectF(left,top,right,bottom);
	}
}
