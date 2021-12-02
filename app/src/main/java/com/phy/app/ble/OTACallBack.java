package com.phy.app.ble;

import android.bluetooth.BluetoothDevice;

/**
 * OTACallBack
 *
 * @author:zhoululu
 * @date:2018/5/22
 */

public interface OTACallBack {

    public void onProcess(float process);
    public void onError(String errorCode);
    public void onComplete();


}
