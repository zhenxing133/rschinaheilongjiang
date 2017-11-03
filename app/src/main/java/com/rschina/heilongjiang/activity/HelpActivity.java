package com.rschina.heilongjiang.activity;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.rschina.heilongjiang.R;
/**
 * Created by Administrator on 2016/9/30.
 */
public class HelpActivity extends Activity{
    private static final String TAG = "HelpActivity";
    private GestureDetector mDetector;
    ImageView iv_help;
    Rect btnRect;
    int w,h;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        mDetector=new GestureDetector(this, new MyGestureListener());
        iv_help=(ImageView)findViewById(R.id.iv_help);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            Log.d(TAG, "onWindowFocusChanged: "+iv_help.getWidth());
            w=iv_help.getWidth();
            h=iv_help.getHeight();
            calRect();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: "+iv_help.getWidth());

    }

    private  void calRect(){
        btnRect=new Rect();
        btnRect.set((int)(12.8/36*w),(int)(50.0/68*h)
                ,(int)(25.0/36*w),(int)(52.5/58*h));
        Log.d(TAG, "calRect: "+btnRect.left+" "+btnRect.right+
        + btnRect.bottom+"  "+btnRect.top);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: "+event.getX()+" "+event.getY());
        return mDetector.onTouchEvent(event)||super.onTouchEvent(event);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener  {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d(TAG, "onSingleTapUp: "+e.getX());
            if(btnRect.contains((int)e.getX(),(int)e.getY())){
                finish();
            }
            return super.onSingleTapUp(e);
        }
    }
}
