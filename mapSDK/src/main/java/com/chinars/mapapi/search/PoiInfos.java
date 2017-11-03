package com.chinars.mapapi.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chinars.mapapi.PoiOverlay;
/**
 * 一次POI搜索返回的结果
 * @author  rsclouds 中科遥感
 *
 */
public class PoiInfos implements Serializable{
	private static final long serialVersionUID = 1L;
	
	public String code;				// 结果状态，0 错误，1 正常
	public String message;				// 状态详细信息
	public int total;					// 结果poi总数
	public int curPage=0;
	public int numPage=0;
	public List<PoiInfo> results;		// poi集合
	
	@Override
	public String toString() {
		if(results==null){
			return "empty results";
		}else{
			StringBuffer buffer=new StringBuffer("{'total':"+total+",'result':[");
			for(int i=0,size=results.size();i<size;i++){
				buffer.append(results.get(i).toString());
			}
			buffer.append("]}");
			return buffer.toString();
		}
	}
}
