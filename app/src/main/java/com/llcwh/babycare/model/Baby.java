package com.llcwh.babycare.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/9/5 0005.
 */
public class Baby {
    @SerializedName("baby_uuid")
    private String baby_uuid;

    public Baby(String baby_uuid) {
        this.baby_uuid = baby_uuid;
    }

    public String getBaby_uuid() {
        return baby_uuid;
    }

    public void setBaby_uuid(String baby_uuid) {
        this.baby_uuid = baby_uuid;
    }
}
