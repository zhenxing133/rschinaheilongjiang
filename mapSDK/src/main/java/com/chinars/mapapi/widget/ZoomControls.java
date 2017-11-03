package com.chinars.mapapi.widget;

import com.chinars.mapapi.utils.ResourseUtil;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ZoomControls  extends BaseWidget {
	public static final int CLASS_ID=6;
	private ImageView imageZoomIn;
	private ImageView imageZoomOut;
	private boolean zoomInEnable=true;
	private boolean zoomOutEnable=true;
	public ZoomControls(Context context) {
		super(context);
		this.classID=CLASS_ID;
		LinearLayout.LayoutParams layLout=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		setLayoutParams(layLout);
		setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams layout=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT, 1);
		imageZoomIn=new ImageView(context);
		imageZoomIn.setImageDrawable(ResourseUtil.getDrawableFromAssets(getContext(), "zoom_in.png"));
		imageZoomIn.setLayoutParams(layout);
		imageZoomOut=new ImageView(context);
		imageZoomOut.setImageDrawable(ResourseUtil.getDrawableFromAssets(getContext(), "zoom_out.png"));
		imageZoomOut.setLayoutParams(layout);
		
		addView(imageZoomIn);
		addView(imageZoomOut);
	}

	public void setOnZoomInClickListener (View.OnClickListener listener){
		imageZoomIn.setOnClickListener(listener);
	}
	
	public void setOnZoomOutClickListener (View.OnClickListener listener){
		imageZoomOut.setOnClickListener(listener);
	}

	
	public boolean getZoomInEnable(){
		return zoomInEnable;
	}
	
	public boolean getZoomOutEnable(){
		return zoomOutEnable;
	}
	
	public void setIsZoomOutEnabled(boolean b) {
		if(b==zoomOutEnable){
			return;
		}
		zoomOutEnable=b;
		if(b){
			imageZoomOut.setImageDrawable(ResourseUtil.getDrawableFromAssets(getContext(), "zoom_out.png"));
		}else{
			imageZoomOut.setImageDrawable(ResourseUtil.getDrawableFromAssets(getContext(), "zoom_out_disabled.png"));
		}
		imageZoomOut.setClickable(b);
	}

	public void setIsZoomInEnabled(boolean b) {
		if(b==zoomInEnable){
			return;
		}
		zoomInEnable=b;
		imageZoomIn.setImageDrawable(ResourseUtil.getDrawableFromAssets(getContext(),b?"zoom_in.png":"zoom_in_disabled.png"));
		imageZoomIn.setClickable(b);
	}
}
