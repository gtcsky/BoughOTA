package com.phy.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.phy.app.R;

/**
 * SplashActivity
 *
 * @author:zhoululu
 * @date:2018/4/13
 */

public class SplashActivity extends AppCompatActivity {

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 1){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                finish();
            }
            return false;
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        handler.sendEmptyMessageDelayed(1,2000);
    }


}
