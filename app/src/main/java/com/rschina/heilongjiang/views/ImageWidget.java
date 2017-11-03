package com.rschina.heilongjiang.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.chinars.mapapi.widget.BaseWidget;
import com.rschina.heilongjiang.R;

public class ImageWidget extends BaseWidget {

	public ImageWidget(Context context) {
		super(context);
	}

	public ImageWidget(Context context, AttributeSet att) {
		super(context, att);
		this.setSizeFixed(false);
		setClickable(true);
	}
	
	public ImageView getImageView(){
		return (ImageView) findViewById(R.id.image);
	}
	
}
