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

import android.graphics.Point;

/**
 * @author rsclouds
 */
public interface Projection {
    /**
     * 屏幕像素坐标转换成经纬度坐标
     * 
     */
    GeoPoint fromPixels(int x, int y);

    /**
     * 计算距离长度对应的像素宽度
     * 
     */
    float metersToEquatorPixels(float meters);

    /**
     * 经纬度坐标转换成像素坐标，返回绝对坐标，坐标原点为(0,0)
     * @param gp 经纬度坐标
     * @return
     */
    Point geoPointToPoint(GeoPoint gp);
    
    /**
     * 经纬度坐标转换成屏幕像素坐标，返回相对坐标,坐标原点为屏幕左上
     * 
     */
    Point toPixels(GeoPoint in);
}