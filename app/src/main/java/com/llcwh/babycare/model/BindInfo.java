package com.llcwh.babycare.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by 蔡小木 on 2016/9/5 0005.
 */
public class BindInfo {
    @SerializedName("status")
    private boolean status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("data")
    private ArrayList<BindInfoData> data;

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

    public ArrayList<BindInfoData> getData() {
        return data;
    }

    public void setData(ArrayList<BindInfoData> data) {
        this.data = data;
    }
}
