package com.llcwh.babycare.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/9/5 0005.
 */
public class LocationResponse implements Parcelable {
    @SerializedName("status")
    private boolean status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("lat")
    private String lat;
    @SerializedName("lng")
    private String lng;
    @SerializedName("address")
    private String address;
    @SerializedName("last_time")
    private String last_time;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
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

    public String getLast_time() {
        return last_time;
    }

    public void setLast_time(String last_time) {
        this.last_time = last_time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeString(this.msg);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.address);
        dest.writeString(this.last_time);
    }

    public LocationResponse() {
    }

    protected LocationResponse(Parcel in) {
        this.status = in.readByte() != 0;
        this.msg = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.address = in.readString();
        this.last_time = in.readString();
    }

    public static final Parcelable.Creator<LocationResponse> CREATOR = new Parcelable.Creator<LocationResponse>() {
        @Override
        public LocationResponse createFromParcel(Parcel source) {
            return new LocationResponse(source);
        }

        @Override
        public LocationResponse[] newArray(int size) {
            return new LocationResponse[size];
        }
    };
}
