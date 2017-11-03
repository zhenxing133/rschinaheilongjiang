package com.chinars.mapapi;

import android.graphics.RectF;
/**
 * 网格图层，用于显示网格和文字信息
 * @author liudanfeng
 * @since 2015-8-26
 */
public class GridLayer extends AbstractMapLayer {
	private int borderWidth;//边框宽度,为0表示没有边框
	
	public GridLayer(String name){
		super(name, 0, 18, null, null);
	}

	@Override
	public LayerType getType() {
		return LayerType.GRID;
	}

	/**
	 * 可以直接返回所要显示的文字，或者返回一个URL链接
	 */
	@Override
	public String getTileUri(int tileX, int tileY, int zoomLevel) {
		return null;
	}

	@Override
	public double getRatio(int zoomLevel) {
		// TODO Auto-generated method stub
		return 0;
	}

}
