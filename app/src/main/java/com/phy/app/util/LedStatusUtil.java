package com.phy.app.util;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import com.phy.app.beans.Const;
import com.phy.app.beans.LEDStatus;
//public interface BleGattCallBack {
//    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);
//    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
//    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
//    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
//    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);
//}

public interface LedStatusUtil {
    /**
     *  解析BLE接收到的数据为Bean
     * @param values
     * @return
     */
    public void parseHexBytes(byte[] values, LEDStatus ledStatus);
        /**
     * 把Bean转换成BLE发送的Byte数据
     * @param ledStatus
     * @return
     */
    public byte[] compressBeanToArray(LEDStatus ledStatus);
    /**
     *  所BLE接收到的Arrow Index信息,解析为APP中的ArrowIndex信息
     * @param data
     * @return
     */
    public  int parseArrowIndex(int data);
    /**
     * 把APP中Index信息转换成BLE终端接收的ArrowIndex
     * @param data
     * @return
     */
    public  int convertBleArrowIndex(int data);
}
