package com.chinars.mapapi.search;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

import com.chinars.mapapi.utils.HttpJsonUtils;
import com.chinars.mapapi.utils.LogUtils;

class PoiSearchTask extends AsyncTask<Object, Void, String>{
	private Integer searchType;
	private PoiSearchListener poiSearchListener=null;
	private int error=1;
	private String url;
	@Override
	protected String doInBackground(Object... params) {
		searchType=(Integer) params[1];
		poiSearchListener=(PoiSearchListener)params[2];
		HttpClient client = new DefaultHttpClient();
		StringBuilder builder = new StringBuilder(); 
		url=(String)params[0];
		LogUtils.d("url:"+url);
		PoiSearch.tasks.add(this);
		HttpGet get = new HttpGet(url);  
		try {  
			HttpResponse response = client.execute(get);  
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));  
			for (String s = reader.readLine(); s != null; s = reader.readLine()) {  
				builder.append(s);  
			}  
			//LogUtils.d(builder.toString());  
		}catch(Exception e){
			error=0;
			LogUtils.wtf(e);
		}
		return builder.toString();
	}
	
	@Override
	protected void onPostExecute(String result) {
		try{
			if(poiSearchListener==null){
				return;
			}
			int pos=result.indexOf("code")+6;
			error=Integer.valueOf(result.substring(pos, pos+1));
			switch(searchType){
			case PoiSearch.TYPE_POI_LIST: 
				poiSearchListener.onGetPoiResult(HttpJsonUtils.jsonStringToPoiInfos(result),error);
				break;
			case PoiSearch.TYPE_DETAIL_SEARCH:case PoiSearch.TYPE_REVERSE_GOECODE:
				LocationInfo locationInfo=HttpJsonUtils.jsonStringToLocationInfo(result);
				poiSearchListener.onGeAddrResult(locationInfo, error);
				break;
			case PoiSearch.TYPE_GEOCODE:
				break;
			case PoiSearch.TYPE_AREA_MULTI_POI_LIST:
				break;
				
			case PoiSearch.TYPE_SEARCH_ALL:
				PoiSearchResult poiSearchResult=new PoiSearchResult();
				poiSearchResult.addPoiInfos(HttpJsonUtils.jsonStringToPoiInfos(result));
				poiSearchListener.onGetMutliPoiResult(poiSearchResult, pos);
//			case PoiSearch.TYPE_REVERSE_GOECODE:
//				PoiInfo poiInfo=HttpJsonUtils.jsonStringToPoiInfo(result);
//				poiSearchListener.onGetAddrResult(poiInfo, error);
//				break;
			}
			PoiSearch.tasks.remove(this);
		}catch(Exception e){
			LogUtils.d("failure", e);
		}
	}
	
}
