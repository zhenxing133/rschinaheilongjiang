package com.rschina.heilongjiang.model;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/9/16.
 */

public class TypeInfo {
    public String dataclsid;
    public String name;
    public String parentid;
    public String relationids;
    public int sort;
    public int status;
    public String tag;



    public void fromJson(JSONObject json) {
        try {
            dataclsid=json.getString("dataclsid");
            name=json.getString("name");
            parentid=json.getString("parentid");
            relationids=json.getString("relationids");
            sort=json.getInt("sort");
            status=json.getInt("status");
            tag=json.getString("tag");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
