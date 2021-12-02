package com.phy.app.ble;

import android.bluetooth.BluetoothDevice;

/**
 * Created by zhoululu on 2017/6/21.
 */

public interface BandBleCallBack {

    public void onScanDevice(BluetoothDevice device, int rssi, byte[] scanRecord);
    public void onConnectDevice(boolean connect);
    public void onConnectDevice(boolean connect,BluetoothDevice device);
    public void onResponse(String operate, Object object);

}
