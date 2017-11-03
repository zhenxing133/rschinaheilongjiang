package com.chinars.mapapi.utils;

import com.chinars.mapapi.GeoPoint;

public class DistanceUtil {
	private final static double  RATIO_LEVEL0=6.874164098211935E-4;
	public final static double EARTH_RADIUS_KM = 6371.137;
	public static int 	getDistance(GeoPoint p1LL, GeoPoint p2LL) {
			if(p1LL==null||p2LL==null){
				return Integer.MAX_VALUE;
			}
		 	double radLat1 = Math.toRadians(p1LL.getLatitude());
	        double radLat2 = Math.toRadians(p2LL.getLatitude());
	        double radLng1 = Math.toRadians(p1LL.getLongitude());
	        double radLng2 = Math.toRadians(p2LL.getLongitude());
	        double deltaLat = radLat1 - radLat2;
	        double deltaLng = radLng1 - radLng2;
	        double distance = 2 * Math.asin(Math.sqrt(Math.pow(
	                        Math.sin(deltaLat / 2), 2)
	                        + Math.cos(radLat1)
	                        * Math.cos(radLat2)
	                        * Math.pow(Math.sin(deltaLng / 2), 2)));
	        distance = distance * EARTH_RADIUS_KM;
	        distance = Math.round(distance * 1000);
	        return (int) distance;
	 }
	
	public static int getLatDistance(double lat1,double lat2){
        double deltaLat = Math.toRadians(lat1 - lat2);
        double distance = deltaLat * EARTH_RADIUS_KM;
        distance = Math.round(distance * 1000);
        return (int) distance;
	}
	
    public static int getLngDistance(double lng1,double lng2,double lat){
    	double radLat = Math.toRadians(lat);
    	double deltaLng = Math.toRadians(lng1-lng2);
    	double distance = 2 * Math.asin(Math.abs(Math.cos(radLat))* Math.sin(deltaLng / 2));
    	distance = distance * EARTH_RADIUS_KM;
    	distance = Math.round(distance * 1000);
    	return (int) distance;
	}
	
	public static int getPixelDistance(GeoPoint pt1,GeoPoint pt2,int zoomLevel){
		double deltaLat = pt1.getLatitude() - pt2.getLatitude();
        double deltaLng = pt1.getLongitude() - pt2.getLongitude();
        double distance=Math.sqrt(deltaLat*deltaLat+deltaLng*deltaLng);
		return (int) (distance/RATIO_LEVEL0*(2<<zoomLevel));
	}
}
