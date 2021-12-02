package com.phy.app.beans;

import java.util.Arrays;

public class NotifyInfo {
    private String uuid;
    private byte[] data;

    public NotifyInfo(String uuid, byte[] data) {
        this.uuid = uuid;
        this.data = data;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "NotifyInfo{" +
                "uuid='" + uuid + '\'' +
                ", data=" + Arrays.toString(data) +
                '}';
    }
}
