package com.chinars.mapapi.offline;

import java.util.List;

public interface OfflineMapListener {
	void onCityListUpdate(List<OfflineMapInfo> cities);
	void onProgressUpdate(int cityId,int progress);
	void onOfflineMapUpdate(int cityId);
	void onDownloadFinish(int cityId);
	void onFailure(int cityId,String msg);
}
