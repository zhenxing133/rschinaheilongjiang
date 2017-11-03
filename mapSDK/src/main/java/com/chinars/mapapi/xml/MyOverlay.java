package com.chinars.mapapi.xml;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.chinars.mapapi.GeoPoint;
import com.chinars.mapapi.MapView;
import com.chinars.mapapi.Overlay;
import com.chinars.mapapi.Projection;
import com.chinars.mapapi.utils.Point;

/**
 * Created by yuanzhenxing on 2017/9/21.
 */

public class MyOverlay extends Overlay {
    private Context mContext;
    private GeoPoint geoPoint;
    private Bitmap bitmap;
    public MyOverlay(FragmentActivity activity,GeoPoint geoPoint,Bitmap bitmap) {
        mContext = activity;
        this.geoPoint = geoPoint;
        this.bitmap = bitmap;
    }

    //处理单击事件
    @Override
    public boolean onTap(GeoPoint p, MapView mapView) {
        return false;
    }

    //绘制view
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
        //获取投影
        Projection projection = mapView.getProjection();
        // 将经纬度转换成手机屏幕上的像素,存储在Point对象中
        //int latitude = (int) (geoPoint.getLatitude()*1e6);
        //int longitude = (int)(geoPoint.getLongitude()*1e6);
        //Log.e("yzx", latitude+"");
        //Log.e("yzx", longitude+"");
        android.graphics.Point point = projection.toPixels(geoPoint);
        Paint paint = new Paint();
        //获取图片
        canvas.drawBitmap(bitmap,point.x,point.y,paint);
        super.draw(canvas, mapView, shadow);
    }
}
