package com.chinars.mapapi.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chinars.mapapi.PoiOverlay;
/**
 * 用于整合多次搜索的结果或者对一次返回过多的POI结果进行再分页
 * @author Administrator
 *
 */
public class PoiSearchResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<PoiInfo> poiList=new ArrayList<PoiInfo>();
	private int pageIndex=0;
	private int pageSize=20;
	
	public void addPoiInfo(PoiInfo poi){
		poiList.add(poi);
	}
	 public void addPoiInfos(PoiInfos pois){
		 if(pois!=null&&pois.results!=null){
			 poiList.addAll(pois.results);
		 }
	 }
	
	 public void setPageIndex(int pageIndex){
		 this.pageIndex=pageIndex;
	 }
	 
	 public void setPageSize(int pageSize){
		 this.pageSize=pageSize;
	 }
	 
	public PoiInfo getPoi(int index){
		return poiList.get(index);
	}
	
	 public List<PoiInfo> getAllPoi(){
		 return poiList;
	 }
	 
	 public int getNumPois(){
		return poiList.size();
	 }
	 
	public List<PoiInfo> getNextPage(){
		 List<PoiInfo> result=new ArrayList<PoiInfo>();
		 for(int i=pageIndex*pageSize;i<(pageIndex+1)*pageSize&&i<poiList.size();i++){
			 result.add(poiList.get(i));
		 }
		 pageIndex=pageIndex+1;
		 return result;
	 }
	 
	  public int getNumPages(){
		 return poiList.size()/pageSize;
	 }
	  
	  public int getPageIndex(){
		  return pageIndex;
	  }
	  
}
