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

/**
 * {@hide}
 * @author Jim Ancona
 */
class TileCoords {
    public final int zoom;
    public final int x;
    public final int y;

    public TileCoords(int x, int y, int zoom) {
        this.x = x;
        this.y = y;
        this.zoom = zoom;
    }
    public int getZoom() {
        return zoom;
    }
    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    @Override
    public String toString() {
        return "" + zoom + "/" + x + "/" + y;
    }
}
