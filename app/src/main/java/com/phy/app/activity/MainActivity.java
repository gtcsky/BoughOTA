package com.phy.app.activity;

import static android.text.TextUtils.isEmpty;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.phy.app.R;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.BleEvent;
import com.phy.app.beans.Connect;
import com.phy.app.beans.Const;
import com.phy.app.beans.TargetInfo;
import com.phy.app.ble.OperateConstant;
import com.phy.app.ble.core.BleCore;
import com.phy.app.util.Util;
import com.phy.app.thread.AutoConnectThread;
import com.phy.app.util.DialogUtils;
import com.phy.app.views.RectImageView;
import com.phy.app.views.RoundImageView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

public class MainActivity extends EventBusBaseActivity {

//    private TextView connectTV;
    private TextView macTV;
    private TextView versionTV;
    private Button reBootButton;
    private VersionHandle handle = new VersionHandle();
    private String TAG=getClass().getSimpleName();
    private RoundImageView img_round;
    private RoundImageView img_connect;
    private RoundImageView img_ota;
    private RectImageView img_console;
    private Bitmap bitmap;


    @Override
    public void initComponent() {
//        connectTV = findViewById(R.id.text_connect);
        macTV = findViewById(R.id.device_mac);
        versionTV = findViewById(R.id.app_version);
        reBootButton = findViewById(R.id.reboot_button);

        versionTV.setText(getString(R.string.app_version, getVersion()));

//        img_connect = (RoundImageView) findViewById(R.id.img_connect);
//        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_connect);
//        img_connect.setBitmap(bitmap);
//        img_ota = (RoundImageView) findViewById(R.id.img_ota);
//        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_update);
//        img_ota.setBitmap(bitmap);
//        img_console=(RectImageView) findViewById(R.id.img_console);
//        bitmap=BitmapFactory.decodeResource(getResources(),R.mipmap.ic_console);
//        img_console.setBitmap(bitmap);

        Thread thread = new Thread(new AutoConnectThread());
        thread.start();
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_main;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Connect connect) {
        if (connect != null) {
            setText(connect.isConnect());
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(byte[] value) {
//        Log.d(TAG, "onMessageEvent: " + Util.hex2AsciiStr(value));
//    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ArrayMap<String, TargetInfo> connectedDevices) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        setText(PHYApplication.getApplication().isConnect());
    }

    private String getVersion() {
        PackageManager manager = getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "1.0";
        }
    }

    private void setText(boolean isConnect) {
        if (isConnect) {
//            connectTV.setText(R.string.disconnect_device);
            String mac = PHYApplication.getApplication().getMac();
            if (null != mac && mac.length() > 9)
//                Log.d(TAG, "setText:connected");
                macTV.setText(getString(R.string.connected_device, PHYApplication.getApplication().getName() + " (" + PHYApplication.getLedStatus().getModelNumber() + ") " + mac.substring(9)));
        } else {
            ArrayMap<String, TargetInfo> devices=PHYApplication.getApplication().getConnectedDevices();
            if(devices!=null&&devices.size()!=0){
                for (Map.Entry<String,TargetInfo> entry:devices.entrySet()){
                    Log.d(TAG, "mac:"+entry.getKey()+"\t info"+entry.getValue());
//                    macTV.setText(getString(R.string.connected_device, entry.getValue().getDeviceName()+" (" + entry.getValue().getModelNumber()  + ") " + entry.getKey().substring(9)));

                    BleCore.bluetoothGatt=entry.getValue().getGatt();
                    PHYApplication.getApplication().setConnect(true);
                    PHYApplication.getApplication().setMac(entry.getKey());
                    PHYApplication.getApplication().setName(entry.getValue().getDeviceName());
                    PHYApplication.getLedStatus().setModelNumber(entry.getValue().getModelNumber());
//                    Log.d(TAG, "current Target: "+entry.getKey());
                }
                macTV.setText(getString(R.string.connected_device, PHYApplication.getApplication().getName() + " (" + PHYApplication.getLedStatus().getModelNumber() + ") " +PHYApplication.getApplication().getMac().substring(9)));
            }else{
                macTV.setText(R.string.unconnected_device);
            }
//            devices=null;
            PHYApplication.getBandUtil().clearTargetNameInfo();
        }

        setBootLoad(isConnect);
    }

    private void setBootLoad(boolean isConnect) {
        if (isConnect) {
            if (PHYApplication.getBandUtil().isOTA()) {
                versionTV.setText(getString(R.string.app_ota_bootload_version, getVersion()));
                reBootButton.setVisibility(View.VISIBLE);
            } else {
                handle.sendEmptyMessageDelayed(1, 1000);
                reBootButton.setVisibility(View.INVISIBLE);
            }
        } else {
            versionTV.setText(getString(R.string.app_version, getVersion()));
            reBootButton.setVisibility(View.INVISIBLE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BleEvent event) {
        if (event.getOperate().equals(OperateConstant.FIRMWARE_VERSION)) {
            if(!macTV.getText().toString().contains(PHYApplication.getLedStatus().getModelNumber()))
                setText(true);
//            Log.d(getClass().getSimpleName(), "***********收到firmware版本***********");
        } else if (event.getOperate().equals(OperateConstant.BOOT_LOAD_VERSION)) {
            byte[] data = (byte[]) event.getObject();
            byte[] bytes = new byte[data.length - 7];
            System.arraycopy(data, 7, bytes, 0, bytes.length);

            Log.e("chars", new String(bytes).trim());
            versionTV.setText(getString(R.string.app_bootload_version, getVersion(), new String(bytes).trim()));
        }
    }

    private boolean checkConnected() {
        if (!PHYApplication.getApplication().isConnect()) {
            showToast(R.string.label_unconnect_device);
            return false;
        }
        return true;
    }

    public void goSearch(View view) {

        Intent intent = new Intent(this, UserSearchDeviceActivity.class);
//        startActivity(intent);
        startActivityForResult(intent, Const.ACTIVITY_RESULT_SEARCH_DEVICE);
    }

//    public void go2Gsensor(View view) {
//        if (!checkIsConnected()) {
//            return;
//        }
//
//        Intent intent = new Intent(this, GsensorActivity.class);
//        startActivity(intent);
//    }
//
//    public void go2HeartRate(View view) {
//        if (!checkIsConnected()) {
//            return;
//        }
//
//        Intent intent = new Intent(this, HearRateActivity.class);
//        startActivity(intent);
//    }

    public void go2LED(View view) {
        if (!checkConnected()) {
            return;
        }
        if(PHYApplication.getApplication().getName().contains("OTA")){
            showToast("OTA模式时,不支持LED控制");
            return;
        }
        if(isEmpty(PHYApplication.getLedStatus().getModelNumber())){
            showToast("未获取目标设备型号,可尝试重新连接,或尝试到设备信息中手动获取.");
            return;
        }


//        Intent intent = new Intent(this, LedNewActivity.class);
        Intent intent = new Intent(this, LedNewActivity.class);
//        startActivityForResult(intent, 100);
        startActivity(intent);
    }

//    public void go2Push(View view) {
//        if (!checkIsConnected()) {
//            return;
//        }
//
//        Intent intent = new Intent(this, PushActivity.class);
//        startActivity(intent);
//    }

    public void go2KeyBoard(View view) {

//        showToast("暂未实现");

        /*if(!checkIsConnected()){
            return;
        }

        Intent intent = new Intent(this,KeyBoardActivity.class);
        startActivity(intent);*/
        if (!checkConnected()) {
            return;
        }
        Intent intent = new Intent(this,DeviceInfoActivity.class);
        startActivity(intent);
    }

    public void go2OTA(View view) {
        if (!checkConnected()) {
            return;
        }

        if (!Util.checkIsCanOTA(BleCore.bluetoothGatt)) {
            showToast("设备不能进行OTA升级");
            return;
        }


        if(isEmpty(PHYApplication.getLedStatus().getModelNumber())&&!PHYApplication.getApplication().getName().contains("OTA")){
            BleCore.getModelNumber();
            showToast("获取产品型号失败,请重试!");
            return;
        }

        if(PHYApplication.getApplication().getConnectedDevices().size()!=1){

             DialogUtils.createUserDialog(MainActivity.this,null,null,getString(R.string.error_title),getString(R.string.single_connect_info),getString(R.string.update_confirm),null,null,null);

        } else {
//            Intent intent = new Intent(this, OTANewActivity.class);
//            Intent intent = new Intent(this, UserOtaActivity.class);
            Intent intent = new Intent(this, UserSmartOtaActivity.class);
            startActivity(intent);
        }
    }

    public void reBoot(View view) {

        Log.d("reBoot", "reBoot");

        PHYApplication.getBandUtil().startReBoot();
        handle.sendEmptyMessageDelayed(2, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.ACTIVITY_RESULT_SEARCH_DEVICE) {
            String modelNo=PHYApplication.getLedStatus().getModelNumber();
            String localName=PHYApplication.getApplication().getName();
            if(checkConnected()&&isEmpty(modelNo)&&!localName.contains("OTA")){
                BleCore.getModelNumber();
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handle.removeCallbacksAndMessages(null);

        /*if((PHYApplication.getApplication().isConnect())){
            PHYApplication.getBandUtil().disConnectDevice();
        }*/
    }

    public static class VersionHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
//                return ;
                PHYApplication.getBandUtil().getBootLoadVersion();
            } else if (msg.what == 2) {
                PHYApplication.getBandUtil().disConnectDevice();
            }
        }
    }

}
