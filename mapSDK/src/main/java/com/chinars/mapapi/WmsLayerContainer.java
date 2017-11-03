package com.chinars.mapapi;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.chinars.mapapi.utils.LogUtils;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.SparseArray;
import android.view.View;

/**
 * Wmslayer 图层容器  默认使用wmts投影，可以设置其他的投影(tms,webmercator等等)以方便进行地图叠加
 * @author liudanfeng
 * @since 2015-8-31
 */
public class WmsLayerContainer extends AbstractLayerContainer {
    private int count=0;
	private boolean loadFinished=false;
	private Vector<ImageTask> unfinishTask=new Vector<WmsLayerContainer.ImageTask>();
	private LayerType type;
	private int halfTilesize;
	
	/**
	 * 创建一个Wtms服务的地图图层
	 * @param mapView 
	 * @param baseMaplayer  地图底图
	 * @param placeHolder  占位图
	 */
	public WmsLayerContainer(MapView mapView,WmsLayer baseMaplayer,Bitmap placeHolder){
		super(mapView, baseMaplayer, placeHolder);
		this.type=LayerType.WMTS;
		halfTilesize=tileSize/2;
	}
	
	
	/**
	 * 创建一个Wtms服务的地图图层
	 * @param mapView 
	 * @param baseMaplayer  地图底图
	 * @param placeHolder  占位图
	 * @param layType 通过指定图层的类型来控制投影的类型，可以为WMTS,WMS,TMS,
	 */
	public WmsLayerContainer(MapView mapView,WmsLayer baseMaplayer,Bitmap placeHolder,LayerType layType){
		super(mapView, baseMaplayer, placeHolder);
		this.type=layType;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		switch(type){
		case WMTS:
			prj=new WmtsProjection(mapView);
			bufferPrj=new WmtsProjection(mapView, bufferWidth, bufferHeight);
			break;
		case TMS:
			break;
		default:
			break;
		}
	}
	
	
	public  void onRefresh(){
		Point centerTileOffset,topLeftinBuffer;
		GeoPoint viewTopLeft;
		if(bufferCenter!=null){
			centerTileOffset = bufferPrj.geoPointToPoint(bufferCenter);
			viewTopLeft = prj.fromPixels(0, 0);
		    topLeftinBuffer=bufferPrj.geoPointToPoint(viewTopLeft);
			offsetX=topLeftinBuffer.x-centerTileOffset.x+bufferWidth/2;
			offsetY=topLeftinBuffer.y-centerTileOffset.y+bufferHeight/2;
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
		bufferCenter=center;
		unfinishTask.clear();
		centerTileOffset = bufferPrj.geoPointToPoint(bufferCenter);
		viewTopLeft = prj.fromPixels(0, 0);
		topLeftinBuffer=bufferPrj.geoPointToPoint(viewTopLeft);
		offsetX=topLeftinBuffer.x-centerTileOffset.x+bufferWidth/2;
		offsetY=topLeftinBuffer.y-centerTileOffset.y+bufferHeight/2;
		//LogUtils.d("offsetX:"+offsetX+","+offsetY);
		TileCoords centerTile = bufferPrj.getTileCoords(center);
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
    			loadFinished=true;
    			int weight=count*4-(Math.abs(tileX-centerTile.x)+1)*(Math.abs(tileY-centerTile.y)+1);
//    			int urid=OfflineMapFileManager.getTileNumURI(tileX, tileY, zoomLevel);
    			int leftPadding =(tileX - centerTile.x)*tileSize  + bufferWidth/2 - centerTileOffset.x;
    			int topPadding =(tileY-centerTile.y)*tileSize + bufferHeight/2 - centerTileOffset.y;
    			dstRect.set(leftPadding, topPadding, leftPadding+tileSize, topPadding+tileSize);
    			//LogUtils.d("urid:"+urid);
    			baseTileUrl=baselayer.getTileUri(tileX,tileY,zoomLevel);
    			baseMapTile=cache.get(baseTileUrl, weight);
    			if(baseMapTile==null){
    				loadFinished=false;
    				baseMapTile=cache.get(baselayer.getTileUri(tileX/2, tileY/2,zoomLevel-1));
    				if(baseMapTile==null){
    					for(int i=0;i<4;i++){ //将下一层的4个瓦片绘制成本层的一个瓦片
    						Bitmap bitmap=cache.get(baselayer.getTileUri(2*tileX+i%2, 2*tileY+i/2,zoomLevel+1));
    						if(bitmap!=null){
    							Rect rect=new Rect(leftPadding+i%2*halfTilesize, topPadding+i/2*halfTilesize, 
        								leftPadding+i%2*halfTilesize+halfTilesize, topPadding+(i+2)/2*halfTilesize);
        						bufferCanvas.drawBitmap(bitmap, null, rect, paint);
    						}else{
    							baseMapTile=placeHolder;
    						}
    					}
    					if(baseMapTile!=null){
    						bufferCanvas.drawBitmap(baseMapTile, null, dstRect, paint); //绘制占位图片
    					}
    				}else{
    					srcRect.set(tileX%2*halfTilesize, tileY%2*halfTilesize,
    							(tileX%2+1)*halfTilesize , (tileY%2+1)*halfTilesize);
    					bufferCanvas.drawBitmap(baseMapTile, srcRect, dstRect, paint); //绘制底图
    				}
    			}else{
    				bufferCanvas.drawBitmap(baseMapTile, null, dstRect, paint); //绘制底图
    			}
    			for(int i=0;i<overMapNum;i++){ 
    				if(overMapVisibles[i]){
    					overTileUrls[i]=overMapLayers.get(i).getTileUri(tileX, tileY,zoomLevel);
    					Bitmap overTile=cache.get(overTileUrls[i], weight);
    					if(overTile!=null){
    						bufferCanvas.drawBitmap(overTile, null, dstRect, paint);// 绘制叠加地图
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
	
	@Override
	public LayerType getSurportLayerType() {
		return LayerType.WMTS;
	}
}
