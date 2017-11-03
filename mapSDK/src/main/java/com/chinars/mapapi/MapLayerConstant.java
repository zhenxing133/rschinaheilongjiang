package com.chinars.mapapi;


import android.graphics.RectF;
/**
 * 地图参数类
 * @author rsclouds
 *
 */
public interface MapLayerConstant {

	
	public  final static double MAX_RESOLUTION_DOT_3519572078484874=0.3519572078484874;
	
	
	public  final static double MAX_RESOLUTION_DOT_703125=0.703125;
	
	public static final double CIRCUMFERENCE_IN_METERS = 40075160.0;//赤道周长
	
	/**
	 * 默认的瓦片大小
	 */
	public static final int TILE_SIZE=256;
	
	/**
	 * 表示整个地球的范围
	 */
	public static final RectF FULL_SPHERE=new RectF(-180.0f, 90.0f, 180f, -90.0f);
	
	public static final String DEFAULT_STYLE="default";
	
	public static final String DEFAULT_WMTS_VERSION="1.0.0";
	
	public static final String DEFAULT_WMS_VERSION="1.1.1";
	
	public static final String DEFAULT_TMS_VERSION="1.0.0";
	
}
