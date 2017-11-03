package com.chinars.mapapi;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;


public class VectorLayerContainer extends AbstractLayerContainer {
	VectorLayer vectorLayer;
	Bounds viewBounds=new Bounds();
	private double halfWidth;
	private double halfHeigth;
	private double olderRatio=0.0;
	boolean delayed;
	private Thread drawThread;
	private Object syncObj=new Object();
	public VectorLayerContainer(MapView mapView, VectorLayer layer) {
		super(mapView, layer, null);
		vectorLayer=layer;
		layer.setMapView(mapView);
		delayed=layer.isDelayedLoad();
		drawThread=new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					try {
						synchronized (syncObj) {
							syncObj.wait();
						}
						vectorLayer.draw(bufferCanvas, viewBounds, bufferPrj);
						postInvalidate();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		drawThread.setDaemon(true);
	}

	@Override
	public LayerType getSurportLayerType() {
		return LayerType.Vector;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		prj=new WmtsProjection(mapView);
		bufferPrj=new WmtsProjection(mapView, bufferWidth, bufferHeight);
		if(!drawThread.isAlive()){
			drawThread.start();
		}
	}
	
	
	
	@Override
	public void onRefresh() {
		Point centerTileOffset,topLeftinBuffer;
		GeoPoint viewTopLeft=prj.fromPixels(0, 0);;
		if(bufferCenter!=null){
			centerTileOffset = bufferPrj.geoPointToPoint(bufferCenter);
		    topLeftinBuffer=bufferPrj.geoPointToPoint(viewTopLeft);
			offsetX=topLeftinBuffer.x-centerTileOffset.x+bufferWidth/2;
			offsetY=topLeftinBuffer.y-centerTileOffset.y+bufferHeight/2;
			if(offsetX>=0&&offsetY>=0&&offsetX<maxOffsetX&&offsetY<maxOffsetY){
				return ;
			}
		}
		if(delayed&&mapView.isBusy()){
			bufferCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
			bufferCenter=null;
			return;
		}
		bufferCenter=center;
		centerTileOffset = bufferPrj.geoPointToPoint(bufferCenter);
		topLeftinBuffer=bufferPrj.geoPointToPoint(viewTopLeft);
		offsetX=topLeftinBuffer.x-centerTileOffset.x+bufferWidth/2;
		offsetY=topLeftinBuffer.y-centerTileOffset.y+bufferHeight/2;
		bufferCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
		if(olderRatio-bufferRatio>0.00001||bufferRatio-olderRatio>0.00001){
			olderRatio=bufferRatio;
			halfWidth=bufferWidth*bufferRatio;
			halfHeigth=bufferHeight*bufferRatio;
		}
		viewBounds.setBounds(center.getLongitude()-halfWidth, center.getLatitude()-halfHeigth, center.getLongitude()+halfWidth
				, center.getLatitude()+halfHeigth);
		synchronized (syncObj) {
			syncObj.notify();
		}
	}
	
	
	
}
