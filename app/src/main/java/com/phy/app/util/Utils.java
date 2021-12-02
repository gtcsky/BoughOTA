package com.phy.app.util;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

/**
 * Utils
 *
 * @author:zhoululu
 * @date:2018/4/13
 */

public class Utils {

    public static boolean blutheIsOpen(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter.isEnabled()){
            return true;
        }
        return false;
    }

    public static void openBlutheActivity(Activity activity){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, 0x1);
    }

    public static String getOTAMac(String deviceAddress,int i){
        final String firstBytes = deviceAddress.substring(0, 15);
        final String lastByte = deviceAddress.substring(15); // assuming that the device address is correct
        final String lastByteIncremented = String.format("%02X", (Integer.valueOf(lastByte, 16) + i) & 0xFF);

        return firstBytes + lastByteIncremented;
    }

}
