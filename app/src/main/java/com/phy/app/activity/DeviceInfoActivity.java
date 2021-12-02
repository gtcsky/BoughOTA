package com.phy.app.activity;


import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.phy.app.R;
import com.phy.app.adapter.CommonExpandableListAdapter;
import com.phy.app.beans.Connect;
import com.phy.app.beans.Device;
import com.phy.app.beans.NotifyInfo;
import com.phy.app.ble.core.BleCore;
import com.phy.app.util.CharacteristicPropertiesUtil;
import com.phy.app.util.Util;
import com.phy.app.util.UuidUtils;
import com.phy.app.fragments.DownloadFragment;
import com.phy.app.fragments.UploadFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

public class DeviceInfoActivity extends EventBusBaseActivity implements View.OnClickListener {
    ExpandableListView servicesListView;
    private DialogFragment currentFragment;
    private TextView settingTV;
    private CommonExpandableListAdapter<BluetoothGattCharacteristic, BluetoothGattService> commonExpandableListAdapter;
    private HashMap<String,Boolean> notifyMap=new HashMap();
    private boolean isOn;
    private DownloadFragment downloadFragment;


    @Override
    public void initComponent() {
        setTitle(R.string.service_title);
//        myEventBus=new EventBus();
        settingTV = toolbar.findViewById(R.id.right_text);
        settingTV.setVisibility(View.VISIBLE);
        settingTV.setOnClickListener(this);
        settingTV.setText(getString(R.string.label_4spaces));
        BleCore.enableUserManualInfoNotifications();
        servicesListView=findViewById(R.id.services_list);
        servicesListView.setGroupIndicator(null);  //去除默认箭头
        commonExpandableListAdapter=new CommonExpandableListAdapter<BluetoothGattCharacteristic, BluetoothGattService>(this,R.layout.item_characteristic_list,R.layout.item_service_list) {
            @Override
            protected void getChildView(ViewHolder holder, final int groupPosition, int childPosition, boolean isLastChild, final BluetoothGattCharacteristic characteristic) {
                TextView characteristicNameView = holder.getView(R.id.characteristic_name_text);
                characteristicNameView.setText(UuidUtils.getCharacterName(characteristic.getUuid()));
                TextView characteristicIdView = holder.getView(R.id.characteristic_id_text);
//                characteristicIdView.setText("UUID:"+characteristic.getUuid().toString());
                characteristicIdView.setText(String.format(getString(R.string.set_text_concat),"UUID:",characteristic.getUuid().toString()));
                TextView characteristicPropertiesView = holder.getView(R.id.properties_text);
                List<String> propertiesNames=CharacteristicPropertiesUtil.getPropertiesName(characteristic.getProperties());
                if(propertiesNames!=null){
                    characteristicPropertiesView.setText(String.format(getString(R.string.set_text_concat),"Properties:",propertiesNames.toString()));
                    ImageView downView=holder.getView(R.id.download_view);
                    ImageView upView=holder.getView(R.id.upload_view);
                    final ImageView notifyView=holder.getView(R.id.notify_view);
                    if (propertiesNames.contains("Read")) {
                        downView.setVisibility(View.VISIBLE);
                    } else {
                        downView.setVisibility(View.GONE);
                    }
                    if (propertiesNames.contains("Write")) {

                        upView.setVisibility(View.VISIBLE);
                    } else {

                        upView.setVisibility(View.GONE);
                    }

                    if (propertiesNames.contains("Notify")) {
                        notifyMap.put(characteristic.getUuid().toString(),false);
                        notifyView.setVisibility(View.VISIBLE);
                    } else {
                        notifyView.setVisibility(View.GONE);
                    }
                    downView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downloadFragment= DownloadFragment.newInstance();
                            downloadFragment.show(getSupportFragmentManager(),"");
                            downloadFragment.setOnActionClickListener(new DownloadFragment.OnActionClickListener() {
                                @Override
                                public void onActionClick(View v) {
                                    if(characteristic!=null)
                                        BleCore.bluetoothGatt.readCharacteristic(characteristic);
                                }
                            });
                            currentFragment=downloadFragment;
                        }
                    });

                    upView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final UploadFragment fragment= UploadFragment.newInstance();
                            fragment.show(getSupportFragmentManager(),"");
                            fragment.setOnActionClickListener(new UploadFragment.OnActionClickListener() {
                                @Override
                                public void onActionClick(View v) {
                                    try {
                                        byte[] value = fragment.getEditAreaContent();
                                        if (value != null) {
                                            characteristic.setValue(value);
                                            BleCore.bluetoothGatt.writeCharacteristic(characteristic);
                                        }
                                    } catch (Exception e) {
                                        showToast(e.getMessage());
                                    }
                                }
                            });
                            Log.d(TAG, "onClick: uploadView");
                            currentFragment=fragment;
                        }
                    });

                    notifyView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String uuid=characteristic.getUuid().toString().trim();
                            boolean result=notifyMap.get(uuid);
                            if(result){
                                notifyView.setColorFilter(getResources().getColor(R.color.grey_500));   //修改前景图片颜色即android:src的图片,对背景图片无效
                                notifyMap.put(uuid,false);
                                BleCore.switchNotify(characteristic,false);
                            }else{
                                notifyView.setColorFilter(getResources().getColor(R.color.white));      //修改前景图片颜色即android:src的图片,对背景图片无效
                                notifyMap.put(uuid,true);
                                BleCore.switchNotify(characteristic,true);
                            }

                        }
                    });
                }
            }

            @Override
            protected void getGroupView(ViewHolder holder, int groupPosition, boolean isExpanded, BluetoothGattService service) {
                TextView serviceNameView=holder.getView(R.id.service_name_text);
                serviceNameView.setText(UuidUtils.getServiceName(service.getUuid()));
                TextView serviceIdView=holder.getView(R.id.service_id_txt);
//                serviceIdView.setText("UUID:"+service.getUuid().toString());
                serviceIdView.setText(String.format(getString(R.string.set_text_concat),"UUID:",service.getUuid().toString()));

                TextView serviceTypeView=holder.getView(R.id.service_type);
                if (service.getType() == 0) {
                    serviceTypeView.setText(getString(R.string.primary_service));
                } else {
                    serviceTypeView.setText(getString(R.string.secondary_service));
                }
                ImageView arrowImage=holder.getView(R.id.groupIcon);
                arrowImage.setImageResource(isExpanded?R.mipmap.ic_up_white:R.mipmap.ic_down_white);

            }
        };
        servicesListView.setAdapter(commonExpandableListAdapter);
        List<BluetoothGattService> services= BleCore.bluetoothGatt.getServices();
        for(BluetoothGattService service:services){
            commonExpandableListAdapter.getGroupData().add(service);
            commonExpandableListAdapter.getChildrenData().add(service.getCharacteristics());
        }
        commonExpandableListAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Device device) {
//        Log.e("device",device.getDevice().getAddress() + "#"+device.getDevice().getName());
//        if(!deviceList.contains(device) && !TextUtils.isEmpty(device.getDevice().getName())){
//            addDevice2List(device);
//        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(byte[] value) {
//        if (currentFragment instanceof DownloadFragment) {
//            if (value != null){
//                try {
//                    ((DownloadFragment) currentFragment).updateEditArea(value);
//                }catch (Exception e){
//                    showToast(e.getMessage());
//                }
//            }
//        } else if (currentFragment instanceof UploadFragment) {
//
//            ((UploadFragment) currentFragment).updateNotifyInfo(value);
//            Toast.makeText(this, Util.hex2AsciiStr(value) + System.lineSeparator() + Util.Hex2Str(value), Toast.LENGTH_SHORT).show();
//        } else if (currentFragment!=null) {
//            Log.d(TAG, "DialogFragment");
//        }
//    }


    @Subscribe(threadMode= ThreadMode.MAIN)
    public void onMessageEvent(NotifyInfo notifyInfo) {
        if (currentFragment instanceof DownloadFragment) {
            if (notifyInfo.getData() != null){
                try {
                    ((DownloadFragment) currentFragment).updateEditArea(notifyInfo);
                }catch (Exception e){
                    showToast(e.getMessage());
                }
            }
        } else if (currentFragment instanceof UploadFragment) {

            ((UploadFragment) currentFragment).updateNotifyInfo(notifyInfo);
            Toast.makeText(this, Util.hex2AsciiStr(notifyInfo.getData()) + System.lineSeparator() + Util.Hex2Str(notifyInfo.getData()), Toast.LENGTH_SHORT).show();
        } else if (currentFragment!=null) {
            Log.d(TAG, "DialogFragment");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Connect connect) {
        if(connect!=null){
            if(!connect.isConnect())
                finish();
            Log.d(getClass().getSimpleName(), "disconnect ");
        }

    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_device_info;
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.right_text){
            Intent intent=new Intent(this,UpdateSettingActivity.class);
            startActivity(intent);
        }
    }
}