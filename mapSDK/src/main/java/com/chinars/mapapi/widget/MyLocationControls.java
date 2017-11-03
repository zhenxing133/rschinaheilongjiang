package com.chinars.mapapi.widget;

import com.chinars.mapapi.utils.ResourseUtil;
import com.chinars.mapsdk.R;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MyLocationControls extends BaseWidget {
	public static final int CLASS_ID=3;
	public MyLocationControls(Context context) {
		super(context);
		this.classID=CLASS_ID;
		LinearLayout.LayoutParams layLout=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams layLout2=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		setLayoutParams(layLout);
		ImageView image=new ImageView(context);
		image.setBackgroundColor(0xC0FFFFFF);
		image.setImageDrawable(ResourseUtil.getDrawableFromAssets(getContext(), "show_my_location.png"));
		image.setLayoutParams(layLout2);
		addView(image);
	}
	
	public void setOnLocationListener(View.OnClickListener listener){
		setOnClickListener(listener);
} 
}
