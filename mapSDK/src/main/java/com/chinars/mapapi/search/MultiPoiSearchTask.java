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

public class MultiPoiSearchTask extends AsyncTask<Object, Void, String>{
	private Integer searchType;
	private PoiSearchListener poiSearchListener=null;
	private int error=1;
	private PoiSearchResult poiSearchResult=new PoiSearchResult();
	@Override
	protected String doInBackground(Object... params) {
		searchType=(Integer) params[1];
		poiSearchListener=(PoiSearchListener)params[2];
		HttpClient client = new DefaultHttpClient();
		StringBuilder builder = new StringBuilder(); 
		LogUtils.d("url:"+params[0]);
		String[] urls=(String[]) params[0];
		for(int i=0;i<urls.length;i++){
			HttpGet get = new HttpGet((String)params[0]);  
			try {  
				HttpResponse response = client.execute(get);  
				BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));  
				for (String s = reader.readLine(); s != null; s = reader.readLine()) {  
					builder.append(s);  
				}  
				LogUtils.d(builder.toString()); 
				poiSearchResult.addPoiInfos(HttpJsonUtils.jsonStringToPoiInfos(builder.toString()));
			}catch(Exception e){
				error=0;
				LogUtils.wtf(e);
				return "fail";
			}
		}
		return "success";
	}

	
	@Override
	protected void onPostExecute(String result) {
		if(result.equals("success")){
			if(poiSearchListener!=null){
				poiSearchListener.onGetMutliPoiResult(poiSearchResult, error);
			}
		}
	}
}
