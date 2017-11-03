package com.chinars.mapapi;

import android.graphics.RectF;
/**
 * WmsLayer 
 * @author liudanfeng
 * @since 2015-8-31
 */
public class WmsLayer extends AbstractMapLayer {
	private String url;
	private String urlPrefix;
	private String SRS="EPSG:4326";
	private long stime=0;
	private long etime=0;
	private static double ratios[];
	public WmsLayer(String name,String url,int minZoom,int maxZoom,GeoPoint topLeft,GeoPoint rightBottom ){
		super(name, minZoom, maxZoom, topLeft, rightBottom);
		this.url=url;
		buildUrlprfix();
	}
	
	public WmsLayer(String name,String url,String format,String style,String version,
			int minZoom,int maxZoom,GeoPoint topLeft,GeoPoint rightBottom){
		super(name, minZoom, maxZoom, topLeft, rightBottom);
		this.url=url;
		this.style=style;
		this.format=format;
		this.version=version;
		buildUrlprfix();
	}
	
	public void setTime(long startTime,long endTime){
		this.stime=startTime;
		this.etime=endTime;
	}
	
	public void setStartTime(long startTime){
		this.stime=startTime;
	}
	
	public void setEndTime(long endTime){
		this.etime=endTime;
	}
	
	private void buildUrlprfix() {
		urlPrefix=url+"?service=wms&request=GetMap&layers="+name+"&SRS="+SRS+"&style="+style+"&format="+format+"&version="+version
				+"&width="+tileSize+"&height="+tileSize;
		WebImageCache.putUrlPrefix(name, urlPrefix);
	}
	
	@Override
	public LayerType getType() {
		return LayerType.WMS;
	}

	@Override
	public void setMaxResolution(double maxResolution) {
		super.setMaxResolution(maxResolution);
		ratios=new double[20];
		for(int i=0;i<20;i++){
			ratios[i]=getRatio(i);
		}
	}
	
	/**
	 * 使用wmts投影的方式请求。原点在左上角
	 */
	@Override
	public String getTileUri(int tileX, int tileY, int zoomLevel) {
		if(zoomLevel>maxZoom||zoomLevel<minZoom){
			return null;
		}
		StringBuilder urlBuilder=new StringBuilder(32);
		double RZ=ratios[zoomLevel];
		float left=(float) (-180+tileX*RZ*tileSize);
		float top=(float) (90-tileY*RZ*tileSize);
		float right=(float) (-180+(tileX+1)*RZ*tileSize);
		float bottom=(float) (90-(tileY+1)*RZ*tileSize);
		urlBuilder.append(name+"@").append("&BBOX=");
		urlBuilder.append(left).append(","+bottom).append(","+right).append(","+top);
		if(stime!=0&&zoomLevel>=13){
			urlBuilder.append("&stime=").append(stime).append("&etime=").append(etime);
		}
//		.append("@")
//        .append(zoomLevel).append("&tilerow=").append(tileX).append("&tilecol=").append(tileY);
		return urlBuilder.toString();
	}

}
