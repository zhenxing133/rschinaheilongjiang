package com.chinars.mapapi.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.chinars.mapapi.Bounds;
import com.chinars.mapapi.GeoPoint;

/**
 * 
 * @author 刘丹枫
 * 
 */
public class GeoAlgorithm  {

	double INFINITY = 1e10;

	double ESP = 1e-5;

	int MAX_N = 1000;
	private final static int INNER=0;
	private final static int LEFT=1;
	private final static int RIGHT=2;
	private final static int BOTTOM=4;
	private final static int TOP=8;
	static final int table[] = {
		0, 1, 1, 2, 
		1, 2, 2, 3, 
		1, 2, 2, 3, 
		2, 3, 3, 4
	};

	// 计算叉乘 |P0P1| × |P0P2|
	static double multi(Point p1, Point p2, Point p0){
		return ( (p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y) );
	}

	// 判断线段是否包含点point
	private boolean isOnline(Point point, LineSegment line)
	{
		return( ( Math.abs(multi(line.start, line.end, point)) < ESP ) &&
				( ( point.x - line.start.x ) * ( point.x - line.end.x ) <= 0 ) &&
				( ( point.y - line.start.y ) * ( point.y - line.end.y ) <= 0 ) );
	}

	//求一个点是否在一条边的内侧，在点序为逆时针的时候（如果点在线上，也算在内侧）
	public static boolean isInside(Point p,LineSegment v){
		return multi(p,v.start,v.end)>=0?true:false;
	}

	// 判断线段相交
	private boolean intersect(LineSegment L1, LineSegment L2){
		return( (Math.max(L1.start.x, L1.end.x) >= Math.min(L2.start.x, L2.end.x)) &&
				(Math.max(L2.start.x, L2.end.x) >= Math.min(L1.start.x, L1.end.x)) &&
				(Math.max(L1.start.y, L1.end.y) >= Math.min(L2.start.y, L2.end.y)) &&
				(Math.max(L2.start.y, L2.end.y) >= Math.min(L1.start.y, L1.end.y)) &&
				(multi(L2.start, L1.end, L1.start) * multi(L1.end, L2.end, L1.start) >= 0) &&
				(multi(L1.start, L2.end, L2.start) * multi(L2.end, L1.end, L2.start) >= 0)
				);
	}

	public static Point intersect(Point start0,Point end0,Point start1,Point end1){
		//由正弦定理推出
		double pX = (multi(start0, end1, start1)*end0.x - multi(end0, end1, start1)*start0.x)/
				(multi(start0, end1, start1) - multi(end0, end1, start1));
		double pY = (multi(start0, end1, start1)*end0.y - multi(end0, end1, start1)*start0.y)/
				(multi(start0, end1, start1) - multi(end0, end1, start1));
		return new Point(pX,pY);
	}


	/* 射线法判断点q与多边形polygon的位置关系，要求polygon为简单多边形，顶点逆时针排列
	如果点在多边形内： 返回0
	如果点在多边形边上： 返回1
	如果点在多边形外： 返回2
	 */
	public boolean InPolygon(List<Point> polygon, double x,double y){
		int n = polygon.size();
		int count = 0;
		LineSegment line=new LineSegment();
		LineSegment side = new LineSegment();
		line.start.x = x;
		line.start.y = y;
		line.end.y = y;
		line.end.x = - INFINITY;
		for( int i = 0; i < n; i++ ) {
			// 得到多边形的一条边
			side.start = polygon.get(i);
			side.end = polygon.get((i + 1) % n);
			if( isOnline(line.start, side) ) {
				return true ;
			}

			// 如果side平行x轴则不作考虑
			if( Math.abs(side.start.y - side.end.y) < ESP ) {
				continue;
			}

			if( isOnline(side.start, line) ) {
				if( side.start.y > side.end.y ) count++;
			} else if( isOnline(side.end, line) ) {
				if( side.end.y > side.start.y ) count++;
			} else if( intersect(line, side) ) {
				count++;
			}
		}
		if ( count % 2 == 1 ) {
			return true;
		}else {
			return false;
		}
	}

	/**
	 *  使用矩形窗口裁剪Polygon
	 * @param geoPoints 多边形的点
	 * @param bounds   裁剪窗口
	 * @return  裁剪之后的多边形
	 */
	public static GeoPoint[] cutPolyGonByWindow(GeoPoint[] geoPoints,Bounds window){
		int polySize=geoPoints.length;
		GeoPoint[] result=null;
		ArrayList<GeoPoint> ret=new ArrayList<GeoPoint>();
		double x=geoPoints[0].getLongitude(),y=geoPoints[0].getLatitude();
		double preX,preY;
		double left=window.left , right=window.right;
		double bottom=window.bottom , top=window.top;
		double intersect,intersectLeft=0,intersectRight=0,intersectTop=0,intersectBottom=0;
		int preCorner=0,corner;//上次添加的窗口的顶点
		int outcode=0,precode=0,difcode=0,sumcode;
		final int leftTop=LEFT|TOP,leftBottom=LEFT|BOTTOM,rightTop=RIGHT|TOP,rightBottom=RIGHT|BOTTOM;
		if(x<left){
			outcode|=LEFT;
		}else if(x>right){
			outcode|=RIGHT;
		}
		if(y<bottom){
			outcode|=BOTTOM;
		}else if(y>top){
			outcode|=TOP;
		}
		for(int i=1;i<polySize;i++){
			precode=outcode;
			preX=x;
			preY=y;
			x=geoPoints[i].getLongitude();
			y=geoPoints[i].getLatitude();
			outcode=0;
			corner=0;
			if(x<left){
				outcode|=LEFT;
			}else if(x>right){
				outcode|=RIGHT;
			}
			if(y<bottom){
				outcode|=BOTTOM;
			}else if(y>top){
				outcode|=TOP;
			}
			difcode=outcode^precode;
			sumcode=outcode|precode;
			switch(table[difcode]){
			case 0:  //在同一区域
				if(outcode==0){
					ret.add(geoPoints[i]);
				}
				continue;
			case 1: //在相邻区域
				switch(difcode){
				case LEFT: 
					intersect=(y-preY)/(x-preX)*(left-preX)+preY;
					if(intersect<bottom){
						if(preCorner!=leftBottom){
							ret.add(new GeoPoint(left, bottom));
						}
					}else if(intersect>top){
						ret.add(new GeoPoint(left, top));
						if(preCorner!=leftTop){
							ret.add(new GeoPoint(left, top));
						}
					}else{
						ret.add(new GeoPoint(left, intersect));
						if(outcode==0){
							ret.add(geoPoints[i]);
						}
					}
					continue;
				case RIGHT:
					intersect=(y-preY)/(x-preX)*(right-preX)+preY;
					if(intersect<bottom){
						if(preCorner!=rightBottom){
							ret.add(new GeoPoint(right, bottom));
						}
					}else if(intersect>top){
						ret.add(new GeoPoint(right, top));
						if(preCorner!=rightTop){
							ret.add(new GeoPoint(right, top));
						}
					}else{
						ret.add(new GeoPoint(right, intersect));
						if(outcode==0){
							ret.add(geoPoints[i]);
						}
					}
					continue;
				case BOTTOM:
					intersect=(x-preX)/(y-preY)*(bottom-preY)+preX;
					if(intersect<left){
						if(preCorner!=leftBottom){
							ret.add(new GeoPoint(left, bottom));
						}
					}else if(intersect>right){
						if(preCorner!=rightBottom){
							ret.add(new GeoPoint(right, bottom));
						}
					}else{
						ret.add(new GeoPoint(intersect,bottom));
						if(outcode==0){
							ret.add(geoPoints[i]);
						}
					}
					continue;
				case TOP:
					intersect=(x-preX)/(y-preY)*(top-preY)+preX;
					if(intersect<left){
						if(preCorner!=leftTop){
							ret.add(new GeoPoint(left, top));
						}
					}else if(intersect>right){
						if(preCorner!=rightTop){
							ret.add(new GeoPoint(right, top));
						}
					}else{
						ret.add(new GeoPoint(intersect,top));
						if(outcode==0){
							ret.add(geoPoints[i]);
						}
					}
					continue;
				}
			default://两个以上交点
				if((difcode&LEFT)==LEFT){
					intersectLeft=(y-preY)/(x-preX)*(left-preX)+preY;
				}
				if((difcode&RIGHT)==RIGHT){
					intersectRight=(y-preY)/(x-preX)*(right-preX)+preY;
				}
				if((difcode&BOTTOM)==BOTTOM){
					intersectBottom=(x-preX)/(y-preY)*(bottom-preY)+preX;
				}
				if((difcode&TOP)==TOP){
					intersectTop=(x-preX)/(y-preY)*(top-preY)+preX;
				}
				switch(precode){
				case 0:
					switch(outcode){
					case 5:
						if(intersectLeft>bottom){
							ret.add(new GeoPoint(left, intersectLeft));
						}else{
							ret.add(new GeoPoint(intersectBottom, bottom));
						}
					case 6:
						if(intersectRight>bottom){
							ret.add(new GeoPoint(right, intersectLeft));
						}else{
							ret.add(new GeoPoint(intersectBottom, bottom));
						}
					case 9:
						if(intersectLeft<top){
							ret.add(new GeoPoint(left, intersectLeft));
						}else{
							ret.add(new GeoPoint(intersectTop, top));
						}
					case 10:
						if(intersectRight<top){
							ret.add(new GeoPoint(right, intersectLeft));
						}else{
							ret.add(new GeoPoint(intersectTop, top));
						}
					}
				}
			}
		}
		result=new GeoPoint[ret.size()];
		ret.toArray(result);
		return result;
	}


	//裁剪算法
	public static List<Point> Sutherland_Hodgeman(List<Point> points,List<LineSegment> vectors){
		List<Point> result = new ArrayList<Point>();
		List<Point> cur = new ArrayList<Point>();

		int vectorsSize = vectors.size();
		int pointSize = points.size();

		Point S = points.get(pointSize-1);
		//初始化操作的集合
		for(int i=0;i<pointSize;i++){
			result.add(points.get(i));
		}

		boolean flag;
		for(int j=0;j<vectorsSize;j++){
			//flag = false表示在内侧，flag = true表示在外侧
			if(isInside(S,vectors.get(j)))
				flag = false;
			else
				flag = true;
			int resultSize = result.size();
			for(int i=0;i<resultSize;i++){
				//证明其在当前vector的内
				if(isInside(result.get(i),vectors.get(j))){
					//如果前一个点在vector的外侧，那么将他们的交点加入到结果集中
					if(flag){
						flag = false;
						cur.add(intersect(S, result.get(i), vectors.get(j).start, vectors.get(j).end));
					}
					//并将当前节点加入到结果集中
					cur.add(result.get(i));
				}
				else{
					//前一个点在外侧吗？
					if(!flag){
						flag = true;
						//如果前一个点在vector的内侧，那么将他们的交点加入到结果集中
						cur.add(intersect(S, result.get(i), vectors.get(j).start, vectors.get(j).end));
					}
				}
				//更新首次比较的节点
				S = result.get(i);
			}
			//将本次结果拷贝出来，作为下次对比的样本，并将本次结果进行清空
			int resultLen = result.size();
			result.clear();
			for(int i=0;i<resultLen;i++){
				result.add(cur.get(i));
			}
			cur.clear();
		}
		return result;
	}
}




class LineSegment
{
	public Point start;
	public Point end;
	public LineSegment()
	{
		this.start = new Point();
		this.end = new Point();
	}
	public LineSegment(Point start,Point end){
		this.start = start;
		this.end = end;
	}
}