package com.chinars.mapapi;

import java.io.Serializable;

import android.graphics.RectF;

public class Bounds implements Serializable{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

/**
    * Property: left
    * {Number} Minimum horizontal coordinate.
    */
   public double left;

   /**
    * Property: bottom
    * {Number} Minimum vertical coordinate.
    */
   public double bottom;

   /**
    * Property: right
    * {Number} Maximum horizontal coordinate.
    */
   public double right;

   /**
    * Property: top
    * {Number} Maximum vertical coordinate.
    */
   public double top;
   
   
   private boolean empty=true;
   /**
    * Property: centerLonLat
    *  A cached center location.  This should not be
    *     accessed directly.  Use <getCenterLonLat> instead.
    */
   private GeoPoint  centerLonLat;
   
   /**create a empty bounds */
   public Bounds(){
	   empty=true;
   }
   
   public Bounds(GeoPoint geoPoint){
	   this(geoPoint.getLongitude(), geoPoint.getLatitude());
   }
   
   public Bounds(double x,double y){
	   this(x, y, x, y);
   }
   
  public Bounds(Bounds bounds){
	  this.left=bounds.left;
	   this.right=bounds.right;
	   this.bottom=bounds.bottom;
	   this.top=bounds.top;
	   empty=false;
  }
   
   public Bounds(double centerX,double centerY,double radius){
	   this(centerX-radius,centerY-radius,centerX+radius,centerY+radius);
   }
   
   public Bounds(RectF rect){
	   this(rect.left,rect.bottom,rect.right,rect.top);
   }
   
   public  Bounds(double left, double bottom,double right,double top){
	   if(left>right||bottom>top){
		   throw new RuntimeException("left>right||bottom>top");
	   }
	   this.left=left;
	   this.right=right;
	   this.bottom=bottom;
	   this.top=top;
	   this.empty=false;
   }
   
   public void  setBounds(double left, double bottom,double right,double top){
	   if(left>right||bottom>top){
		   throw new RuntimeException("left>right||bottom>top");
	   }
	   this.left=left;
	   this.right=right;
	   this.bottom=bottom;
	   this.top=top;
	   this.empty=false;
	   centerLonLat=null;
   }
   
   public boolean isEmpty() {
	   return empty;
   }
   
   public void setEmpty(){
	   this.empty=true;
	   this.left=0;
	   this.right=0;
	   this.bottom=0;
	   this.top=0;
   }
   
   /**
    * @return The center of the bounds in map space.
    */
   public GeoPoint getCenterLonLat(){
	   if(centerLonLat==null){
		   centerLonLat=new GeoPoint((left+right)/2, (top+bottom)/2);
	   }
	   return centerLonLat;
   }
   /**
    * Scales the bounds around a pixel or lonlat. Note that the new 
    *     bounds may return non-integer properties, even if a pixel
    *     is passed. 
    * ratio - {Float} 
    * origin - {<OpenLayers.Pixel> or <OpenLayers.LonLat>}
    *          Default is center.
    * Returns:
    *  A new bounds that is scaled by ratio
    *                      from origin.
    */
   public Bounds scale(double ratio, GeoPoint origin){
	   if(origin == null){
           origin = getCenterLonLat();
       }
       double origx,origy;
       origx = origin.getLongitude();
       origy = origin.getLatitude();
       double left = (this.left - origx) * ratio + origx;
       double bottom = (this.bottom - origy) * ratio + origy;
       double right = (this.right - origx) * ratio + origx;
       double top = (this.top - origy) * ratio + origy;
       return new Bounds(left, bottom, right, top);
   }
   
   public double  getWidth(){
	   return (this.right - this.left);
   }

   public double getHeight(){
	   return (this.top - this.bottom);
   }
   
   public double  getSize(){
	   return (this.right - this.left)*(this.top - this.bottom);
   }

   public Bounds  add(double deltaX,double deltaY){
	   return new Bounds(left+deltaX, bottom+deltaY, right+deltaX, top+deltaY);
   }
   
   public void extend(GeoPoint geoPoint) {
	   extend(geoPoint.getLongitude(),geoPoint.getLatitude());
   }

   public void extend(double x,double y){
	   this.centerLonLat = null;
	   if(empty){
		   this.left=x;
		   this.right=x;
		   this.bottom=y;
		   this.top=y;
		   this.empty=false;
	   }
       if (x < this.left) {
           this.left = x;
       }
       if (y < this.bottom) {
           this.bottom = y;
       }
       if (x > this.right) {
           this.right = x;
       }
       if (y > this.top) {
           this.top = y;
       }
   }
   
  
   
   public void extend(Bounds bounds){
	   this.centerLonLat = null;
	   if(empty){
		   this.left=bounds.left;
		   this.right=bounds.right;
		   this.bottom=bounds.bottom;
		   this.top=bounds.top;
		   empty=false;
	   }
       if (bounds.left < this.left) {
           this.left = bounds.left;
       }
       if (bounds.bottom < this.bottom) {
           this.bottom = bounds.bottom;
       }
       if (bounds.right > this.right) {
           this.right = bounds.right;
       }
       if (bounds.top > this.top) {
           this.top = bounds.top;
       }
   }
   
   /**
    * @return Returns whether the bounds object contains the given geoPoint
    */
   public boolean containsLonLat(GeoPoint geoPoint){
	   return !empty&&contains(geoPoint.getLongitude(), geoPoint.getLatitude());
   }
   
   public boolean contains(double x,double y){
	 return !empty&&((x >= this.left) && (x <= this.right) && 
               (y >= this.bottom) && (y <= this.top));
   }
   
   /**
    *  Determine whether the target bounds intersects this bounds.  Bounds are
     *     considered intersecting if any of their edges intersect or if one
     *     bounds contains the other.
    */
   public boolean intersectsBounds(Bounds bounds){
	   if(empty){
		   return false;
	   }
	   boolean inBottom = (
               ((bounds.bottom >= this.bottom) && (bounds.bottom <= this.top)) ||
               ((this.bottom >= bounds.bottom) && (this.bottom <= bounds.top))
           );
           boolean inTop = (
               ((bounds.top >= this.bottom) && (bounds.top <= this.top)) ||
               ((this.top > bounds.bottom) && (this.top < bounds.top))
           );
           boolean inLeft = (
               ((bounds.left >= this.left) && (bounds.left <= this.right)) ||
               ((this.left >= bounds.left) && (this.left <= bounds.right))
           );
           boolean inRight = (
               ((bounds.right >= this.left) && (bounds.right <= this.right)) ||
               ((this.right >= bounds.left) && (this.right <= bounds.right)));
	   return (inBottom || inTop) && (inLeft || inRight);
   }
   
   /**
    * @param bounds
    * @return  
    */
   public static Bounds intersects(Bounds src1,Bounds src2 ){
	   double x1 = Math.max(src1.left, src2.left);
       double y1 = Math.max(src1.bottom, src2.bottom);
       double x2 = Math.min(src1.right, src2.right);
       double y2 = Math.min(src1.top, src2.top);
	   return new Bounds(x1, y1, x2, y2 );
   }
   
   public boolean containsBounds(Bounds bounds){
	   boolean bottomLeft  = this.contains(bounds.left, bounds.bottom);
       boolean bottomRight = this.contains(bounds.right, bounds.bottom);
       boolean topLeft  = this.contains(bounds.left, bounds.top);
       boolean topRight = this.contains(bounds.right, bounds.top);
	   return bottomLeft && bottomRight && topLeft && topRight;
   }
   
   
}
