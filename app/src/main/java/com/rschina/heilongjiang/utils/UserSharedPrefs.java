package com.rschina.heilongjiang.utils;

import com.chinars.mapapi.GeoPoint;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashSet;
import java.util.Set;


public class UserSharedPrefs {
	private SharedPreferences sp;
	private Editor editor;
	public UserSharedPrefs(Context context) {
		sp = context.getSharedPreferences(Constants.SP_USER, Context.MODE_PRIVATE);
	}
	
	public void removeCookie(){
		editor.remove("set_Cookie");
	}
	public void openEditor() {
		editor = sp.edit();
	}
	
	public void closeEditor() {
		editor.commit();
	}
	
	
	public String getCookie() {
		return sp.getString("set_Cookie", "");
	}

	public void setCookie(String setCookie) {
		editor.putString("set_Cookie", setCookie);
	}

	// 设置当前用户账户名
	public String getUsername() {
		return sp.getString("username", "");
	}

	public void setUsername(String s_username) {
		editor.putString("username", s_username);
	}
	
	// 设置当前用户密码
	public void setPassword(String password) {
		editor.putString("password", password);
	}
	
	public String getPassword() {
		return sp.getString("password", "");
	}

	public void setPushKeys(Set<String> pushKeys){
		editor.putStringSet("pushKeys",pushKeys);
	}

	public Set<String> getPushKeys(){
		return  sp.getStringSet("pushKeys",new HashSet<String>());
	}

	// 设置当前用户是否登陆过
	public void setHasLogin(boolean hasLogin) {
		editor.putBoolean("hasLogin", hasLogin);
	}
	
	public boolean getHasLogin() {
		return sp.getBoolean("hasLogin", false);
	}
	// 设置当前用户是否登陆过
	public void setHasRunned(boolean hasRunned) {
		editor.putBoolean("hasRunned", hasRunned);
	}
	
	public boolean getHasRunned() {
		return sp.getBoolean("hasRunned", false);
	}
	// 设置在收藏中是否点了不再提示网路流量
	public void setHintNoMore(boolean hintNoMore) {
		editor.putBoolean("hintNoMore", hintNoMore);
	}
	
	public boolean getHintNoMore() {
		return sp.getBoolean("hintNoMore", false);
	}
	
	// 服务器后台session
	public void setSessionId(String sessionId) {
		editor.putString("SESSION_ID", sessionId);
	}

	public String getSessionId() {
		return sp.getString("SESSION_ID", "");
	}
	
	//保存用户ID
	public void setUserId(String userId) {
		editor.putString("user_id", userId);
	}
	
	public String getUserId() {
		return sp.getString("user_id", "");
	}

	//保存用户性别
	public void setSex(int userSex) {
		editor.putInt("user_sex", userSex);
	}

	public int getSex() {
		return sp.getInt("user_sex",3);
	}

	//保存用户昵称
	public void setUserNickname(String userNickname) {
		editor.putString("user_niakname", userNickname);
	}

	public String getUserNickname() {
		return sp.getString("user_niakname", "");
	}

	public void setLocation(GeoPoint latLng){
		editor.putString("location",latLng.getLongitude()+","+latLng.getLatitude());
	}

	public GeoPoint getLocation(){
		String temp=sp.getString("location",null);
		if (temp!=null){
			String[] latLng=temp.split(",");
			return new GeoPoint(latLng[0],latLng[1]);
		}else {
			return null;
		}
	}

	//用户头像
	public String getUser_picture() {
		return sp.getString("user_picture", "");
	}
	public void setUser_picture(String user_picture) {
		editor.putString("user_picture", user_picture);
	}

	//登录所取得有效location地址(半小时有效)
	public String getTGT() {
		return sp.getString("TGT", "");
	}
	public void setTGT(String TGT) {
		editor.putString("TGT", TGT);
	}

	//用户注册所使用的手机号码
	public String getUserPhone() {
		return sp.getString("userPhone", "");
	}
	public void setUserPhone(String userPhone) {
		editor.putString("userPhone", userPhone);
	}

	public int getNeglectVersionNum(){
		return sp.getInt("neglectVersionNum", 1);
	}
	
	public void setNeglectVersionNum(int versionNum){
		editor.putInt("neglectVersionNum", versionNum);
	}
	
	//用户注册所使用的邮箱
	public String getUserEmail() {
		return sp.getString("userEmail", "");
	}
	public void setUserEmail(String userEmail) {
		editor.putString("userEmail", userEmail);
	}
	
	public void doClear(){
		editor.clear();
	}

}