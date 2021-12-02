package com.phy.app.activity;

import static android.text.TextUtils.isEmpty;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.phy.app.R;
import com.phy.app.adapter.FileListAdapter;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.Connect;
import com.phy.app.ble.bean.FirmWareFile;
import com.phy.app.ble.bean.Partition;
import com.phy.app.ble.core.BleCore;
import com.phy.app.util.Utils;
import com.phy.ota.sdk.OTASDKUtils;
import com.phy.ota.sdk.firware.UpdateFirewareCallBack;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * OTANewActivity
 *
 * @author:zhoululu
 * @date:2018/7/25
 */

public class OTANewActivity extends EventBusBaseActivity implements EasyPermissions.PermissionCallbacks, UpdateFirewareCallBack {

    List<String> fileList;
    FileListAdapter fileListAdapter;

//    String path = Environment.getExternalStorageDirectory().getPath();
    private String filePath;

    private TextView opertionTV;
    private ProgressBar bar;

    private OTASDKUtils otasdkUtils;

    private String mac;
    private String otaMac;

    private boolean isOTAING;

    private final int READ_VERSION=1;
    private final int AUTO_EXIT=2;

    private final String[][] alertInformation={{"   更新文件与当前连接产品不匹配,强行更新可能会造成产品无法正常开机,请确定是否继续?","别废话,赶紧!","手抖了,不好意思."},
                                                {"   看在你年幼无知的份上,这次我原谅你的任性.天堂和地狱往往只在一念之间,你确定要继续任由你心中的恶魔支配下去吗?","不成功,便成仁!","算了,回头是岸."},
                                                {"   你的坚持可能会造成无法接受的后果，确定要继续这份坚持吗?","没有撤退可言.","战略性放弃."},
                                                {"   接下来你可能会经历九九八十一难中的第一难-无法开机,是否还要继续?","我是会怕的人?","珍惜美好生活."},
                                                {"   虽说不经风雨就没有彩虹,但这次的选择多半是只有风吹雨打,没有半点收获,还要继续吗?","前方必然阳光明媚.","不想成为落汤鸡."},
                                                {"   少侠,江湖险恶,一步踏错就没有重来一次的机会了,确定继续吗?","不要逼老子开骂.","放弃也是一种勇敢."},
                                                {"   都说人生如戏,可游戏里可以回档,但生活却没有存档.一定要这么坚持吗?","宁可英雄一秒,也不苟活一世.","继续享受人生."},
                                                {"   往前一步是黄昏,永夜将致.退后一步是人生,五彩斑斓.请慎重选择.","就要夜夜夜的黑","保留现状也挺好."},
                                                {"   当你再次选择确定,就可能会面对你想破脑袋,都无法解决的困难,是否继续?","智商180,怕这?","保命重要."},
                                                {"   错误的更新文件,会使本产品再也无法为你的绝世容颜添砖加瓦,锦上添花了,还要继续更新吗?","冒险是值得的.","要啥自行车."},
                                                {"   虽然看起来差不多,但这是别人的女朋友,再动的话十年起步了.你确定要继续吗?","就喜欢别人女友.","看看就好."},
                                                {"   一个萝卜一个坑,每把锁都有自己的钥匙,不要随便试图用你家的钥匙去开别人家的锁.","就插插不走心.","还是原配的好."},
                                                {"   成则春光灿烂,鸟语花香,欣喜若狂.败则白雪茫茫,寒风刺骨,悔不当初.你准备好了吗?","向春天前进.","以不变应万变."},
                                                {"   野花虽比家花香,但路边的野花别乱采,瞬间的快感,可能使你就要和以往正常的生活说再见了.要继续吗?","不撞南墙不回头.","浪子回头金不换."},
                                                {"   没有人知道明天和意外究竟哪一个先来报到,但我能告诉你现在的选择就是意外.不改了吗?","意外惊喜吗?","坐等明天."},
                                                {"   猎人终将成为猎物,太子酒店都被查封了,确定还要继续狩猎寻找野味吗?","一路向西.","知足常乐."},
                                                {"   别闹了,再点错就更新失败,可能要返修,老板要开喷,你我都得要加班了.","关我P事","放你一马."},
                                                };

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == READ_VERSION){
                BleCore.getModelNumber();
                Log.d(getClass().getSimpleName(), " ****正在获取*** ");
                handler.sendEmptyMessageDelayed(AUTO_EXIT, 500);
            }else if(msg.what == AUTO_EXIT){
               finish();                                          //结束当前页面
            }
            return false;
        }
    });

    @Override
    public void initComponent() {
        setTitle(R.string.label_ota);

        opertionTV = findViewById(R.id.current_opertion);
        bar = findViewById(R.id.progress_bar);

        ListView fileListView = findViewById(R.id.file_list);
        fileList = new ArrayList<>();

        fileListAdapter = new FileListAdapter(this, R.layout.item_file_list);
        fileListView.setAdapter(fileListAdapter);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            searchFile();
        } else {
            initRequiredPermission();
        }

        if (PHYApplication.getBandUtil().isOTA()) {
            mac = Utils.getOTAMac(PHYApplication.getApplication().getMac(), -1);
            otaMac = PHYApplication.getApplication().getMac();
        } else {
            otaMac = Utils.getOTAMac(PHYApplication.getApplication().getMac(), 1);
            mac = PHYApplication.getApplication().getMac();
        }

        otasdkUtils = new OTASDKUtils(OTANewActivity.this, this);
        if(isEmpty(PHYApplication.getLedStatus().getModelNumber())&&!PHYApplication.getApplication().getName().contains("OTA")){
            showToast("model No获取失败");
            return;
        }
        fileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!isOTAING) {
//                    filePath = path+"/"+fileList.get(position);
                    filePath = getAssetsCacheFile(fileList.get(position));
//                    Log.e("onItemClick", ": " + "filePath:" + filePath);
                    String fileNo = filePath.substring(filePath.lastIndexOf("/") + 1).substring(0, 6);
                    if (fileNo.contains("(") || fileNo.contains(" ")||fileNo.contains("_")||fileNo.contains("-"))
                        fileNo = fileNo.substring(0, 5);

                    if (PHYApplication.getApplication().getName().contains("OTA") || PHYApplication.getLedStatus().getModelNumber().contains(fileNo)) {
                        if (fileList.get(position).endsWith(".res")) {
                            updateResource(filePath);
                        } else if (fileList.get(position).endsWith(".hex") || fileList.get(position).endsWith(".hexe")) {
                            updateFirmware(filePath);
                        }
                    } else {
                        showToast("更新文件与当前连接产品不匹配");
                        new AlertDialog.Builder(OTANewActivity.this)
                                .setTitle("警告")
                                .setMessage(alertInformation[0][0])
                                .setPositiveButton(alertInformation[0][1], new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                      int index=(1 + (int) (Math.random() * (alertInformation.length - 2 + 1)));            //获取数组下标1~Max的随机数
//                                        int index=new Random().nextInt(alertInformation.length - 2)+1;                    //获取数组下标1~Max的随机数
                                        new AlertDialog.Builder(OTANewActivity.this)
                                                .setTitle("再次警告")
                                                .setMessage(alertInformation[index][0])
                                                .setPositiveButton(alertInformation[index][1], new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        updateFirmware(filePath);
                                                    }
                                                })
                                                .setNegativeButton(alertInformation[index][2], null)
                                                .show();
                                    }
                                })
                                .setNegativeButton(alertInformation[0][2], null)
                                .show();
                    }
                }

            }
        });
    }

    private void updateFirmware(String file){
        isOTAING = true;
        opertionTV.setText(getString(R.string.ota_starting));
        Log.e(TAG, "updateFirmware: "+file );
        otasdkUtils.updateFirware(PHYApplication.getApplication().getMac(),file);
    }

    private void updateResource(final String file){

        FirmWareFile firmWareFile = new FirmWareFile(file);

        StringBuilder sb=new StringBuilder();
//        String message = "";
        for (int i=0;i<firmWareFile.getList().size();i++){
            Partition partition = firmWareFile.getList().get(i);
            sb.append("address:").append(partition.getAddress()).append("\n").append("size:").append(partition.getPartitionLength()).append("\n");
        }
        String message=sb.toString().trim();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //    设置Title的内容
        builder.setTitle("Resource Update");
        //    设置Content来显示一个信息
        builder.setMessage(message);

        //    设置一个PositiveButton
        builder.setPositiveButton(getString(R.string.start_resource_update), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                isOTAING = true;
                opertionTV.setText(getString(R.string.ota_starting));
                Log.e(TAG, "OTAFilename: "+file );
                otasdkUtils.updateResource(PHYApplication.getApplication().getMac(),file);

                dialog.dismiss();
            }
        });

        //Dialog dialog = builder.create();
        //dialog.setCanceledOnTouchOutside(false);

        builder.show();
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
        handler.sendEmptyMessageDelayed(READ_VERSION, 1000);
//        Log.d(getClass().getSimpleName(), " ****500ms获取*** ");
//        Log.d(getClass().getSimpleName(), "更新完成");
//        BleCore.getModelNumber();
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
//                if(f.getName().endsWith(".hex")  || f.getName().endsWith(".hexe")  || f.getName().endsWith(".res")) {
//                    fileList.add(f.getName());
//                    fileListAdapter.setData(fileList);
//                }
//            }
//        }else {
//            showToast("sdcard not found");
//        }

        try{
            String[] filename = getAssets().list("");
            for (int i = 0; i < Objects.requireNonNull(filename).length; i++) {
                if (filename[i].endsWith(".hex") || filename[i].endsWith(".hexe") || filename[i].endsWith(".res")) {
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
    public void onMessageEvent(Connect connect) {
        if(connect.isConnect()){
            Log.e("connect success","connected");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleCore.getModelNumber();
//        Log.d(getClass().getSimpleName(), " onDestory: getName");
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
                }catch (Exception e){
                    throw new RuntimeException("read file error");
                }finally {
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
