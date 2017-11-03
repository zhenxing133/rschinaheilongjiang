package com.chinars.mapapi.utils;

import java.util.ArrayList;

import com.chinars.mapapi.Bounds;
import com.chinars.mapapi.GeoPoint;
/**
 * 多边形裁剪，不能多线程
 * @author liudanfeng
 * @since 2015-10-14
 */
public class PolygonClip {
	private final double precision=1e-8;
	private final  int LEFT=1;
	private final  int RIGHT=2;
	private final  int BOTTOM=4;
	private final  int TOP=8;
	private double x,y;
	private double preX,preY;
	private double left,right;
	private double bottom,top;
	private double intersect;
	private int preCorner;//上次添加的窗口的顶点
	ArrayList<GeoPoint> ret=new ArrayList<GeoPoint>();
	private GeoPoint lb=new GeoPoint(left, bottom);
	private GeoPoint lt=new GeoPoint(left, top);
	private GeoPoint rb=new GeoPoint(right, bottom);
	private GeoPoint rt=new GeoPoint(right, top);
	
	public PolygonClip() {
	}
	public PolygonClip(Bounds bounds) {
		setWindow(bounds);
	}
	public void setWindow(Bounds bounds){
		this.left=bounds.left;
		this.right=bounds.right;
		this.bottom=bounds.bottom;
		this.top=bounds.top;
		lb=new GeoPoint(left, bottom);   
		lt=new GeoPoint(left, top);      
		rb=new GeoPoint(right, bottom);  
		rt=new GeoPoint(right, top);     
	}
	
	/**
	 *  使用矩形窗口裁剪Polygon
	 * @param geoPoints 多边形的点
	 * @param bounds   裁剪窗口
	 * @return  裁剪之后的多边形
	 */
	public  GeoPoint[] clipPolyGon(GeoPoint[] geoPoints){
		ret.clear();
		preCorner=0;
		int outcode=0,precode=0,difcode=0,sumcode;
		int polySize=geoPoints.length;
		GeoPoint[] result=null;
		x=geoPoints[0].getLongitude();
		y=geoPoints[0].getLatitude();
		if(x<left){
			outcode=LEFT;
		}else if(x>right){
			outcode=RIGHT;
		}
		if(y<bottom){
			outcode|=BOTTOM;
		}else if(y>top){
			outcode|=TOP;
		}
		for(int i=1;i<polySize;i++){
			precode=outcode;
			preX=x;preY=y;
			x=geoPoints[i].getLongitude();
			y=geoPoints[i].getLatitude();
			outcode=0;
			if(x<left){
				outcode=LEFT;
			}else if(x>right){
				outcode=RIGHT;
			}
			if(y<bottom){
				outcode|=BOTTOM;
			}else if(y>top){
				outcode|=TOP;
			}
			difcode=outcode^precode;
			sumcode=outcode|precode;
			switch(difcode){
			case 0:  //在同一区域
				if(outcode==0){
					ret.add(geoPoints[i]);
				}
				continue;
			case 1:
				if(sumcode==1){
					addLeft();
					if(outcode==0){
						ret.add(geoPoints[i]);
					}
				}else if(sumcode==5){
					addLeftBottom();
				}else{
					addLeftTop();
				}
				continue;
			case 2:
				if(sumcode==2){
					addRight();
					if(outcode==0){
						ret.add(geoPoints[i]);
					}
				}else if(sumcode==6){
					addRightBottom();
				}else{
					addRightTop();
				}
				continue;
			case 3:
				if(sumcode==3){
					if(outcode==1){
						addRight();addLeft();
					}else{
						addLeft();addRight();
					}
				}else if(sumcode==7){
					if(outcode==5){
						addRightBottom();addLeftBottom();
					}else{
						addLeftBottom();addRightBottom();
					}
				}else{
					if(outcode==9){
						addRightTop();addLeftTop();
					}else{
						addLeftTop();addRightTop();
					}
				}
				continue;
			case 4:
				if(sumcode==4){
					addBottom();
					if(outcode==0){
						ret.add(geoPoints[i]);
					}
				}else if(sumcode==5){
					addLeftBottom();
				}else{
					addRightBottom();
				}
				continue;
			case 5:
				intersect=intersectBottom();
				if(intersect>left){
					if(outcode!=4){
						ret.add(new GeoPoint(intersect, bottom));
						if(outcode==0){
							 ret.add(geoPoints[i]);
						}else if(outcode==1){
							addLeft();
						}
					}else{
						addLeft();
						ret.add(new GeoPoint(intersect, bottom));
					}
				}else{
					if(outcode==0){
						 addLeft();
						 ret.add(geoPoints[i]);
					}else if(outcode==5){
						 addLeft();
					}else{
						addLeftBottom();
					}
				}
				continue;
			case 6:
				intersect=intersectBottom();
				if(intersect<right){
					if(outcode!=4){
						ret.add(new GeoPoint(intersect, bottom));
						if(outcode==0){
							 ret.add(geoPoints[i]);
						}else if(outcode==1){
							addRight();
						}
					}else{
						addRight();
						ret.add(new GeoPoint(intersect, bottom));
					}
				}else{
					if(outcode==0){
						addRight();
						 ret.add(geoPoints[i]);
					}else if(outcode==5){
						addRight();
					}else{
						addRightBottom();
					}
				}
				continue;
			case 7:
				if(outcode==1){
					if(((preX-left)*(y-bottom)-(x-left)*(preY-bottom))<-precision){
						ret.add(lb);
					}else{
						intersect=intersectBottom();
						if(intersect<right){
							ret.add(new GeoPoint(intersect, bottom));
						}else{
							addRight();
						}
						addLeft();
					}
				}else if(outcode==2){
					if(((preX-right)*(y-bottom)-(x-right)*(preY-bottom))>precision){
						ret.add(rb);
					}else{
						intersect=intersectBottom();
						if(intersect>left){
							ret.add(new GeoPoint(intersect, bottom));
						}else{
							addLeft();
						}
						addRight();
					}
				}else if(outcode==5){
					if(((preX-right)*(y-bottom)-(x-right)*(preY-bottom))<-precision){
						ret.add(rb);
					}else{
						addRight();
						intersect=intersectBottom();
						if(intersect>left){
							ret.add(new GeoPoint(intersect, bottom));
						}else{
							addLeft();
						}
					}
				}else if(outcode==6){
					if(((preX-left)*(y-bottom)-(x-left)*(preY-bottom))>precision){
						ret.add(lb);
					}else{
						addLeft();
						intersect=intersectBottom();
						if(intersect<right){
							ret.add(new GeoPoint(intersect, bottom));
						}else{
							addRight();
						}
					}
				}
				continue;
			case 8:
				if(sumcode==8){
					addTop();
					if(outcode==0){
						ret.add(geoPoints[i]);
					}
				}else if(sumcode==9){
					addLeftTop();
				}else{
					addRightTop();
				}
				continue;
			case 9:
				intersect=intersectTop();
				if(intersect>left){
					if(outcode!=8){
						ret.add(new GeoPoint(intersect, top));
						if(outcode==0){
							 ret.add(geoPoints[i]);
						}else if(outcode==1){
							addLeft();
						}
					}else{
						addLeft();
						ret.add(new GeoPoint(intersect, top));
					}
				}else{
					if(outcode==0){
						 addLeft();
						 ret.add(geoPoints[i]);
					}else if(outcode==9){
						 addLeft();
					}else{
						addLeftTop();
					}
				}
				continue;
			case 10:
				intersect=intersectTop();
				if(intersect<right){
					if(outcode!=8){
						ret.add(new GeoPoint(intersect, top));
						if(outcode==0){
							 ret.add(geoPoints[i]);
						}else if(outcode==1){
							addRight();
						}
					}else{
						addRight();
						ret.add(new GeoPoint(intersect, top));
					}
				}else{
					if(outcode==0){
						addRight();
						ret.add(geoPoints[i]);
					}else if(outcode==10){
						addRight();
					}else{
						addRightTop();
					}
				}
				continue;
			case 11:
				if(outcode==1){
					if(((preX-left)*(y-top)-(x-left)*(preY-top))>precision){
						ret.add(lt);
						continue;
					}
					intersect=intersectTop();
					if(intersect<right){
						ret.add(new GeoPoint(intersect, top));
					}else{
						addRight();
					}
					addLeft();
				}else if(outcode==2){
					if(((preX-right)*(y-top)-(x-right)*(preY-top))<-precision){
						ret.add(rt);
						continue;
					}
					intersect=intersectTop();
					if(intersect>left){
						ret.add(new GeoPoint(intersect, top));
					}else{
						addLeft();
					}
					addRight();
				}else if(outcode==9){
					if(((preX-right)*(y-top)-(x-right)*(preY-top))>precision){
						ret.add(rt);
						continue;
					}
					addRight();
					intersect=intersectTop();
					if(intersect>left){
						ret.add(new GeoPoint(intersect, top));
					}else{
						addLeft();
					}
				}else if(outcode==10){
					if(((preX-left)*(y-top)-(x-left)*(preY-top))<-precision){
						ret.add(lt);
						continue;
					}
					addLeft();
					intersect=intersectTop();
					if(intersect<right){
						ret.add(new GeoPoint(intersect, top));
					}else{
						addRight();
					}
				}
				continue;
			case 12:
				if(sumcode==12){
					if(outcode==4){
						addTop();addBottom();
					}else{
						addBottom();addTop();
					}
				}else if(sumcode==13){
					if(outcode==5){
						addLeftTop();addLeftBottom();
					}else{
						addLeftBottom();addLeftTop();
					}
				}else{
					if(outcode==6){
						addRightTop();addRightBottom();
					}else{
						addRightBottom();addRightTop();
					}
				}
				continue;
			case 13:
				if(outcode==4){
					if(((preX-left)*(y-bottom)-(x-left)*(preY-bottom))>precision){
						ret.add(lb);
						continue;
					}
					intersect=intersectTop();
					if(intersect>left){
						ret.add(new GeoPoint(intersect, top));
					}else{
						addLeft();
					}
					addBottom();
				}else if(outcode==5){
					if(((preX-left)*(y-top)-(x-left)*(preY-top))>precision){
						ret.add(lt);
						continue;
					}
					addTop();
					intersect=intersectBottom();
					if(intersect>left){
						ret.add(new GeoPoint(intersect, bottom));
					}else{
						addLeft();
					}
				}else if(outcode==8){
					if(((preX-left)*(y-top)-(x-left)*(preY-top))<-precision){
						ret.add(lt);
						continue;
					}
					intersect=intersectBottom();
					if(intersect>left){
						ret.add(new GeoPoint(intersect, bottom));
					}else{
						addLeft();
					}
					addTop();
				}else if(outcode==9){
					if(((preX-left)*(y-bottom)-(x-left)*(preY-bottom))<-precision){
						ret.add(lb);
						continue;
					}
					addBottom();
					intersect=intersectTop();
					if(intersect>left){
						ret.add(new GeoPoint(intersect, top));
					}else{
						addLeft();
					}
				}
				continue;
			case 14:
				if(outcode==4){
					if(((preX-right)*(y-bottom)-(x-right)*(preY-bottom))<-precision){
						ret.add(rb);
						continue;
					}
					intersect=intersectTop();
					if(intersect<right){
						ret.add(new GeoPoint(intersect, top));
					}else{
						addRight();
					}
					addBottom();
				}else if(outcode==6){
					if(((preX-right)*(y-top)-(x-right)*(preY-top))<-precision){
						ret.add(rt);
						continue;
					}
					addTop();
					intersect=intersectBottom();
					if(intersect<right){
						ret.add(new GeoPoint(intersect, bottom));
					}else{
						addRight();
					}
				}else if(outcode==8){
					if(((preX-right)*(y-top)-(x-right)*(preY-top))>precision){
						ret.add(rt);
						continue;
					}
					intersect=intersectBottom();
					if(intersect<right){
						ret.add(new GeoPoint(intersect, bottom));
					}else{
						addRight();
					}
					addTop();
				}else if(outcode==10){
					if(((preX-right)*(y-top)-(x-right)*(preY-top))>precision){
						ret.add(rb);
						continue;
					}
					addBottom();
					intersect=intersectTop();
					if(intersect<right){
						ret.add(new GeoPoint(intersect, top));
					}else{
						addRight();
					}
				}
				continue;
			case 15:
				if(outcode==5){
					addRightTop();
					if(((preX-left)*(y-top)-(x-left)*(preY-top))>precision){
						addLeftTop();
						continue;
					}
					if(((preX-right)*(y-bottom)-(x-right)*(preY-bottom))<-precision){
						addRightBottom();
						continue;
					}
					intersect=intersectTop();
					if(intersect<right){
						ret.add(new GeoPoint(intersect, top));
						addLeft();
					}else{
						addRight();addBottom();
					}
				}else if(outcode==6){
					addLeftTop();
					if(((preX-right)*(y-top)-(x-right)*(preY-top))<-precision){
						addRightTop();continue;
					}
					if(((preX-left)*(y-bottom)-(x-left)*(preY-bottom))>precision){
						addLeftBottom();continue;
					}
					intersect=intersectTop();
					if(intersect>left){
						ret.add(new GeoPoint(intersect, top));
						addRight();
					}else{
						addLeft();addBottom();
					}
				}else if(outcode==9){
					addRightBottom();
					if(((preX-right)*(y-top)-(x-right)*(preY-top))>precision){
						addRightTop();continue;
					}
					if(((preX-left)*(y-bottom)-(x-left)*(preY-bottom))<-precision){
						addLeftBottom();continue;
					}
					intersect=intersectTop();
					if(intersect>left){
						addRight();
						ret.add(new GeoPoint(intersect, top));
					}else{
						addBottom();addLeft();
					}
				}else{//outcode=10
					addLeftBottom();
					if(((preX-left)*(y-top)-(x-left)*(preY-top))<-precision){
						addLeftTop();
						continue;
					}
					if(((preX-right)*(y-bottom)-(x-right)*(preY-bottom))>precision){
						addRightBottom();
						continue;
					}
					intersect=intersectTop();
					if(intersect<right){
						addLeft();
						ret.add(new GeoPoint(intersect, top));
					}else{
						addBottom();addRight();
					}
				}
				continue;
			}
		}
		result=new GeoPoint[ret.size()];
		ret.toArray(result);
		return result;
	}

	public Bounds getWindow(){
		return new Bounds(left, bottom, right, top);
	}
	
	private void addLeft(){
		ret.add(new GeoPoint(left, (y-preY)/(x-preX)*(left-preX)+preY));
	}
	
	private void addRight(){
		ret.add(new GeoPoint(right, (y-preY)/(x-preX)*(right-preX)+preY));
	}
	
	private void addBottom(){
		ret.add(new GeoPoint((x-preX)/(y-preY)*(bottom-preY)+preX, bottom));
	}
	
	private void addTop(){
		ret.add(new GeoPoint((x-preX)/(y-preY)*(top-preY)+preX, top));
	}
	
	private void addLeftBottom(){
		if(preCorner!=5){
			ret.add(lb);
		}
		preCorner=5;
	}
	private void addLeftTop(){
		if(preCorner!=9){
			ret.add(lt);
		}
		preCorner=9;
	}
	private void addRightBottom(){
		if(preCorner!=6){
			ret.add(rb);
		}
		preCorner=6;
	}
	private void addRightTop(){
		if(preCorner!=10){
			ret.add(rt);
		}
		preCorner=10;
	}

	private double intersectBottom(){
		return (x-preX)/(y-preY)*(bottom-preY)+preX;
	}
	
	private double intersectTop(){
		return (x-preX)/(y-preY)*(top-preY)+preX;
	}
}


