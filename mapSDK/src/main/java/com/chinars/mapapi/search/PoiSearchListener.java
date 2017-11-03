package com.chinars.mapapi.search;
/**
 * 
 * @author rsclouds 中科遥感
 *
 */
public interface PoiSearchListener {
	void onGetPoiResult(PoiInfos result,int error);
	void onGeAddrResult(LocationInfo result,int error);
	void onGetMutliPoiResult(PoiSearchResult result,int error);
}
