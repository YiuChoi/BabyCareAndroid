package com.llcwh.babycare.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/9/5 0005.
 */
public class Baby {
    @SerializedName("baby_uuid")
    private String baby_uuid;
    @SerializedName("user_relation")
    private String user_relation;

    public Baby(String baby_uuid, String user_relation) {
        this.baby_uuid = baby_uuid;
        this.user_relation = user_relation;
    }

    public String getUser_relation() {
        return user_relation;
    }

    public void setUser_relation(String user_relation) {
        this.user_relation = user_relation;
    }

    public String getBaby_uuid() {
        return baby_uuid;
    }

    public void setBaby_uuid(String baby_uuid) {
        this.baby_uuid = baby_uuid;
    }
}
