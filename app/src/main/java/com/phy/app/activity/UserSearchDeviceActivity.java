package com.phy.app.activity;

import static android.text.TextUtils.isEmpty;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.phy.app.R;
import com.phy.app.adapter.DeviceAdapter;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.Connect;
import com.phy.app.beans.Const;
import com.phy.app.beans.Device;
import com.phy.app.beans.SingleSettingInfo;
import com.phy.app.beans.TargetInfo;
import com.phy.app.ble.core.BleCore;
import com.phy.app.util.Util;
import com.phy.app.thread.RssiAutoGetThread;
import com.phy.app.util.DialogUtils;
import com.phy.app.util.Utils;
import com.phy.app.views.SlideRecyclerView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class UserSearchDeviceActivity extends EventBusBaseActivity implements EasyPermissions.PermissionCallbacks, View.OnClickListener {
    //public class UserSearchDeviceActivity extends EventBusBaseActivity {
    private SlideRecyclerView recycler_view_list;
    private DeviceAdapter mDeviceAdapter;
    private static final int STOP_SEARCH = 100;
    private static final int SYNC_TIME = 200;
    private static final int CONNECT_OVER = 300;
    private SwipeRefreshLayout swipe_refresh_layout;
    private List<Device> deviceList;
    private boolean isScanning = false;
    private boolean isConnecting = false;
    private TextView searchTV;
    private Device device;
    private boolean isSearchDeviceActivityStarting = false;
    private Thread rssiThread;
    private int retryTimes;
    private MotionEvent touchEvent;

    //    private LinearLayout deviceLayout;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == STOP_SEARCH) {
                PHYApplication.getBandUtil().stopScanDevice();
                swipe_refresh_layout.setRefreshing(false);
                searchTV.setText(getString(R.string.label_start_search));

                isScanning = false;
                if (deviceList.size() < 1) {
                    Toast.makeText(UserSearchDeviceActivity.this, getText(R.string.device_search_fail), Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == CONNECT_OVER) {
                if (isConnecting) {
                    if (retryTimes++ >= 4) {
                        BleCore.getModelNumber();
                        handler.sendEmptyMessageDelayed(CONNECT_OVER, 500);
                    } else {
                        Log.d(TAG, "handleMessage: connecting");
                        handler.sendEmptyMessageDelayed(CONNECT_OVER, 500);
                    }
                } else {
                    Log.d(TAG, "handleMessage: connect over");
                    showToast(R.string.label_device_info_over);
                    finish();
                }

            } else if (msg.what == SYNC_TIME) {

                PHYApplication.getBandUtil().syncTime(new Date());
                if (device == null) {
                    device = new Device(BleCore.bluetoothGatt.getDevice(), -40, 0);
                    addDevice2List(device);
                }
                PHYApplication.getApplication().setMac(device.getDevice().getAddress());
                PHYApplication.getApplication().setName(device.getDevice().getName());
                isConnecting = false;
//                finish();                                                     //结束当前页面
            }
            return false;
        }
    });

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            if (blueState == BluetoothAdapter.STATE_ON) {
                checkSearchDevice();
            }
        }
    };


    @Override
    public void initComponent() {
        if (!isSearchDeviceActivityStarting)
            isSearchDeviceActivityStarting = true;
        else
            return;
//        deviceLayout=findViewById(R.id.device_layout);
//        deviceLayout.setOnTouchListener(this);
        Log.d(TAG, "initComponent: *****************");
        rssiThread = new Thread(new RssiAutoGetThread());
        rssiThread.start();
        searchTV = toolbar.findViewById(R.id.right_text);
        searchTV.setVisibility(View.VISIBLE);
        searchTV.setOnClickListener(this);

        recycler_view_list = findViewById(R.id.recycler_view_list);
//        recycler_view_list.setOnTouchListener(this);
        recycler_view_list.setLayoutManager(new LinearLayoutManager(UserSearchDeviceActivity.this, LinearLayoutManager.VERTICAL, false));
        DividerItemDecoration itemDecoration = new DividerItemDecoration(UserSearchDeviceActivity.this, DividerItemDecoration.VERTICAL);
        recycler_view_list.addItemDecoration(itemDecoration);

        //下拉刷新设置
        swipe_refresh_layout = findViewById(R.id.deviceLayout);
        swipe_refresh_layout.setColorSchemeResources(R.color.yellow_100, R.color.yellow_200, R.color.yellow_300, R.color.yellow_400, R.color.yellow_500, R.color.yellow_600, R.color.yellow_700, R.color.yellow_800, R.color.yellow_900);
        swipe_refresh_layout.setProgressBackgroundColorSchemeResource(R.color.color_00000000);
        deviceList = new ArrayList<>();

        mDeviceAdapter = new DeviceAdapter(this, deviceList);
        recycler_view_list.setAdapter(mDeviceAdapter);
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isScanning) {
                    searchTV.setText(getString(R.string.label_stop_search));
                    checkSearchDevice();
                }
            }
        });

        //设置item点击监听器
        mDeviceAdapter.setOnItemClickListener(new DeviceAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                if (isScanning) {
                    isScanning = false;
                    PHYApplication.getBandUtil().stopScanDevice();
                    handler.removeCallbacksAndMessages(null);
                    searchTV.setText(getString(R.string.label_start_search));
                    swipe_refresh_layout.setRefreshing(false);
                }

                ArrayMap<String, TargetInfo> devices = PHYApplication.getApplication().getConnectedDevices();
                String mac = mDeviceAdapter.getItem(position).getDevice().getAddress();
                if (devices.size() != 0 && devices.containsKey(mac)&&devices.get(mac).isfIsConnected()) {
//                    Intent intent = new Intent(UserSearchDeviceActivity.this, DeviceInfoActivity.class);
//                            startActivity(intent);
                    if (!Util.checkIsCanOTA(BleCore.bluetoothGatt)) {
                        showToast("设备不能进行OTA升级");
                        return;
                    }

                    if (isEmpty(PHYApplication.getLedStatus().getModelNumber()) && !PHYApplication.getApplication().getName().contains("OTA")) {
                        BleCore.getModelNumber();
                        showToast("获取产品型号失败,请重试!");
                        return;
                    }
                    if (PHYApplication.getApplication().getConnectedDevices().size() != 1) {

                        DialogUtils.createUserDialog(UserSearchDeviceActivity.this, null, null, getString(R.string.error_title), getString(R.string.single_connect_info), getString(R.string.update_confirm), null, null, null);

                    } else {
                        if(!isConnecting){
                            isScanning=false;
//                            Intent intent = new Intent(UserSearchDeviceActivity.this, UserSmartOtaActivity.class);
////                            startActivity(intent);
//                            startActivityForResult(intent, Const.ACTIVITY_RESULT_OTA);
                            Intent intent=new Intent(UserSearchDeviceActivity.this, UserSmartOtaActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            UserSearchDeviceActivity.this.finish(); // if the activity running has it's own context

                        }
                    }
                } else {
                    connectViewItem(position);
                }

            }
        });

        //左滑断开连接菜单点击监听器
        mDeviceAdapter.setOnDeleteClickListener(new DeviceAdapter.OnDeleteClickLister() {
            @Override
            public void onDeleteClick(View view, int position) {
//                Log.d(TAG, "onItemClick: 点击" + ((TextView) view).getText()+"\tpositon="+position);
                Device device = mDeviceAdapter.getItem(position);

                if (((TextView) view).getText().equals(UserSearchDeviceActivity.this.getResources().getString(R.string.disconnect_device))) {
                    if (!PHYApplication.getBandUtil().disconnectDevice(device.getDevice().getAddress())) {
                        Log.d(TAG, "onContextItemSelected:  disconnect device error");
                    } else {
                        if (PHYApplication.getApplication().getConnectedDevices().containsKey(device.getDevice().getAddress())) {
                            Objects.requireNonNull(PHYApplication.getApplication().getConnectedDevices().get(device.getDevice().getAddress())).setfIsConnected(false);
                        }
                        mDeviceAdapter.notifyDataSetChanged();
//                        mDeviceAdapter.notifyItemRangeChanged(0,position);
                    }
                } else {
                    connectViewItem(position);
                }
                recycler_view_list.closeMenu();         //关闭侧滑菜单
            }
        });
        //左滑连接菜单点击监听器
        mDeviceAdapter.setOnConnectClickListener(new DeviceAdapter.OnConnectClickLister() {
            @Override
            public void onConnectClick(View view, int position) {
                Log.d(TAG, "onItemClick: 点击" + ((TextView) view).getText());
//                Device device = (Device) mDeviceAdapter.getItem(position);
                connectViewItem(position);
                recycler_view_list.closeMenu();         //关闭侧滑菜单
            }
        });

        //设置触摸监听器
        mDeviceAdapter.setOnItemTouchListener(new DeviceAdapter.OnItemTouchListener() {
            @Override
            public void onItemTouch(View view, int pos, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    /**
                     * 点击的开始位置
                     */
                    touchEvent = event;
                }
            }
        });

        //长按弹出菜单监听器
        mDeviceAdapter.setOnItemLongClickListener(new DeviceAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int pos) {
                View menuView = LayoutInflater.from(UserSearchDeviceActivity.this).inflate(R.layout.menu_pop, null, false);
                Button firstBtn = menuView.findViewById(R.id.btn_first);
                Button secondBtn = menuView.findViewById(R.id.btn_second);

                final BluetoothDevice target = (mDeviceAdapter.getItem(pos)).getDevice();
                ArrayMap<String, TargetInfo> devices = PHYApplication.getApplication().getConnectedDevices();
                if (devices.containsKey(target.getAddress()) && devices.get(target.getAddress()).isfIsConnected()) {
                    secondBtn.setVisibility(View.GONE);
                    firstBtn.setVisibility(View.VISIBLE);
                } else {
                    firstBtn.setVisibility(View.GONE);
                    secondBtn.setVisibility(View.VISIBLE);
                }

                final PopupWindow popWindow = new PopupWindow(menuView,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

                popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画


                popWindow.setTouchable(true);                       //点击非PopupWindow区域，PopupWindow会消失，
//                popWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));    //要为popWindow设置一个背景才有效

                //设置popupWindow显示的位置，参数依次是参照View，x轴的偏移量，y轴的偏移量
                if (touchEvent != null) {
                    float offsetX = (touchEvent.getX() >= 300) ? (touchEvent.getX() - 300) : 0;
                    popWindow.showAsDropDown(view, (int) offsetX, 0);
                } else{
                    popWindow.showAsDropDown(view, 400, 0);
                }

                //设置popupWindow里的按钮的事件
                firstBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!PHYApplication.getBandUtil().disconnectDevice(target.getAddress())) {
                            Log.d(TAG, "onContextItemSelected:  disconnect device error");
                        } else {
                            if (PHYApplication.getApplication().getConnectedDevices().containsKey(target.getAddress())) {
                                Objects.requireNonNull(PHYApplication.getApplication().getConnectedDevices().get(target.getAddress())).setfIsConnected(false);
                            }
                            mDeviceAdapter.notifyDataSetChanged();
                        }
                        popWindow.dismiss();
                    }
                });
                secondBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connectViewItem(pos);
                        popWindow.dismiss();
                    }
                });
            }
        });


        swipe_refresh_layout.setRefreshing(true);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBluetoothReceiver, filter);
        checkSearchDevice();

    }

    private void checkSearchDevice() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            PHYApplication.getBandUtil().readRssi();
            searchDevice();
        } else {
            initRequiredPermission();
        }
    }

    private void addDevice2List(Device device) {
        deviceList.add(device);
        mDeviceAdapter.notifyDataSetChanged();
//        mDeviceAdapter.notifyItemRangeChanged(deviceList.size());
//        mDeviceAdapter.notifyItemChanged(deviceList.size()-1);
    }


    private void searchDevice() {
        if (!Utils.blutheIsOpen()) {
            Utils.openBlutheActivity(this);
            return;
        }
//        PHYApplication.getBandUtil().readRssi();
        deviceList.clear();
        mDeviceAdapter.notifyDataSetChanged();

        for (Map.Entry<String, BluetoothGatt> entry : BleCore.gattArrayMap.entrySet()) {
            addDevice2List(new Device(entry.getValue().getDevice(), PHYApplication.getApplication().getConnectedDevices().get(entry.getValue().getDevice().getAddress()).getConnectedRssi(), 0));

        }

        if (!isScanning) {

            searchTV.setText(getString(R.string.label_stop_search));
            PHYApplication.getBandUtil().scanDevice();
            isScanning = true;
            swipe_refresh_layout.setRefreshing(true);
            handler.sendEmptyMessageDelayed(STOP_SEARCH, 10000);
        }
    }

    @AfterPermissionGranted(100)
    private void initRequiredPermission() {
        String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        boolean hasPermissions = EasyPermissions.hasPermissions(this, permissions);
        if (!hasPermissions) {
            EasyPermissions.requestPermissions(this, getString(R.string.label_location_tips), 100, permissions);
        } else {
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
        if (isScanning) {
            PHYApplication.getBandUtil().stopScanDevice();
            isScanning = false;
        }

        handler.removeCallbacksAndMessages(null);

    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isConnecting) {
            showToast(R.string.label_device_info_tips);
            handler.sendEmptyMessageDelayed(CONNECT_OVER, 500);
            retryTimes = 0;
        } else {
            onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && isConnecting) {
            showToast(R.string.label_device_info_tips);
            handler.sendEmptyMessageDelayed(CONNECT_OVER, 500);
            retryTimes = 0;
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Device device) {
        Log.e("device", device.getDevice().getAddress() + "#" + device.getDevice().getName());
        if (!deviceList.contains(device) && !TextUtils.isEmpty(device.getDevice().getName())) {
            addDevice2List(device);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(byte[] value) {
        Log.d(TAG, "onMessageEvent: " + Util.hex2AsciiStr(value));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SingleSettingInfo settingInfo) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ArrayMap<String, TargetInfo> connectedDevices) {
        for (Device device : deviceList) {
            String mac = device.getDevice().getAddress().trim();
            if (connectedDevices.containsKey(mac)) {
                device.setRssi(Objects.requireNonNull(connectedDevices.get(mac)).getConnectedRssi());
            }
        }
        mDeviceAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Connect connect) {
        if (connect.isConnect()) {
//            ArrayMap<String, TargetInfo> connectedDevices= PHYApplication.getApplication().getConnectedDevices();
            mDeviceAdapter.notifyDataSetChanged();
            handler.sendEmptyMessageDelayed(SYNC_TIME, 1000);
        } else {
//            ArrayMap<String, TargetInfo> connectedDevices= PHYApplication.getApplication().getConnectedDevices();
            mDeviceAdapter.notifyDataSetChanged();
            isConnecting = false;
        }

    }


    @Override
    public int getContentLayout() {
        return R.layout.activity_user_search_device;
    }

    @Override
    public void onClick(View v) {
        if (!isScanning) {
            searchTV.setText(getString(R.string.label_stop_search));
            checkSearchDevice();
        } else {
            isScanning = false;
            searchTV.setText(getString(R.string.label_start_search));
            PHYApplication.getBandUtil().stopScanDevice();
            swipe_refresh_layout.setRefreshing(false);
        }
    }

    void connectViewItem(int position) {
        device = mDeviceAdapter.getItem(position);
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
//                devices = null;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        rssiThread.interrupt();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Const.ACTIVITY_RESULT_OTA) {
            if (!isScanning)
                initComponent();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if(!isScanning)
//            searchDevice();
//        if(!isScanning)
//            checkSearchDevice();
        if (rssiThread.getState() == Thread.State.TERMINATED) {
            rssiThread = new Thread(new RssiAutoGetThread());
            rssiThread.start();
//            Log.d(TAG, "onResume: restart read rssi");
        }
    }


    public void stopRssiThread() {
        rssiThread.interrupt();
    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        Log.d(TAG, "onTouch: ****************");
//        switch (event.getAction()) {
//            /**
//             * 点击的开始位置
//             */
//            case MotionEvent.ACTION_DOWN:
////                tvTouchShowStart.setText("起始位置：(" + event.getX() + "," + event.getY());
//                Log.d(TAG, "onTouch: x="+event.getX()+"\t"+"y="+event.getY());
//                break;
//            /**
//             * 触屏实时位置
//             */
//            case MotionEvent.ACTION_MOVE:
////                tvTouchShow.setText("实时位置：(" + event.getX() + "," + event.getY());
//                break;
//            /**
//             * 离开屏幕的位置
//             */
//            case MotionEvent.ACTION_UP:
////                tvTouchShow.setText("结束位置：(" + event.getX() + "," + event.getY());
//                break;
//            default:
//                break;
//        }
//        /**
//         *  注意返回值
//         *  true：view继续响应Touch操作；
//         *  false：view不再响应Touch操作，故此处若为false，只能显示起始位置，不能显示实时位置和结束位置
//         */
//        return true;
//    }
}