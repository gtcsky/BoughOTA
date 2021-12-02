package com.phy.app.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.phy.app.R;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.BleEvent;
import com.phy.app.ble.OperateConstant;
import com.phy.app.ble.bean.MessageType;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * PushActivity
 *
 * @author:zhoululu
 * @date:2018/4/15
 */

public class PushActivity extends EventBusBaseActivity {

    private boolean isSendCall = false;
    private EditText titleEdit,messageEdit;
    private String msg,title;

    @Override
    public void initComponent() {
        setTitle(R.string.label_push);

        titleEdit = findViewById(R.id.message_title);
        messageEdit = findViewById(R.id.message_content);
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_push;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BleEvent event) {
        if(OperateConstant.SEND_MESSAGE.equals(event.getOperate())){
            showToast(R.string.send_success_tips);
        }
    }

    public void inCome(View view){
        if(isSendCall){
            return;
        }
        PHYApplication.getBandUtil().sendMsg("phone","", MessageType.PHONEIN);
        isSendCall = true;
    }

    public void pickUp(View view){
        if(!isSendCall){
            showToast(R.string.no_call_tips);
            return;
        }
        PHYApplication.getBandUtil().sendMsg("phone","", MessageType.PHONEEND);
        isSendCall = false;
    }

    private boolean checkMsg(){
        msg = messageEdit.getText().toString();
        title = titleEdit.getText().toString();

        if(TextUtils.isEmpty(title)){
            showToast(R.string.title_null_tips);
            return false;
        }

        if(TextUtils.isEmpty(msg)){
            showToast(R.string.message_null_tips);
            return false;
        }

        return true;
    }

    public void message(View view){

        if(!checkMsg()){
            return;
        }

        PHYApplication.getBandUtil().sendMsg(title,msg, MessageType.MESSAGE);
    }

    public void wx(View view){

        if(!checkMsg()){
            return;
        }

        PHYApplication.getBandUtil().sendMsg(title,msg, MessageType.WX);
    }

}
