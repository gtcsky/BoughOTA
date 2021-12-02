package com.phy.app.ble.bean;

/**
 * MessageType
 *
 * @author:zhoululu
 * @date:2018/4/19
 */

public enum  MessageType {

    PHONEIN(1),PHONEEND(2),MESSAGE(3),WX(5);

    private int type;
    // 构造方法
    private MessageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
