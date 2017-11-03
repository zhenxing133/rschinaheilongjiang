package com.chinars.mapapi.xml;

import com.chinars.mapapi.utils.DistanceUtil;
import com.chinars.mapapi.utils.LogUtils;

public class ImageCoordinate {

	  public double x;
	  public double y;
	  public double z;
	  public double x1;
	  public double y1;
	  public double x2;
	  public double y2;
	  public double x3;
	  public double y3;
	  public double x4;
	  public double y4;
	  
	  
	  public Vector3D getDirectionVector(){
		  double avgX=(x1+x2+x3+x4)/4.0;
		  double avgY=(y1+y2+y3+y4)/4.0;
		  LogUtils.d(avgX+","+avgY);
		  return new Vector3D(DistanceUtil.getLngDistance(avgX, x,y), DistanceUtil.getLatDistance(avgY, y), z);
	  }
	  
	  public int getAngle(){
		  Vector3D vector=getDirectionVector();
		  return (int)Math.toDegrees(Math.atan2(z,Math.sqrt(vector.x*vector.x+vector.y*vector.y)));
	  }
}
