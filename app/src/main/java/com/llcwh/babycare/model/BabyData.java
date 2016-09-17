package com.llcwh.babycare.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/9/5 0005.
 */
public class BabyData implements Parcelable {
    @SerializedName("is_admin")
    private boolean is_admin;
    @SerializedName("relationship")
    private String relationship;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("baby_uuid")
    private String baby_uuid;
    @SerializedName("lat")
    private String lat;
    @SerializedName("lng")
    private String lng;
    @SerializedName("address")
    private String address;
    @SerializedName("last_time")
    private String last_time;
    @SerializedName("upload_user")
    private String upload_user;

    public boolean is_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getUpload_user() {
        return upload_user;
    }

    public void setUpload_user(String upload_user) {
        this.upload_user = upload_user;
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

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getBaby_uuid() {
        return baby_uuid;
    }

    public void setBaby_uuid(String baby_uuid) {
        this.baby_uuid = baby_uuid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.is_admin ? (byte) 1 : (byte) 0);
        dest.writeString(this.relationship);
        dest.writeString(this.nickname);
        dest.writeString(this.baby_uuid);
        dest.writeString(this.lat);
        dest.writeString(this.lng);
        dest.writeString(this.address);
        dest.writeString(this.last_time);
        dest.writeString(this.upload_user);
    }

    public BabyData() {
    }

    protected BabyData(Parcel in) {
        this.is_admin = in.readByte() != 0;
        this.relationship = in.readString();
        this.nickname = in.readString();
        this.baby_uuid = in.readString();
        this.lat = in.readString();
        this.lng = in.readString();
        this.address = in.readString();
        this.last_time = in.readString();
        this.upload_user = in.readString();
    }

    public static final Parcelable.Creator<BabyData> CREATOR = new Parcelable.Creator<BabyData>() {
        @Override
        public BabyData createFromParcel(Parcel source) {
            return new BabyData(source);
        }

        @Override
        public BabyData[] newArray(int size) {
            return new BabyData[size];
        }
    };
}
