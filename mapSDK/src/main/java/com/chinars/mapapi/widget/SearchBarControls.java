package com.chinars.mapapi.widget;

import com.chinars.mapapi.utils.LogUtils;
import com.chinars.mapapi.utils.ResourseUtil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchBarControls extends BaseWidget{
	public final int CLASS_ID=3;
    private SearchBarOnClickListener mListener;
    private TextView textView;
    private ImageView iv_voice;
    private ImageView iv_more;
	
	public SearchBarControls(Context context) {
		super(context);
		this.classID=CLASS_ID;
		DisplayMetrics metric = new DisplayMetrics();
		WindowManager mWm = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		mWm.getDefaultDisplay().getMetrics(metric);
		int densityDpi = metric.densityDpi;  // 像素密度（像素)
		int width=metric.widthPixels;
		double dpiRatio=densityDpi/250.0;
		if(dpiRatio<1){
			dpiRatio=1;
		}
		this.setSizeFixed(false);//自适应
		int tendp=(int)(10*dpiRatio);
		this.setCenter(CENTER_HORIZONTAL);
		this.vertical=tendp;
		LinearLayout.LayoutParams llParam=new LinearLayout.LayoutParams(width-2*tendp,5*tendp);
		llParam.gravity=Gravity.CENTER_VERTICAL;
		setLayoutParams(llParam);
		setPadding(10, 10, 10, 10);
		setOrientation(LinearLayout.HORIZONTAL);
		setBackgroundColor(0xC0FFFFFF);
		setMinimumHeight(5*tendp);
		
		textView=new TextView(context);
		LinearLayout.LayoutParams tvParams=new LayoutParams(width-12*tendp,LayoutParams.MATCH_PARENT);
		textView.setPadding(10, 0, 0, 0);
		textView.setClickable(true);
		textView.setGravity(Gravity.CENTER_VERTICAL);
		textView.setCompoundDrawablePadding(10);
		textView.setText("搜索地点");
		textView.setTextSize(16);
		Drawable drawable=ResourseUtil.getDrawableFromAssets(context, "icon_search.png");
		drawable.setBounds(0, 0,(int)(drawable.getIntrinsicWidth()*dpiRatio), (int)(drawable.getIntrinsicHeight()*dpiRatio));
		textView.setCompoundDrawables(drawable, null, null, null);
		
		iv_voice=new ImageView(context);
		LinearLayout.LayoutParams iv_voiceParams=new LayoutParams(4*tendp,4*tendp);
		iv_voice.setImageDrawable(ResourseUtil.getDrawableFromAssets(context, "voice.png"));
		
		View view=new View(context);
		LinearLayout.LayoutParams viewParams=new LayoutParams(2,LayoutParams.MATCH_PARENT);
		view.setBackgroundColor(0xFFAAAAAA);
		
		iv_more=new ImageView(context);
		iv_more.setPadding(tendp, 5, tendp,5 );
		LinearLayout.LayoutParams iv_moreParams=new LayoutParams(4*tendp,4*tendp);
		iv_moreParams.topMargin=5;
		iv_moreParams.leftMargin=tendp;
		iv_moreParams.rightMargin=tendp;
		iv_more.setImageDrawable(ResourseUtil.getDrawableFromAssets(context, "more.png"));
		
		
		addView(textView, tvParams);
		addView(iv_voice, iv_voiceParams);
		addView(view, viewParams);
		addView(iv_more, iv_moreParams);
	}
	
	public void setSearchBarOnClickListener(SearchBarOnClickListener listener){
		this.mListener=listener;
		iv_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListener.onMoreClick();
			}
		});
		textView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onSearchClick();
			}
		});
		iv_voice.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mListener.onVoiceClick();
			}
		});
		
	}
	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//	}
}
