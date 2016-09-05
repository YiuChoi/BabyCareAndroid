package com.llcwh.babycare.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/9/5 0005.
 */
public class BindInfoData {
    @SerializedName("is_admin")
    private boolean is_admin;
    @SerializedName("baby_uuid")
    private String baby_uuid;
    @SerializedName("relationship")
    private String relationship;

    public boolean is_admin() {
        return is_admin;
    }

    public void setIs_admin(boolean is_admin) {
        this.is_admin = is_admin;
    }

    public String getBaby_uuid() {
        return baby_uuid;
    }

    public void setBaby_uuid(String baby_uuid) {
        this.baby_uuid = baby_uuid;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }
}
