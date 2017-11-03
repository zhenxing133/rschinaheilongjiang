package com.chinars.mapapi;

import android.graphics.Color;

/**
 * 带孔的多边形
 * @author liudanfeng
 * @since 2015-10-9
 */
public class HoledPolygon {
	 GeoPoint[][] polygons;
	 Bounds bounds;
	 int minZoomLevel;
	 int shapeColor=0x33FFFFFF;
	 double size;
	/**
	 * polygons[1]为外多边形，其余的为内多边形
	 * @param polygons
	 */
	public HoledPolygon(GeoPoint[][] polygons){
		if(polygons==null||polygons[0].length<3){
			throw new IllegalArgumentException();
		}
		this.polygons=polygons;
		bounds=new Bounds();
		for(int i=1;i<polygons[0].length;i++){
			bounds.extend(polygons[0][i]);
		}
		double factor=1/0.3519572078484874;
		for(int i=0;i<18;i++){
			if((bounds.getHeight()*(1<<i)*factor)>1&&
					(bounds.getWidth()*(1<<i)*factor)>1){
				minZoomLevel=i;
				break;
			}
		}
		size=bounds.getSize();
	}
	
	public Bounds getBounds(){
		return bounds;
	}
	
	public double getSize(){
		return size;
	}
	public HoledPolygon(GeoPoint[] polygon){
		this.polygons=new GeoPoint[1][];
		polygons[0]=polygon;
		bounds=new Bounds();
		for(int i=1;i<polygons[0].length;i++){
			bounds.extend(polygons[0][i]);
		}
		double factor=1/0.3519572078484874;
		for(int i=0;i<18;i++){
			if((bounds.getHeight()*(1<<i)*factor)>2&&
					(bounds.getWidth()*(1<<i)*factor)>2){
				minZoomLevel=i;
				break;
			}
		}
		size=bounds.getSize();
	}
	
	public GeoPoint getGeoCenter(){
		return bounds.getCenterLonLat();
	}
	
	public void setColor(int color){
		this.shapeColor=color;
	}
	
	/**
	 * 增加内多边形
	 * @param polygons
	 * @return
	 */
	public boolean addHoles(GeoPoint[][] polygons){
		return false;
	}
	
	/**
	 * 删除内多边形
	 * @param polygon
	 * @return
	 */
	public boolean removeHole(GeoPoint[] polygon){
		
		return false;
	}
	
	public boolean removeHole(int index){
		return false;
	}
	
	public int getHoleNum(){
		return polygons.length-1;
	}
}
