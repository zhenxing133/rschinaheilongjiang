package com.chinars.mapapi;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chinars.mapapi.offline.RSOfflineMap;
import com.chinars.mapapi.utils.LogUtils;
import com.chinars.mapapi.utils.ResourseUtil;
import com.chinars.mapapi.widget.BaseWidget;
import com.chinars.mapapi.widget.MyLocationControls;
import com.chinars.mapapi.widget.ZoomControls;
/**
 * 显示地图的View。
 * @author rsclouds
 *
 */
public class MapView 
    extends ViewGroup 
    implements ViewManager, ViewParent, Drawable.Callback, KeyEvent.Callback {
    private static final int MAX_CACHE_SIZE = 101;
    private  double maxResolution;//第0层分辨率
    private double tileZoom=1;//切片缩放比例
    private double tileZoomArray[]=new double[]{1,1.1,1.21,1.33,1.46,1.6};//切片缩放比例数组
    private int tileZoomIndex=0;//切片缩放比例所在数字的位置
    private GeoPoint center = new GeoPoint(0, 0);
    private MyLocationOverlay myLocation=null;
    private int zoomLevel = 1,minZoomLevel=0,maxZoomLevel=1,maxUserLevel=20,minUserLevel=0;
    private int width, height;
    private List<Overlay> overlays = new ArrayList<Overlay>();
    private WebImageCache cache;
    private Bitmap placeholder;
    private GestureDetector mDetector; 
    private float beforeLenght, afterLenght;// 两触点距离
    private boolean isZooming=false,zoomMode=false;
    private boolean isFling;
    private int touchDownX;
    private int touchDownY;
    private int moveDistance=0;
    private List<BaseWidget> widgets=new ArrayList<BaseWidget>();
    private BaseWidget logo=new BaseWidget(getContext());
    private MyLocationControls myLocationControls=new MyLocationControls(getContext());
    private ZoomControls zoomControls = new ZoomControls(getContext());
	private MapTouchListener mapTouchListenner;
	private MapViewRefreshListener mapViewRefreshListener;
	private Runnable onSingleUpListener;
	private Handler handler;
	private OverlayView overlayView;
	private List<MapLayer> mapLayers=new ArrayList<MapLayer>();//图层
	private List<AbstractLayerContainer> layerContainers=new ArrayList<AbstractLayerContainer>();//图层容器
	private double dpiRatio=1.0;//控件和地图根据屏幕DPI的放大倍数
	private CountDownTimer timer=null;
	private boolean centerChanged=true;
	private boolean busy=false; //是否处于不停变化状态
	private Bounds maxBounds=new Bounds(-180,-90,180,90);
    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }
    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
    public MapView(Context context, String apiKey) {
        super(context);
        init(context, null);
    }
    private void init(Context context, AttributeSet attrs) {
    	maxResolution=MapLayerConstant.MAX_RESOLUTION_DOT_3519572078484874;
    	DisplayMetrics metric = new DisplayMetrics();
		WindowManager mWm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		mWm.getDefaultDisplay().getMetrics(metric);
		int densityDpi = metric.densityDpi;  // 像素密度（像素）
		dpiRatio=densityDpi/240.0;
		if(dpiRatio<=1){
			dpiRatio=1.2;
		}
		LogUtils.d("zoomRate"+dpiRatio);
        placeholder = ResourseUtil.getBitmapFromAssets(context, "blank_trans.png");
        zoomControls.setOnZoomOutClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if(tileZoom<=1){
            		setZoomLevel(zoomLevel - 1);
            	}
            	tileZoom=1;
            	checkZoomControl();
            	refreshNow();
            }
        });
        zoomControls.setOnZoomInClickListener(new OnClickListener() {
            public void onClick(View v) {
            	if(tileZoom>=1){
            		setZoomLevel(zoomLevel + 1);
            	}
            	tileZoom=1;
                checkZoomControl();
               refreshNow();
            }
        });
        ImageView logoImage=new ImageView(context);
        logoImage.setImageDrawable(ResourseUtil.getDrawableFromAssets(context, "logo.png"));
        LinearLayout.LayoutParams layLout=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        logo.setLayoutParams(layLout);
        logo.addView(logoImage);
        myLocationControls.setOnLocationListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(myLocation!=null){
					setMapCenter(myLocation.getMyLocation());
					refreshNow();
				}
			}
		});
        mDetector=new GestureDetector(context, new MyGestureListener());
        handler=new Handler(Looper.getMainLooper());
        
        addWidget(logo);
        addWidget(myLocationControls);
        addWidget(zoomControls);
        
        overlayView=new OverlayView(context);
        addView(overlayView);
        onResume();
    }
   
    Bitmap getPlaceholder(){
    	return placeholder;
    }
    
    /**
     * 当图层名变化时调用该方法重新加载数据
     * @param layer
     */
    public void  refreshData(MapLayer layer){
    	for(AbstractLayerContainer container:layerContainers){
    		if(container.contains(layer)){
    			container.refreshData();
    		}
    	}
    }

	public  boolean setLayerVisibility(MapLayer layer,boolean visible){
		if(contains(layer)){
			for(AbstractLayerContainer container:layerContainers){
				if(container.contains(layer)){
					container.setVisible(visible);
				}
			}
			return  true;
		}else {
			return  false;
		}
	}

	public  boolean setLayerAlpha(MapLayer layer,int alpha){
		if(contains(layer)){
			for(AbstractLayerContainer container:layerContainers){
				if(container.contains(layer)){
					container.setLayerAlpha(alpha);
				}
			}
			return  true;
		}else {
			return  false;
		}
	}

	public boolean setDisplayRatio(MapLayer layer,double ratio){
		if(contains(layer)){
			for(AbstractLayerContainer container:layerContainers){
				if(container.contains(layer)){
					container.setDisplayRatio(ratio);
					break;
				}
			}
			return  true;
		}else {
			return  false;
		}
	}

    /**
     * 添加独立的图层,每一个图层都使用单独的图层容器
     * @param layer
     */
    public boolean addLayer(MapLayer layer){
    	Bitmap holder=null;
    	if(mapLayers.contains(layer)){
    		return false;
    	}
    	if(layerContainers.isEmpty()){
    		holder=placeholder;
    	}
    	AbstractLayerContainer container=null;
		switch (layer.getType()) {
			case WMTS:
				container = new WmtsLayerContainer(this, (WmtsLayer) layer, holder);
				break;
			case WMS:
				container = new WmsLayerContainer(this, (WmsLayer) layer, holder);
				break;
			case Vector:
				container = new VectorLayerContainer(this, (VectorLayer) layer);
				break;
			case SphericalMercator:
				container = new SphericalMercatorLayerContainer(this,(SphericalMercatorLayer)layer,holder);
				break;
			default:
				break;
		}
    	if(container!=null){
    		mapLayers.add(layer);
    		layerContainers.add(container);
    		recalMaxMinZoom();
    		addView(container);
    		if(width>0){
    			container.layout(0, 0, width, height);
    		}
    		bringChildToFront(overlayView);
        	refresh();
    	}
    	return true;
    }

	public  void disableLogo(){
		removeWidget(logo);
		removeView(logo);
	}

   public boolean contains(MapLayer layer){
	   return mapLayers.contains(layer);
   }
    
    /**
     * 把一个图层添加到紧跟着另外一个图层之上
     * @param mapLayer 要添加的图层
     * @param below  下面的图层
     * @return
     */
    public boolean addLayer(MapLayer mapLayer,MapLayer below){
    	if(mapLayers.contains(mapLayer)||!mapLayers.contains(below)){
    		return false;
    	}
    	AbstractLayerContainer container=null;
    	switch(mapLayer.getType()){
    	case WMTS:
    		container=new WmtsLayerContainer(this,(WmtsLayer)mapLayer, null);
    		break;
    	case WMS:
    		container=new WmsLayerContainer(this,(WmsLayer)mapLayer, null);
		default:
			break;
    	}
    	int pos=0;
    	for(int i=0;i<layerContainers.size();i++){
			if(layerContainers.get(i).contains(below)){
				pos=i+1;
				break;
			}
		}
    	if(container!=null){
    		mapLayers.add(mapLayer);
    		layerContainers.add(pos,container);
    		recalMaxMinZoom();
    		addView(container);
    		if(width>0){
    			container.layout(0, 0, width, height);
    		}
    		for(int i=pos+1;i<layerContainers.size();i++){
    			bringChildToFront(layerContainers.get(i));
    		}
    		bringChildToFront(overlayView);
        	refresh();
    	}
    	return true;
    }

	public void setOnSingleUpListener(Runnable onSingleUpListener){
		this.onSingleUpListener=onSingleUpListener;
	}

    /**
     * 显示指定的区域地图，地图的中心点显示为bounds的中心点，自动根据bounds大小调节地图放大级别
     * @param bounds 要显示的区域
     */
    public void setViewBounds(Bounds bounds){
    	int zoom=0;
    	if(bounds.getWidth()>bounds.getHeight()){
    		zoom=(int) Math.log(maxResolution*width/bounds.getWidth());
    	}else{
    		zoom=(int) Math.log(maxResolution*height/bounds.getHeight());
    	}
    	setZoomLevel(zoom+1);
    	setMapCenter(bounds.getCenterLonLat());
    	
    }
    
    /**
     * 把一个图层添加到紧跟着另外一个图层之上,且显示的范围不会超过BaseLayer的范围
     * @param overlayer 要添加的图层
     * @param baseLayer 底图
     * @return
     */
    public boolean addOverLayer(MapLayer overlayer,MapLayer baseLayer){
    	if(mapLayers.contains(overlayer)||!mapLayers.contains(baseLayer)){
    		return false;
    	}
		mapLayers.add(overlayer);
    	for(AbstractLayerContainer container:layerContainers){
    		if(container.contains(baseLayer)){
    			container.addLayer(overlayer);
				container.refreshData();
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * 替换新的图层
     * @param newLayer 	新的图层
     * @param oldLayer 旧的图层
     * @return
     */
    public boolean replaceLayer(MapLayer newLayer,MapLayer oldLayer){
    	if(!mapLayers.contains(oldLayer)){
    		return false;
    	}
    	int index=mapLayers.indexOf(oldLayer);
		if(index>=0){
			mapLayers.remove(index);
			mapLayers.add(index, newLayer);
		}
    	for(LayerContainer container:layerContainers){
    		if(container.contains(oldLayer)){
    			container.replaceLayer(newLayer, oldLayer);
    			((AbstractLayerContainer)container).refreshData();
				if(newLayer.getMaxZoom()!=oldLayer.getMaxZoom()){
					recalMaxMinZoom();
				}
    			return true;
    		}
    	}
    	return true;
    }
    
    double getDpiRatio(){
    	return dpiRatio;
    }
    
    /**
	 * 重新计算最大和最小放大层数
	 */
	private void  recalMaxMinZoom(){
		minZoomLevel=mapLayers.get(0).getMinZoom();
		maxZoomLevel=mapLayers.get(0).getMaxZoom();
		for(int i=1;i<mapLayers.size();i++){
		    MapLayer layer=mapLayers.get(i);
		    minZoomLevel=Math.min(minZoomLevel, layer.getMinZoom());
		    maxZoomLevel=Math.max(maxZoomLevel, layer.getMaxZoom());
		}
		minZoomLevel=Math.max(minZoomLevel, minUserLevel);
		maxZoomLevel=Math.min(maxZoomLevel, maxUserLevel);
		if(zoomLevel<minZoomLevel){
			zoomLevel=minZoomLevel;
		}else if(zoomLevel>maxZoomLevel){
			zoomLevel=maxZoomLevel;
		}
	}
    
	/**
	 * @return 获取地图最大范围
	 */
	public  Bounds getMaxBounds(){ return  maxBounds;}

	/**
	 * 设置地图最大范围
	 */
	public void setMaxBounds(Bounds maxBounds){this.maxBounds=maxBounds;}
	
	public  boolean inMaxBounds(GeoPoint p){
		return  maxBounds.containsLonLat(p);
	}
	
   /**
    * 一次添加多个相同类型的图层,放入同一个图层容器里。把第一个图层当作基础图层
    * @param layers
    */
    public void addLayers(List<MapLayer> layers){
    	MapLayer baseLayer=layers.get(0);
    	mapLayers.addAll(layers);
    	recalMaxMinZoom();
    	switch(baseLayer.getType()){
    	case WMTS:
    		WmtsLayerContainer container=new WmtsLayerContainer(this, (WmtsLayer)baseLayer, placeholder);
    		container.addlayers(layers);
    		layerContainers.add(container);
    		addView(container);
		default:
			break;
    	}
    }
    
    /**
     * 删除图层
     * @param layer
     */
    public void removeLayer(MapLayer layer){
    	if(mapLayers.remove(layer)){
    		for(AbstractLayerContainer container:layerContainers){
				if(container.contains(layer)){
					if(container.isBaseLayer(layer)){
						mapLayers.removeAll(container.getOverMapLayers());
						container.removeLayer(layer);
						removeView(container);
						layerContainers.remove(container);
						break;
					}else {
						container.removeLayer(layer);
						container.refreshData();
					}
				}
        	}
        	recalMaxMinZoom();
    	}
    }
    
    /**
     * 删除指定图层名的图层	
     * @param layerName 图层名字
     */
    public void romoveLayerByName(String layerName){
    	for(int i=0;i<mapLayers.size();i++){
    		if(layerName.equals(mapLayers.get(i).getName())){
    			mapLayers.remove(i);
    		}
    	}
    	for(LayerContainer container:layerContainers){
    		container.removeLayerByName(layerName);
    	}
    	recalMaxMinZoom();
    }
    
    /**
     * 添加地图控件
     * @param widget
     */
    public void addWidget(BaseWidget widget){
		widget.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
    	if(widget.isSizeFixed()){ //如果是使用像素，则按照dpi比例进行缩放
    		widget.measure(MeasureSpec.EXACTLY|(int)(widget.getMeasuredWidth()*Math.pow(dpiRatio, 0.75)), 
        			MeasureSpec.EXACTLY|(int)(widget.getMeasuredHeight()*Math.pow(dpiRatio, 0.75)));
    	}
    	widget.setVisibility(View.VISIBLE);
    	addView(widget);
    	if(widgets.isEmpty()||widget.getZIndex()>=widgets.get(widgets.size()-1).getZIndex()){
    		widgets.add(widget);
    	}
    	for(int i=0;i<widgets.size();i++){
    		if(widget.getZIndex()<widgets.get(i).getZIndex()){
    			widgets.add(i-1, widget);
    			break;
    		}
    	}
    }
    
    /**
     * 显示或隐藏所有相同类型的控件
     * @param classid 控件的类ID
     * @param show  显示或隐藏
     */
    public void displayWidget(int classid,boolean show){
    	for(BaseWidget widget: widgets){
    		if(widget.classID==classid){
    			widget.setVisible(show);
    			if(show){
    				widget.setVisibility(View.VISIBLE);
    			}else{
    				widget.setVisibility(View.INVISIBLE);
    			}
    		}
    	}
    }
    
    private void displayWidget(BaseWidget widget){
    	if(widget.visible){
    		bringChildToFront(widget);
    		int startX=0,startY=0,dx=0,dy=0;
    		if(widget.isSizeFixed()){
    			dx=(int) (widget.horizontal*dpiRatio);
    			dy=(int)(widget.vertical*dpiRatio);
    		}else{
    			dx=widget.horizontal;
    			dy=widget.vertical;
    		}
    		switch(widget.getCenter()){
        	case BaseWidget.CENTER:
        		startX=(width-widget.getMeasuredWidth())/2+dx;
        		startY=(height-widget.getMeasuredHeight())/2+dy;
        		break;
        	case BaseWidget.CENTER_HORIZONTAL:
        		startX=(width-widget.getMeasuredWidth())/2+dx;
        		startY=dy>=0?dy:height-widget.getMeasuredHeight()+dy;
        		break;
        	case BaseWidget.DEFAULT:
        		startX=dx>=0?dx:width-widget.getMeasuredWidth()+dx;
        		startY=dy>=0?dy:height-widget.getMeasuredHeight()+dy;
        		break;
        	case BaseWidget.CENTER_VERTICAL:
        		startY=(height-widget.getMeasuredHeight())/2+dy;
        		startX=dx>=0?dx:width-widget.getMeasuredWidth()+dx;
        		break;
        	}
    		widget.layout(startX+0, startY+0, startX+widget.getMeasuredWidth(),
    				startY+widget.getMeasuredHeight());
    	}
    }
    /**
     * 设置最大分辨率，即第0层的分辨率
     */
    public void setMaxResolution(double ratio){
    	maxResolution=ratio;
    }
    
    /**
     * @return 最大分辨率
     */
    public double getMaxResolution(){
    	return maxResolution;
    }
    
    /**
     * 获取指定类别的地图控件
     * @param classid 类别ID
     * @return
     */
    public List<BaseWidget> getWidgetsByClassID(int classid){
    	List<BaseWidget> result=new ArrayList<BaseWidget>();
    	for(BaseWidget widget: widgets){
    		if(widget.classID==classid){
    			result.add(widget);
    		}
    	}
    	return result;
    }
    
    /**
     * 禁用默认内置地图控件，包括缩放控件和我的位置控件
     */
    public void disableAllBuildinWidget(){
    	//removeWidget(zoomControls);
		removeWidget(myLocationControls);
		//removeView(zoomControls);
		removeView(myLocationControls);
    }
    
    /**
     * 删除地图控件	
     * @param widget  the widget to remove
     */
    public void removeWidget(BaseWidget widget){
    	if(widgets.remove(widget)){
    		removeView(widget);
    	}
    }
    
    public void removeWidgetsByClassID(int classid){
    	for(int i=0;i<widgets.size();i++){
    		BaseWidget widget=widgets.get(i);
    		if(widget.classID==classid){
    			removeView(widget);
    			widget.visible=false;
    			widgets.remove(i);
    			i--;
    		}
    	}
    }
    
    /**
     * 地图是否繁忙，当地图处于拖动，缩放和滑动（flipping）时候设置为繁忙状态，此时不显示一些不重要的图层以减少资源占用
     * @return
     */
    public boolean isBusy() {
		return busy;
	}
    
    /**
     * 获取地图的截屏
     * @param withOverlay 是否包含overlay图层
     * @return
     */
    public Bitmap getMapShot(boolean withOverlay){
    	Bitmap bitmap = Bitmap.createBitmap(width, height,
    			Bitmap.Config.ARGB_8888);
    	Canvas canvas = new Canvas(bitmap);
    	for(AbstractLayerContainer container:layerContainers){
    		container.draw(canvas);
    	}
    	if(withOverlay){
    		overlayView.draw(canvas);
    	}
    	return bitmap;
    }
    
    /**
     * 设置地图最大缩放层级
     * @param maxLevel
     */
    public void setMaxZoomlevel(int maxLevel){
    	if(maxLevel>0){
    		this.maxUserLevel=maxLevel;
    		recalMaxMinZoom();
    	}
    }
    /**
     * 设置地图最小的缩放层级
     * @param minLevel
     */
    public void setMinZoomlevel(int minLevel){
    	if(minLevel>0){
    		this.minUserLevel=minLevel;
    		recalMaxMinZoom();
    	}
    }
    
    /**
     * 更新地图显示
     */
    public synchronized void refresh(){
    	if(cache!=null){
    		for(LayerContainer container:layerContainers){
    			container.refresh();
    		}
    		handler.post(new Runnable() {
				@Override
				public void run() {
					for(AbstractLayerContainer container: layerContainers){
			    		container.invalidate();
			    	}
					overlayView.invalidate();
					for(int i=0;i<widgets.size();i++){
			    		displayWidget(widgets.get(i));
			    	}
				}
			});
    	}
    	return;
    }
    
    private void refreshNow(){
    	for(LayerContainer container:layerContainers){
			container.refresh();
		}
    	for(AbstractLayerContainer container: layerContainers){
    		container.invalidate();
    	}
		overlayView.invalidate();
		for(int i=0;i<widgets.size();i++){
    		displayWidget(widgets.get(i));
    	}
		if(mapViewRefreshListener!=null&&centerChanged){
    		mapViewRefreshListener.onMapCenterChanged(center);
    		centerChanged=false;
    	}
    }
    
    void checkZoomControl() {
        zoomControls.setIsZoomOutEnabled(zoomLevel > minZoomLevel);
        zoomControls.setIsZoomInEnabled(zoomLevel < getMaxZoomLevel());
    }
  
    /**
     * 设置我的位置图层
     */
    public void setMyLocationOverlay(MyLocationOverlay myLocationOverlay){
    	myLocation=myLocationOverlay;
    }
    
    public GeoPoint getMyLocation(){
    	if(myLocation==null){
    		return null;
    	}
    	return myLocation.getMyLocation();
    }
    
  
    
    double getTileZoom(){
    	return tileZoom*dpiRatio;
    }
    
    /**
     * 返回当前地图分辨率
     * @return
     */
   public double getRatio(){
    	return maxResolution/(1<<zoomLevel)/(tileZoom*dpiRatio) ;
    }
    
   /**
    * @return  返回MapView当前的经纬度范围
    */
   public Bounds getViewBounds() {
	   Projection projection = getProjection();
	   GeoPoint lt = projection.fromPixels(0, 0);
	   GeoPoint rb = projection.fromPixels(width, height);
	   return new Bounds(lt.getLongitude(),rb.getLatitude(),rb.getLongitude(),lt.getLatitude());
   }
  
    public void displayZoomControls(boolean takeFocus) {
        addView(zoomControls);
        if (takeFocus)
            requestChildFocus(zoomControls, zoomControls);
    }

    @Override
    public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return super.generateLayoutParams(attrs);
    }

    public MapController getController() {
        return new MapController(this);
    }

    public double getLatitudeSpan() {
        Projection p = getProjection();
        GeoPoint viewTopLeft = p.fromPixels(0, 0);
        GeoPoint viewBottomRight = p.fromPixels(width - 1, height - 1);
        return viewTopLeft.getLatitude() - viewBottomRight.getLatitude();
    }

    public double getLongitudeSpan() {
    	Projection p = getProjection();
        GeoPoint viewTopLeft = p.fromPixels(0, 0);
        GeoPoint viewBottomRight = p.fromPixels(width - 1, height - 1);
        return viewBottomRight.getLongitude() - viewTopLeft.getLongitude();
    }

    public  boolean setMapCenter(GeoPoint center) {
		if(inMaxBounds(center)){
			this.center = center;
			refresh();
			if(mapViewRefreshListener!=null){
				mapViewRefreshListener.onMapCenterChanged(center);
			}
			return  true;
		}
        return false;
    }

	private boolean setMapCenterNow(GeoPoint center){
		if(inMaxBounds(center)){
			this.center = center;
			refreshNow();
			if(mapViewRefreshListener!=null){
				mapViewRefreshListener.onMapCenterChanged(center);
			}
			return  true;
		}
		return false;
	}

    public GeoPoint getMapCenter() {
        return center;
    }

    public int getMaxZoomLevel() {
        return maxZoomLevel;
    }

    public final List<Overlay> getOverlays() {
        return overlays;
    }

   public void removeOverlay(Overlay overlay){
	   overlays.remove(overlay);
   }
    
    public void addOverlay(Overlay overlay){
    	overlays.add(overlay);
    }


	public void clearOverlays(){
    	overlays.clear();
    }
    
    /**
     * @return 返回第一个容器的投影
     */
    public Projection getProjection() {
    	return ((LayerContainer) layerContainers.get(0)).getProjection();
    }

    public  boolean zoomIn() {
         return setZoomLevel(zoomLevel + 1);
    }
    public boolean zoomOut() {
        return setZoomLevel(zoomLevel - 1);
    }
   public boolean setZoomLevel(int zoomLevel) {
	    int oldLevel=this.zoomLevel;
        if (zoomLevel < minZoomLevel)
            this.zoomLevel = minZoomLevel;
        else if (zoomLevel > getMaxZoomLevel())
            zoomLevel = getMaxZoomLevel();
        else if(zoomLevel!=this.zoomLevel){
        	 this.zoomLevel = zoomLevel;
        }
        if(oldLevel!=this.zoomLevel){
        	if(mapViewRefreshListener!=null){
        		mapViewRefreshListener.onZoomChanged(zoomLevel);
        	}
        	checkZoomControl();
        }
        return this.zoomLevel == zoomLevel;
    }
 
    /**
     * @return 地图当前缩放层级
     */
    public int getZoomLevel() {
        return zoomLevel;
    }

  
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        if(w<720&&cache!=null){
        	cache.setMaxCacheSize(81);
        }
        for(AbstractLayerContainer container :layerContainers){  //改变所有容器的窗口大小
        	container.layout(0, 0, width, height);
        }
        overlayView.layout(0, 0, width, height);
        int marginSearchBar=40;
        logo.setMargin(20, -20);
    	myLocationControls.setMargin(marginSearchBar, -80);
    	LogUtils.d(""+myLocationControls.getMeasuredHeight());
    	LogUtils.d(""+zoomControls.getMeasuredWidth());
    	zoomControls.setMargin(-marginSearchBar,-100);
    }

    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	GeoPoint newCenter;
        for (Overlay ov : this.overlays)
            if (ov.onTouchEvent(event, this))
                return true;
        if(mDetector.onTouchEvent(event)){
        	return true;
        }


        switch (event.getAction()& MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN:
            	touchDownX = (int)event.getX();
                touchDownY = (int)event.getY();
                if (event.getPointerCount() == 2) {
        			isZooming=true;
        			zoomMode=true;
        			beforeLenght = getDistance(event);// 获取两点的距离
        		}
    			break;    
            case MotionEvent.ACTION_MOVE:
            	busy=true;
            	if(isZooming){
            		afterLenght = getDistance(event);// 获取两点的距离
        			float gapLenght = afterLenght - beforeLenght;// 变化的长度
        			if (Math.abs(gapLenght) > 20F) {
        				double scale_temp = afterLenght / beforeLenght;// 求的缩放的比例
        				if(scale_temp>1.1&&zoomLevel<getMaxZoomLevel()){
        					tileZoomIndex++;
        					if(tileZoomIndex<6){
        						tileZoom=tileZoomArray[tileZoomIndex];
        					}else{
        						tileZoomIndex=0;
        						tileZoom=tileZoomArray[tileZoomIndex];
        						zoomIn();
        					}
        					refreshNow();
        					beforeLenght = afterLenght;
        				}else if(scale_temp<0.9){
        					tileZoomIndex--;
        					if(zoomLevel>=minZoomLevel&&tileZoomIndex>=0){
        						tileZoom=tileZoomArray[tileZoomIndex];
        					}else{
        						if(zoomLevel==minZoomLevel){
        							tileZoomIndex=1;
        							tileZoom=1;
        							break;
        						}
        						tileZoomIndex=5;
        						tileZoom=tileZoomArray[tileZoomIndex];
        						zoomOut();
        					}
        					refreshNow();
        					beforeLenght = afterLenght;
        				}
        				refreshNow();
        			}
            		break;
            	}
            	moveDistance+=getDeltaDistance(event);
            	if(getDeltaDistance(event)>2&&isFling==false){
            		if(zoomMode){
            			touchDownX = (int)event.getX();
                        touchDownY = (int)event.getY();
                        zoomMode=false;
                        break;
            		}
            		newCenter = getProjection().fromPixels(
            				width / 2 - (int)(event.getX() - touchDownX),
            				height / 2 - (int)(event.getY() - touchDownY));
					setMapCenterNow(newCenter);
            	}
                touchDownX = (int)event.getX();
                touchDownY = (int)event.getY();
                return true;
            case MotionEvent.ACTION_POINTER_UP:
    			isZooming=false;
    			busy=false;
    			refreshNow();
    			break;
    			
            case MotionEvent.ACTION_UP:
            	busy=false;
            	GeoPoint touchPoint=getProjection().fromPixels((int)event.getX(),(int) event.getY());
				if(onSingleUpListener!=null){
					onSingleUpListener.run();
				}
            	if(moveDistance<10){
            		if(mapTouchListenner!=null){
                		mapTouchListenner.onTap(touchPoint);
                	}
                	for (Overlay ov : this.overlays){
                		ov.onTap(touchPoint, this);
                	}
            	}
            	moveDistance=0;
            	refreshNow();
            	break;
        }
        return true;
    }
   /**
    * 当所属的Activity隐藏时调用
    */
    public void onPause(){
    	if(cache!=null){
    		cache.destroy();
        	cache=null;
    	}
    	for(AbstractLayerContainer container :layerContainers){
    		container.onPause();
    	}
    }
    
    /**
     * 当所属Activity显示时调用
     */
    public void onResume(){
    	if(cache==null){
    		cache = new WebImageCache(this,MAX_CACHE_SIZE);
    	}
		for(AbstractLayerContainer container :layerContainers){
			container.onResume();
		}
		refresh();
    }
    
	/** 获取两点的距离 **/
	float getDistance(MotionEvent event) {
		float x,y;
		try{
			x = event.getX(0) - event.getX(1);
			y = event.getY(0) - event.getY(1);
		}catch(Exception e){
			return 0;
		}
		return (float) Math.sqrt(x * x + y * y);
	}
	
	private float getDeltaDistance(MotionEvent event){
		float x = event.getX() - touchDownX;
		float y = event.getY() - touchDownY;
		return (float) Math.sqrt(x * x + y * y);
	}
	
   @Override
    protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
        return p instanceof MapView.LayoutParams;
    }
 
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    protected android.view.ViewGroup.LayoutParams generateLayoutParams(
            android.view.ViewGroup.LayoutParams p) {
        return new LayoutParams(p.width, p.height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(resolveSize(width, widthMeasureSpec), resolveSize(height,
                heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    public WebImageCache getCache() {
    	if(cache==null){
    		cache=new WebImageCache(this,MAX_CACHE_SIZE);
    	}
		return cache;
	}
    
    public void setMapTouchListenner(MapTouchListener mapTouchListenner){
    	this.mapTouchListenner=mapTouchListenner;
    }
    
    public void setMapViewRefreshListener(MapViewRefreshListener listener){
    	this.mapViewRefreshListener=listener;
    }
    /**
     * 处理双击和快速滑动
     * @author liudanfeng
     * @since 2015-12-10
     */
	class MyGestureListener extends GestureDetector.SimpleOnGestureListener  {
		private static final int SPEED_NORMAL=2000;
        public boolean onDoubleTap(MotionEvent e) {
			setMapCenter(getProjection().fromPixels((int)e.getX(), (int)e.getY()));
        	if (zoomLevel < getMaxZoomLevel()){
				tileZoomIndex=0;
        		tileZoom=1;
        		zoomIn();
        		refreshNow();
        	}
        	if(mapTouchListenner!=null){
        		mapTouchListenner.onDoubleTap(center);
        	}
        	return true;
        }
         
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX,
        		final float velocityY) {
        	if(isZooming){
        		return false;
        	}
        	final int speedX=(int) Math.sqrt(velocityX * velocityX + velocityY* velocityY)/SPEED_NORMAL+2;
        	if(speedX>3){
        		isFling=true;
        		busy=true;
        		//LogUtils.d("isFling");
        		timer=new CountDownTimer(100*speedX, 25) {
        			int dx=(int)(velocityX/40/Math.sqrt(speedX));
        			int dy=(int)(velocityY/40/Math.sqrt(speedX));
    				@Override
    				public void onTick(long millisUntilFinished) {
    					dx=(int) (dx*0.95);
    					dy=(int) (dy*0.95);
						GeoPoint newCenter=getProjection().fromPixels(width / 2 - dx, height / 2 - dy);
						setMapCenterNow(newCenter);
    				}
    				@Override
    				public void onFinish() {
    					isFling=false;
    					busy=false;
    					refreshNow();
    				}
    			}.start();
        	}else{
        		busy=false;
        		refreshNow();
        	}
			touchDownX = (int)e2.getX();
			touchDownY = (int)e2.getY();
        	return true;
        }
        
        @Override
        public boolean onDown(MotionEvent event) {
        	switch (event.getAction()& MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                touchDownX = (int)event.getX();
                touchDownY = (int)event.getY();
                if(timer!=null){
                	timer.cancel();
                	timer=null;
                	isFling=false;
                }
                return true;
                
            case MotionEvent.ACTION_POINTER_DOWN:
            	touchDownX = (int)event.getX();
                touchDownY = (int)event.getY();
                if (event.getPointerCount() == 2) {
        			isZooming=true;
        			beforeLenght = getDistance(event);// 获取两点的距离
        		}
    			break; 
        	}
        	return true;
        }
        
        @Override
        public void onLongPress(MotionEvent e) {
        	final GeoPoint pt=getProjection().fromPixels((int)e.getX(), (int)e.getY());
        	super.onLongPress(e);
        	if(mapTouchListenner!=null){
        		new Handler().post( new Runnable() {
        			@Override
        			public void run() {
        				mapTouchListenner.onLongPress(pt);
        			}
        		} );  
        	}
        }
    }
	
	class OverlayView extends View{

		public OverlayView(Context context) {
			super(context);
		}
		
		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			if(myLocation!=null){
	    		myLocation.draw(canvas, MapView.this, false);
	    	}

	    	for(int i=0;i<overlays.size();i++){
	    		overlays.get(i).draw(canvas, MapView.this, false);
	    	}
		}
	}
	
}


