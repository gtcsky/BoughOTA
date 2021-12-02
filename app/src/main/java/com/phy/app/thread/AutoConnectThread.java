package com.phy.app.thread;

import android.util.ArrayMap;
import android.util.Log;

import com.phy.app.app.PHYApplication;
import com.phy.app.beans.AutoConnectBean;
import com.phy.app.beans.TargetInfo;

import java.util.HashMap;
import java.util.Map;

public class AutoConnectThread implements Runnable {
    private String TAG=getClass().getSimpleName();
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            HashMap<String, AutoConnectBean> autoConnectDevices = PHYApplication.getApplication().getAutoConnectDevices();
            if (!autoConnectDevices.isEmpty()) {
                ArrayMap<String, TargetInfo> connectedDevices = PHYApplication.getApplication().getConnectedDevices();
                for (Map.Entry<String, AutoConnectBean> autoConnectBeanEntry : autoConnectDevices.entrySet()) {
                    if ((autoConnectBeanEntry.getValue().isNeedAutoConnect() || autoConnectBeanEntry.getValue().isAlwaysAutoConnect()) && (!autoConnectBeanEntry.getValue().isWaitScanning())) {
                        if (connectedDevices == null || !connectedDevices.containsKey(autoConnectBeanEntry.getKey()) || !connectedDevices.get(autoConnectBeanEntry.getKey()).isfIsConnected()) {
                            PHYApplication.getBandUtil().connectDevice(autoConnectBeanEntry.getKey());      //尝试连接
                            break;
                        }
                    }
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
