package com.phy.app.app;

import android.bluetooth.BluetoothDevice;
import android.util.ArrayMap;
import android.util.Log;

import com.phy.app.beans.BleEvent;
import com.phy.app.beans.Connect;
import com.phy.app.beans.Device;
import com.phy.app.beans.TargetInfo;
import com.phy.app.beans.UpdateFirmwareEvent;
import com.phy.app.ble.BandBleCallBack;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * BandBleCallBackImpl
 *
 * @author:zhoululu
 * @date:2018/4/14
 */

public class BandBleCallBackImpl implements BandBleCallBack{
    public String TAG = getClass().getSimpleName();
    private static BandBleCallBackImpl bandBleCallBack;

    private BandBleCallBackImpl(){}

    public static BandBleCallBackImpl getBandBleCallBack(){
        if(bandBleCallBack == null){
            bandBleCallBack = new BandBleCallBackImpl();
        }

        return bandBleCallBack;
    }

    @Override
    public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {

        Device device1 = new Device(device,rssi,1);
        EventBus.getDefault().post(device1);

    }

    @Override
    public void onConnectDevice(boolean connect) {
        if(connect){
            Log.d(TAG, "onConnectDevice: Connected");
        }else {
            Log.d(TAG, "onConnectDevice: Disconnected");
        }
        EventBus.getDefault().post(new Connect(connect));                               //发送事件到EventActivity
        PHYApplication.getApplication().setConnect(connect);
    }

    @Override
    public void onConnectDevice(boolean connect, BluetoothDevice device) {
        PHYApplication.getApplication().getConnectedDevices().get(device.getAddress()).setfIsConnected(true);
        this.onConnectDevice(connect);
    }

    @Override
    public void onResponse(String operate, Object object) {
        EventBus.getDefault().post(new BleEvent(operate,object));
    }

}
