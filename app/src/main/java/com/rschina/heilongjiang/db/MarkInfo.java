package com.rschina.heilongjiang.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/10/11.
 */

public class MarkInfo extends DataSupport{

    private int number;

    private String name;

    private String des;

    private double latitude;

    private double longitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
