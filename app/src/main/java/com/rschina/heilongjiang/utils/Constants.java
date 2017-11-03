package com.rschina.heilongjiang.utils;

public class Constants {






	/**SharedPreferences*/
	
	/**用户相关SharedPreferences*/
	public static  final String SP_USER = "user";
	/**用户设置SharedPreferences*/
	public static  final String SP_SET = "userSet";
	/**分享相关参数ShareMsgSharedPrefs*/
	public static  final String SP_SHARE = "shareMsg";
	public static final String RULE_SERVER_HOST="http://www.1010earth.com/use_regulations_app";
	public static final String SHARE_SERVER_HOST="http://www.1010earth.com/index?";

	
	/********************************服务器通信ID*******************************************/
	/**是否已登录*/
	public static  final int IS_LOGIN =							0x77020000;
	/**用户登录*/
	public static  final int USER_LOGIN =						0x77020001;
	/**用户注销登录*/
	public static  final int USER_LOGOUT =						0x77020001;
	/**用户注册*/
	public static  final int USER_REGIST =						0x77020006;
	/**用户头像下载*/
	public static  final int USER_PIC_DOWNLOAD =				0x77020020;
	/**密码找回时  发送手机号码获取验证码*/
	public static  final int PSD_GET_SEND_PHONE =				0x77020007;
	/**密码找回时  发送邮箱获取验证码*/
	public static  final int PSD_GET_SEND_EMAIL =				0x77020008;
	/**通过验证码修改密码*/
	public static  final int USER_SET_NEW_PSD_BY_CODE =		0x77020009;
	/**提交建议*/
	public static  final int COMMIT_ADVICE =					0x77020010;
	/**检验验证码*/
	public static  final int CHECK_CODE =						0x77020011;	
	/**修改密码*/
	public static  final int SET_NEW_PWD =						0x77020012; 
	/**获取收藏列表*/
	public static  final int GET_FAVORITE =						0x77020013;
	/**删除收藏*/
	public static  final int Delete_FAVORITE=					0x77020014;
	/**意见反馈*/
	public static  final int FEEDBACK =							0x77020015;
	/**获取分享链接*/
	public static  final int GET_URL =							0x77020016;		


}
