package com.chinars.mapapi;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.chinars.mapapi.utils.LogUtils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Bitmap.Config;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
/**
 * 文本图层类
 * @author rsclouds 中科遥感
 *
 */
public class TextOverlay extends Overlay {
	private static final long serialVersionUID = 1L;
	private List<TextItem> textOverlays=new ArrayList<TextItem>();
	Bitmap cacheBmap=null;
	Canvas cacheCanvas;
	Drawable drawble;
	
	
	public void addText(TextItem item){
		textOverlays.add(item);
	}
	
	public java.util.List<TextItem> getAllText(){
		return textOverlays;
	}
	
	public TextItem getText(int index){
		return textOverlays.get(index);
	}
	
	public boolean removeText(TextItem item){
		return textOverlays.remove(item);
	}
	
	public void removeAll(){
		textOverlays.clear();
	}
	
	
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		cacheBmap=Bitmap.createBitmap(mapView.getWidth(), mapView.getHeight(), Config.ARGB_8888);
    	cacheCanvas=new Canvas(cacheBmap);
		Paint paint=new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setDither(true);
		paint.setTypeface(Typeface.DEFAULT_BOLD); 
		for(Iterator<TextItem> itr=textOverlays.iterator();itr.hasNext();){
			TextItem textItem=itr.next();
			paint.setColor(textItem.fontColor);
			paint.setTextSize(textItem.fontSize);
			Projection p=mapView.getProjection();
			Point point=p.toPixels(textItem.pt);
			LogUtils.d("coods : ("+point.x+","+point.y+")");
			cacheCanvas.drawText(textItem.text, point.x, point.y, paint);
		}
		
		drawble=new BitmapDrawable(mapView.getResources(),cacheBmap);
		drawble.setBounds(0,0,mapView.getWidth(),mapView.getHeight());
		drawble.draw(canvas);
		return false;
	}
}
