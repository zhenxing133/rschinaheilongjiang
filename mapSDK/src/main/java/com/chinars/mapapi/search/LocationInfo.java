package com.chinars.mapapi.search;

import java.io.Serializable;

public class LocationInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public String code;		// 结果状态，0 错误，1 正常
	public String message;		// 状态详细信息
	public String detail;		// 详细位置，省直辖市地级市区县
	public String province;		// 省直辖市
	public String city;			// 地级市
	public String district;		// 区县
	
	@Override
	public String toString() {
		return "{'detail':"+detail+",'city':"+city+"}";
	}
}
