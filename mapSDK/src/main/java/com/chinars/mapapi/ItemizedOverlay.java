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

import android.graphics.drawable.Drawable;

/**
 * @author Jim Ancona
 */
public abstract class ItemizedOverlay<Item extends OverlayItem> {
    public ItemizedOverlay(Drawable defaultMarker) {
    }

    protected static Drawable boundCenterBottom(Drawable balloon) {
        throw new NotImplementedException();
    }

    protected static Drawable boundCenter(Drawable balloon) {
        throw new NotImplementedException();
    }

    protected abstract OverlayItem createItem(int arg0);

    public abstract int size();

    public GeoPoint getCenter() {
        throw new NotImplementedException();
    }

    protected int getIndexToDraw(int drawingOrder) {
        throw new NotImplementedException();
    }

    public void draw(android.graphics.Canvas canvas, MapView mapView, boolean shadow) {
        throw new NotImplementedException();
    }

    public int getLatSpanE6() {
        throw new NotImplementedException();
    }

    public int getLonSpanE6() {
        throw new NotImplementedException();
    }

    protected final void populate() {
        throw new NotImplementedException();
    }

    protected void setLastFocusedIndex(int lastFocusedIndex) {
        throw new NotImplementedException();
    }

    public void setFocus(OverlayItem item) {
        throw new NotImplementedException();
    }

    public OverlayItem getFocus() {
        throw new NotImplementedException();
    }

    public final int getLastFocusedIndex() {
        throw new NotImplementedException();
    }

    public final OverlayItem getItem(int position) {
        throw new NotImplementedException();
    }

    public OverlayItem nextFocus(boolean forwards) {
        throw new NotImplementedException();
    }

    public boolean onTap(GeoPoint p, MapView mapView) {
        throw new NotImplementedException();
    }

    public boolean onTrackballEvent(android.view.MotionEvent event, MapView mapView) {
        throw new NotImplementedException();
    }

    public boolean onKeyUp(int keyCode, android.view.KeyEvent event, MapView mapView) {
        throw new NotImplementedException();
    }

    public boolean onTouchEvent(android.view.MotionEvent event, MapView mapView) {
        throw new NotImplementedException();
    }

    protected boolean hitTest(OverlayItem item, Drawable marker, int hitX, int hitY) {
        throw new NotImplementedException();
    }

    public void setOnFocusChangeListener(ItemizedOverlay.OnFocusChangeListener l) {
        throw new NotImplementedException();
    }

    public void setDrawFocusedItem(boolean drawFocusedItem) {
        throw new NotImplementedException();
    }

    protected boolean onTap(int index) {
        throw new NotImplementedException();
    }

    public interface OnFocusChangeListener {
        @SuppressWarnings("unchecked")
        void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus);
    }

}
