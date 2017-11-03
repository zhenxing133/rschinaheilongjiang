package com.chinars.mapapi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.view.View;
import android.view.View.MeasureSpec;
/**
 * 弹出图层，用以在地图上显示图片或View
 * @author Administrator
 *
 */
public class PopupOverlay extends Overlay{
	private static final long serialVersionUID = 1L;
	private MapView mapView = null;
	PopupClickListener listenner = null;
	private static int index= 0;
	private Bitmap bitmap;
	private Point loc=null;
	private GeoPoint pt;
	private int offset;

	public PopupOverlay(MapView mapView, PopupClickListener listener){
		this.mapView=mapView;
		this.listenner=listener;
	}
	
	/**
	 * 
	 * @param pop
	 * @param point
	 * @param yOffset
	 */
	public void showPopup(Bitmap[] pop,	GeoPoint point,int yOffset){
		bitmap=mergeBitmap(pop);
		showPopup(bitmap,point,yOffset);
	}
	
	/**
	 * 
	 * @param pop
	 * @param point
	 * @param yOffset
	 */
	public void showPopup(Bitmap pop, GeoPoint point,int yOffset){
		if(point==null||pop==null){
			return;
		}
		bitmap=pop;
		pt=point;
		offset=yOffset;
		mapView.refresh();
	}
	
	/**
	 * 在屏上像素位置上显示图片
	 * @param pop
	 * @param topLeft
	 */
	public void showPopup(Bitmap pop,Point topLeft){
		bitmap=pop;
		loc=topLeft;
		mapView.refresh();
	}
	
	public void  hidePopup(){
		if(mapView==null){
			return;
		}
		loc=null;
		bitmap=null;
		mapView.refresh();
	}
	
	/**
	 * 
	 * @param view
	 * @param point
	 * @param yOffset
	 */
	public void showPopup(View view,GeoPoint point,int yOffset){
		bitmap=getBitmapFromView(view);
		showPopup(bitmap,point,yOffset);
	}
	
	
	@Override
	public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
			long when) {
		if(loc!=null){
			canvas.drawBitmap(bitmap, loc.x, loc.y, null);
		}
		if(bitmap==null||pt==null){
			return false;
		}
		Projection p = mapView.getProjection();
		Point locCenter = p.toPixels(pt); 
		canvas.drawBitmap(bitmap, locCenter.x-bitmap.getWidth()/2, locCenter.y-bitmap.getHeight()/2-offset, null);
		return true;
	}
	
	
	private Bitmap mergeBitmap(Bitmap pop[])
	  {
		if(pop==null){
			return null;
		}
		int width=0,height=0;
		for(int i=0;i<pop.length&&pop[i]!=null;i++){
			width=width+pop[i].getWidth();
			height=Math.max(height,pop[i].getHeight());
		}
	    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap);
	    width=0;
	    for(int i=0;i<pop.length&&pop[i]!=null;i++){
	    	 canvas.drawBitmap(pop[i],width, 0, null);
	    	 width=width+pop[i].getWidth();
	    }
	    canvas.save(Canvas.ALL_SAVE_FLAG);
	    canvas.restore();
	    return bitmap;
	  }
	
	
	public Bitmap getBitmapFromView(View view){
		 view.setDrawingCacheEnabled(true);  
		    // 必须调用measure和layout方法才能成功保存可视组件的截图到png图像文件  
		    // 测量View大小  
		    view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));  
		    // 发送位置和尺寸到View及其所有的子View  
		    view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());  
		    // 获得可视组件的截图  
		    Bitmap bitmap = view.getDrawingCache();  
		    return bitmap;  
	}
	
	public interface  PopupClickListener{
		void onClickedPopup(int index);
	}


}
