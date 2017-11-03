package com.chinars.mapapi;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * 图形类
 * @author rsclouds 
 *
 */
public class GraphicsItem {

	
	/*形状显示的位置*/
	 GeoPoint center;
	/*形状颜色*/
	 int shapeColor=Color.RED;
	 GeoPoint[] pts;
	 int radius;
	 int type=-1;
	 Bounds bounds;//
	 int minZoomLevel;
	 Paint paint;
	 public static final int POINT=1;
	 public static final int CIRCLE=2;
	 public static final int RECTANGLE=3;
	 public static final int POLYGON=4;
	 public static final int POLYLINE=5;
	 public static final int OVAL=6;
	
	 public GraphicsItem() {
	}
	 
	 public GraphicsItem(int color) {
		 this.shapeColor=color;
	 }
		 
	 
	/**
	 * 设置图形为点 
	 * @param geoPoint 地理坐标
	 * @param pixel 点的像素大小
	 */
	public void setPoint(GeoPoint geoPoint,
            int pixel){
		this.type=POINT;
		this.center=geoPoint;
		this.radius=pixel;
		bounds=new Bounds(geoPoint);
	}

	public  void setPaint(Paint paint){
		this.paint=paint;
	}
	public Paint getPaint(){
		return paint;
	}
	public int getColor(){
		return shapeColor;
	}
	
	/**
	 * 设置图形为线 
	 * @param geoPoints 绘制线的点集地理坐标
	 */
	public void setPolyLine(GeoPoint[] geoPoints){
		this.type=POLYLINE;
		this.pts=geoPoints;
		calculateBounds();
	}
	
	private void  calculateBounds(){
		if(pts==null){
			bounds=new Bounds(center);
		}else{
			bounds=new Bounds(pts[0]);
			for(int i=1;i<pts.length;i++){
				bounds.extend(pts[i]);
			}
		}
		if(bounds.getSize()!=0){  //计算最小出现的层级数
			for(int i=0;i<18;i++){
				if((int)(bounds.getHeight()*(1<<i)/0.3519572078484874)>1&&
						(int)(bounds.getWidth()*(1<<i)/0.3519572078484874)>1){
					minZoomLevel=i;
					break;
				}
			}
		}
	}
	
	/**
	 * 设置图形为填充多边形 
	 * @param geoPoints
	 */
	public void setPolygon(GeoPoint[] geoPoints){
		this.type=POLYGON;
		this.pts=geoPoints;
		calculateBounds();
	}
	
	public void setPolygon(double[] longtitude,double[] latitude){
		
	}
	
	public void setColor(int color){
		this.shapeColor=color;
	}
	
	/**
	 * 设置图形为圆 
	 * @param geoPoint 地理坐标
	 * @param radius 圆的半径，单位：米
	 */
	public void setCircle(GeoPoint geoPoint,
            int radius){
		this.type=CIRCLE;
		this.center=geoPoint;
		this.radius=radius;
		calculateBounds();
	}
	
	/**
	 * 设置图形为矩形
	 * @param pt1  矩形左上角地理坐标
	 * @param pt2 矩形右下角地理坐标
	 */
	public void setRectangle(GeoPoint pt1,
            GeoPoint pt2){
		this.type=RECTANGLE;
		this.pts=new GeoPoint[]{pt1,pt2};
		calculateBounds();
	}
	
	public Bounds getBounds(){
		return bounds;
	}
}
