package com.phy.app.ble.bean;

import android.text.TextUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Message
 *
 * @author:zhoululu
 * @date:2018/4/19
 */

public class Message {

    private String title;
    private String msg;
    private List<byte[]> msgList = new ArrayList<>();

    public Message(String title, String msg) {
        this.title = title+"\0";

        if(!TextUtils.isEmpty(msg)){
            this.msg = msg+"\0";
        }

        genMsgList();
    }

    private void genMsgList(){
        msgList.add(title.getBytes(Charset.forName("UTF-8")));
        if (!TextUtils.isEmpty(msg)){
            genMsgList(msg);
        }
    }

    private void genMsgList(String message) {

        byte[] msgBytes = message.getBytes(Charset.forName("UTF-8"));

        int index = msgBytes.length % 15 == 0 ? msgBytes.length / 15 : msgBytes.length / 15 + 1;

        for (int i = 1; i <= index; i++) {
            if (i * 15 <= msgBytes.length) {
                byte[] apduByte = new byte[15];
                System.arraycopy(msgBytes,(i - 1) * 15,apduByte,0,apduByte.length);
                msgList.add(apduByte);
            } else {
                byte[] apduByte = new byte[15 - (i*15 - msgBytes.length)];
                System.arraycopy(msgBytes,(i - 1) * 15,apduByte,0,apduByte.length);
                msgList.add(apduByte);
            }
        }
    }

    public List<byte[]> getMsgList() {
        return msgList;
    }
}
