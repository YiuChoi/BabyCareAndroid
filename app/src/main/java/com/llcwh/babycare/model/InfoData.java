package com.llcwh.babycare.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by caiya on 2016/9/16 0016.
 */

public class InfoData implements Parcelable {
    @SerializedName("status")
    private boolean status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    ArrayList<BabyData> babyDatas;

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

    public ArrayList<BabyData> getBabyDatas() {
        return babyDatas;
    }

    public void setBabyDatas(ArrayList<BabyData> babyDatas) {
        this.babyDatas = babyDatas;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.status ? (byte) 1 : (byte) 0);
        dest.writeString(this.msg);
        dest.writeTypedList(this.babyDatas);
    }

    public InfoData() {
    }

    protected InfoData(Parcel in) {
        this.status = in.readByte() != 0;
        this.msg = in.readString();
        this.babyDatas = in.createTypedArrayList(BabyData.CREATOR);
    }

    public static final Parcelable.Creator<InfoData> CREATOR = new Parcelable.Creator<InfoData>() {
        @Override
        public InfoData createFromParcel(Parcel source) {
            return new InfoData(source);
        }

        @Override
        public InfoData[] newArray(int size) {
            return new InfoData[size];
        }
    };
}
