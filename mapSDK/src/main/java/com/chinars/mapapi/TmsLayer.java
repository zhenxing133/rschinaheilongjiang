package com.chinars.mapapi;

class TmsLayer extends AbstractMapLayer{
	private double tileOriginX=-180;// The top-left corner of the tile matrix in map units
	private double tileOriginY=-90;
	private String url;
	private String urlPrefix;
	
	@Override
	public String getName() {
		return name;
	}

	public TmsLayer(MapType type){
		super("", 0, 16, null, null);
	}
	

	@Override
	public String getTileUri(int x, int y, int zoom) {
		StringBuilder urlBuilder=new StringBuilder(64);
		zoom=zoom;
		urlBuilder.append(name+"@").append(zoom).append("%");
		urlBuilder.append(x).append("%").append(y).append(".png");
		return urlBuilder.toString();
	}

	
	@Override
	public LayerType getType() {
		return LayerType.TMS;
	}

	@Override
	public GeoPoint getOrigin() {
		return new GeoPoint(tileOriginX, tileOriginY);
	}
	
}
