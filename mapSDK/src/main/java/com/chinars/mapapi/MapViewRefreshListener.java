package com.chinars.mapapi;

public interface MapViewRefreshListener {
	/**
	 * 监听地图中心点位置改变
	 * @param center
	 */
	void onMapCenterChanged(GeoPoint center);
	/**
	 * 监听地图缩放层级发生改变
	 * @param zoomLevel
	 */
	void onZoomChanged(int zoomLevel);
}
