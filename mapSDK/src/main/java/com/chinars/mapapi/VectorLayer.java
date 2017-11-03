package com.chinars.mapapi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.chinars.mapapi.utils.DistanceUtil;
import com.chinars.mapapi.utils.GeoAlgorithm;
import com.chinars.mapapi.utils.LogUtils;
import com.chinars.mapapi.utils.PolygonClip;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.graphics.PorterDuff;


public class VectorLayer extends AbstractMapLayer {

	/*形状颜色*/
	private int defaultColor=0xff0000;
	Paint paint=new Paint();
	private Bounds bounds=new Bounds() ;
	private List<GraphicsItem> graphicsItems=new ArrayList<GraphicsItem>();
	private List<HoledPolygon> holedPolygons=new ArrayList<HoledPolygon>();
	private Xfermode clearMode= new PorterDuffXfermode(PorterDuff.Mode.CLEAR);
	private Xfermode normarlMode= new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER);
	private MapView mapView;
	PolygonClip cliper=new PolygonClip();
	
	public VectorLayer(String name, int minZoom, int maxZoom, GeoPoint topLeft,
			GeoPoint rightBottom) {
		super(name, minZoom, maxZoom, topLeft, rightBottom);
	}

	@Override
	public LayerType getType() {
		return LayerType.Vector;
	}

	@Override
	public String getTileUri(int tileX, int tileY, int zoomLevel) {
		return null;
	}
	
	public void setMapView(MapView mapView){
		this.mapView=mapView;
	}
	
	/**
	 * 增加图形
	 * @param item
	 */
	public void addGraphic(GraphicsItem item){
		graphicsItems.add(item);
		bounds.extend(item.bounds);
	}
	
	public void addHoledPolygon(HoledPolygon holedPolygon){
		synchronized (holedPolygons) {
			holedPolygons.add(holedPolygon);
		}
		bounds.extend(holedPolygon.bounds);
	}
	
	public void removeHoledPolygon(HoledPolygon holedPolygon){
		synchronized (holedPolygons) {
			holedPolygons.remove(holedPolygon);
		}
	}
	
	public void clearHoledPolygon(){
		synchronized (holedPolygons) {
			holedPolygons.clear();
		}
	}
	
	public  List<HoledPolygon> getAllHoledPolygon(){
		return holedPolygons;
	}
	
	public java.util.List<GraphicsItem> getAllGraphicItems(){
		return graphicsItems;
	}
	
	public GraphicsItem getGraphic(int index){
		return graphicsItems.get(index);
	}
	
	public void removeLast(){
		if(!graphicsItems.isEmpty()){
			graphicsItems.remove(graphicsItems.size()-1);
		}
	}
	
	public boolean removeGraphic(GraphicsItem item){
		return graphicsItems.remove(item);
	}
	
	public void removeAll(){
		graphicsItems.clear();
	}
	
	public boolean draw(Canvas canvas, Bounds viewBounds,Projection p) {
		if(!viewBounds.intersectsBounds(bounds)){
			return false;
		}
		cliper.setWindow(viewBounds);
		paint.setColor(defaultColor);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setDither(true);
		paint.setStrokeWidth(2);
		int zoomLevel=mapView.getZoomLevel();
		Point point = new Point();
		Path path =new Path();
		for(Iterator<GraphicsItem> itr=graphicsItems.iterator();itr.hasNext();){
			GraphicsItem item=itr.next();
			if(item.minZoomLevel>zoomLevel||!viewBounds.intersectsBounds(item.bounds)){
				continue;
			}
			paint.setColor(item.shapeColor);
			switch(item.type){
			case GraphicsItem.POINT:
				point=p.toPixels(item.center);
				paint.setStrokeWidth(item.radius);
				canvas.drawPoint(point.x, point.y, paint);
				break;
			case GraphicsItem.CIRCLE:
				point=p.toPixels(item.center);
				paint.setStrokeWidth(0);
				int distance=DistanceUtil.getDistance(item.center, p.fromPixels(point.x+10, point.y));
				canvas.drawCircle(point.x, point.y, item.radius/distance*10, paint);
				break;
			case GraphicsItem.RECTANGLE:
				Point leftTop=p.toPixels(item.pts[0]);
				Point rigthBottom=p.toPixels(item.pts[1]);
				canvas.drawRect(leftTop.x, leftTop.y, rigthBottom.x, rigthBottom.y, paint);
				break;
			case GraphicsItem.POLYGON:
				path.reset();
				GeoPoint drawPoints[]=null;
				if(zoomLevel-item.minZoomLevel>7){ //显示的形状过大，需要裁剪
					drawPoints=cliper.clipPolyGon(item.pts);
					if(drawPoints.length<3){
						continue;
					}
				}else{
					drawPoints=item.pts;
				}
				point=p.toPixels(drawPoints[0]);
				path.moveTo(point.x, point.y);
				for(int i=1;i<drawPoints.length;i++){
					point=p.toPixels(drawPoints[i]);
					path.lineTo(point.x, point.y);
				}
				path.close();
				canvas.drawPath(path, paint);
				break;
			case GraphicsItem.POLYLINE:
				Point start,end;
				path.reset();
				start=p.toPixels(item.pts[0]);
				path.moveTo(start.x, start.y);
				for(int i=1;i<item.pts.length;i++){
					end=p.toPixels(item.pts[i]);
					canvas.drawLine(start.x, start.y, end.x, end.y, paint);
					start=end;
				}
				break;
			case GraphicsItem.OVAL:
				break;
			default:
				break;
			}
		}
		synchronized (holedPolygons) {
			WmtsProjection wp=((WmtsProjection)p).update();
			for(Iterator<HoledPolygon> itr=holedPolygons.iterator();itr.hasNext();){
				HoledPolygon holedPolygon=itr.next();;
				if(holedPolygon.minZoomLevel>zoomLevel||!viewBounds.intersectsBounds(holedPolygon.bounds)){
					continue;
				}
				int length=holedPolygon.polygons.length;
				paint.setColor(holedPolygon.shapeColor);
				GeoPoint drawPoints[][]=new GeoPoint[length][];
				if(zoomLevel-holedPolygon.minZoomLevel>8){ //显示的形状过大，需要裁剪
					for(int i=0;i<length;i++){
						drawPoints[i]=cliper.clipPolyGon(holedPolygon.polygons[i]);
					}
				}else{
					drawPoints=holedPolygon.polygons;
				}
				int d=8-zoomLevel;
				d=d>0?d:1;
//				LogUtils.d("length="+drawPoints[0].length+" d:"+d);
				for(int i=0;i<length;i++){
					if(drawPoints[i].length/d<4){
						continue;
					}
					path.reset();
					wp.fastToPixels(drawPoints[i][0],point);
					path.moveTo(point.x, point.y);
					int len=drawPoints[i].length;
					for(int j=1;j<len;j=j+d){
						wp.fastToPixels(drawPoints[i][j],point);
						path.lineTo(point.x, point.y);
					}
					if(len/d!=0){
						wp.fastToPixels(drawPoints[i][len-1],point);
						path.lineTo(point.x, point.y);
					}
					if(i>0){
						paint.setColor(Color.TRANSPARENT);
						paint.setXfermode(clearMode);
						canvas.drawPath(path, paint);
					}else{
						canvas.drawPath(path, paint);
					}
				}
				paint.setXfermode(normarlMode);
			}
		}
		return false;
	}
	
}
