/*
 * Copyright (C) 2009 James Ancona
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chinars.mapapi;

import com.chinars.mapapi.utils.DistanceUtil;
import com.chinars.mapapi.utils.LogUtils;
import com.chinars.mapapi.utils.ResourseUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * @author Jim Ancona
 */
public class MyLocationOverlay extends Overlay implements SensorEventListener, LocationListener {
	private static final long serialVersionUID = 1L;
	
    private final MapView mapView;
    private GeoPoint defaultLocation=new GeoPoint(113.88177,22.93061);
    private boolean compassEnabled;
    private boolean myLocationEnabled;
    private SensorManager sensorManager;
    private Sensor aSensor; 
    private Sensor mSensor; 
    float[] accelerometerValues = new float[3]; 
    float[] magneticFieldValues = new float[3]; 
    
    private LocationManager locationManager;
    private float orientation=0 ;
    private Runnable runOnFirstFix = null;
    private LocationData locationData;
    private Location lastFix = null;
    private Bitmap compassArrow;
    private Bitmap bitmapcute;
    private Matrix matrix=new Matrix();
    private Paint paint = new Paint();
    private long lastFixTime=Integer.MAX_VALUE;
    private float movingDirection=0;//移动方向
    private GeoPoint lastPt=null,nowPt=null;

    public MyLocationOverlay(Context context, MapView mapView) {
//        this.context = context;
        this.mapView = mapView;
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        compassArrow = ResourseUtil.getBitmapFromAssets(context, "arrow.png");
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); 
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); 
    }

    public  MyLocationOverlay(MapView mapView,Bitmap compass){
        this.mapView = mapView;
        Context context=mapView.getContext();
        sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        compassArrow = compass;
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        aSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }


    public boolean isCompassEnabled() {
        return compassEnabled;
    }

	public synchronized boolean enableCompass() {

		sensorManager.registerListener(this, aSensor, SensorManager.SENSOR_DELAY_NORMAL); 
		sensorManager.registerListener(this, mSensor,SensorManager.SENSOR_DELAY_NORMAL); 
		compassEnabled=true;
		return compassEnabled;
	}
	public synchronized void disableCompass() {
		sensorManager.unregisterListener(this,aSensor);
		sensorManager.unregisterListener(this,mSensor);
		compassEnabled=false;
	}

    public boolean isMyLocationEnabled() {
        return myLocationEnabled;
    }
    public synchronized void enableMyLocation() {
    	if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
    		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 5L, this);
    	}else  if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
    		LogUtils.d("LocationManager.NETWORK_PROVIDER");
    		myLocationEnabled = true;
    		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 50L, this);
    	} 
    	myLocationEnabled=true;
    }
    public synchronized void disableMyLocation() {
        if (myLocationEnabled) 
            locationManager.removeUpdates(this);
        myLocationEnabled = false;
        lastFix = null;
    }

    /**
     * 手动设置定位信息
     * @param locData 定位信息
     */
    public void setData(LocationData locData){
    	this.locationData=locData;
    	lastPt=new GeoPoint(locData.longitude,locData.latitude );
        mapView.refresh();
    }
    
    @Override
    public synchronized boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
    	if(lastFix!=null){
    		drawMyLocation(canvas, mapView, lastFix.getAccuracy(), getMyLocation(), when);
    	}else if(locationData!=null){
    		drawMyLocation(canvas, mapView, locationData.accuracy, getMyLocation(), when);
    	}
    	drawCompass(canvas, getMyLocation());
        return false;
    }
    
    /**
     * 设置在没有获取到定位信息时默认返回的位置
     * @param location
     */
    public void setDefaultLocation(GeoPoint location){
    	defaultLocation=location;
    }
    
    protected void drawCompass(Canvas canvas,GeoPoint myLocation) {
    	 Projection p = mapView.getProjection();
    	 Point point = p.toPixels(myLocation);
    	 Rect r=new Rect( point.x-compassArrow.getWidth()/2, point.y-compassArrow.getHeight()/2, point.x + compassArrow.getWidth()/2, point.y
                 + compassArrow.getHeight()/2);
    	 if(r.right<0||r.left>canvas.getWidth()||r.bottom<0||r.top>canvas.getHeight()){
    		 return;
    	 }
    	 if(compassEnabled&&bitmapcute!=null){
    		 canvas.drawBitmap(bitmapcute, null, r, paint);
    	 }else{
    		 canvas.drawBitmap(compassArrow, null, r, paint);
    	 }
    }

    protected void drawMyLocation(Canvas canvas, MapView mapView, float  accuracy,
            GeoPoint myLocation, long when) {
        Projection p = mapView.getProjection();
        Point loc = p.toPixels(myLocation);
        float accuracyRadius = (float) (p.metersToEquatorPixels(accuracy) /(Math.cos(Math.toRadians(myLocation.getLatitude()))));
        if(loc.x+accuracyRadius<0||loc.x-accuracyRadius>canvas.getWidth()||loc.y+accuracyRadius<0
        		||loc.y+accuracyRadius>canvas.getHeight()){
        	return;
        }
        if (accuracy > 10.0f) {
            paint.setAlpha(50);
            canvas.drawCircle(loc.x, loc.y, accuracyRadius, paint);
        }
        paint.setAlpha(255);
        //canvas.drawCircle(loc.x, loc.y, 10, paint);
        
    }


    public Location getLastFix() {
        return lastFix;
    }
    /**
     * @return 返回定位的经纬度
     */
    public GeoPoint getMyLocation() {
    	if(lastFix!=null){
    	  return new GeoPoint(lastFix.getLongitude(),lastFix.getLatitude());
    	}else if(locationData!=null){
    		return lastPt;
    	}
    	return defaultLocation;
    }
    public float getOrientation() {
        return orientation;
    }
    public synchronized void onLocationChanged(Location location) {
    	LogUtils.d("onLocationChanged");
    	lastFix = location;
    	locationData=null;
        nowPt=new GeoPoint(location.getLongitude(),location.getLatitude() );
        if (runOnFirstFix != null) {
            runOnFirstFix.run();
            runOnFirstFix = null;
        }
        int distance=DistanceUtil.getDistance(lastPt, nowPt);
        if(distance>location.getAccuracy()/2||location.getTime()-lastFixTime>5000){
        	lastFixTime=location.getTime();
        	if(distance>location.getAccuracy()/2&&lastPt!=null){
        		movingDirection=(float) (Math.atan(((nowPt.getLongitude()-lastPt.getLongitude())/(nowPt.getLatitude()-lastPt.getLatitude())))/Math.PI*180);
        		matrix.reset();
            	matrix.setRotate(movingDirection,24,24);
            	bitmapcute = Bitmap.createBitmap(compassArrow, 0, 0,compassArrow.getWidth() , compassArrow.getHeight(), matrix, true); 
        	}else{
        		movingDirection=0;
        	}
        	mapView.refresh();
        	LogUtils.d("movingDirection:"+movingDirection);
        }
        lastPt=nowPt;
    }
    public void onProviderDisabled(String provider) {
    	lastFix=null;
    	
    }
    public void onProviderEnabled(String provider) {
    }
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
    public synchronized boolean runOnFirstFix(Runnable runnable) {
        if (lastFix == null) {
            runOnFirstFix = runnable;
            return false;
        } else {
            runnable.run();
            return true;
        }
    }
 
    public void setAsMapCenter(){
    	mapView.setMapCenter(getMyLocation());
    }
    
    @Override
    public boolean onTap(GeoPoint p, MapView map) {
//        Projection projection = map.getProjection();
//        Point tapPoint = projection.toPixels(p, null);
//        Point myPoint = projection.toPixels(getMyLocation(), null);
//        if (Math.pow(tapPoint.x - myPoint.x, 2.0) + Math.pow(tapPoint.y - myPoint.y, 2.0)  < Math.pow(20.0, 2)) {
//            // Is it within 20 pixels?
//            return dispatchTap();
//        } else {
//            return false;
//        }
    	return false;
    }
    protected boolean dispatchTap() {
        return false;
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){ 
			magneticFieldValues = event.values; 
			calculateOrientation(); 
		}
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			accelerometerValues = event.values; 
			calculateOrientation(); 
		}
	}
	
	private  void calculateOrientation() { 
        float[] values = new float[3]; 
        float[] R = new float[9]; 
        SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);          
        SensorManager.getOrientation(R, values); 
        values[0] = (float) Math.toDegrees(values[0]); 
        if(orientation-values[0]>5||orientation-values[0]<-5){
        	orientation=values[0];
        	if(movingDirection==0){
        		matrix.reset();
            	matrix.setRotate(orientation,24,24);
            	bitmapcute = Bitmap.createBitmap(compassArrow, 0, 0,compassArrow.getWidth() , compassArrow.getHeight(), matrix, true);
        	}
        	mapView.refresh();
        }
	}
}
