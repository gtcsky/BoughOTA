package com.phy.app.activity;


import static android.text.TextUtils.isEmpty;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

import com.phy.app.R;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.AutoConnectBean;
import com.phy.app.beans.Connect;
import com.phy.app.beans.Const;
import com.phy.app.beans.Device;
import com.phy.app.beans.SettingName;
import com.phy.app.beans.SingleSettingInfo;
import com.phy.app.ble.OperateConstant;
import com.phy.app.ble.core.BleCore;
import com.phy.app.util.Util;
import com.phy.app.util.DialogUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateSettingActivity extends EventBusBaseActivity implements View.OnClickListener {
    Button defaultSettingBtn,deviceNameHexBtn, deviceNameAsciiBtn, localNameHexBtn, localNameAsciiBtn, modelNoHexBtn, modelNoAsciiBtn, serialNoHexBtn, serialNoAsciiBtn;
    EditText deviceNameHexText, deviceNameAsciiText, localNameHexText, localNameAsciiText, modelNoHexText, modelNoAsciiText, serialNoHexText, serialNoAsciiText;

    private final int CHECK_LOCAL_NAME=1;
    private final int CHECK_MODEL_NUMBER=2;
    private final int CHECK_SERIAL_NUMBER=3;
    private final int CHECK_DEVICE_NAME=4;
    private final int DIALOG_AUTO_DISMISS=5;
    private int checkTimes=0;
    private boolean isScanning;
    private AlertDialog waitingDialog;
    private AlertDialog.Builder waitingBuilder;
    private boolean isWaitingDialogVisable;

    private AlertDialog warringDialog;
    private AlertDialog.Builder warringBuilder;

    private AlertDialog noServiceDialog;
    private AlertDialog.Builder noServiceBuilder;




    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == CHECK_MODEL_NUMBER) {
                if (isEmpty(modelNoAsciiText.getText().toString().trim())) {
                    if (checkTimes++ > 20) {
                        handler.sendEmptyMessageDelayed(CHECK_SERIAL_NUMBER, 100);
                        checkTimes = 0;
                    } else {
                        handler.sendEmptyMessageDelayed(CHECK_MODEL_NUMBER, 100);
                    }
                } else {
                    handler.sendEmptyMessageDelayed(CHECK_SERIAL_NUMBER, 100);
                    checkTimes = 0;
                }
            } else if (msg.what == CHECK_SERIAL_NUMBER) {
                if (isEmpty(serialNoAsciiText.getText().toString().trim())) {
                    if (checkTimes++ % 5 == 0) {
                        BleCore.userReadCharacteristic(OperateConstant.SERVICE_DEVICE_INFO_UUID, OperateConstant.SERIAL_NUMBER_UUID);        //读取serial number
                        handler.sendEmptyMessageDelayed(CHECK_SERIAL_NUMBER, 100);
                    } else if (checkTimes > 20) {       //skip ,and check next
                        handler.sendEmptyMessageDelayed(CHECK_DEVICE_NAME, 100);
                        checkTimes = 0;
                    } else {
                        handler.sendEmptyMessageDelayed(CHECK_SERIAL_NUMBER, 100);
                    }
                } else {
                    handler.sendEmptyMessageDelayed(CHECK_DEVICE_NAME, 100);
                    checkTimes = 0;
                }
            } else if (msg.what == CHECK_DEVICE_NAME) {
                if (isEmpty(deviceNameAsciiText.getText().toString().trim())) {
                    if (checkTimes++ % 5 == 0) {
                        BleCore.userReadCharacteristic(OperateConstant.SERVICE_GENERIC_ACCESS_UUID, OperateConstant.CHARACTERISTIC_DEVICE_NAME_UUID);         //读取serial number
                        handler.sendEmptyMessageDelayed(CHECK_DEVICE_NAME, 100);
                    } else if (checkTimes > 20) {       //skip ,end of check
                        checkTimes = 0;
                    } else {
                        handler.sendEmptyMessageDelayed(CHECK_DEVICE_NAME, 100);
                    }
                }
            } else if (msg.what == DIALOG_AUTO_DISMISS) {
                Log.d(TAG, "handleMessage: DIALOG_AUTO_DISMISS time out***********");
                if(waitingDialog!=null&&isWaitingDialogVisable){
                    waitingDialog.dismiss();
                }
            }
            return false;
        }
    });



    @Override
    public void initComponent() {
        setTitle(getString(R.string.label_setting));
        String localName=PHYApplication.getApplication().getName();
        String modelNo=PHYApplication.getLedStatus().getModelNumber();
        if(!isEmpty(modelNo))
            modelNo=modelNo.substring(0,PHYApplication.getLedStatus().getModelNumber().indexOf(' '));
        deviceNameHexBtn = findViewById(R.id.btn_device_name_hex);
        deviceNameAsciiBtn = findViewById(R.id.btn_device_name_ascii);
        localNameHexBtn = findViewById(R.id.btn_local_name_hex);
        localNameAsciiBtn = findViewById(R.id.btn_local_name_ascii);
        modelNoHexBtn = findViewById(R.id.btn_model_number_hex);
        modelNoAsciiBtn = findViewById(R.id.btn_model_number_ascii);
        serialNoHexBtn = findViewById(R.id.btn_serial_number_hex);
        serialNoAsciiBtn = findViewById(R.id.btn_serial_number_ascii);
        defaultSettingBtn = findViewById(R.id.btn_default_setting);

        deviceNameHexText = findViewById(R.id.device_name_hex);
        deviceNameAsciiText = findViewById(R.id.device_name_ascii);
        localNameHexText = findViewById(R.id.local_name_hex);
        localNameHexText.setText(Util.hex2AsciiStr(localName.getBytes()));
        localNameAsciiText = findViewById(R.id.local_name_ascii);
        localNameAsciiText.setText(localName);
        modelNoHexText = findViewById(R.id.model_number_hex);
        if(!isEmpty(modelNo))
            modelNoHexText.setText(Util.hex2AsciiStr(modelNo.getBytes()));
        modelNoAsciiText = findViewById(R.id.model_number_ascii);
        modelNoAsciiText.setText(modelNo);
        serialNoHexText = findViewById(R.id.serial_number_hex);
        serialNoAsciiText = findViewById(R.id.serial_number_ascii);

        setEditTextHexInput(modelNoHexText);                            //只允许输入16进制范围字符
        setEditTextHexInput(deviceNameHexText);
        setEditTextHexInput(serialNoHexText);
        setEditTextHexInput(localNameHexText);

        deviceNameHexBtn.setOnClickListener(this);
        deviceNameAsciiBtn.setOnClickListener(this);
        localNameHexBtn.setOnClickListener(this);
        localNameAsciiBtn.setOnClickListener(this);
        modelNoHexBtn.setOnClickListener(this);
        modelNoAsciiBtn.setOnClickListener(this);
        serialNoHexBtn.setOnClickListener(this);
        serialNoAsciiBtn.setOnClickListener(this);
        defaultSettingBtn.setOnClickListener(this);

//        BleCore.userReadCharacteristic(OperateConstant.SERVICE_DEVICE_INFO_UUID,OperateConstant.SERIAL_NUMBER_UUID);        //读取serial number
        if(isEmpty(modelNo)){
            BleCore.userReadCharacteristic(OperateConstant.SERVICE_DEVICE_INFO_UUID, OperateConstant.MODULE_NUMBER_UUID);
            handler.sendEmptyMessageDelayed(CHECK_MODEL_NUMBER, 50);
        }else{
            handler.sendEmptyMessageDelayed(CHECK_SERIAL_NUMBER,50);
        }
        checkTimes=0;
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_update_setting;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_device_name_hex) {
            sendInputInfo(Const.UPDATE_DEVICE_NAME_COMMAND, deviceNameHexText.getText().toString().trim(), "Device Name");
        } else if (id == R.id.btn_device_name_ascii) {
            sendInputInfo(Const.UPDATE_DEVICE_NAME_COMMAND, deviceNameAsciiText.getText().toString().getBytes(), "Device Name");
        } else if (id == R.id.btn_local_name_hex) {
            sendInputInfo(Const.UPDATE_LOCAL_NAME_COMMAND, localNameHexText.getText().toString().trim(), "local Name");
            PHYApplication.getBandUtil().scanDevice();
            isScanning=true;
        } else if (id == R.id.btn_local_name_ascii) {
            sendInputInfo(Const.UPDATE_LOCAL_NAME_COMMAND, localNameAsciiText.getText().toString().getBytes(), "local Name");
            PHYApplication.getBandUtil().scanDevice();
            isScanning=true;
        } else if (id == R.id.btn_serial_number_hex) {
            sendInputInfo(Const.UPDATE_SERIAL_NUMBER_COMMAND, serialNoHexText.getText().toString().trim(), "Serial No");
        } else if (id == R.id.btn_serial_number_ascii) {
            sendInputInfo(Const.UPDATE_SERIAL_NUMBER_COMMAND, serialNoAsciiText.getText().toString().getBytes(), "Serial No");
        } else if (id == R.id.btn_model_number_hex) {
            sendInputInfo(Const.UPDATE_MODEL_NUMBER_COMMAND, modelNoHexText.getText().toString().trim(), "Model No");
        } else if (id == R.id.btn_model_number_ascii) {
            sendInputInfo(Const.UPDATE_MODEL_NUMBER_COMMAND, modelNoAsciiText.getText().toString().getBytes(), "Model No");
        } else if (id == R.id.btn_default_setting) {

            warringDialog=DialogUtils.createUserDialog(UpdateSettingActivity.this,warringBuilder,warringDialog,"警告",getString(R.string.default_setting_info),getString(R.string.update_confirm),getString(R.string.update_cancel),new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    byte[] bytes={(byte) 0x24,(byte)0x29,(byte)0x40,(byte)0x3f,(byte)(0x22)};
                    sendInputInfo(Const.LOAD_TARGET_DEFAULT_SETTING, bytes, " ");
                    PHYApplication.getBandUtil().scanDevice();
                    isScanning=true;
                }
            },null);

        }
    }


    private void pormptInfoShow(){


        waitingDialog= DialogUtils.createUserDialog(UpdateSettingActivity.this,waitingBuilder,waitingDialog,"提示",getString(R.string.waiting_connect),null,getString(R.string.update_confirm),null,null,new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                isWaitingDialogVisable = false;
                handler.removeMessages(DIALOG_AUTO_DISMISS);
            }
        });

        isWaitingDialogVisable=true;
        handler.sendEmptyMessageDelayed(DIALOG_AUTO_DISMISS, 6000);
    }


    private void noServiceInfoShow() {
        noServiceDialog = DialogUtils.createUserDialog(UpdateSettingActivity.this, waitingBuilder, waitingDialog, "提示", getString(R.string.not_support), null, getString(R.string.update_confirm), null, null, null);
    }


    /**
     *
     * @param cmd               待修改的设置
     * @param bytes             ASCII字符串转换成的byte数组.
     * @param tag               错误提示的主语
     */
    private void sendInputInfo(byte cmd,byte[] bytes,String tag){
        if (null != bytes && bytes.length != 0) {
            if(PHYApplication.getBandUtil().userUpdateTargetSystemSetting(cmd,bytes)){
                pormptInfoShow();
            }else{
                noServiceInfoShow();
            }
        } else {
            showToast(tag+"不能是空.");
        }
    }
    /**
     *
     * @param cmd               待修改的设置
     * @param inputString       Hex字符串
     * @param tag               错误提示的主语
     */
    private void sendInputInfo(byte cmd,String inputString,String tag){
//        String modelNoHexString=modelNoHexText.getText().toString().trim();
        if(isEmpty(inputString)){
            showToast(tag+"不能是空.");
        }else{
            String[] strings= inputString.split(" ");
            if(strings[strings.length-1].length()!=2){
                showToast(tag+"输入不完整.");
            }else{
                byte[] bytes=new byte[strings.length];
                int i=0;
                for (String str:strings) {
                    bytes[i++]=(byte)(Integer.valueOf(str,16)&0x000000ff);
                }
                if(PHYApplication.getBandUtil().userUpdateTargetSystemSetting(cmd,bytes)){
                    pormptInfoShow();
                } else{
                    noServiceInfoShow();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Connect connect) {
        if(connect.isConnect()){
//            initComponent();                                                                                                          //重新加载当前界面
//            String localName=PHYApplication.getApplication().getName();
//            String modelNo=PHYApplication.getLedStatus().getModelNumber().substring(0,PHYApplication.getLedStatus().getModelNumber().indexOf(' '));
//            BleCore.userReadCharacteristic(OperateConstant.SERVICE_DEVICE_INFO_UUID,OperateConstant.SERIAL_NUMBER_UUID);              //读取serial number
//            handler.sendEmptyMessageDelayed(CHECK_SERIAL_NUMBER,100);
//            modelNoHexText.setText(Util.hex2AsciiStr(modelNo.getBytes()));
//            modelNoAsciiText.setText(modelNo);

            handler.sendEmptyMessageDelayed(CHECK_MODEL_NUMBER,100);
            checkTimes=0;
//            localNameHexText.setText(Util.hex2AsciiStr(localName.getBytes()));
//            localNameAsciiText.setText(localName);
        }else{
            if(waitingDialog!=null&&isWaitingDialogVisable){
                waitingDialog.dismiss();
            }
            deviceNameHexText.setText("");
            deviceNameAsciiText.setText("");
            localNameHexText.setText("");
            localNameAsciiText.setText("");
            modelNoHexText.setText("");
            modelNoAsciiText.setText("");
            serialNoHexText.setText("");
            serialNoAsciiText.setText("");
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SingleSettingInfo info) {
        if (info.getSettingName() == SettingName.SERIAL_NUMBER) {
            serialNoHexText.setText(Util.hex2AsciiStr(info.getValue()));
            serialNoAsciiText.setText(Util.Hex2Str(info.getValue()));
            handler.removeMessages(CHECK_SERIAL_NUMBER);
            handler.removeMessages(CHECK_DEVICE_NAME);
            handler.sendEmptyMessageDelayed(CHECK_DEVICE_NAME, 20);
            checkTimes = 0;
        } else if (info.getSettingName() == SettingName.DEVICE_NAME) {
            deviceNameHexText.setText(Util.hex2AsciiStr(info.getValue()));
            deviceNameAsciiText.setText(Util.Hex2Str(info.getValue()));
        } else if (info.getSettingName() == SettingName.MODEL_NUMBER) {
            modelNoHexText.setText(Util.hex2AsciiStr(info.getValue()));
            modelNoAsciiText.setText(Util.Hex2Str(info.getValue()));
        } else if (info.getSettingName() == SettingName.LOCAL_NAME) {
//            showToast(Util.Hex2Str(info.getValue()));
//            Log.d(TAG, "onMessageEvent: ******************");
            localNameHexText.setText(Util.hex2AsciiStr(info.getValue()));
            localNameAsciiText.setText(Util.Hex2Str(info.getValue()));
            PHYApplication.getApplication().setName(Util.Hex2Str(info.getValue()));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Device device) {
        String mac;
        mac=device.getDevice().getAddress();
        isScanning=true;
//        Log.e(TAG+" device",mac + "#"+device.getDevice().getName());
//        if(!deviceList.contains(device) && !TextUtils.isEmpty(device.getDevice().getName())){
//            addDevice2List(device);
//        }
        HashMap<String, AutoConnectBean> autoConnectDevices = PHYApplication.getApplication().getAutoConnectDevices();
        if (autoConnectDevices != null) {
            if (autoConnectDevices.containsKey(mac)) {
                if (autoConnectDevices.get(mac).isWaitScanning()) {
                    autoConnectDevices.get(mac).setWaitScanning(false);
                }
            }
            boolean someDeviceWaitingScan = false;
            for (Map.Entry<String, AutoConnectBean> connectBean : autoConnectDevices.entrySet()) {
                if (connectBean.getValue().isWaitScanning()) {
                    someDeviceWaitingScan = true;             //检测是否仍有需要重新扫描的设备
                    break;
                }
            }
            if (!someDeviceWaitingScan) {
                PHYApplication.getBandUtil().stopScanDevice();
                isScanning = false;
            }
        }
    }


    /**
     *
     * @param editText  输入
     */

    public  void setEditTextHexInput(final EditText editText) {
        editText.addTextChangedListener(
                new TextWatcher() {
                    private boolean flag = true;
                    private int index = 0;
                    private int beforeLen = 0;

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (flag) {
                            beforeLen = s.length();
                            index = start - count + 1;
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        flag = false;
                        String hex = toHex(s.toString());
                        if (hex.equals(s.toString())) {
                            flag = true;
                            if (beforeLen < hex.length()) {
                                index = index + hex.length() - beforeLen - 1;
                            }
                            if (index < 0) index = 0;
                            if (index > hex.length()) index = hex.length();
                            editText.setSelection(index);
                            return;
                        }
                        editText.setText(hex);
                    }
                });
    }

    /**
     *  限制十六进制以外的字符输入
     * @param src 输入字符串
     * @return 返回合法的十六进制字符串
     */
    public static String toHex(String src) {
        if (isEmpty(src)) {
            return "";
        }
        src = src.replaceAll("\\s+", "").replaceAll("[^0-9a-fA-F\\*]", "");
        char[] chars = src.toCharArray();
        StringBuilder sb = new StringBuilder();
        int i = 0, len = chars.length % 2 == 0 ? chars.length : chars.length - 1;
        for (; i < len; i++) {
            sb.append(chars[i]);
            sb.append(chars[++i]);
            sb.append(" ");
        }
        if (i < chars.length) {
            sb.append(chars[i]);
        }
        return sb.toString().trim();
    }

    /**
     *
     * 删除所有需要单次自动连接的设备
     *
     */
    private void clearAutoConnectDeviceInfo() {
        HashMap<String, AutoConnectBean> autoConnectDevices = PHYApplication.getApplication().getAutoConnectDevices();
        ArrayList<String> needRemove = new ArrayList();
        if (autoConnectDevices != null) {
            for (Map.Entry<String, AutoConnectBean> entry : autoConnectDevices.entrySet()) {
                if (!entry.getValue().isAlwaysAutoConnect()) {
                    needRemove.add(entry.getKey());
                }
            }
        }

        for (String mac : needRemove) {
            autoConnectDevices.remove(mac);             //删除所有需要单次自动连接的设备
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if(isScanning){
            PHYApplication.getBandUtil().stopScanDevice();
            isScanning=false;
        }
        clearAutoConnectDeviceInfo();
        isWaitingDialogVisable=false;
    }

}