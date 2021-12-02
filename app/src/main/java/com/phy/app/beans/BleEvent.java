package com.phy.app.beans;

/**
 * BleEvent
 *
 * @author:zhoululu
 * @date:2018/4/16
 */

public class BleEvent {

    private String operate;
    private Object object;


    public BleEvent(String operate, Object object) {
        this.operate = operate;
        this.object = object;
    }

    public String getOperate() {
        return operate;
    }

    public Object getObject() {
        return object;
    }
}
