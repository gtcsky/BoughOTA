package com.phy.app.beans;

public class AutoConnectBean {
    private String mac;
    private boolean isNeedAutoConnect;                    //单次自动连接
    private boolean isAlwaysAutoConnect;                  //总是自动连接
    private boolean isWaitScanning;                       //扫描完成后,才自动连接

    public AutoConnectBean(String mac, boolean isNeedAutoConnect, boolean isAlwaysAutoConnect, boolean isWaitScanning) {
        this.mac = mac;
        this.isNeedAutoConnect = isNeedAutoConnect;
        this.isAlwaysAutoConnect = isAlwaysAutoConnect;
        this.isWaitScanning = isWaitScanning;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public boolean isNeedAutoConnect() {
        return isNeedAutoConnect;
    }

    public void setNeedAutoConnect(boolean needAutoConnect) {
        isNeedAutoConnect = needAutoConnect;
    }

    public boolean isAlwaysAutoConnect() {
        return isAlwaysAutoConnect;
    }

    public void setAlwaysAutoConnect(boolean alwaysAutoConnect) {
        isAlwaysAutoConnect = alwaysAutoConnect;
    }

    public boolean isWaitScanning() {
        return isWaitScanning;
    }

    public void setWaitScanning(boolean waitScanning) {
        isWaitScanning = waitScanning;
    }

    @Override
    public String toString() {
        return "AutoConnectBean{" +
                "mac='" + mac + '\'' +
                ", isNeedAutoConnect=" + isNeedAutoConnect +
                ", isAlwaysAutoConnect=" + isAlwaysAutoConnect +
                ", isWaitScanning=" + isWaitScanning +
                '}';
    }
}
