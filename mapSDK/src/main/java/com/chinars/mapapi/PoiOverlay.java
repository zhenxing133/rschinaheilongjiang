package com.chinars.mapapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.chinars.mapapi.search.PoiClass;
import com.chinars.mapapi.search.PoiInfo;
import com.chinars.mapapi.utils.LogUtils;
import com.chinars.mapapi.utils.ResourseUtil;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * POI图层
 * @author Administrator
 *
 */
public class PoiOverlay extends Overlay {
	private static final long serialVersionUID = -1949456287913789122L;
	private static final String TAG = "PoiOverlay";

	public final static String [] greenDrawableIds=new String[]{"poi_a_green.png","poi_b_green.png","poi_c_green.png","poi_d_green.png",
		"poi_e_green.png","poi_f_green.png","poi_g_green.png","poi_h_green.png","poi_i_green.png","poi_j_green.png","poi_green.png"};
	public final static String [] redDrawableIds=new String[]{"poi_a_red.png","poi_b_red.png","poi_c_red.png","poi_d_red.png",
		"poi_e_red.png","poi_f_red.png","poi_g_red.png","poi_h_red.png","poi_i_red.png","poi_j_red.png","poi_red.png"};
	private  Drawable[] greenDrawables;
	private  Drawable[] redDrawables;
	private  int activeIndex=-1;
	private Drawable normal;
	private Drawable active;
	private int size;
	private int maxSize;
	private  PoiTapListener poiTapListenner;
	private  List<PoiInfo> data;
	private double tileZoom=-1;
	private int zoomLevel=1;
	private Point[] drawPt;
	private MapView mapView;

	

	/**
	 * 构造一个空白的POI图层，之后可以通过setData函数添加POI数据
	 * @param mapView Mapview对象
	 */
	public PoiOverlay(MapView mapView){
		this.mapView=mapView;
		this.maxSize=16;
		this.size=0;
		data=new ArrayList<>();
		drawPt=new Point[maxSize];
		greenDrawables=new Drawable[greenDrawableIds.length];
		redDrawables=new Drawable[redDrawableIds.length];
		for(int i=0;i<greenDrawableIds.length;i++){
			greenDrawables[i]=ResourseUtil.getDrawableFromAssetsForDp(mapView.getContext(), greenDrawableIds[i]);
		}
		for(int i=0;i<redDrawableIds.length;i++){
			redDrawables[i]=ResourseUtil.getDrawableFromAssetsForDp(mapView.getContext(), redDrawableIds[i]);
		}
	}

	/**
	 * 构造函数
	 * @param mapView Mapview对象
	 * @param data   POI点列表
	 */
	public PoiOverlay(MapView mapView,List<PoiInfo> data){
		this(mapView);
		this.data=data;
		size=data.size();
		this.maxSize=2*size;
		drawPt=new Point[maxSize];
	}
	
	
	/**
	 * 构造函数
	 * @param mapView Mapview对象
	 * @param data   POI点列表
	 * @param type  POI类别
	 */
	public PoiOverlay(MapView mapView,List<PoiInfo> data,int type){
		this.mapView=mapView;
		this.data=data;
		size=data.size();
		this.maxSize=size;
		switch(type){
		case PoiClass.TYPE_TRANSPORTATION_BICYCLE:
			normal=ResourseUtil.getDrawableFromAssets(mapView.getContext(), "poi_bicycle_normal.png");
			active=ResourseUtil.getDrawableFromAssets(mapView.getContext(), "poi_bicycle_active.png");
			break;
		case PoiClass.TYPE_COMMUNITY_WIFI:
			normal=ResourseUtil.getDrawableFromAssets(mapView.getContext(), "poi_wifi_normal.png");
			active=ResourseUtil.getDrawableFromAssets(mapView.getContext(), "poi_wifi_active.png");
			break;
		case PoiClass.TYPE_TRANSPORTATION_BUS_STATION:
			normal=ResourseUtil.getDrawableFromAssets(mapView.getContext(), "poi_bus_normal.png");
			active=ResourseUtil.getDrawableFromAssets(mapView.getContext(), "poi_bus_active.png");
			break;
		default:
			normal=ResourseUtil.getDrawableFromAssets(mapView.getContext(), redDrawableIds[10]);
			active=ResourseUtil.getDrawableFromAssets(mapView.getContext(), greenDrawableIds[10]);
			break;
		}
		drawPt=new Point[size];
	}
	
	/**
	 * 设置POI图层显示的点集
	 * @param data POI点列表
	 */
	public void setData(List<PoiInfo> data){
		this.data=data;
		size=data.size();
		this.maxSize=size*2;
		drawPt=new Point[maxSize];
		tileZoom=-1;
	}

	public void addPoiInfo(PoiInfo poiInfo){
		if(size==maxSize){
			maxSize*=2;
			drawPt=new Point[maxSize];
		}
		data.add(poiInfo);
		size++;
	}

	public void notifyDataChanged(){
		tileZoom=-1;
	}


	
	public int size(){
		return size;
	}

	
	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		Projection prj=(Projection) mapView.getProjection();
		Point tapPoint =prj.geoPointToPoint(p);
		int pixel,minPixel=100;
		int dx,dy;
		int tapIndex=0;
		for(int i=0;i<size;i++ ){
			dx=tapPoint.x - drawPt[i].x;
			dy=tapPoint.y - drawPt[i].y;
			if(dx>minPixel||dy>minPixel||-dx>minPixel||-dy>minPixel){
				continue;
			}
			pixel=(int) Math.sqrt(dx*dx+dy*dy);
			if(pixel<minPixel){
				minPixel=pixel;
				tapIndex=i;
			}
		}
		if(minPixel<25){
			performTap(tapIndex);
		}else{
			activeIndex=-1;
		}
		return false;
	}

	public void setPoiTapListenner(PoiTapListener listenner){
		poiTapListenner=listenner;
	}

	public boolean performTap(int index) {
		activeIndex=index;
		if(poiTapListenner!=null){
			poiTapListenner.onPoiTap(index,data.get(index));
		}
		return true;
	}

	@Override
	public boolean draw( Canvas canvas,  MapView mapView,  boolean shadow,long when) {
		Projection prj=(Projection) mapView.getProjection();
		Point c = prj.geoPointToPoint(mapView.getMapCenter());
		int x,y;
		int w=mapView.getWidth()/2,h=mapView.getHeight()/2;
		if(Math.abs(mapView.getZoomLevel()-zoomLevel+mapView.getTileZoom()-tileZoom)>0.1){
			for(int i=0;i<size;i++){
				drawPt[i]=prj.geoPointToPoint(data.get(i).geoPoint);
			}
			tileZoom=mapView.getTileZoom();
			zoomLevel=mapView.getZoomLevel();
		}
		for(int i=0;i<size;i++){
			x=drawPt[i].x+w-c.x;
			y=drawPt[i].y+h-c.y;
			if(x>0&&y>0&&x<2*w&&y<2*h){
				if(data.get(i).marker!=null){
					drawAt(canvas, data.get(i).marker, x,y, shadow);
				}else {
					if(redDrawables!=null){
						if(i<10){
							drawAt(canvas, redDrawables[i], x,y, shadow);
						}else{
							drawAt(canvas, redDrawables[10], x,y, shadow);
						}
					}else{
						drawAt(canvas, normal, x,y, shadow);
					}
				}
			}
		}
		if(activeIndex!=-1){
			if(data.get(activeIndex).marker==null){
				if(greenDrawables!=null){
					drawAt(canvas, greenDrawables[10], drawPt[activeIndex].x+w-c.x, drawPt[activeIndex].y+h-c.y, shadow);
				}else{
					drawAt(canvas, active, drawPt[activeIndex].x+w-c.x,drawPt[activeIndex].y+h-c.y, shadow);
				}
			}
		}
		return true;
	}
	
	public void animateTo(int index){
		mapView.setMapCenter(data.get(index).geoPoint);
		mapView.refresh();
	}
}
