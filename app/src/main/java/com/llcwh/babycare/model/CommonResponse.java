package com.llcwh.babycare.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/9/2 0002.
 */
public class CommonResponse {
    @SerializedName("status")
    boolean status;
    @SerializedName("msg")
    String msg;

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
}
