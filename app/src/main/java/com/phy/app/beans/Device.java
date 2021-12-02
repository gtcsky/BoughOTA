package com.phy.app.beans;

import android.bluetooth.BluetoothDevice;

/**
 * Device
 *
 * @author:zhoululu
 * @date:2018/4/13
 */

public class Device {

    private BluetoothDevice device;
    private int rssi;
    private int broadcastType;

    public Device(BluetoothDevice device,int rssi,int broadcastType){
        this.device = device;
        this.rssi = rssi;
        this.broadcastType = broadcastType;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getBroadcastType() {
        return broadcastType;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Device) {
            final Device that = (Device) o;
            return device.getAddress().equals(that.device.getAddress());
        }
        return super.equals(o);
    }

}
