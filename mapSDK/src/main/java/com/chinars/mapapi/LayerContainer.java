package com.chinars.mapapi;

import java.util.List;

/**
 * 图层容器类接口
 * @author liudanfeng
 * @since 2015-8-18
 */
public interface LayerContainer {
	
	/**
	 * 判断一个图层是否在当前容器中为基础图层
	 * @param layer
	 */
	boolean isBaseLayer(MapLayer layer);
	
	/**
	 * 添加一个叠加图层
	 * @param layer
	 * @return
	 */
	boolean addLayer(MapLayer layer);
	
	/**
	 * 添加一个叠加图层,并设置该图层的透明度为alpha
	 * @param layer
	 * @param alpha
	 * @return
	 */
	boolean addLayer(MapLayer layer,int alpha);
	/**
	 * 一次添加多个叠加图层
	 * @param layers
	 */
	void addlayers(List<MapLayer> layers);
	
	/**
	 * 删除一个图层
	 * @param layer
	 * @return
	 */
	boolean removeLayer(MapLayer layer);
	/**
	 * 删除指定名字的图层
	 * @param name
	 * @return
	 */
	boolean removeLayerByName(String name);
	
	/**
	 * 替换一个图层
	 * @param newLayer
	 * @param oldLayer
	 * @return
	 */
	boolean replaceLayer(MapLayer newLayer,MapLayer oldLayer);
	
	/**
	 * 判断图层是否在当前容器中
	 * @param layer
	 * @return
	 */
	boolean contains(MapLayer layer);
	
	/**
	 * 判断容器中是否包含指定name的图层
	 * @param layerName
	 * @return
	 */
	boolean contains(String layerName);
	/**
	 * 
	 * @return 容器使用的地图投影
	 */
	Projection getProjection();
	
	/**
	 * @return 容器包含的图层数
	 */
	int getNumLayers();
	
	/**
	 * @return 返回容器支持的图层类型
	 */
	LayerType getSurportLayerType();
	
	/**
	 * 刷新视图
	 */
	void refresh();
}
