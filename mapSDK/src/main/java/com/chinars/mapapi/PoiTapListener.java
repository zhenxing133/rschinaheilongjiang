package com.chinars.mapapi;

import com.chinars.mapapi.search.PoiInfo;
/**
 * POI点击事件监听器
 * @author rsclouds
 *
 */
public interface PoiTapListener{
	void onPoiTap(int index,PoiInfo poiInfo);
}