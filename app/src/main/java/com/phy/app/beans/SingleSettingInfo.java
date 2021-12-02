package com.phy.app.beans;

import java.util.Arrays;

public class SingleSettingInfo {
    private SettingName settingName;
    private byte value[];

    public SingleSettingInfo(SettingName settingName, byte[] value) {
        this.settingName = settingName;
        this.value = value;
    }

    public SettingName getSettingName() {
        return settingName;
    }

    public void setSettingName(SettingName settingName) {
        this.settingName = settingName;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "SingleSettingInfo{" +
                "settingName=" + settingName +
                ", value=" + Arrays.toString(value) +
                '}';
    }
}
