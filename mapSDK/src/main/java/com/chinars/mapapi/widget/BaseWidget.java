package com.chinars.mapapi.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class BaseWidget extends LinearLayout {
	public static final int DEFAULT=0;
	public static final int CENTER=1;
	public static final int CENTER_VERTICAL=2;
	public static final int CENTER_HORIZONTAL=3;
	
	public int classID;
	public int horizontal=0;
	public int vertical=0;
	private int center;
	public boolean visible=true;
	private int zIndex=0;
	private boolean sizeFixed=true;
	
	public BaseWidget(Context context) {
		super(context);
	}


	public BaseWidget(Context context,AttributeSet att) {
		super(context, att);
	}
	
	/**
	 * @param horizona 水平偏移 ，  正数表示从左开始，负数表示从右开始
	 * @param vertical  垂直平移， 正数表示从上开始，负数表示从下开始
	 */
	public void setMargin(int horizontal,int vertical){
		this.horizontal=horizontal;
		this.vertical=vertical;
	}
	
	/**
	 * 设置控件大小是否固定，需要再次调整。如果使用px则设置为true,使用dp(自适应)则设置为false
	 */
	public void setSizeFixed(boolean fixed){
		this.sizeFixed=fixed;
	}
	
	public void setCenter(int center){
		this.center=center;
	}
	
	public int getCenter(){
		return center;
	}
	
	/**
	 * 设置控件是否可见
	 * @param show
	 */
	public void setVisible(boolean show){
		this.visible=show;
		if(visible){
			setVisibility(View.VISIBLE);
		}else{
			setVisibility(View.INVISIBLE);
		}
	}
	
	public boolean isSizeFixed() {
		return sizeFixed;
	}

	/**
	 * 设置控件的层次，index值大的控件覆盖index值小的
	 * @param index
	 */
	public void setZIndex(int index){
		this.zIndex=index;
	}
	
	public int getZIndex(){
		return zIndex;
	}
	
}
