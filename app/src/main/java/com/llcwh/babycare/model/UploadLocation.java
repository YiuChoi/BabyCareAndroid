package com.llcwh.babycare.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/9/5 0005.
 */
public class UploadLocation {
    @SerializedName("lat")
    private String lat;
    @SerializedName("lng")
    private String lng;
    @SerializedName("address")
    private String address;
    @SerializedName("baby_uuid")
    private String baby_uuid;

    public UploadLocation(String lat, String lng, String address, String baby_uuid) {
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.baby_uuid = baby_uuid;
    }

    public String getLac() {
        return lat;
    }

    public void setLac(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBaby_uuid() {
        return baby_uuid;
    }

    public void setBaby_uuid(String baby_uuid) {
        this.baby_uuid = baby_uuid;
    }
}
