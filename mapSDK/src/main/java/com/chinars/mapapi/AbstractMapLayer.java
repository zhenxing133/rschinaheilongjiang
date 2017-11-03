package com.chinars.mapapi;

import android.graphics.RectF;

abstract class AbstractMapLayer implements MapLayer {
	protected String name;
	protected int maxZoom=16;//最大层级
	protected int minZoom=0;//最小层级
	protected int tileSize=256;
	protected double maxResolution=MapLayerConstant.MAX_RESOLUTION_DOT_3519572078484874;//最大分辨率,默认为0.3519572078484874
	protected RectF geoBound=MapLayerConstant.FULL_SPHERE; //瓦片的经纬度范围,默认为全球范围
	protected String format="image/png";//The image MIME type.  Default is “image/png”.
	protected String style="default";//样式
	protected String version="1.0.0";//版本号
	protected boolean transparent=true;//图层是否透明，默认为透明
	protected boolean delayed=false;
	protected  Bounds bounds;
	public AbstractMapLayer(String name,int minZoom,int maxZoom,GeoPoint topLeft,GeoPoint rightBottom ){
		this.name=name;
		this.minZoom=minZoom;
		this.maxZoom=maxZoom;
		if(topLeft!=null&&rightBottom!=null){
			geoBound=new RectF((float)topLeft.getLongitude(),(float) topLeft.getLatitude(),
					(float)rightBottom.getLongitude(),(float) rightBottom.getLatitude());
			bounds=new Bounds(geoBound);
		}
	}
	
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getMaxZoom() {
		return maxZoom;
	}

	@Override
	public int getMinZoom() {
		return minZoom;
	}

	@Override
	public int getTileSize() {
		return tileSize;
	}

	@Override
	public RectF getGeoBounds() {
		return geoBound;
	}

	@Override
	public Bounds getBounds() {
		return bounds;
	}

	@Override
	public GeoPoint getGeoCenter() {
		return new GeoPoint((geoBound.left+geoBound.right)/2,(geoBound.top+geoBound.bottom)/2);
	}

	public void setMaxResolution(double maxResolution){
		this.maxResolution=maxResolution;
	}
	
	@Override
	public double getRatio(int zoomLevel) {
		return maxResolution/(1<<zoomLevel);
	}

	@Override
	public boolean isTransparent() {
		return transparent;
	}

	@Override
	public void setDelayedLoad(boolean delayed) {
		this.delayed=delayed;
	}
	
	public boolean isDelayedLoad(){
		return delayed;
	}
	
	@Override
	public GeoPoint getOrigin() {
		return null;
	}
}
