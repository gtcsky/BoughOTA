package com.phy.app.beans;

/**
 * UpdateFirmwareEvent
 *
 * @author:zhoululu
 * @date:2018/5/19
 */

public class UpdateFirmwareEvent {

    private int code;
    private String operate;
    private float process;

    public UpdateFirmwareEvent(int code, String operate, float process) {
        this.code = code;
        this.operate = operate;
        this.process = process;
    }

    public int getCode() {
        return code;
    }

    public String getOperate() {
        return operate;
    }

    public float getProcess() {
        return process;
    }
}
