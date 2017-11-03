package com.chinars.mapapi;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import com.chinars.mapapi.utils.LogUtils;

import java.util.Vector;

/**
 * Created by Administrator on 2016/2/26.
 */
public class SphericalMercatorLayerContainer extends AbstractLayerContainer{
    private int count=0;
    private boolean loadFinished=false;
    private Vector<ImageTask> unfinishTask=new Vector<AbstractLayerContainer.ImageTask>();
    private int halfTilesize;
    boolean delayed;

    public  SphericalMercatorLayerContainer(MapView mapView,SphericalMercatorLayer baseMaplayer ,Bitmap placeHolder){
        super(mapView, baseMaplayer, placeHolder);
        halfTilesize=tileSize/2;
        delayed=baseMaplayer.isDelayedLoad();
    }

    @Override
    public void onRefresh() {
//        LogUtils.d("onRefresh");
        if(bufferCenter!=null){
            calOffset();
            if(offsetX>=0&&offsetY>=0&&offsetX<maxOffsetX&&offsetY<maxOffsetY){
                if(!unfinishTask.isEmpty()){
                    synchronized (unfinishTask) {
                        for(int i=0;i<unfinishTask.size();i++){
                            if(unfinishTask.get(i).drawImage()){
                                unfinishTask.remove(i);
                                i--;
                            }
                        }
                    }
                }
                return ;
            }
        }
        if(delayed&&mapView.isBusy()){
            bufferCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            bufferCenter=null;
            return;
        }
        bufferCenter=center;
        unfinishTask.clear();
        calOffset();
        Point centerTileOffset = bufferPrj.geoPointToPoint(bufferCenter);
        TileCoords centerTile = bufferPrj.getTileCoords(bufferCenter);
        GeoPoint bufferTopLeft = bufferPrj.fromPixels(0, 0);
        TileCoords topLeftTile = bufferPrj.getTileCoords(bufferTopLeft);
        GeoPoint viewBottomRight = bufferPrj.fromPixels(bufferWidth-1, bufferHeight-1);
        TileCoords bottomRightTile = bufferPrj.getTileCoords(viewBottomRight);
        centerTileOffset.x-=(centerTile.x * tileSize);
        centerTileOffset.y-=(centerTile.y * tileSize);
        for(int i=0;i<overMapNum;i++){
            MapLayer mapInfo=overMapLayers.get(i);
            if(zoomLevel<mapInfo.getMinZoom()||zoomLevel>mapInfo.getMaxZoom()
                    ||!viewInbound(centerTile.x,centerTile.y,overMapBounds.get(i))){
                overMapVisibles[i]=false;
            }else{
                overMapVisibles[i]=true;
            }
        }
        bufferCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        int left=Math.max(topLeftTile.x, baseMapBound.left);
        int right=Math.min(bottomRightTile.x, baseMapBound.right);
        int top=Math.max(topLeftTile.y, baseMapBound.top);
        int bottom=Math.min(bottomRightTile.y, baseMapBound.bottom);
        for (int tileX =left; tileX <= right;tileX++) {
            for (int tileY=top;tileY<=bottom; tileY++) {
                Bitmap baseMapTile=null;
                String baseTileUrl=null;
                String[] overTileUrls=overMapNum>0?new String[overMapNum]:null;
                int leftPadding =(tileX - centerTile.x)*tileSize  + bufferWidth/2 - centerTileOffset.x;
                int topPadding =(tileY-centerTile.y)*tileSize + bufferHeight/2 - centerTileOffset.y;
                dstRect.set(leftPadding, topPadding, leftPadding+tileSize, topPadding+tileSize);
                loadFinished=true;
                int weight=count*4-(Math.abs(tileX-centerTile.x)+1)*(Math.abs(tileY-centerTile.y)+1);
//    			int urid=OfflineMapFileManager.getTileNumURI(tileX, tileY, zoomLevel);
                //LogUtils.d("urid:"+urid);
                baseTileUrl=baselayer.getTileUri(tileX, tileY, zoomLevel);
                baseMapTile=cache.get(baseTileUrl, weight);
                if(baseMapTile==null){
                    loadFinished=false;
                    baseMapTile=cache.get(baselayer.getTileUri(tileX/2, tileY/2, zoomLevel-1));
                    if(baseMapTile==null){
                        for(int i=0;i<4;i++){ //将下一层的4个瓦片绘制成本层的一个瓦片
                            Bitmap bitmap=cache.get(baselayer.getTileUri(2*tileX+i%2, 2*tileY+i/2, zoomLevel+1));
                            if(bitmap!=null){
                                Rect rect=new Rect(leftPadding+i%2*halfTilesize, topPadding+i/2*halfTilesize,
                                        leftPadding+i%2*halfTilesize+halfTilesize, topPadding+(i+2)/2*halfTilesize);
                                bufferCanvas.drawBitmap(bitmap, null, rect, null);
                            }else{
                                baseMapTile=placeHolder;
                            }
                        }
                        if(baseMapTile!=null){
                            bufferCanvas.drawBitmap(baseMapTile, null, dstRect, null); //绘制占位图片
                        }
                    }else{
                        srcRect.set(tileX%2*halfTilesize, tileY%2*halfTilesize,
                                (tileX%2+1)*halfTilesize , (tileY%2+1)*halfTilesize);
                        bufferCanvas.drawBitmap(baseMapTile, srcRect, dstRect, null); //绘制上一层的1/4个瓦片
                    }
                }else{
                    bufferCanvas.drawBitmap(baseMapTile, null, dstRect, null); //绘制底图
                }
                for(int i=0;i<overMapNum;i++){
                    if(overMapVisibles[i]){
                        overTileUrls[i]=overMapLayers.get(i).getTileUri(tileX, tileY,zoomLevel);
                        Bitmap overTile=cache.get(overTileUrls[i], weight);
                        if(overTile!=null){
                            bufferCanvas.drawBitmap(overTile, null, dstRect, null);// 绘制叠加地图
                        }else{
                            loadFinished=false;
                        }
                    }
                }
                if(!loadFinished){
                    unfinishTask.add(new ImageTask(baseTileUrl,overTileUrls, leftPadding, topPadding));
                }
            }
        }
        if(!unfinishTask.isEmpty()){
            cache.startWork();
        }
        count++;
    }

    private boolean viewInbound(int centerX,int centerY,Rect bound){
        int halfRowNum=height/512;
        int halfColNum=width/512;
        if(centerX+halfColNum>bound.left&&centerX-halfColNum<bound.right
                &&centerY+halfRowNum>bound.top&&centerY-halfRowNum<bound.bottom){
            return true;
        }
        return false;
    }

    private void calOffset(){
        Point centerTileOffset = bufferPrj.geoPointToPoint(bufferCenter);//mapView中心点在Buffer投影上的坐标
        GeoPoint viewTopLeft = prj.fromPixels(0, 0);//mapView左上点坐标
        Point topLeftinBuffer=bufferPrj.geoPointToPoint(viewTopLeft);//mapView左上点在Buffer投影上的坐标
        offsetX=topLeftinBuffer.x-centerTileOffset.x+bufferWidth/2;//mapView左上点相对于Buffer左上点的偏移
        offsetY=topLeftinBuffer.y-centerTileOffset.y+bufferHeight/2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        prj=new SphericalMeterMercatorProjection(mapView);
        bufferPrj=new SphericalMeterMercatorProjection(mapView, bufferWidth, bufferHeight);
    }

    @Override
    public LayerType getSurportLayerType() {
        return LayerType.SphericalMercator;
    }
}
