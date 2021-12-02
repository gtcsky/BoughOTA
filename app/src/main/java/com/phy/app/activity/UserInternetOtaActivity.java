package com.phy.app.activity;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.phy.app.R;
import com.phy.app.adapter.OtaFileInfoAdapter;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.Const;
import com.phy.app.beans.NotifyInfo;
import com.phy.app.ble.OperateConstant;
import com.phy.app.ble.bean.OtaFileInfo;
import com.phy.app.ble.core.BleCore;
import com.phy.app.util.Util;
import com.phy.app.util.DownloadFileUtils;
import com.phy.app.util.JsonUtils;
import com.phy.app.util.MD5Utils;
import com.phy.app.util.UrlUtil;
import com.phy.app.util.Utils;
import com.phy.ota.sdk.OTASDKUtils;
import com.phy.ota.sdk.firware.UpdateFirewareCallBack;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class UserInternetOtaActivity extends EventBusBaseActivity implements UpdateFirewareCallBack {
    private String filePath;
    private TextView opertionTV;
    private ProgressBar bar;
    private OtaFileInfo selectedInfo;
    private StringBuilder mainSb;
    int startIndex;
    private ListView listView;
    private OtaFileInfoAdapter adapter;
    private OTASDKUtils otasdkUtils;
    private boolean isOTAING;
    private String mac;
    private String otaMac;
    private boolean isDownloading;
    private SwipeRefreshLayout swip_refresh_layout;
    private boolean isOtaInfoGod;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == Const.HTTP_RESPOND) {
                int total = mainSb.length();
                if (total - startIndex > 1024) {
                    startIndex += 1024;
                    handler.sendEmptyMessage(Const.HTTP_RESPOND);
                } else {
//                    Log.d(TAG, "handleMessage: "+mainSb.toString());
                    List<OtaFileInfo> otaFileInfos= JsonUtils.parseJsonString(mainSb.toString());
                    for(OtaFileInfo otaFileInfo:otaFileInfos){
                        Log.d(TAG, "handleMessage:HTTP_RESPOND "+otaFileInfo);
                    }
                    adapter.setData(otaFileInfos);
                    swip_refresh_layout.setRefreshing(false);
                }
            }else if(Const.READ_VERSION==msg.what){
                BleCore.getModelNumber();
                Log.d(getClass().getSimpleName(), " ****正在获取*** ");
                handler.sendEmptyMessageDelayed(Const.AUTO_EXIT, 500);
            }else if(Const.AUTO_EXIT==msg.what){
                finish();                                          //结束当前页面
//                Intent intent=new Intent(UserInternetOtaActivity.this,MainActivity.class);
//                startActivity(intent);
            }else if(Const.SWITCH_NOTIFY_TIMEOUT==msg.what){
                BleCore.writeOtaInfoCharacteristic(new byte[]{(byte)0x42,(byte)0x42,(byte)0x6f,(byte)0x75,(byte)0x67,(byte)0x68,(byte)0x20,(byte)0x55,(byte)0x73,(byte)0x65,(byte)0x72,(byte)0x20,(byte)0x4f,(byte)0x54,(byte)0x41});
                Log.d(TAG, "handleMessage: 42426f7567682055736572204f5441");
                handler.sendEmptyMessageDelayed(Const.OTA_INFO_NOTIFY_TIMEOUT,1000);
            }else if(Const.HTTP_NO_RESPOND==msg.what){
                showToast("获取服务器上升级文件失败,可尝试下拉刷新.");
                swip_refresh_layout.setRefreshing(false);
            }else if(Const.OTA_INFO_NOTIFY_TIMEOUT==msg.what){
                isOtaInfoGod=false;
                String modelNo=PHYApplication.getLedStatus().getModelNumber();
                if(modelNo.contains("OTA")){
                    showToast("获取在线OTA文件失败.");
                    swip_refresh_layout.setRefreshing(false);
                }else{          //设备中没有FFF6 读取OTA在线文件信息的功能,则尝试推断服务器上OTA文件对应的地址.
                    modelNo=modelNo.substring(0,modelNo.lastIndexOf(' '));
                    String jsonAddress=Const.PRE_ADDRESS+modelNo+"D001E001/releaseOTA.json";
                    downloadJsonFile(jsonAddress);
                    isOtaInfoGod=true;
//                    handler.removeMessages(Const.OTA_INFO_NOTIFY_TIMEOUT);
                }
                Log.d(TAG, "handleMessage: OTA_INFO_NOTIFY_TIMEOUT****************"+modelNo);

            }else if(Const.FILE_DOWNLOAD_OVER==msg.what){
                Log.d(TAG, "handleMessage: download over!");
                isDownloading=false;
                File cacheFile = null;
                try {
                    cacheFile = UrlUtil.getFileByUrl(UserInternetOtaActivity.this,UrlUtil.parseUrl(selectedInfo));
                    if(cacheFile.exists()&&cacheFile.length()!=0){
                        Log.d(TAG, "onItemClick: MD5="+MD5Utils.getFileMD5(cacheFile)+"\t len="+cacheFile.length());
                        Log.d(TAG, "ori: MD5="+selectedInfo.getMD5());
                        if(MD5Utils.getFileMD5(cacheFile).equalsIgnoreCase(selectedInfo.getMD5().trim())){
//                            Log.d(TAG, "handleMessage: valid File:"+cacheFile.getAbsolutePath());
                            updateFirmware(cacheFile.getAbsolutePath());

                        }else{
                            Log.d(TAG, "handleMessage: invalid File");
                            showToast("下载完成后文件校验失败.");
                        }
                    }else{
//                        Log.d(TAG, "handleMessage: file not exist!");
                        showToast("目标文件不存在.");
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    });

    @Override
    public void initComponent() {
        setTitle(R.string.label_ota);
        opertionTV = findViewById(R.id.current_opertion);
        bar = findViewById(R.id.progress_bar);
        otasdkUtils = new OTASDKUtils(UserInternetOtaActivity.this, this);
        BleCore.switchNotify(OperateConstant.SERVICE_UUID,OperateConstant.NOTIFICATION_USER_MANUAL_INFO_UUID,true);
        handler.sendEmptyMessageDelayed(Const.SWITCH_NOTIFY_TIMEOUT,500);
        isOtaInfoGod=false;


        if (PHYApplication.getBandUtil().isOTA()) {
            mac = Utils.getOTAMac(PHYApplication.getApplication().getMac(), -1);
            otaMac = PHYApplication.getApplication().getMac();
        } else {
            otaMac = Utils.getOTAMac(PHYApplication.getApplication().getMac(), 1);
            mac = PHYApplication.getApplication().getMac();
        }

        listView=findViewById(R.id.id_ota_info_list);
        adapter=new OtaFileInfoAdapter(this,R.layout.item_ota_file);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isDownloading && !isOTAING) {
                    selectedInfo = (OtaFileInfo) parent.getItemAtPosition(position);
                    isDownloading=true;
                    Log.d(TAG, "onItemClick: " + selectedInfo);
                    try {
                        URL url = UrlUtil.parseUrl(selectedInfo);
                        DownloadFileUtils.download(UserInternetOtaActivity.this, url);
                    } catch (MalformedURLException e) {
                        isDownloading=false;
                        e.printStackTrace();
                    } catch (IOException e) {
                        isDownloading=false;
                        e.printStackTrace();
                    }
                }else{
                    if(isDownloading){
                        showToast("正在下载更新文件.");
                    }else{
                        showToast("正在更新,请稍候!");
                    }
                }
            }
        });

        swip_refresh_layout = findViewById(R.id.userOtaLayout);
        swip_refresh_layout.setColorSchemeResources(R.color.yellow_100,R.color.yellow_200,R.color.yellow_300,R.color.yellow_400,R.color.yellow_500,R.color.yellow_600,R.color.yellow_700,R.color.yellow_800,R.color.yellow_900);
        swip_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.color_00000000);
        swip_refresh_layout.setRefreshing(true);
        swip_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                BleCore.switchNotify(OperateConstant.SERVICE_UUID,OperateConstant.NOTIFICATION_USER_MANUAL_INFO_UUID,true);
                handler.sendEmptyMessageDelayed(Const.SWITCH_NOTIFY_TIMEOUT,500);
                isOtaInfoGod=false;
                swip_refresh_layout.setRefreshing(true);
            }
        });
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_user_internet_ota;
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    public void onMessageEvent(NotifyInfo notifyInfo) {
        if(notifyInfo.getUuid().equalsIgnoreCase(OperateConstant.NOTIFICATION_USER_MANUAL_INFO_UUID)){
            String receivedStr=Util.Hex2Str(notifyInfo.getData());
            if(receivedStr.charAt(0)==(Const.OTA_COMMAND_START_BYTE)){
                String jsonFileAddress=UrlUtil.getJsonFileAddress(receivedStr.substring(1));
                if(jsonFileAddress!=null){
                    isOtaInfoGod=true;
                    downloadJsonFile(jsonFileAddress);
                }
                handler.removeMessages(Const.OTA_INFO_NOTIFY_TIMEOUT);
            }else{
                Log.d(TAG, "onMessageEvent: error:"+receivedStr);
            }
        }
    }

    public Handler getHandler() {
        return handler;
    }

    private void downloadJsonFile(final String netAddress){
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    URL url = null;
                    HttpURLConnection conn = null;
                    InputStream is = null;
                    StringBuilder sb = new StringBuilder();
                    try {
                        url = new URL(netAddress.trim());
                        conn = (HttpURLConnection) url.openConnection();
                        conn.setConnectTimeout(3000);
                        is = conn.getInputStream();
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        while ((len = is.read(bytes)) > 0) {
                            sb.append(new String(bytes, 0, len));
                        }
                        startIndex = 0;
                        mainSb = sb;
                        handler.sendEmptyMessage(Const.HTTP_RESPOND);
                    } catch (Exception e) {

                        handler.sendEmptyMessage(Const.HTTP_NO_RESPOND);
                        e.printStackTrace();
                    } finally {
                        if (conn != null)
                            conn.disconnect();
                        if (is != null) {
                            try {
                                is.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        thread.start();

    }

    private void updateFirmware(String file){
        isOTAING = true;
        opertionTV.setText(getString(R.string.ota_starting));
        Log.e(TAG, "updateFirmware: "+file );
        otasdkUtils.updateFirware(PHYApplication.getApplication().getMac(),file);

    }

    @Override
    public void onError(final int i) {
        isOTAING = false;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                opertionTV.setText(getString(R.string.ota_error,i));
            }
        });
        Log.e(TAG, "onError: "+i );

        PHYApplication.getApplication().setMac(otaMac);
    }

    @Override
    public void onProcess(final float v) {
        Log.e(TAG, "onProcess: "+v );

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                opertionTV.setText(getString(R.string.ota_progress,v));
                bar.setProgress((int) v);
            }
        });
    }

    @Override
    public void onUpdateComplete() {
        isOTAING = false;

        Log.e(TAG, "onUpdateComplete: " );
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                opertionTV.setText(getString(R.string.ota_finish));
                showToast("更新完成");
            }
        });

        PHYApplication.getApplication().setMac(mac);
        PHYApplication.getBandUtil().connectDevice(mac);
        handler.sendEmptyMessageDelayed(Const.READ_VERSION, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(isOTAING){
            showToast("正在更新系统,请稍候.");
        }else{
            finish();
        }
    }
}