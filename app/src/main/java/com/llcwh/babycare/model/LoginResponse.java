package com.llcwh.babycare.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/9/2 0002.
 */
public class LoginResponse {
    @SerializedName("access_token")
    String access_token;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
