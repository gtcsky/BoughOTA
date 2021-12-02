package com.phy.app.ble.bean;

/**
 * LightColor
 *
 * @author:zhoululu
 * @date:2018/4/17
 */

public enum LightColor {

    RED(0),GREE(1),BLUE(2);

    private int color;
    // 构造方法
    private LightColor(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }
}
