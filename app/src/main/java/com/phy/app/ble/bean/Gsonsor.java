package com.phy.app.ble.bean;

/**
 * Gsonsor
 *
 * @author:zhoululu
 * @date:2018/4/17
 */

public class Gsonsor {

    private int x;
    private int y;
    private int z;

    public Gsonsor(byte[] data) {
        this.x = (data[2] & 0xff) | (data[3] & 0xff)<<8 | (data[4] & 0xff)<<16 | (data[5] & 0xff)<<24;
        this.z = (data[6] & 0xff) | (data[7] & 0xff)<<8 | (data[8] & 0xff)<<16 | (data[9] & 0xff)<<24;
        this.y = (data[10] & 0xff) | (data[11] & 0xff)<<8 | (data[12] & 0xff)<<16 | (data[13] & 0xff)<<24;

    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }
}
