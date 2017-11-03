package com.chinars.mapapi;
/**
 * 地图点击事件监听器
 * @author rclouds
 *
 */
public interface MapTouchListener {
	/**
	 * 地图点击事件
	 * @param p 点击的位置
	 */
	void onTap(GeoPoint p);
	/**
	 * 地图双击事件
	 * @param p
	 */
	boolean onDoubleTap(GeoPoint p);
	/**
	 * 地图长按事件
	 * @param p
	 */
	void onLongPress(GeoPoint p);
}
