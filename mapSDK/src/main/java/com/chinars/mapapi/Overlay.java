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

import java.io.Serializable;

import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * Overlay是一个基类，它表示可以显示在地图上方的覆盖物。
 * @author chinars
 */
public class Overlay implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static final float SHADOW_X_SKEW = -0.8999999761581421f;

    protected static final float SHADOW_Y_SCALE = 0.5f;

    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
        return false;
    }

    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        draw(canvas, mapView, shadow);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event, MapView mapView) {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event, MapView mapView) {
        return false;
    }

    /**
     * 单击事件
     * @param p  点击的位置
     * @param mapView
     * @return
     */
    public boolean onTap(GeoPoint p, MapView mapView) {
        return false;
    }

    public boolean onTouchEvent(MotionEvent e, MapView mapView) {
        return false;
    }

    public boolean onTrackballEvent(MotionEvent event, MapView mapView) {
        return false;
    }

    protected static void drawAt(Canvas canvas, Drawable drawable, int x, int y, boolean shadow) {
        if (!shadow) {
            drawable.setBounds(x-drawable.getIntrinsicWidth()/2, y-drawable.getIntrinsicHeight()/2, x + drawable.getIntrinsicWidth()/2, y
                    + drawable.getIntrinsicHeight()/2);
            drawable.draw(canvas);
        } else {
            // Draw shadow
        }
    }

    public static interface Snappable {
        public abstract boolean onSnapToItem(int i, int j, Point point, MapView mapview);
    }
}
