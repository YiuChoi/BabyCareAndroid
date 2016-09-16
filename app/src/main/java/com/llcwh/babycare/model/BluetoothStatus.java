package com.llcwh.babycare.model;

/**
 * Created by caiya on 2016/9/16 0016.
 */

public class BluetoothStatus {
    boolean status;

    public BluetoothStatus(boolean status) {
        this.status = status;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
