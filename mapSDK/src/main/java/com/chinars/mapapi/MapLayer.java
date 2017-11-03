/*
 * Copyright (C) 2009 James Ancona
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chinars.mapapi;

import android.graphics.RectF;

/**
 * 地图的数据源类
 * 
 * {@hide}
 * @author rsclouds
 */
public interface MapLayer {
	
	
    /**
     * @return 图层名字
     */
    String getName();
    
    
    /**
     * @return 图层类型
     */
    LayerType getType();

    /**
     * 
     * @param tileX    切片坐标x
     * @param tileY	          切片坐标Y
     * @param zoomLevel 层数
     * @return  切片的URI
     */
    String getTileUri(int tileX, int tileY, int zoomLevel);

    /**
     * @return 最大层数
     */
    int getMaxZoom();
    
    
    /**
     * 
     * @return 最小层数
     */
    int getMinZoom();

    /**
     * @return 切片大小
     */
    int getTileSize();
    
    /**
     * 
     * @return 可显示的经纬度范围
     */
    RectF getGeoBounds();

    /**
     *
     * @return 可显示的经纬度范围
     */
    Bounds getBounds();
    
    /**
     * @return 图层中心点位置
     */
    GeoPoint getGeoCenter();
    
    /**
     * @return 切图使用的起点坐标
     */
    GeoPoint getOrigin();
    
    void setDelayedLoad(boolean delayed);
    
    boolean isDelayedLoad();
    
    /**
     * 
     * @param zoomLevel
     * @return  返回地图分辨率
     */
    double getRatio(int zoomLevel);
    
   /**
    * 是否为透明图层，默认为透明图层
    * @return
    */
   boolean isTransparent(); 
}
