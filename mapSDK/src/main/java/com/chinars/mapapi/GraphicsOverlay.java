package com.chinars.mapapi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.chinars.mapapi.utils.DistanceUtil;
import com.chinars.mapapi.utils.GeoAlgorithm;
import com.chinars.mapapi.utils.PolygonClip;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
/**
 * 图形叠加层,支持点，线，圆，椭圆和简单多边形
 * @author rsclouds
 *
 */
public class GraphicsOverlay extends Overlay{

	private static final long serialVersionUID = -278217021028305634L;
	
	/*形状颜色*/
	private int defaultColor=0xff0000;
	Paint paint=new Paint();
	private Bounds bounds=new Bounds() ;
	private List<GraphicsItem> graphicsItems=new ArrayList<GraphicsItem>();
	private int maxShowLevel=10;
	PolygonClip cliper=new PolygonClip();
	/**
	 * 增加图形
	 * @param item
	 */
	public void addGraphic(GraphicsItem item){
		graphicsItems.add(item);
		bounds.extend(item.bounds);
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
	
	public void setMaxShowLevel(int level){
		this.maxShowLevel=level;
	}
	
	public boolean removeGraphic(GraphicsItem item){
		return graphicsItems.remove(item);
	}
	
	public void removeAll(){
		graphicsItems.clear();
	}
	
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		Bounds viewBounds=mapView.getViewBounds();
		if(!viewBounds.intersectsBounds(bounds)){
			return false;
		}
		cliper.setWindow(viewBounds);
		paint.setColor(defaultColor);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setDither(true);
		paint.setStrokeWidth(2);
		Projection p=mapView.getProjection();
		int zoomLevel=mapView.getZoomLevel();
		Point point = null;
		for(Iterator<GraphicsItem> itr=graphicsItems.iterator();itr.hasNext();){
			GraphicsItem item=itr.next();
			if(item.minZoomLevel>zoomLevel||!viewBounds.intersectsBounds(item.bounds)){
				continue;
			}
			paint.setColor(item.shapeColor);
			Paint itemPaint=item.getPaint();
			if(itemPaint==null){
				itemPaint=paint;
			}
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
				canvas.drawCircle(point.x, point.y, item.radius/distance*10, itemPaint);
				break;
			case GraphicsItem.RECTANGLE:
				Point leftTop=p.toPixels(item.pts[0]);
				Point rigthBottom=p.toPixels(item.pts[1]);
				canvas.drawRect(leftTop.x, leftTop.y, rigthBottom.x, rigthBottom.y, itemPaint);
				break;
			case GraphicsItem.POLYGON:
				Path path=new Path();
				GeoPoint drawPoints[]=null;
				if(zoomLevel-item.minZoomLevel>7){ //显示的形状过大，需要裁剪
					if(zoomLevel>maxShowLevel){
						continue;
					}
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
				canvas.drawPath(path, itemPaint);
				break;
			case GraphicsItem.POLYLINE:
				Point start,end;
				path=new Path();
				start=p.toPixels(item.pts[0]);
				path.moveTo(start.x, start.y);
				for(int i=1;i<item.pts.length;i++){
					end=p.toPixels(item.pts[i]);
					canvas.drawLine(start.x, start.y, end.x, end.y, itemPaint);
					start=end;
				}
				break;
			case GraphicsItem.OVAL:
				break;
			default:
				break;
			}
		}
		return false;
	}
	
}
