package com.phy.app.ble;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;

/**
 * Created by zhoululu on 2017/6/21.
 */

public class BleScanner {

    //private MyLeScanCallback leScanCallback;
    //private MyScanCallBack scanCallBack;

    private no.nordicsemi.android.support.v18.scanner.ScanCallback scanCallback = new no.nordicsemi.android.support.v18.scanner.ScanCallback() {
        @Override
        public void onScanResult(int callbackType, no.nordicsemi.android.support.v18.scanner.ScanResult result) {
            if(BandUtil.bandBleCallBack != null){
//                System.out.println("\n************************************************\n");
//                System.out.println(result.getScanRecord().getDeviceName());
//                System.out.println("\n************************************************\n");
                BandUtil.bandBleCallBack.onScanDevice(result.getDevice(),result.getRssi(),result.getScanRecord().getBytes());
            }else {
                throw  new RuntimeException("bleCallBack is null");
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            throw  new RuntimeException("Scan error");
        }
    };

    private Context context;

    public BleScanner(Context context) {
        this.context = context;
    }

    public void scanDevice(){

        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.startScan(scanCallback);

        /*BluetoothManager mBluetoothManager = (BluetoothManager) context.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = mBluetoothManager.getAdapter();

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){

            leScanCallback = new MyLeScanCallback();

            adapter.startLeScan(leScanCallback);
        }else {

            scanCallBack = new MyScanCallBack();

            adapter.getBluetoothLeScanner().startScan(scanCallBack);
        }*/
    }

    public void stopScanDevice(){
        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.stopScan(scanCallback);

        /*BluetoothManager mBluetoothManager = (BluetoothManager) context.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter adapter = mBluetoothManager.getAdapter();
        if(adapter != null && adapter.isEnabled()){

            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                adapter.stopLeScan(leScanCallback);
            }else {
                adapter.getBluetoothLeScanner().stopScan(scanCallBack);
            }
        }*/
    }

    class MyLeScanCallback implements BluetoothAdapter.LeScanCallback{

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(BandUtil.bandBleCallBack != null){
                BandUtil.bandBleCallBack.onScanDevice(device,rssi,scanRecord);

            }else {
                throw  new RuntimeException("bleCallBack is null");
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    class MyScanCallBack extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if(BandUtil.bandBleCallBack != null){
                BandUtil.bandBleCallBack.onScanDevice(result.getDevice(),result.getRssi(),result.getScanRecord().getBytes());
            }else {
                throw  new RuntimeException("bleCallBack is null");
            }
        }
    }

}
