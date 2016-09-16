package com.llcwh.babycare.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by caiya on 2016/9/16 0016.
 */

public class LocationData implements Parcelable {
    @SerializedName("status")
    private boolean status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    ArrayList<LocationResponse> locationResponses;

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

    public ArrayList<LocationResponse> getLocationResponses() {
        return locationResponses;
    }

    public void setLocationResponses(ArrayList<LocationResponse> locationResponses) {
        this.locationResponses = locationResponses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeString(this.msg);
        dest.writeTypedList(this.locationResponses);
    }

    public LocationData() {
    }

    protected LocationData(Parcel in) {
        this.status = in.readByte() != 0;
        this.msg = in.readString();
        this.locationResponses = in.createTypedArrayList(LocationResponse.CREATOR);
    }

    public static final Parcelable.Creator<LocationData> CREATOR = new Parcelable.Creator<LocationData>() {
        @Override
        public LocationData createFromParcel(Parcel source) {
            return new LocationData(source);
        }

        @Override
        public LocationData[] newArray(int size) {
            return new LocationData[size];
        }
    };
}
