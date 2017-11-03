package com.chinars.mapapi;

import java.io.Serializable;

/**
 * 位置类，用经纬度表示
 * @author rsclouds
 */
public class GeoPoint implements Serializable{

	private static final long serialVersionUID = 1L;
	public double latitude;
    public double longitude;
    
    public GeoPoint(double longitude, double latitude) {
    	this.latitude=latitude;
    	this.longitude=longitude;
    }

    public GeoPoint(String longtidue,String latitude){
    	this(Double.valueOf(longtidue),Double.valueOf(latitude));
    }
  
    public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
    @Override
    public String toString() {
        return "GeoPoint: Latitude: " + latitude + ", Longitude: " + longitude;
    }
}
