package com.phy.app.app;

import android.app.Application;
import android.util.ArrayMap;

import com.phy.app.beans.AutoConnectBean;
import com.phy.app.beans.LEDStatus;
import com.phy.app.beans.TargetInfo;
import com.phy.app.ble.BandUtil;
import com.phy.app.util.LedStatusUtil;

import java.util.HashMap;

/**
 * PUYApplication
 *
 * @author:zhoululu
 * @date:2018/4/14
 */

public class PHYApplication extends Application{

    private static BandUtil bandUtil;
    private static boolean isLedCtrlMode=false;
    private String mac;
    private String name;
    private boolean connect;
    private static PHYApplication application;
    private static LEDStatus ledStatus;
    private static LedStatusUtil ledStatusUtil=null;
    public static boolean isAutoHidden=true;
    private ArrayMap<String,TargetInfo> connectedDevices=new ArrayMap<>();
    private HashMap<String, AutoConnectBean> autoConnectDevices=new HashMap<>();
    static{
        if(ledStatus==null)
            ledStatus=new LEDStatus();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        application = this;

        bandUtil = BandUtil.getBandUtil(this);
        bandUtil.setBandBleCallBack(BandBleCallBackImpl.getBandBleCallBack());

    }

    public static PHYApplication getApplication(){
        return application;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public static BandUtil getBandUtil() {
        return bandUtil;
    }

    public boolean isConnect() {
        return connect;
    }

    public void setConnect(boolean connect) {
        this.connect = connect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static LEDStatus getLedStatus() {
        return ledStatus;
    }

    public static void setLedStatus(LEDStatus ledStatus) {
        PHYApplication.ledStatus = ledStatus;
    }

    public static boolean isIsLedCtrlMode() {
        return isLedCtrlMode;
    }

    public static void setIsLedCtrlMode(boolean isLedCtrlMode) {
        PHYApplication.isLedCtrlMode = isLedCtrlMode;
    }

    public static LedStatusUtil getLedStatusUtil() {
        return ledStatusUtil;
    }

    public static void setLedStatusUtil(LedStatusUtil ledStatusUtil) {
        PHYApplication.ledStatusUtil = ledStatusUtil;
    }

    public ArrayMap<String, TargetInfo> getConnectedDevices() {
        return connectedDevices;
    }

    public void setConnectedDevices(ArrayMap<String, TargetInfo> connectedDevices) {
        this.connectedDevices = connectedDevices;
    }

    public HashMap<String, AutoConnectBean> getAutoConnectDevices() {
        return autoConnectDevices;
    }

}
