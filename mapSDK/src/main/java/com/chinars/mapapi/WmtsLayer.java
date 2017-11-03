package com.chinars.mapapi;



public class WmtsLayer extends AbstractMapLayer{
	private String url;
	private String urlPrefix;
	GeoPoint origin;
	private String tileMatrixSet;//
	private MapType mapType;
	private String extraParams=null;
	private String[] tilematrixArray=null;
	private String srs;//坐标系
	
	/**
	 * @param  name    the map name
	 * @param  url    The base URL or request URL template for the WMTS service. 
	 * @param minZoom 最小层级  
	 * @param maxZoom 最大层级
	 * @param topLeft 表示图层范围的左上角的经纬度坐标
	 * @param rightBottom 表示图层范围的右下角的经纬度坐标
	 */
	public WmtsLayer(String name,String url,String tileMatrixSet,int minZoom
			,int maxZoom,GeoPoint topLeft,GeoPoint rightBottom){
		super(name, minZoom, maxZoom, topLeft, rightBottom);
		this.url=url;
		this.tileMatrixSet=tileMatrixSet;
		buildUrlprfix();
	}
	
	public WmtsLayer(MapType mapType){
		super("", 0, 18, null, null);
		this.mapType=mapType;
		switch (mapType){
		case TIANDITU_JIEDAO:
			break;
		case TIANDITU_YINXIANG:
			urlPrefix="http://t#.tianditu.com/DataServer?T=img_c&l=";
			name="TIANDITU_YINXIANG";
			break;
		case TIANDITU_YINGWENZHUJI:
			break;
		case TIANDITU_ZHONGWENZHUJI:
			urlPrefix="http://t#.tianditu.com/DataServer?T=cia_c&l=";
			name="TIANDITU_ZHONGWENZHUJI";
			break;
		default:
			throw new RuntimeException("MapType not Surported");
		}
		WebImageCache.putUrlPrefix(name, urlPrefix);
	}
	
	public WmtsLayer(String name,String url,String tileMatrixSet,String format,int minZoom
			,int maxZoom,GeoPoint topLeft,GeoPoint rightBottom){
		super(name, minZoom, maxZoom, topLeft, rightBottom);
		this.url=url;
		this.tileMatrixSet=tileMatrixSet;

		tilematrixArray=new String[maxZoom-minZoom+1];
		for(int i=0;i<tilematrixArray.length;i++){
			tilematrixArray[i]=tileMatrixSet+":"+i;
		}
		buildUrlprfix();
		
		
	}
	
	public WmtsLayer(String name,String url,String tileMatrixSet,String[] tileMaxtrixArray,String style,String format,double maxResolution,int minZoom
			,int maxZoom,GeoPoint topLeft,GeoPoint rightBottom,boolean transparent,String extraParams){
		super(name, minZoom, maxZoom, topLeft, rightBottom);
		this.url=url;
		this.tileMatrixSet=tileMatrixSet;
		this.format=format;
		this.transparent=transparent;
		this.style=style;
		this.maxResolution=maxResolution;
		this.extraParams=extraParams;
		this.tilematrixArray=tileMaxtrixArray;
		buildUrlprfix();
	}
	
	/**
	 * 构建瓦片请求前缀，并放入HASHMAP中
	 */
	private void buildUrlprfix(){
		urlPrefix=url+"?request=GetTile&layer="+name+"&style="+style+"&tilematrixset="
	            +tileMatrixSet+"&format="+format+"&tilematrix=";
		if(extraParams!=null){
			urlPrefix+=extraParams;
		}
		WebImageCache.putUrlPrefix(name, urlPrefix);
		//LogUtils.d(urlPrefix);
	}
	
	@Override
	public String getTileUri(int x, int y, int zoom) {
		if(zoom>maxZoom||zoom<minZoom){
			return null;
		}
		StringBuilder urlBuilder=new StringBuilder(32);
		if(mapType!=null){
			switch (mapType) {
			case TIANDITU_JIEDAO: case TIANDITU_ZHONGWENZHUJI:case TIANDITU_YINXIANG:case TIANDITU_YINGWENZHUJI:
				zoom=zoom+1;
				urlBuilder.append(name+"@").append(zoom).append("&y=");
				urlBuilder.append(y).append("&x=").append(x);
				return urlBuilder.toString().toString();
			default:
				break;
			}
		}
		urlBuilder.append(name+"@").append(tilematrixArray[zoom-minZoom]).append("&tilerow=");
		urlBuilder.append(y).append("&tilecol=").append(x);
		return urlBuilder.toString();
	}

	@Override
	public LayerType getType() {
		return LayerType.WMTS;
	}
	
	@Override
	public GeoPoint getOrigin() {
		return origin;
	}
	
	public void setOrigin(GeoPoint origin){
		this.origin=origin;
	}
}
