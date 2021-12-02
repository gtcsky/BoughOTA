package com.phy.app.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

/**
 * Created by zhoululu on 2017/7/27.
 */

public interface BleGattCallBack {
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState);
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status);
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic);
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status);
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status);
}
