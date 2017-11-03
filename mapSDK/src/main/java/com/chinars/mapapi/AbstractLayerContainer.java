package com.chinars.mapapi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import com.chinars.mapapi.utils.LogUtils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.View;

abstract class AbstractLayerContainer extends View implements
		LayerContainer {
	protected MapView mapView;
	protected MapLayer baselayer;//底图(基础图层），指定地图的范围
	protected List<MapLayer> overMapLayers;
	protected int overMapNum;//叠加地图源的数量，overMapInfos的大小
	protected GeoPoint center;//MapView中心点的经纬度
	protected GeoPoint bufferCenter;//缓存中中心点的经纬度
	protected AbstractProjection prj;//当前地图投影
	protected AbstractProjection bufferPrj;//缓冲画布投影
	protected int width;//MapView的宽
	protected int height;//MapView的高
	protected int bufferWidth=0,bufferHeight;//缓冲画布的宽高
	protected int memWidth,memHeight;//屏幕对应在缓存画布中的大小
	protected Bitmap bufferBitmap;//缓冲数据
	protected Canvas bufferCanvas;//缓冲画布
	protected int tileSize=256;
	protected Rect baseMapBound;//底图的范围
	protected List<Rect> overMapBounds;//叠加图的范围
	protected int zoomLevel=-1,minZoom=0,maxZoom=0;
	protected Bitmap placeHolder;
	protected Paint paint=null;
	protected int offsetX,offsetY;//瓦片在缓存中的偏移
	protected int maxOffsetX,maxOffsetY;
	protected WebImageCache cache;
	protected boolean visible=true;
	protected boolean[] overMapVisibles=new boolean[10];//叠加地图是否在可视区域
	protected boolean destroyed=false;
	protected HashSet<String> nameSet=new HashSet<String>();
	protected Rect srcRect=new Rect();//
	protected Rect dstRect=new Rect();//
	protected Rect screenRect=new Rect();//整个屏幕区域
	private  Rect partRect=null ;
	private  double noDisplayRatio=0;
	protected boolean refreshed=false;
	protected double dpiRatio;
	protected double bufferZoom;//画布的放大显示倍数
	protected double mapRatio=0;
	protected double bufferRatio=0;
	protected int additionWidth;
	
	public AbstractLayerContainer(MapView mapView,MapLayer baseMaplayer ,Bitmap placeHolder){
		super(mapView.getContext());
		this.mapView=mapView;
		this.baselayer=baseMaplayer;
		overMapLayers=new ArrayList<MapLayer>();
		overMapBounds=new ArrayList<Rect>();
		overMapNum=0;
		cache=mapView.getCache();
		this.placeHolder=placeHolder;
		nameSet.add(baselayer.getName());
		minZoom=baseMaplayer.getMinZoom();
		maxZoom=baseMaplayer.getMaxZoom();
		dpiRatio=mapView.getDpiRatio();
		tileSize=baseMaplayer.getTileSize();
	}
	
	
	public boolean isVisible(){
		return visible;
	}
	
	public void setVisible(boolean visible){
		this.visible=visible;
		invalidate();
	}
	
	public void  onResume(){
		if(cache==null){
			cache=mapView.getCache();
			if(bufferWidth>0){
				calBufferSize();
				bufferBitmap = Bitmap.createBitmap(bufferWidth,bufferHeight, Bitmap.Config.ARGB_8888);//创建内存位图
				bufferCanvas = new Canvas(bufferBitmap);//创建绘图画布
				refreshed=false;
			}
		}
	}
	
	public void onPause(){
		cache=null;
		bufferCenter=null;
		if(bufferBitmap!=null){
			bufferBitmap.recycle();
		}
		bufferBitmap=null;
		bufferCanvas=null;
	}
	
	@Override
	public Projection getProjection() {
		return prj;
	}
	
	/**
	 * 重新加载数据
	 */
	public void refreshData(){
		bufferCenter=null;
		refresh();
		postInvalidate();
	}
	
	private void calBufferSize(){
		 ActivityManager am = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
		 ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
	     am.getMemoryInfo(mi);
	     if(mi.availMem/(1024*1024)>100){
	    	 additionWidth=512;
	     }else{
	    	 additionWidth=256;
	     }
	     bufferWidth=(int)(width/dpiRatio)+additionWidth;
	     bufferHeight=(int)(height/dpiRatio)+256;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		width=w;
		height=h;
		screenRect.set(0, 0, width, height);
		mapRatio=0;
		calBufferSize();
		if(bufferWidth>2048||bufferHeight>2560){
			throw new RuntimeException("bufferCanvas is too large");
		}
		bufferBitmap = Bitmap.createBitmap(bufferWidth,bufferHeight, Bitmap.Config.ARGB_8888);//创建内存位图
		bufferCanvas = new Canvas(bufferBitmap);//创建绘图画布
		LogUtils.d("mapView:"+width+","+height);
		LogUtils.d("buffer:"+bufferWidth+","+bufferHeight);
	}
	
	@Override
	public void refresh() {
		refreshed=false;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(!visible||cache==null){
			return;
		}
		if(!refreshed){
			if(Math.abs(mapRatio-mapView.getRatio())>0.00000001){
				mapRatio=mapView.getRatio();
				bufferRatio=mapRatio*mapView.getTileZoom();
				if(zoomLevel!=mapView.getZoomLevel()){
					zoomLevel=mapView.getZoomLevel();
					bufferCenter=null;
					baseMapBound=bufferPrj.getBoundingBox(baselayer.getGeoBounds());
					overMapBounds.clear();
					for(int i=0;i<overMapNum;i++){
						Rect bound=bufferPrj.getBoundingBox(overMapLayers.get(i).getGeoBounds());
						overMapBounds.add(bound);
					}
				}
				if(zoomLevel>maxZoom||zoomLevel<minZoom){
					bufferCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); 
					return;
				}
				bufferZoom=mapView.getTileZoom()/dpiRatio;
				memWidth=(int)(width/dpiRatio/bufferZoom);
				memHeight=(int)(height/dpiRatio/bufferZoom);
				maxOffsetX=bufferWidth-memWidth;
				maxOffsetY=bufferHeight-memHeight;
			}
			center=mapView.getMapCenter();
			if(zoomLevel>maxZoom||zoomLevel<minZoom){
				return;
			}
			onRefresh();
			refreshed=true;
		}
		if(partRect==null){
			srcRect.set(offsetX, offsetY,offsetX+memWidth, offsetY+memHeight);
			canvas.drawBitmap(bufferBitmap,srcRect, screenRect,paint);
		}else {
			srcRect.set(offsetX, offsetY+(int)(noDisplayRatio*memHeight),offsetX+memWidth, offsetY+memHeight);
			canvas.drawBitmap(bufferBitmap,srcRect, partRect,paint);
		}

	}
	
	/**
	 * 在该方法里实现具体的绘制代码
	 */
	abstract public void onRefresh();
	
	
	public void setLayerAlpha(int alpha){
		if(paint==null){
			paint= new Paint();
		}
		paint.setAlpha(alpha);
		invalidate();
	}

	public void setStartHeight(int h){
			if(height==0){
				partRect=null;
			}else {
				partRect=new Rect(0,h,width,height);
				noDisplayRatio=h*1.0/height;
			}
		invalidate();
	}

	/**
	 *
	 * @param ratio
     */
	public void  setDisplayRatio(double ratio){
		if(ratio>0&&ratio<1){
			noDisplayRatio=1-ratio;
			partRect=new Rect(0,(int)(height*noDisplayRatio),width,height);
		}else {
			partRect=null;
		}
		invalidate();
	}

	@Override
	public boolean isBaseLayer(MapLayer layer) {
		return layer==baselayer;
	}

	public List<MapLayer> getOverMapLayers() {
		return overMapLayers;
	}

	@Override
	public boolean addLayer(MapLayer layer) {
		if(contains(layer)){
			return false;
		}
		overMapLayers.add(layer);
		overMapNum++;
		if(overMapNum>10){
			overMapVisibles=new boolean[overMapNum];
		}
		if(bufferPrj!=null){
			Rect bound=bufferPrj.getBoundingBox(layer.getGeoBounds());
			overMapBounds.add(bound);
		}
		minZoom=minZoom<layer.getMinZoom()?minZoom:layer.getMinZoom();
		maxZoom=maxZoom>layer.getMaxZoom()?maxZoom:layer.getMaxZoom();
		return true;
	}

	@Override
	public void addlayers(List<MapLayer> layers) {
		for(MapLayer layer: layers){
			addLayer(layer);
		}
	}

	@Override
	public boolean removeLayer(MapLayer layer) {
		/**
		 * 如果删除的是基础图层，那么将销毁容器
		 */
		if(isBaseLayer(layer)){
			destroy();
			return true;
		}
		if(overMapLayers!=null&&overMapLayers.remove(layer)){
			overMapNum--;
			nameSet.remove(layer.getName());
			return true;
		}else{
			return false;
		}
	}
	
	@Override
	public boolean replaceLayer(MapLayer newLayer, MapLayer oldLayer) {
		if(contains(newLayer)||!contains(oldLayer)){
			return false;
		}
		if(baselayer==oldLayer){
			baselayer=newLayer;
		}
		int index=overMapLayers.indexOf(oldLayer);
		if(index>=0){
			overMapLayers.remove(index);
			overMapLayers.add(index, newLayer);
		}
		recalMaxMinZoom();
		visible=true;
		refreshData();
		return true;
	}
	
	/**
	 * 重新计算最大和最小放大层数
	 */
	private void  recalMaxMinZoom(){
		maxZoom=baselayer.getMaxZoom();minZoom=baselayer.getMinZoom();
		for(int i=0;i<overMapNum;i++){
		    MapLayer mapInfo=overMapLayers.get(i);
			minZoom=minZoom<mapInfo.getMinZoom()?minZoom:mapInfo.getMinZoom();
			maxZoom=maxZoom>mapInfo.getMaxZoom()?maxZoom:mapInfo.getMaxZoom();
		}
	}
	
	public void destroy(){
		baselayer=null;
		overMapLayers=null;
		overMapBounds=null;
		if(bufferBitmap!=null){
			bufferBitmap.recycle();
		}
		bufferBitmap=null;
		bufferCanvas=null;
		nameSet=null;
		destroyed=true;
	}
	
	public boolean isDestroyed(){
		return destroyed;
	}
	
	@Override
	public boolean removeLayerByName(String name) {
		if(nameSet.contains(name)){
			if(baselayer.getName().equals(name)){
				destroy();
				return true;
			}else{
				for(MapLayer layer:overMapLayers){
					if(layer.getName().equals(name)){
						overMapLayers.remove(layer);
						overMapNum--;
						nameSet.remove(layer.getName());
						recalMaxMinZoom();
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean addLayer(MapLayer layer, int alpha) {
		
		return false;
	}

	@Override
	public boolean contains(MapLayer layer) {
		return (baselayer==layer)||(overMapLayers!=null&&overMapLayers.contains(layer));
	}

	@Override
	public boolean contains(String layerName) {
		return nameSet.contains(layerName);
	}

	@Override
	public int getNumLayers() {
		if(baselayer==null){
			return 0;
		}
		return overMapNum+1;
	}
	
	class ImageTask{
		String baseUrl;
		String[] overMapUrls;
		int leftPadding;
		int topPadding;
		ImageTask(String baseUrl,int leftPadding,int topPadding){
			this.baseUrl=baseUrl;
			overMapUrls=null;
			this.leftPadding=leftPadding;
			this.topPadding=topPadding;
		}

		ImageTask(String baseUrl,String[] overMapUrls, int leftPadding,int topPadding){
			this.baseUrl=baseUrl;
			this.overMapUrls=overMapUrls;
			this.leftPadding=leftPadding;
			this.topPadding=topPadding;
		}
		boolean drawImage(){
			boolean finished=true;
			Bitmap baseMapTile=cache.get(baseUrl,0);
			dstRect.set(leftPadding, topPadding, leftPadding+tileSize, topPadding+tileSize);
			if(baseMapTile!=null){
				bufferCanvas.save();
				bufferCanvas.clipRect(leftPadding, topPadding, leftPadding+tileSize, topPadding+tileSize);
				bufferCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
				bufferCanvas.restore();
				bufferCanvas.drawBitmap(baseMapTile, null, dstRect, paint); //绘制底图
			}else{
				finished=false;
			}
			for(int i=0;i<overMapNum;i++){ 
				if(overMapVisibles[i]){
					Bitmap overTile=cache.get(overMapUrls[i], 0);
					if(overTile!=null){
						bufferCanvas.drawBitmap(overTile, null, dstRect, paint);// 绘制叠加地图
					}else{
						finished=false;
					}
				}
			}
			return finished;
		}
	}
}
