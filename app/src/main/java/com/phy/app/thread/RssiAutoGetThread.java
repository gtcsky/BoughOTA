package com.phy.app.thread;
import android.util.Log;

import com.phy.app.app.PHYApplication;

public class RssiAutoGetThread implements Runnable {
    private String TAG = getClass().getSimpleName();

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            PHYApplication.getBandUtil().readRssi();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
//                e.printStackTrace();
                Log.d(TAG, "RssiAutoGetThread stop");
                break;
            }
        }
//        Log.d(TAG, "run: ***************线程终止***************");
    }
}
