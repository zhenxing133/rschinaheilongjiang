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

import com.chinars.mapapi.widget.SearchBarOnClickListener;

import android.os.Message;
import android.view.KeyEvent;
import android.view.View;

/**
 * 地图控制类，用于设置地图中心位置、缩放等等
 * @author Jim Ancona
 */
public class MapController implements View.OnKeyListener {
    private MapView mapView;

    MapController(MapView mapView) {
        this.mapView = mapView;
    }

    public void animateTo(GeoPoint point, Runnable runnable) {
        animateTo(point);
    }

    public void animateTo(GeoPoint point) {
        mapView.setMapCenter(point);
    }

    public void animateTo(GeoPoint point, Message message) {
        animateTo(point);
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) {
        throw new NotImplementedException();
    }

    public void scrollBy(int x, int y) {
        throw new NotImplementedException();
    }

    /**
     * 设置屏幕中心点的地图位置
     * @param point  位置点
     */
    public void setCenter(GeoPoint point) {
        mapView.setMapCenter(point);
    }

    /**
     * 设置地图放大倍数
     * @param zoomLevel 缩放倍数，范围0-21
     * @return
     */
    public int setZoom(int zoomLevel) {
        mapView.setZoomLevel(zoomLevel);
        return mapView.getZoomLevel();
    }

    public void stopAnimation(boolean jumpToFinish) {
        throw new NotImplementedException();
    }

    public void stopPanning() {
        throw new NotImplementedException();
    }

    public boolean zoomIn() {
        return mapView.zoomIn();
    }
    
    public boolean zoomInFixing(int xPixel, int yPixel) {
        Projection p = mapView.getProjection();
        GeoPoint point = p.fromPixels(xPixel, yPixel);
        boolean ret = mapView.zoomIn();
        animateTo(point);
        return ret;
    }

    public boolean zoomOut() {
        return mapView.zoomOut();
    }

    public boolean zoomOutFixing(int xPixel, int yPixel) {
        Projection p = mapView.getProjection();
        GeoPoint point = p.fromPixels(xPixel, yPixel);
        boolean ret = mapView.zoomOut();
        animateTo(point);
        return ret;
    }

    public void zoomToSpan(int latSpanE6, int lonSpanE6) {
        throw new NotImplementedException();
    }

    
}
