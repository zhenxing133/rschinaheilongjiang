package com.chinars.mapapi;

/**
 * 地图图层的类型
 * @author liudanfeng
 * @since 2015-8-18
 */
public enum LayerType {
	WMTS,
	WMS,
	TMS,
	GRID,
	Vector,
	SphericalMercator;

	public int getValue()
	{
		return this.ordinal();
	}

	public static LayerType forValue(int value)
	{
		return values()[value];
	}
}
