package com.chinars.mapapi;
/**
 * 地图类型接口，定义一些公用的地图类型的常量
 * @author rsclouds 
 *
 */
public enum MapType {

	/**
	 * 天地图 影像
	 */
	 TIANDITU_YINXIANG,
	
	/**
	 * 天地图 街道地图
	 */
	TIANDITU_JIEDAO,
	
	/**
	 * 天地图 中文注记
	 */
	TIANDITU_ZHONGWENZHUJI,
	
	/**
	 * 天地图 英文注记
	 */
    TIANDITU_YINGWENZHUJI,

	/**
	 * arcgisonline中国轮廓底图
	 */
	ARCGIS_ONLINE_CHINA_OUTLINE,
	
	
	/**
	 * 微软必应街道地图
	 */
	 BING_JIEDAO,
	
	/**
	 * 微软必应卫星地图
	 */
	 BING_YINXIANG,
	
	/**
	 * Google街道地图
	 */
	 GOOGLE_JIEDAO,
	
	/**
	 * Google卫星地图
	 */
	 GOOGLE_YINXIANG,
	
	
	/**
	 * 百度地图
	 */
	 BAIDUMAP,
	
	/**
	 * 搜狗地图
	 */
	 SOGOUMAP,
	
	/**
	 * 高德地图
	 */ 
     GAODEMAP;
	
}
