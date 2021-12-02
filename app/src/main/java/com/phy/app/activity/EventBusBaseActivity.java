package com.phy.app.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.phy.app.beans.Connect;
import com.phy.app.beans.LEDStatus;
import com.phy.app.beans.NotifyInfo;
import com.phy.app.beans.SingleSettingInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * EventBusBaseActivity
 *
 * @author:zhoululu
 * @date:2018/4/13
 */

public abstract class EventBusBaseActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        register(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregister(this);
    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    public void onMessageEvent(LEDStatus ledStatus) {

    }


    @Subscribe(threadMode= ThreadMode.MAIN)
    public void onMessageEvent(SingleSettingInfo settingInfo) {

    }

    @Subscribe(threadMode= ThreadMode.MAIN)
    public void onMessageEvent(NotifyInfo notifyInfo) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(byte[] value){

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Connect connect){

    }

    private void register(Object object){

        if (!EventBus.getDefault().isRegistered(object)) {
            EventBus.getDefault().register(object);

            Log.e(TAG, "register: ");
        }
    }

    private void unregister(Object object){
        if (EventBus.getDefault().isRegistered(object)){
            EventBus.getDefault().unregister(object);

            Log.e(TAG, "unregister");
        }
    }
}
