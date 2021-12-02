package com.phy.app.beans;

import android.bluetooth.BluetoothGatt;

public class TargetInfo {

    private String macAddress;
    private String modelNumber;
    private String deviceName;
    private boolean fIsConnected = false;
    private BluetoothGatt gatt;
    private boolean isWantConnect;
    private boolean isModelNumberGot;
    private int connectedRssi;

    public TargetInfo() {
    }

    public TargetInfo(String macAddress) {
        this.macAddress = macAddress;
    }

    public TargetInfo(String macAddress, boolean fIsConnected) {
        this.macAddress = macAddress;
        this.fIsConnected = fIsConnected;
    }

    public TargetInfo(String macAddress, boolean fIsConnected, boolean isWantConnect) {
        this.macAddress = macAddress;
        this.fIsConnected = fIsConnected;
        this.isWantConnect = isWantConnect;
    }

    public TargetInfo(String macAddress, boolean fIsConnected, BluetoothGatt gatt, boolean isWantConnect) {
        this.macAddress = macAddress;
        this.fIsConnected = fIsConnected;
        this.gatt = gatt;
        this.isWantConnect = isWantConnect;
    }

    public TargetInfo(String deviceName,String macAddress, boolean fIsConnected, BluetoothGatt gatt, boolean isWantConnect) {
        this(macAddress,fIsConnected,gatt,isWantConnect);
        this.deviceName=deviceName;
    }


    public TargetInfo(String macAddress, String modelNumber) {
        this.macAddress = macAddress;
        this.modelNumber = modelNumber;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public boolean isfIsConnected() {
        return fIsConnected;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public void setfIsConnected(boolean fIsConnected) {
        this.fIsConnected = fIsConnected;
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setGatt(BluetoothGatt gatt) {
        this.gatt = gatt;
    }

    public boolean isWantConnect() {
        return isWantConnect;
    }

    public void setWantConnect(boolean wantConnect) {
        isWantConnect = wantConnect;
    }

    public boolean isModelNumberGot() {
        return isModelNumberGot;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setModelNumberGot(boolean modelNumberGot) {
        isModelNumberGot = modelNumberGot;
    }

    public int getConnectedRssi() {
        return connectedRssi;
    }

    public void setConnectedRssi(int connectedRssi) {
        this.connectedRssi = connectedRssi;
    }

    @Override
    public String toString() {
        return "TargetInfo{" +
                "macAddress='" + macAddress + '\'' +
                ", modelNumber='" + modelNumber + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", fIsConnected=" + fIsConnected +
                ", gatt=" + gatt +
                ", isWantConnect=" + isWantConnect +
                ", isModelNumberGot=" + isModelNumberGot +
                ", connectedRssi=" + connectedRssi +
                '}';
    }
}
