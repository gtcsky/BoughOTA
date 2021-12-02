package com.phy.app.activity;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.phy.app.R;
import com.phy.app.adapter.FileListAdapter;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.BleEvent;
import com.phy.app.beans.Connect;
import com.phy.app.beans.Device;
import com.phy.app.ble.OTACallBack;
import com.phy.app.ble.OperateConstant;
import com.phy.app.util.HexString;
import com.phy.app.util.LogUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * OTAActivity
 *
 * @author:zhoululu
 * @date:2018/5/19
 */

public class OTAActivity extends EventBusBaseActivity implements EasyPermissions.PermissionCallbacks, OTACallBack {

    List<String> fileList;
    FileListAdapter fileListAdapter;

    String path = Environment.getExternalStorageDirectory().getPath();
    private String filePath;

    private String operation = "";

    private TextView opertionTV;
    private ProgressBar bar;

    private static final String STARTOTA = "STARTOTA";
    //private static final String SCANDEVICE = "SCANDEVICE";
    private static final String SENDDATA = "SENDDATA";
    private static final String FINISH = "FINISH";
    private static final String CONNECTING = "CONNECTING";
    private static final String FINSHCONNECTING = "FINSHCONNECTING";

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 200){
                opertionTV.setText("正在重新连接");
            }else if(msg.what == 300){
                Bundle bundle = msg.getData();
                opertionTV.setText("错误："+bundle.getString("error"));
            }else {
                opertionTV.setText("正在发送数据"+msg.what+"%");
            }
        }
    };

    @Override
    public void initComponent() {
        setTitle(R.string.label_ota);

        opertionTV = findViewById(R.id.current_opertion);
        bar = findViewById(R.id.progress_bar);

        ListView fileListView = findViewById(R.id.file_list);
        fileList = new ArrayList<String>();

        fileListAdapter = new FileListAdapter(this, R.layout.item_file_list);
        fileListView.setAdapter(fileListAdapter);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            searchFile();
        }else {
            initRequiredPermission();
        }

        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                filePath = path+"/"+fileList.get(position);
                filePath = getAssetsCacheFile(fileList.get(position));
                Log.e("onItemClick", ": "+"filePath:"+filePath );

                LogUtil.getLogUtilInstance().createLogFile();

                /*if(PHYApplication.getApplication().getName().startsWith("PhyWrist")){
                    PHYApplication.getBandUtil().startOTA();

                    operation = STARTOTA;
                }else if(PHYApplication.getApplication().getName().startsWith("PPlusOTA")){
                    operation = SENDDATA;
                    opertionTV.setText("正在发送数据");

                    PHYApplication.getBandUtil().updateFirmware("", filePath, OTAActivity.this);

                }*/
            }
        });
    }


    public void reBoot(View view){
        //PHYApplication.getBandUtil().startReBoot();
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_ota;
    }

    private void searchFile(){

        fileList.clear();

//        File file = new File(path);
//        if(file.exists()){
//            File[] listFiles = file.listFiles();
//            for (File f : listFiles){
//                if(f.getName().endsWith(".hex")) {
//                    fileList.add(f.getName());
//                    fileListAdapter.setData(fileList);
//                }
//            }
//        }else {
//            showToast("sdcard not found");
//        }

        try{
            String[] filename = getAssets().list("");
            for(int i = 0; i < filename.length; i++)
            {
                if(filename[i].endsWith(".hex") || filename[i].endsWith(".hexe") || filename[i].endsWith(".res"))
                {
                    fileList.add(filename[i]);
                    fileListAdapter.setData(fileList);
                }
            }
        } catch (Exception e) {
            showToast("Firmware  not found");
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BleEvent event){
        if(event.getOperate().equals(OperateConstant.START_OTA)){
            PHYApplication.getBandUtil().scanDevice();

            opertionTV.setText("进入ota模式");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Connect connect) {
        if(connect.isConnect()){
            Log.e("connect success","connected");

            if(operation.equals(CONNECTING)){

                operation = SENDDATA;
                opertionTV.setText("正在发送数据");

                //PHYApplication.getBandUtil().updateFirmware("", filePath, this);
            }else if(operation.equals(FINSHCONNECTING)){
                showToast("升级成功");
                finish();
            }

        }else{
              if(STARTOTA.equals(operation)){

              }else if(FINISH.equals(operation)){
                  PHYApplication.getBandUtil().scanDevice();
              }else{
                  opertionTV.setText("已断开连接");
              }
        }
    }

    @Override
    public void onComplete() {
        Log.e(TAG, "finish: " );
        operation = FINISH;

        handler.sendEmptyMessage(200);

        PHYApplication.getBandUtil().disConnectDevice();
    }

    @Override
    public void onProcess(float process) {
        Log.e(TAG, "onProcess: "+process );
        bar.setProgress((int) process);

        handler.sendEmptyMessage((int) process);
    }


    @Override
    public void onError(String errorCode) {
        Log.e(TAG, "error: "+errorCode );

        Message message = new Message();
        message.what = 300;
        Bundle bundle = new Bundle();
        bundle.putString("error",errorCode);
        message.setData(bundle);

        handler.sendMessage(message);

        operation = "";
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Device event){

        if(operation.equals(STARTOTA)){
            if(event.getDevice().getAddress().toUpperCase().equals(genMac(PHYApplication.getApplication().getMac()))){
                PHYApplication.getApplication().setMac(event.getDevice().getAddress());
                PHYApplication.getBandUtil().stopScanDevice();
                PHYApplication.getBandUtil().connectDevice(event.getDevice().getAddress());

                operation = CONNECTING;
            }
        }else if(operation.equals(FINISH)){
            if(event.getDevice().getAddress().toUpperCase().equals(mac(PHYApplication.getApplication().getMac()))){
                PHYApplication.getApplication().setMac(event.getDevice().getAddress());
                PHYApplication.getBandUtil().stopScanDevice();
                PHYApplication.getBandUtil().connectDevice(event.getDevice().getAddress());

                operation = FINSHCONNECTING;
            }
        }

    }

    private String genMac(String mac){
        int macInt = Integer.parseInt(mac.substring(mac.length()-2,mac.length()));
        if(macInt == 255){
            macInt = 0;
        }else{
            macInt ++;
        }
        return (mac.substring(0,mac.length()-2)+ HexString.int2ByteString(macInt)).toUpperCase();
    }

    private String mac(String mac){
        int macInt = Integer.parseInt(mac.substring(mac.length()-2,mac.length()));
        if(macInt == 0){
            macInt = 255;
        }else{
            macInt --;
        }
        return (mac.substring(0,mac.length()-2)+ HexString.int2ByteString(macInt)).toUpperCase();
    }

    @AfterPermissionGranted(100)
    private void initRequiredPermission(){

        String[] permissions =new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        boolean hasPermissions = EasyPermissions.hasPermissions(this, permissions);
        if (!hasPermissions) {
            EasyPermissions.requestPermissions(this, getString(R.string.label_read_tips),100, permissions);
        }else {
           searchFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
       searchFile();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        showToast(R.string.label_read_tips);
    }

    public String getAssetsCacheFile( String fileName) {
        File cacheFile = new File(this.getCacheDir(), fileName);
        try {
            InputStream inputStream = this.getAssets().open(fileName);
            try {
                FileOutputStream outputStream = new FileOutputStream(cacheFile);
                try {
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buf)) > 0) {
                        outputStream.write(buf, 0, len);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cacheFile.getAbsolutePath();
    }
}
