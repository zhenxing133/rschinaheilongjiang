package com.chinars.mapapi;

/**
 * 当前位置数据类，用于MyLocationOverlay.setData(LocationData locData)
 * @author rsclouds
 *
 */
public class LocationData {
	public double longitude;
	public double latitude;
	public float speed;
	public float direction;
	public float accuracy;
	public int satellitesNum;
}
