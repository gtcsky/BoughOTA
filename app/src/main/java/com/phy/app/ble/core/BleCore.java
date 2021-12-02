package com.phy.app.ble.core;

import static android.text.TextUtils.isEmpty;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Handler;
import android.util.ArrayMap;
import android.util.Log;

import com.phy.app.app.PHYApplication;
import com.phy.app.beans.AutoConnectBean;
import com.phy.app.beans.Const;
import com.phy.app.beans.LEDStatus;
import com.phy.app.beans.NotifyInfo;
import com.phy.app.beans.SettingName;
import com.phy.app.beans.SingleSettingInfo;
import com.phy.app.beans.TargetInfo;
import com.phy.app.ble.BandUtil;
import com.phy.app.ble.BleAnalyze;
import com.phy.app.ble.BleGattCallBack;
import com.phy.app.ble.BandGattCallBack;
import com.phy.app.ble.OperateConstant;
import com.phy.app.ble.bean.Message;
import com.phy.app.ble.bean.MessageType;
import com.phy.app.util.BG5xxLedStatusUtil;
import com.phy.app.util.BG93xLedStatusUtil;
import com.phy.app.util.HexString;
import com.phy.app.util.Util;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by zhoululu on 2017/6/21.
 */

public class BleCore implements BleGattCallBack {
    public String TAG = getClass().getSimpleName();
    private Context context;
    private List<byte[]> msgList;
    public static BluetoothGatt bluetoothGatt;
    private MessageType type;
    private byte[] commond = null;
    public static ArrayMap<String, BluetoothGatt> gattArrayMap = new ArrayMap<>();
    //private static OTAImpl otaImpl;

    private int retryTimes;
    private String macAddress;
    private boolean isWantConnect;
    private LEDStatus ledStatus=PHYApplication.getLedStatus();

    private boolean isModelNumberGot;
    private byte[] syncDataFromBle=new byte[Const.LED_CTRL_COMMAND_PACKAGE_LEN];
    private final int WAIT_NOTIRY=10;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            if (msg.what == WAIT_NOTIRY){
                getModelNumber();
            }
            return false;
        }
    });

    public BleCore(Context context) {
        this.context = context;
    }

    /*public void setOtaCallBack(OTAImpl otaCallBack1) {
        otaImpl = otaCallBack1;

        otaImpl.setBluetoothGatt(bluetoothGatt);
        sendOTACommand("01"+HexString.int2ByteString(otaImpl.getFirmWareFile().getList().size())+"00",true);
    }*/

    public void connect(String mac){
        macAddress = mac;
        isWantConnect = true;

        BluetoothManager mBluetoothManager = (BluetoothManager) context.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothDevice device = mBluetoothManager.getAdapter().getRemoteDevice(mac);
        bluetoothGatt = device.connectGatt(context.getApplicationContext(),false, BandGattCallBack.getGattCallBack());
        BandGattCallBack.getGattCallBack().setBleGattCallBack(this);
    }


    public void disConnect(){
        if (bluetoothGatt != null){
            isWantConnect = false;
            bluetoothGatt.disconnect();
        }
        isModelNumberGot=false;
    }

    public boolean disConnect(String mac){
        BluetoothGatt gatt=gattArrayMap.get(mac);
        if (gatt != null){
            isWantConnect = false;
            gatt.disconnect();
        }else{
//            for (Map.Entry<String,BluetoothGatt> entry:gattArrayMap.entrySet()){
//                Log.d(TAG, "MAC:"+entry.getKey()+"\t Gatt:"+entry.getValue());
//            }
            Log.d(TAG, "disConnect: gatt is null");
            return  false;
        }
//        HashMap<String, TargetInfo> connectedDevices=(HashMap<String, TargetInfo>)PHYApplication.getApplication().getConnectedDevices();
        isModelNumberGot=false;
        return true;
    }

    public static boolean enableNotifications() {

        return switchNotify(OperateConstant.SERVICE_UUID, OperateConstant.CHARACTERISTIC_WRITE_UUID, true);

    }

    public static boolean enableIndicateNotifications() {

        return switchNotify(OperateConstant.SERVICE_OTA_UUID, OperateConstant.CHARACTERISTIC_OTA_INDICATE_UUID, true);

    }

    public static boolean enableCmdIndicateNotifications() {

        return switchNotify(OperateConstant.SERVICE_UUID, OperateConstant.NOTIFICATION_INDICATE_UUID, true);

    }

    public static boolean enableUserManualInfoNotifications() {

        return switchNotify(OperateConstant.SERVICE_UUID, OperateConstant.NOTIFICATION_USER_MANUAL_INFO_UUID, true);

    }

    /**
     *                  开关指定的Notify
     * @param serviceUuid
     * @param charUuid
     * @param tag
     * @return
     */
    public static boolean switchNotify(String serviceUuid,String charUuid,boolean tag){
        //在OTA模式，不开启
        if(Util.checkIsOTA(bluetoothGatt)){
            return false;
        }

        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(serviceUuid));
        if(bluetoothGattService == null){
            return false;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(charUuid));
        if(bluetoothGattCharacteristic == null){
            return false;
        }

        if (tag)
            bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, true);
        else
            bluetoothGatt.setCharacteristicNotification(bluetoothGattCharacteristic, false);

        BluetoothGattDescriptor bluetoothGattDescriptor = bluetoothGattCharacteristic.getDescriptor(UUID.fromString(OperateConstant.DESCRIPTOR_UUID));
        if(bluetoothGattDescriptor == null){
            return false;
        }
        if(tag)
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        else
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        return bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
    }

    /**
     *                  开关指定的notify
     * @param characteristic
     * @param tag
     * @return
     */
    public static boolean switchNotify(BluetoothGattCharacteristic characteristic, boolean tag) {
        if (characteristic == null) {
            return false;
        }
        if (tag)
            bluetoothGatt.setCharacteristicNotification(characteristic, true);
        else
            bluetoothGatt.setCharacteristicNotification(characteristic, false);
        BluetoothGattDescriptor bluetoothGattDescriptor = characteristic.getDescriptor(UUID.fromString(OperateConstant.DESCRIPTOR_UUID));
        if (bluetoothGattDescriptor == null) {
            return false;
        }
        if (tag)
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        else
            bluetoothGattDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
        return bluetoothGatt.writeDescriptor(bluetoothGattDescriptor);
    }


    public void getBattery(){

        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(OperateConstant.SERVICE_BATTERY_UUID));
        if(bluetoothGattService == null){

            return;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(OperateConstant.CHARACTERISTIC_BATTERY_READ_UUID));
        if(bluetoothGattCharacteristic == null){
            return;
        }

        bluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);

    }


    public static void getModelNumber() {

        userReadCharacteristic(OperateConstant.SERVICE_DEVICE_INFO_UUID,OperateConstant.MODULE_NUMBER_UUID);

    }

    public static void userReadCharacteristic(String serviceId,String characteristicId){

        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(serviceId));
        if (bluetoothGattService == null) {
            return;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(characteristicId));
        if (bluetoothGattCharacteristic == null) {
            return;
        }

        bluetoothGatt.readCharacteristic(bluetoothGattCharacteristic  );

    }

    public static void getFirmwareVersion() {

        userReadCharacteristic(OperateConstant.SERVICE_DEVICE_INFO_UUID, OperateConstant.FIRMWARE_VERSION_UUID);

    }


    public void sendMsg(String title,String msg, MessageType type){

        Message message = new Message(title,msg);
        msgList = message.getMsgList();

        this.type = type;

        byte[] command ;

        if(type == MessageType.PHONEIN || type == MessageType.PHONEEND){
            command = genMsgBytes(0);
        }else{
            command = genMsgBytes(1);
        }

        sendMsg(command);
    }

    private void sendMsg(byte[] command){
        sendCommand((byte) 0x38,command);
    }

    private byte[] genMsgBytes(int status){
        int size = msgList.get(0).length+1;
        byte[] command = new byte[size];

        command[0] = (byte)(type.getType() | status << 6);

        System.arraycopy(msgList.get(0),0,command,1,msgList.get(0).length);

        return command;
    }

    public void sendCommand(byte method,byte[] data){
        byte[] command = genCommand(method, data);
        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(OperateConstant.SERVICE_UUID));
        if (bluetoothGattService == null) {
            Log.e("service", "service is null");
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(OperateConstant.CHARACTERISTIC_WRITE_UUID));

        if (bluetoothGattCharacteristic == null) {
            return;
        }

        bluetoothGattCharacteristic.setValue(command);
        bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);

        Log.d("send command", HexString.parseStringHex(command));
//        Log.d(TAG, "send Command: ");
    }

    public void sendCommand2Target(byte[] data, BluetoothGatt gatt) {
//        Log.d(TAG, "send data: "+Util.hex2AsciiStr(data));
        BluetoothGattService bluetoothGattService = gatt.getService(UUID.fromString(OperateConstant.SERVICE_UUID));
        if (bluetoothGattService == null) {
            Log.e("service", "service is null");
            return;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(OperateConstant.CHARACTERISTIC_WRITE_UUID));

        if (bluetoothGattCharacteristic == null) {
            return;
        }

        bluetoothGattCharacteristic.setValue(data);
        int retry=20;
        do {
            if (gatt.writeCharacteristic(bluetoothGattCharacteristic)) {
//                Log.d(TAG, "sendCommand2Target: " + gatt + "\t success");
                break;
            } else {
//                Log.d(TAG, "sendCommand2Target: " + gatt + "\t fail");
                try {
                    synchronized ( Thread.currentThread()) { //把wait代码放在synchronized块中，锁线程自己
                        Thread.currentThread().wait(100);
                    }
                }catch (Exception e){
                    Log.d(TAG, "Exception: "+e.getMessage());
                }
                retry-=1;
            }
        }while(retry!=0);
    }

    public boolean updateSystemSetting2Target(byte[] data, BluetoothGatt gatt) {
        BluetoothGattService bluetoothGattService = gatt.getService(UUID.fromString(OperateConstant.SERVICE_UUID));
        if (bluetoothGattService == null) {
            Log.e("service", "service is null");
            return false;
        }
        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(OperateConstant.WRITE_USER_MANUAL_INFO_UUID));
        if (bluetoothGattCharacteristic == null) {
            return false;
        }

        bluetoothGattCharacteristic.setValue(data);
        int retry=20;
        do {
            if (gatt.writeCharacteristic(bluetoothGattCharacteristic)) {
                return true;
//                break;
            } else {
                try {
                    synchronized ( Thread.currentThread()) { //把wait代码放在synchronized块中，锁线程自己
                        Thread.currentThread().wait(100);
                    }
                }catch (Exception e){
                    Log.d(TAG, "Exception: "+e.getMessage());
                }
                retry-=1;
            }
        }while(retry!=0);
        return false;
    }

    public void userSendCommand(byte[] data) {
        for (Map.Entry<String, BluetoothGatt> entry : gattArrayMap.entrySet()) {
            sendCommand2Target(data, entry.getValue());
        }
    }

    public boolean userUpdateTargetSystemSetting(byte[] data) {
        for (Map.Entry<String, BluetoothGatt> entry : gattArrayMap.entrySet()) {
            if (updateSystemSetting2Target(data, entry.getValue())) {
                if (data[0] == Const.LOAD_TARGET_DEFAULT_SETTING || data[0] == Const.UPDATE_LOCAL_NAME_COMMAND) {       //local 需要重新扫描后才会更新.
                    PHYApplication.getApplication().getAutoConnectDevices().put(entry.getKey(), new AutoConnectBean(entry.getKey(), true, false, true));
                } else {
                    PHYApplication.getApplication().getAutoConnectDevices().put(entry.getKey(), new AutoConnectBean(entry.getKey(), true, false, false));
                }
            } else {
                return false;
            }
        }
        return true;
    }

    private byte[] genCommand(byte method,byte[] data){
        byte csn = Util.getCSN();
        byte verifyByte;
        byte[] command;

        if(data == null){
            command = new byte[3];
            verifyByte = Util.genVerifyByte(method,csn);
            command[0] = method;
            command[1] = csn;
            command[2] = verifyByte;
        }else {
            command = new byte[3+data.length];
            verifyByte = Util.genVerifyByte(method,csn,data);
            command[0] = method;
            command[1] = csn;
            System.arraycopy(data,0,command,2,data.length);
            command[command.length-1] = verifyByte;
        }

        return command;
    }

//    public void startOTA(){
//        sendOTACommand("0102",false);
//
//        BandUtil.bandBleCallBack.onResponse(OperateConstant.START_OTA,null);
//
//    }

    public void getBootLoadVersion(){
        sendOTACommand("0200",true);
    }

    public void startReBoot(){
        sendOTACommand("04",false);
    }

    public boolean isOTA(){
        if(bluetoothGatt != null){
            return Util.checkIsOTA(bluetoothGatt);
        }
        return false;
    }

    private void sendOTACommand(String commd,boolean respons){

        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(OperateConstant.SERVICE_OTA_UUID));
        if(bluetoothGattService == null){
            Log.e(" OTA service", "service is null");
            return;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(OperateConstant.CHARACTERISTIC_OTA_WRITE_UUID));
        if(bluetoothGattCharacteristic == null){
            return;
        }

        if(!respons){
            bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
        }else{
            bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
        }
        bluetoothGattCharacteristic.setValue(HexString.parseHexString(commd));
        bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);

        Log.d("send ota commond", commd);

       // LogUtil.getLogUtilInstance().save("send ota commond: "+commd);
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        String mac=gatt.getDevice().getAddress();
        if(newState == BluetoothProfile.STATE_CONNECTED){
            EventBus.getDefault().post(new SingleSettingInfo(SettingName.LOCAL_NAME,gatt.getDevice().getName().getBytes()));
            gatt.discoverServices();
            retryTimes = 0;
            isWantConnect = false;
            gattArrayMap.put(mac,gatt);
            PHYApplication.getApplication().getConnectedDevices().put(mac,new TargetInfo(gatt.getDevice().getName(),mac,true,gatt,false));
            PHYApplication.getApplication().setName(gatt.getDevice().getName());
            for(Map.Entry<String, TargetInfo> entry:PHYApplication.getApplication().getConnectedDevices().entrySet()){
                Log.d(TAG, "---"+entry.getValue());
            }
            HashMap<String, AutoConnectBean> autoConnectDevices = PHYApplication.getApplication().getAutoConnectDevices();
            if(autoConnectDevices!=null&&!autoConnectDevices.isEmpty()){
                if(autoConnectDevices.containsKey(mac)&&autoConnectDevices.get(mac)!=null){
                    if(Objects.requireNonNull(autoConnectDevices.get(mac)).isNeedAutoConnect()){
                        Objects.requireNonNull(autoConnectDevices.get(mac)).setNeedAutoConnect(false);
                        if(!Objects.requireNonNull(autoConnectDevices.get(mac)).isNeedAutoConnect()&&!Objects.requireNonNull(autoConnectDevices.get(mac)).isAlwaysAutoConnect()){
                            autoConnectDevices.remove(mac);             //连接成功后从待自动连接map中删除不需要自动连接的设备.
                        }
                    }
                }
            }

            handler.sendEmptyMessageDelayed(WAIT_NOTIRY,3000);

            Log.d(TAG, "onConnectionStateChange: connected");
        }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
//            Log.d(TAG, "onConnectionStateChange:   disconnect by BluetoothGatt.c");
            for(Map.Entry<String, TargetInfo> entry:PHYApplication.getApplication().getConnectedDevices().entrySet()){
                Log.d(TAG, "---"+entry.getValue());
            }
            PHYApplication.getApplication().getConnectedDevices().remove(mac);
            if(gattArrayMap.size()!=0&&gattArrayMap.containsKey(mac)){

                gattArrayMap.remove(mac);

            }
            if(gatt != null){

                gatt.close();

            }
            if(isWantConnect){
                retryTimes++;
                if(retryTimes > 2){
                    BandUtil.bandBleCallBack.onConnectDevice(false);

                    retryTimes = 0;
                    isWantConnect = false;
                }else{
                    connect(macAddress);
                }
            }else {
                BandUtil.bandBleCallBack.onConnectDevice(false);
            }

            Log.d(TAG, "onConnectionStateChange: disconnected");
        } else if (newState == BluetoothProfile.STATE_DISCONNECTING) {

            Log.d(TAG, "onConnectionStateChange: disconnecting");

        }

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        String uuid = characteristic.getUuid().toString().trim();
        if(uuid.equalsIgnoreCase(OperateConstant.CHARACTERISTIC_BATTERY_READ_UUID) && status == 0){
            BleAnalyze.batteryDataAnalysis(characteristic.getValue());
            Log.d("battery", HexString.parseStringHex(characteristic.getValue()));
        }else if(uuid.equalsIgnoreCase(OperateConstant.FIRMWARE_VERSION_UUID)){
            if(ledStatus==null)
                ledStatus=new LEDStatus();
            String firmwareVersion=Util.Hex2Str(characteristic.getValue());
            if(!isEmpty(ledStatus.getModelNumber())&&!ledStatus.getModelNumber().contains(firmwareVersion)){
                ledStatus.setModelNumber(ledStatus.getModelNumber()+" "+firmwareVersion);
                TargetInfo targetInfo=PHYApplication.getApplication().getConnectedDevices().get(gatt.getDevice().getAddress());
                assert targetInfo != null;
                targetInfo.setModelNumber(targetInfo.getModelNumber()+" "+Util.Hex2Str(characteristic.getValue()));
            }
            Log.d(getClass().getSimpleName(), "Version:"+firmwareVersion);
            BleAnalyze.firmwareVersionAnalysis(5);
        }else if(uuid.equalsIgnoreCase(OperateConstant.NOTIFICATION_USER_MANUAL_INFO_UUID)){
            Log.d("UserManual read", HexString.parseStringHex(characteristic.getValue()));
        }else if(uuid.equalsIgnoreCase(OperateConstant.MODULE_NUMBER_UUID)) {
            Objects.requireNonNull(PHYApplication.getApplication().getConnectedDevices().get(gatt.getDevice().getAddress())).setModelNumberGot(true);
            if (ledStatus == null)
                ledStatus = new LEDStatus();
            String targetModelNo = HexString.parseBytes2String(characteristic.getValue());
            ledStatus.setModelNumber(targetModelNo);
            if (!isEmpty(targetModelNo)) {
                Objects.requireNonNull(PHYApplication.getApplication().getConnectedDevices().get(gatt.getDevice().getAddress())).setModelNumber(targetModelNo);
                if (targetModelNo.contains("BG93") || targetModelNo.contains("BG92")) {
                    PHYApplication.setLedStatusUtil(new BG93xLedStatusUtil());
                    System.out.println("\n********TAR:BG9xx*************\n");
                } else {
                    PHYApplication.setLedStatusUtil(new BG5xxLedStatusUtil());
                    System.out.println("\n********TAR:BG5xx*************\n");
                }
                PHYApplication.getLedStatusUtil().parseHexBytes(syncDataFromBle, ledStatus);
                EventBus.getDefault().post(new SingleSettingInfo(SettingName.MODEL_NUMBER, characteristic.getValue()));
            }else{
                getFirmwareVersion();
                return;
            }
            getFirmwareVersion();
        }else if(uuid.equalsIgnoreCase(OperateConstant.SERIAL_NUMBER_UUID)){
            EventBus.getDefault().post(new SingleSettingInfo(SettingName.SERIAL_NUMBER,characteristic.getValue()));
        }else if(uuid.equalsIgnoreCase(OperateConstant.CHARACTERISTIC_DEVICE_NAME_UUID)){
            EventBus.getDefault().post(new SingleSettingInfo(SettingName.DEVICE_NAME,characteristic.getValue()));
        }
//        EventBus.getDefault().post(characteristic.getValue());
        EventBus.getDefault().post(new NotifyInfo(uuid,characteristic.getValue()));
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        if(characteristic.getUuid().toString().equals(OperateConstant.CHARACTERISTIC_WRITE_UUID) && status == 0){

            if(commond != null){
                sendMsg(commond);
            }
        }
//        else if(characteristic.getUuid().toString().equals(OperateConstant.CHARACTERISTIC_OTA_WRITE_UUID)){

            /*LogUtil.getLogUtilInstance().save("send ota commond uuid: "+characteristic.getUuid().toString());

            if(otaImpl != null)
                otaImpl.onCharacteristicWrite(gatt, characteristic,status);*/
//        }else if(characteristic.getUuid().toString().equals(OperateConstant.CHARACTERISTIC_OTA_DATA_WRITE_UUID)){

            /*LogUtil.getLogUtilInstance().save("send ota data uuid: "+characteristic.getUuid().toString());

            if(otaImpl != null)
                otaImpl.onCharacteristicWrite(gatt, characteristic,status);*/

 //       }

    }

    /**
     *              终端Notify到APP的数据入口
     * @param gatt
     * @param characteristic
     */
    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        String uuid = characteristic.getUuid().toString().trim();
        if (uuid.equalsIgnoreCase(OperateConstant.CHARACTERISTIC_WRITE_UUID)) {
            Log.d("response", HexString.parseStringHex(characteristic.getValue()));
            byte[] response = characteristic.getValue();
            if (response[0] == 0x38) {
                if (msgList.size() > 0) {
                    msgList.remove(0);
                }

                if (response[2] == 0) {
                    if (msgList.size() > 0) {
                        msgList.clear();
                    }

                    BleAnalyze.bleDataAnalysis(characteristic.getValue());

                    commond = null;
                } else if (response[2] == 1) {

                    if (msgList.size() > 1) {
                        commond = genMsgBytes(3);
                    } else if (msgList.size() == 1) {
                        commond = genMsgBytes(2);
                    }
                }

            } else {
                BleAnalyze.bleDataAnalysis(characteristic.getValue());
            }
        } else if (uuid.equalsIgnoreCase(OperateConstant.CHARACTERISTIC_OTA_INDICATE_UUID)) {

            BleAnalyze.bootLoadDataAnalysis(characteristic.getValue());

            Log.d("response OTA", HexString.parseStringHex(characteristic.getValue()));
        } else if (uuid.equalsIgnoreCase(OperateConstant.NOTIFICATION_INDICATE_UUID)) {
            Log.d(TAG, "notify data:" + HexString.parseStringHex(characteristic.getValue()));
//            if (isModelNumberGot == false) {
            if (!Objects.requireNonNull(PHYApplication.getApplication().getConnectedDevices().get(gatt.getDevice().getAddress())).isModelNumberGot()) {
                handler.removeMessages(WAIT_NOTIRY);
                System.arraycopy(characteristic.getValue(), 0, syncDataFromBle, 0, characteristic.getValue().length);
                getModelNumber();
                return;
            } else {
                PHYApplication.getLedStatusUtil().parseHexBytes(characteristic.getValue(), ledStatus);
                if (ledStatus != null) {
                    EventBus.getDefault().post(ledStatus);
                }
            }
        } else if (uuid.equalsIgnoreCase(OperateConstant.NOTIFICATION_USER_MANUAL_INFO_UUID)) {
            Log.d("upload manual info:", HexString.parseStringHex(characteristic.getValue()));
//            EventBus.getDefault().post(characteristic.getValue());
            EventBus.getDefault().post(new NotifyInfo(uuid,characteristic.getValue()));
        } else {
            Log.d("notify information", "\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
            Log.d(uuid, ":"+HexString.parseStringHex(characteristic.getValue()));
            Log.d("notify information", "\n^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n");
        }
    }

    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        if(OperateConstant.CHARACTERISTIC_WRITE_UUID.equals(descriptor.getCharacteristic().getUuid().toString())){
            enableIndicateNotifications();
        }
    }
    @Override
    public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status){
        Log.d(TAG, "onDescriptorRead: "+descriptor.getUuid()+":"+status);
    }

    @Override
    public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
//        Log.d(TAG, gatt.getDevice().getAddress()+"onReadRemoteRssi: "+rssi);
        for (Map.Entry<String, TargetInfo> entry : PHYApplication.getApplication().getConnectedDevices().entrySet()) {
            if (gatt.getDevice().getAddress().equalsIgnoreCase(entry.getKey())) {
                entry.getValue().setConnectedRssi(rssi);
            }
        }
        if( PHYApplication.getApplication().getConnectedDevices().size()!=0)
            EventBus.getDefault().post(PHYApplication.getApplication().getConnectedDevices());
//        PHYApplication.getApplication().getConnectedDevices().get(entry.getValue().getDevice().getAddress()).getConnectedRssi()
    }

    /**
     * 获取已连接设备的RSSI
     */
    public void getConnectedDevicesRssi() {
        if (gattArrayMap.size() != 0) {
            for (Map.Entry<String, BluetoothGatt> entry : gattArrayMap.entrySet()) {
                entry.getValue().readRemoteRssi();
                try {
                    synchronized (Thread.currentThread()) { //把wait代码放在synchronized块中，锁线程自己
                        Thread.currentThread().wait(20);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Exception: " + e.getMessage());
                }
            }
        }
    }
//    public boolean isModelNumberGot() {
//        return isModelNumberGot;
//    }

    public void setModelNumberGot(boolean modelNumberGot) {
        isModelNumberGot = modelNumberGot;
    }



    public static void writeOtaInfoCharacteristic(byte[] value) {
        BluetoothGattService bluetoothGattService = BleCore.bluetoothGatt.getService(UUID.fromString(OperateConstant.SERVICE_UUID));
        if (bluetoothGattService == null) {
            Log.e(" OTA service", "service is null");
            return;
        }

        BluetoothGattCharacteristic characteristic = bluetoothGattService.getCharacteristic(UUID.fromString(OperateConstant.NOTIFICATION_USER_MANUAL_INFO_UUID));
        if (characteristic == null) {
            return;
        }
        characteristic.setValue(value);
        bluetoothGatt.writeCharacteristic(characteristic);
    }

}

