package com.phy.app.activity;

import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phy.app.R;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.BleEvent;
import com.phy.app.ble.OperateConstant;
import com.phy.app.ble.bean.Gsonsor;
import com.phy.app.views.GLImage;
import com.phy.app.views.MyGLRenderer2;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * GsensorActivity
 *
 * @author:zhoululu
 * @date:2018/4/15
 */

public class GsensorActivity extends EventBusBaseActivity {

    TextView dataTV;
    LinearLayout layout;
    MyGLRenderer2 myGLRenderer2;
    int y=1000;
    int x=0;
    int z=0;
    int deplay = 500;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){

                myGLRenderer2.modify(x,y,z);

                handler.sendEmptyMessageDelayed(0,deplay);
            }
        }
    };

    @Override
    public void initComponent() {
        setTitle(R.string.label_sensor);

        dataTV = findViewById(R.id.data_text);

        PHYApplication.getBandUtil().startGsensor();

        layout = findViewById(R.id.layout);
        GLImage.load(this.getResources());

        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        myGLRenderer2 = new MyGLRenderer2(this);
        glSurfaceView.setRenderer(myGLRenderer2);

        layout.addView(glSurfaceView);
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_gsensor;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(BleEvent event) {
        if(OperateConstant.START_GSENSOR.equals(event.getOperate())){
            Log.d(TAG, "start gsensor");

            handler.sendEmptyMessageDelayed(0,deplay);
        }else if(OperateConstant.GSENSOR_DATA.equals(event.getOperate())){
            Gsonsor gsonsor = (Gsonsor) event.getObject();
            Log.d(TAG, gsonsor.getX()+"**"+gsonsor.getY()+"**"+gsonsor.getZ());
            dataTV.setText("x:"+gsonsor.getX()+"  y:"+gsonsor.getY()+"  z:"+gsonsor.getZ());
            transData(gsonsor);
        }
    }

    private void transData(Gsonsor gsonsor){

        int sonsorX = trans(gsonsor.getX());
        int sonsorY = trans(gsonsor.getY());
        int sonsorZ = trans(gsonsor.getZ());

        float angleY = (sonsorX - x)*0.09f;
        float angleX = (sonsorY - y)*0.09f;

        if(sonsorX <0 && sonsorY >0){
            myGLRenderer2.setRotateY(angleY);
        }else if(sonsorX<0 && sonsorY<0){
            myGLRenderer2.setRotateY(-angleY);
        }else if(sonsorX>0 && sonsorY<0){
            myGLRenderer2.setRotateY(-angleY);
        }else if(sonsorX>0 && sonsorY>0){
            myGLRenderer2.setRotateY(angleY);
        }

        if(sonsorZ <0 && sonsorY >0){
            myGLRenderer2.setRotateX(-angleX);
        }else if(sonsorZ<0 && sonsorY<0){
            myGLRenderer2.setRotateX(-angleX);
        }else if(sonsorZ>0 && sonsorY<0){
            myGLRenderer2.setRotateX(angleX);
        }else if(sonsorZ>0 && sonsorY>0){
            myGLRenderer2.setRotateX(angleX);
        }

        x = sonsorX;
        y = sonsorY;

        z = sonsorZ;
    }

    public void add(View v){
        myGLRenderer2.setRotateY(10);
    }

    public void add_(View v){
        myGLRenderer2.setRotateY(-10);
    }

    private int trans(int y){
        if(y > 1000){
            return 1000;
        }else if(y < -1000){
            return  -1000;
        }else{
            return y;
        }
    }

    /*@Override
    protected void onResume() {
        super.onResume();

        if(glSurfaceView != null){
            glSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(glSurfaceView != null){
            glSurfaceView.onPause();
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PHYApplication.getBandUtil().stopGsensor();
    }
}
