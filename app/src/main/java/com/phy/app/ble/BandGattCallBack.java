package com.phy.app.ble;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

import com.phy.app.ble.core.BleCore;

import java.util.List;

/**
 * Created by zhoululu on 2017/6/21.
 */

public class BandGattCallBack extends BluetoothGattCallback {

    private static BandGattCallBack gattCallBack;

    private BleGattCallBack bleGattCallBack;

    private BandGattCallBack() {}

    public static BandGattCallBack getGattCallBack(){
        if(gattCallBack == null){
            synchronized (BandUtil.class){
             if(gattCallBack == null){
                 gattCallBack = new BandGattCallBack();
             }
            }
        }

        return gattCallBack;
    }

    public void setBleGattCallBack(BleGattCallBack bleGattCallBack) {
        this.bleGattCallBack = bleGattCallBack;
    }


    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        bleGattCallBack.onConnectionStateChange(gatt, status, newState);
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {

        boolean isContainService = false;

        List<BluetoothGattService> serviceList = gatt.getServices();
        for (BluetoothGattService service : serviceList) {
            Log.d("service uuid", service.getUuid().toString());

            if (service.getUuid().toString().toLowerCase().equals(OperateConstant.SERVICE_UUID)) {
                isContainService = true;
            }

            List<BluetoothGattCharacteristic> characteristicList = service.getCharacteristics();
            for (BluetoothGattCharacteristic characteristic : characteristicList) {
                Log.d("charac uuid", characteristic.getUuid().toString());

                List<BluetoothGattDescriptor> descriptorList = characteristic.getDescriptors();
                for (BluetoothGattDescriptor descriptor : descriptorList) {
                    Log.d("descriptor uuid", descriptor.getUuid().toString());
                }
            }
        }

        if (isContainService) {
//            BleCore.enableNotifications();
            BleCore.enableCmdIndicateNotifications();
        }/*else{
            BandUtil.bandBleCallBack.onConnectDevice(true);
        }*/

//        //因为其他设备可能包含ff01service，为防止开启通知失败，在此处直接返回连接成功
////        BandUtil.bandBleCallBack.onConnectDevice(true);
        BandUtil.bandBleCallBack.onConnectDevice(true, gatt.getDevice());
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        bleGattCallBack.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        bleGattCallBack.onCharacteristicWrite(gatt,characteristic,status);
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        bleGattCallBack.onCharacteristicChanged(gatt,characteristic);

    }

    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorRead(gatt, descriptor, status);
        Log.d("onDescriptorRead", "onDescriptorRead");
        bleGattCallBack.onDescriptorRead(gatt,descriptor,status);
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
       //BandUtil.bandBleCallBack.onConnectDevice(true);
        bleGattCallBack.onDescriptorWrite(gatt,descriptor,status);

        Log.d("onDescriptorWrite", "onDescriptorWrite" + status);
    }

    @Override
    public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
        super.onReliableWriteCompleted(gatt, status);
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
//        super.onReadRemoteRssi(gatt, rssi, status);
        bleGattCallBack.onReadRemoteRssi(gatt, rssi, status);
    }

    @Override
    public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
        super.onMtuChanged(gatt, mtu, status);
    }
}
