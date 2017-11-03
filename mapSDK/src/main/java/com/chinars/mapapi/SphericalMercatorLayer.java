package com.chinars.mapapi;

import android.graphics.RectF;

import com.chinars.mapapi.utils.LogUtils;

import org.apache.commons.logging.Log;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * Created by Administrator on 2016/2/26.
 */
public class SphericalMercatorLayer extends AbstractMapLayer{
    private String url;
    private String urlPrefix;
    private String layerName;
    private String extraParams=null;
    private double originX=-20037508.342787;
    private double originY=20037508.342787;
    private NumberFormat numberFormat=NumberFormat.getInstance();
    private boolean isWms=true;
    private String[] tileMatrixArray;
    private String[] resArray;
    private int[] zoomArray;
    private String timeString;
    private  MapType mapType;

    public  SphericalMercatorLayer(String name,String url,String format,String layerName,int minZoom
            ,int maxZoom,GeoPoint topLeft,GeoPoint rightBottom ,String extraParams){
        super(name,minZoom,maxZoom,topLeft,rightBottom);
        this.url=url;
        this.layerName=layerName;
        if(geoBound.top>80&&geoBound.bottom<-80){
            geoBound=new RectF(geoBound.left,80,geoBound.right,-80);
        }
        numberFormat.setGroupingUsed(false);
        numberFormat.setMaximumFractionDigits(8);
        this.format=format;
        this.extraParams=extraParams;
        buildUrlPrefix();
    }

    public SphericalMercatorLayer(String name,String url,String tileMatrixSet,String[] tileMatrixArray,String format,int minZoom
            ,int maxZoom,GeoPoint topLeft,GeoPoint rightBottom){
        super(name,minZoom,maxZoom,topLeft,rightBottom);
        this.url=url;
        this.layerName=tileMatrixSet;
        this.format=format;
        if(geoBound.top>85&&geoBound.bottom<-85){
            geoBound=new RectF(geoBound.left,85,geoBound.right,-85);
        }
        this.tileMatrixArray=tileMatrixArray;
        isWms=false;
        buildUrlPrefix();
    }
    
    public  SphericalMercatorLayer(MapType mapType){
        this(mapType,new GeoPoint(74,54),new GeoPoint(136,3));
    }
    public SphericalMercatorLayer(MapType mapType,GeoPoint topLeft,GeoPoint rightBottom){
        super("",4,16,topLeft,rightBottom);
        switch (mapType){
            case ARCGIS_ONLINE_CHINA_OUTLINE:
                this.mapType=mapType;
                isWms=false;
                name="arcgis_china";
                urlPrefix="http://cache1.arcgisonline.cn/ArcGIS/rest/services/SimpleFeature/ChinaBoundaryLine/MapServer/tile/";
                break;
            default:
                throw new RuntimeException("not surport map type");
        }
        WebImageCache.putUrlPrefix(name, urlPrefix);
    }

    private void buildUrlPrefix(){
        if(isWms){
            urlPrefix=url+"?request=GetMap&SERVICE=WMS&VERSION=1.1.1&WIDTH=256&HEIGHT=256&SRS=EPSG%3A3857&style="+style+"&LAYERS="
                    +layerName+"&format="+format;
        }else{
            urlPrefix=url+"?SERVICE=WMTS&request=GetTile&VERSION=1.0.0&layer="+name+"&style="+style+"&tilematrixset="
                    +layerName+"&format="+format+"&tilematrix=";
        }
        if(extraParams!=null){
            urlPrefix+=extraParams;
        }
        WebImageCache.putUrlPrefix(name, urlPrefix);
    }

    public void setResArray(int[] zooms,String[] resArray){
        this.zoomArray=zooms;
        this.resArray=resArray;
    }

    public void setTimeString(String timeString){
        this.timeString=timeString;
    }

    @Override
    public LayerType getType() {
        return LayerType.SphericalMercator;
    }

    @Override
    public String getTileUri(int tileX, int tileY, int zoomLevel) {
        if(zoomLevel>maxZoom||zoomLevel<minZoom){
            return null;
        }
        StringBuilder urlBuilder=new StringBuilder(32);
        if(isWms){
            double tileSize=156543.033928*128/Math.pow(2, zoomLevel);
            double left= originX+tileX*tileSize;
            double top= (originY -tileY*tileSize);
            double right= (originX+(tileX+1)*tileSize);
            double bottom= (originY -(tileY+1)*tileSize);
            urlBuilder.append(name+"@").append("&BBOX=");
//            urlBuilder.append(numberFormat.format(left)).append("," + numberFormat.format(bottom)).append(","+numberFormat.format(right)).append(","+numberFormat.format(top));
            urlBuilder.append(left).append("," +bottom).append(","+right).append(","+top);
            if(zoomArray!=null){
                for(int i=0;i<zoomArray.length;i++){
                    if(zoomLevel<=zoomArray[i]){
                        urlBuilder.append(resArray[i]);
                        break;
                    }
                }
            }
            if(timeString!=null){
                urlBuilder.append(timeString);
            }
        }else {
            if(mapType!=null){
                switch (mapType) {
                    case ARCGIS_ONLINE_CHINA_OUTLINE:
                        urlBuilder.append(name+"@").append(zoomLevel+1).append("%").append(tileY).append("%").append(tileX);
                        return  urlBuilder.toString();
                }
            }
            String tileMatrix=null;
            if(tileMatrixArray==null){
                tileMatrix=String.valueOf(zoomLevel+1);
            }else {
                tileMatrix=tileMatrixArray[zoomLevel];
            }
            urlBuilder.append(name+"@").append(tileMatrix).append("&tilerow=");
            urlBuilder.append(tileY).append("&tilecol=").append(tileX);
        }
         return urlBuilder.toString();
    }
}
