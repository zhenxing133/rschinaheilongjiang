package com.chinars.mapapi.utils;
import java.util.ArrayList;
import org.json.JSONArray;  
import org.json.JSONException;  
import org.json.JSONObject;

import com.chinars.mapapi.GeoPoint;
import com.chinars.mapapi.search.LocationInfo;
import com.chinars.mapapi.search.PoiInfo;
import com.chinars.mapapi.search.PoiInfos;

public class HttpJsonUtils {

	public static PoiInfo jsonStringToPoiInfo (String json) throws JSONException{
		JSONObject jsonObject=new JSONObject(json);
		PoiInfo poiInfo=new PoiInfo();
		poiInfo.addr=jsonObject.optString("addr");
		poiInfo.admincode=jsonObject.optString("admincode");
		poiInfo.city=jsonObject.optString("city");
		poiInfo.district=jsonObject.optString("district");
		poiInfo.geoPoint=new GeoPoint(jsonObject.optDouble("lon"),jsonObject.optDouble("lat"));
		poiInfo.poiType=jsonObject.optString("kind");
		poiInfo.name=jsonObject.optString("name");
		poiInfo.province=jsonObject.optString("province");
		poiInfo.py=jsonObject.optString("py");
		poiInfo.telephone=jsonObject.optString("telephone");
		poiInfo.vadmincode=jsonObject.optString("vadmincode");
		poiInfo.zipcode=jsonObject.optString("zipcode");
		//LogUtils.d(poiInfo.toString());
		return poiInfo;
	}
	
	public static LocationInfo jsonStringToLocationInfo(String json) throws JSONException{
		JSONObject jsonObject=new JSONObject(json);
		LocationInfo locationInfo=new LocationInfo();
		locationInfo.city=jsonObject.optString("city");
		locationInfo.code=jsonObject.optString("code");
		locationInfo.detail=jsonObject.optString("detail");
		locationInfo.district=jsonObject.optString("district");
		locationInfo.message=jsonObject.optString("message");
		locationInfo.province=jsonObject.optString("province");
		//LogUtils.d(locationInfo.toString());
		return locationInfo;
	}
	
	public static PoiInfos jsonStringToPoiInfos(String json)throws JSONException{
		JSONObject jsonObject=new JSONObject(json);
		PoiInfos poiInfos=new PoiInfos();
		poiInfos.code=jsonObject.optString("code");
		poiInfos.message=jsonObject.optString("message");
		poiInfos.total=jsonObject.optInt("total");
		JSONArray jsonArray=jsonObject.optJSONArray("results");
		if("1".equals(poiInfos.code)&&jsonArray!=null&&jsonArray.length()>0){
			if(poiInfos.message.contains("页")){
				String[] arr = poiInfos.message.split("页");
				poiInfos.curPage=Integer.valueOf(arr[0].replaceAll("\\D", ""));
				poiInfos.numPage=Integer.valueOf(arr[1].replaceAll("\\D", ""));
			}
			LogUtils.d(""+poiInfos.curPage);
			poiInfos.results=new ArrayList<PoiInfo>();
			for(int i=0,size=jsonArray.length();i<size;i++){
				poiInfos.results.add(jsonStringToPoiInfo(jsonArray.get(i).toString()));
			}
		}
		
		//LogUtils.d(poiInfos.toString());
		return poiInfos;
	}
}
