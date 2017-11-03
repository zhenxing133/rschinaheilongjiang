package com.rschina.heilongjiang.model;

import android.graphics.Bitmap;

import com.android.volley.Response;
import com.chinars.mapapi.GeoPoint;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

public interface DiscoveryService {
    /**
     * restful请求名称
     */
    String LIST_DISCOVERY="discovery/listDiscovery";
    String LIST_COMMENT="discovery/listComment";
    String ADD_COMMENT="discovery/addComment";
    String ADD_LIKE="discovery/addLike";
    String DELETE_LIKE ="discovery/deleteLike";
    String MODIFY_SEX="user/editSex";
    String MODIFY_AVATAR="user/editPic";
    String MODIFY_NICKNAME="user/editNickname";
    String GET_TIME_AND_RES="getImgTimeAndRes";
    String ADD_SUBSCRIPTION="saveSubscription";
    String DELETE_SUBSCRIPTION="unSubscription";
    String LIST_SUBSCRIPTION="getSubscriptionInfosByUser";
    String MARK_SUBSCRIPTION="markSubscriptionUpdate";
    String GET_SUBSCRIPTION_DETAIL="getSubscriptionUpdateInfo";

    /**
     *获取最新话题列表,最多返回10条
     */
    public void getLastTopics(CustomResponseListener listener);

    /**
     *获取地图更新列表,最多返回5条
     */
    public void getMapMessage(CustomResponseListener listener);

    /**
     * 获取某个时间点之前的10条数据
     * @param time 时间点
     */
    public void getTenTopicsBefore(long time, CustomResponseListener listener);

    /**
     *获取某一个话题的评论,最新10条
     * @param topicId
     */
    public void getLastComments(String topicId, CustomResponseListener listener);

    /**
     * 获取某个时间点之前的10条评论
     * @param time 时间点
     */
    public  void getCommentsBefore(String topicId, long time, CustomResponseListener listener);

    /**
     * 添加评论
     * @param topicId
     * @param content
     */
    public void  addComment(String topicId, String content, CustomResponseListener listener);

    /**
     * 话题点赞
     * @param topicId
     * @param isLike 是否点赞
     */
    public  void setLike(String topicId, boolean isLike, CustomResponseListener listener);


    public  void modifyNickname(String nickName, CustomResponseListener listener);

    public  void modifySex(int sex, CustomResponseListener listener);

    public  void modifyAvatar(File bitmapFile, CustomResponseListener listener);

    public  boolean getTimeAndRes(int zoom, GeoPoint center, CustomResponseListener listener);

    public  void getFiveWeatherTime(String type, CustomResponseListener listener);

    /**
     *
     * @param point  位置
     * @param zoom   缩放
     * @param sIndex  服务编号：1-5，最近1天为1，最近2天为2
     * @param listener  响应接口
     */
    public  void getAqiOfPoint(GeoPoint point, int zoom, int sIndex, CustomResponseListener listener);

    void addSubscription(String name, GeoPoint center, int zoom, double spanX, double spanY, CustomResponseListener listener);

    void deleteSubscription(String id, CustomResponseListener listener);

    void listSubscription(CustomResponseListener listener);

    void getSubscriptionDetail(String id, CustomResponseListener listener);

    void markSubscription(String id, CustomResponseListener listener);

}
