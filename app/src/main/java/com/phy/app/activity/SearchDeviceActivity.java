package com.phy.app.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.phy.app.R;
import com.phy.app.adapter.DeviceListAdapter;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.Connect;
import com.phy.app.beans.Device;
import com.phy.app.beans.TargetInfo;
import com.phy.app.ble.core.BleCore;
import com.phy.app.util.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * SearchDeviceActivity
 *
 * @author:zhoululu
 * @date:2018/4/13
 */

public class SearchDeviceActivity extends EventBusBaseActivity implements EasyPermissions.PermissionCallbacks,View.OnClickListener{

    private static final int STOP_SEARCH = 100;
    private static final int SYNC_TIME = 200;
    private SwipeRefreshLayout swip_refresh_layout;
    private List<Device> deviceList;
    private boolean isScaning = false;
    private boolean isConnecting = false;
    private TextView searchTV;
    private DeviceListAdapter deviceAdapter;
    private Device device;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == STOP_SEARCH){
                PHYApplication.getBandUtil().stopScanDevice();
                swip_refresh_layout.setRefreshing(false);
                searchTV.setText(getString(R.string.label_start_search));

                isScaning = false;
                if(deviceList.size() < 1){
                    Toast.makeText(SearchDeviceActivity.this,getText(R.string.device_search_fail),Toast.LENGTH_SHORT).show();
                }
            }else if(msg.what == SYNC_TIME){

                PHYApplication.getBandUtil().syncTime(new Date());
                PHYApplication.getApplication().setMac(device.getDevice().getAddress());
                PHYApplication.getApplication().setName(device.getDevice().getName());
//                finish();                                                     //结束当前页面
            }
            return false;
        }
    });

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            if (blueState==BluetoothAdapter.STATE_ON){
                checkSearchDevice();
            }
        }
    };

    void connectListViewItem(int position) {
        device = (Device) deviceAdapter.getItem(position);
        if (!isConnecting) {
            if (!Utils.blutheIsOpen()) {
                showToast(R.string.label_bluetooth_closed);
            } else {
                ArrayMap<String, TargetInfo> devices = PHYApplication.getApplication().getConnectedDevices();
                if (devices.size() == 0 || devices.get(device.getDevice().getAddress()) == null || !devices.get(device.getDevice().getAddress()).isfIsConnected()) {
                    PHYApplication.getBandUtil().connectDevice(device.getDevice().getAddress());
                    isConnecting = true;
                    showToast(R.string.label_connecting);
                }
                devices = null;
            }
        }
    }


    @Override
    public void initComponent() {
        setTitle(R.string.search_title);

        searchTV = toolbar.findViewById(R.id.right_text);
        searchTV.setVisibility(View.VISIBLE);
        searchTV.setOnClickListener(this);

        ListView deviceListView = findViewById(R.id.device_list);
        deviceList = new ArrayList<>();
        deviceAdapter = new DeviceListAdapter(this, R.layout.item_device_list);
//        deviceListView.setBackgroundColor(Color.BLUE);
        deviceListView.setBackgroundColor(0x2b2b2b);
        deviceListView.setAdapter(deviceAdapter);
        swip_refresh_layout = findViewById(R.id.deviceLayout);
        swip_refresh_layout.setColorSchemeResources(R.color.yellow_100,R.color.yellow_200,R.color.yellow_300,R.color.yellow_400,R.color.yellow_500,R.color.yellow_600,R.color.yellow_700,R.color.yellow_800,R.color.yellow_900);
        swip_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.color_00000000);
//        swip_refresh_layout.setBackgroundColor(0xFF0000FF);
        deviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(isScaning){
                    isScaning = false;
                    PHYApplication.getBandUtil().stopScanDevice();
                    handler.removeCallbacksAndMessages(null);
                    searchTV.setText(getString(R.string.label_start_search));
                    swip_refresh_layout.setRefreshing(false);
                }
                connectListViewItem(position);
            }
        });
        deviceListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
                BluetoothDevice target = ((Device) deviceAdapter.getItem((int) info.id)).getDevice();
                ArrayMap<String, TargetInfo> devices= PHYApplication.getApplication().getConnectedDevices();
                if(devices.keySet().contains(target.getAddress())&&devices.get(target.getAddress()).isfIsConnected()){
                    menu.add(0, 0, 0, R.string.disconnect_device);
                }else{
                    menu.add(0, 0, 0, R.string.connect_device);
                }
            }
        });


        swip_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isScaning){
                    searchTV.setText(getString(R.string.label_stop_search));
                    checkSearchDevice();
                }
            }
        });
        swip_refresh_layout.setRefreshing(true);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReceiver,filter);
        checkSearchDevice();
    }

    // 长按菜单响应函数
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        MID = (int) info.id;// 这里的info.id对应的就是数据库中_id的值
        switch (item.getItemId()) {
            case 0:
                // 断开
                Log.d(TAG, "断开连接 +position="+info.id);
//                Log.d(TAG, "onContextItemSelected: "+item.getTitle()+"\t value="+this.getResources().getString(R.string.disconnect_device));
                device = (Device) deviceAdapter.getItem((int) info.id);
                if (item.getTitle().toString().trim().equalsIgnoreCase(this.getResources().getString(R.string.disconnect_device))) {
                    if (!PHYApplication.getBandUtil().disconnectDevice(device.getDevice().getAddress())) {
                        Log.d(TAG, "onContextItemSelected:  disconnect device error");
                    } else {
                        if (PHYApplication.getApplication().getConnectedDevices().containsKey(device.getDevice().getAddress())) {
                            PHYApplication.getApplication().getConnectedDevices().get(device.getDevice().getAddress()).setfIsConnected(false);
                        }
                        deviceAdapter.notifyDataSetChanged();
                    }
                }else{
                    connectListViewItem((int) info.id);
                }
                break;
            default:
                Log.d(TAG, "其它");
                break;
        }
        return super.onContextItemSelected(item);
    }



    @Override
    public int getContentLayout() {
        return R.layout.activity_search_device;
    }

    @Override
    public void onClick(View v) {
        if(!isScaning){
            searchTV.setText(getString(R.string.label_stop_search));
            checkSearchDevice();
        }else {
            isScaning = false;
            searchTV.setText(getString(R.string.label_start_search));
            PHYApplication.getBandUtil().stopScanDevice();
            swip_refresh_layout.setRefreshing(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Device device) {
        Log.e("device",device.getDevice().getAddress() + "#"+device.getDevice().getName());
        if(!deviceList.contains(device) && !TextUtils.isEmpty(device.getDevice().getName())){
            addDevice2List(device);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Connect connect) {
        isConnecting = false;
        if(connect.isConnect()){
            ArrayMap<String, TargetInfo> connectedDevices=PHYApplication.getApplication().getConnectedDevices();
            deviceAdapter.notifyDataSetChanged();
            handler.sendEmptyMessageDelayed(SYNC_TIME,1000);
        }else {
            ArrayMap<String, TargetInfo> connectedDevices=PHYApplication.getApplication().getConnectedDevices();
            deviceAdapter.notifyDataSetChanged();
        }

    }

    private void addDevice2List(Device device){
        deviceList.add(device);
        deviceAdapter.setData(deviceList);
        deviceAdapter.notifyDataSetChanged();
    }


    private void checkSearchDevice(){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            searchDevice();
        }else {
            initRequiredPermission();
        }
    }

    private void searchDevice(){
        if(!Utils.blutheIsOpen()){
            Utils.openBlutheActivity(this);
            return;
        }

        deviceList.clear();
        deviceAdapter.setData(deviceList);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        for(Map.Entry<String,BluetoothGatt> entry:BleCore.gattArrayMap.entrySet()){
//                entry.getValue().getDevice();
//            addDevice2List(new Device(entry.getValue().getDevice(), -10, 0));
            addDevice2List(new Device(entry.getValue().getDevice(),  PHYApplication.getApplication().getConnectedDevices().get(entry.getValue().getDevice().getAddress()).getConnectedRssi(), 0));
        }

        if(!isScaning){

            searchTV.setText(getString(R.string.label_stop_search));

            PHYApplication.getBandUtil().scanDevice();
            isScaning = true;
            swip_refresh_layout.setRefreshing(true);
            handler.sendEmptyMessageDelayed(STOP_SEARCH,10000);
        }
    }

    @AfterPermissionGranted(100)
    private void initRequiredPermission(){
        String[] permissions =new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        boolean hasPermissions = EasyPermissions.hasPermissions(this, permissions);
        if (!hasPermissions) {
            EasyPermissions.requestPermissions(this, getString(R.string.label_location_tips),100, permissions);
        }else {
            searchDevice();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        searchDevice();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        showToast(R.string.label_location_tips);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothReceiver);

        deviceList.clear();
        if(isScaning){
            PHYApplication.getBandUtil().stopScanDevice();
            isScaning = false;
        }

        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if(isConnecting){
            showToast(R.string.label_connecting_tips);
        }else {
            onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && isConnecting) {
            showToast(R.string.label_connecting_tips);
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }



}
