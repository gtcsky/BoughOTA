package com.phy.app.activity;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.phy.app.R;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.Const;
import com.phy.app.beans.LEDStatus;
import com.warkiz.widget.IndicatorSeekBar;

/**
 * LEDActivity
 *
 * @author:zhoululu
 * @date:2018/4/15
 */

public class LEDActivity extends EventBusBaseActivity implements IndicatorSeekBar.OnSeekBarChangeListener{

    private IndicatorSeekBar huesBar,satuBar,brightBar,cctBar,effectBar,speedBar;
    private TextView huesValue,saturationValue,brightnessValue,cctValue,effectValue,speedValue;
    private int huesData,saturationData,brightnessData,cctData,effectData,speedData;
    private boolean isHuesRunning,isSaturationRunning,isBrightnessRunning,isCctRunning,isEffectRunning,isSpeedRunning;

    private static final int HUES_TAG = 1;
    private static final int SATURATION_TAG = 2;
    private static final int BRIGHT_TAG = 3;
    private static final int CCT_TAG = 4;
    private static final int EFFECT_TAG = 5;
    private static final int SPEED_TAG = 6;
    private static final int INTERVAL = 300;

    private LEDStatus ledStatus;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == HUES_TAG){
                PHYApplication.getBandUtil().userLedSetting(ledStatus);
                if(isHuesRunning){
                    handler.sendEmptyMessageDelayed(HUES_TAG,INTERVAL);
                }
            }else if(msg.what == SATURATION_TAG){
                PHYApplication.getBandUtil().userLedSetting(ledStatus);
                if(isSaturationRunning){
                    handler.sendEmptyMessageDelayed(SATURATION_TAG,INTERVAL);
                }
            }else if(msg.what == BRIGHT_TAG){
                PHYApplication.getBandUtil().userLedSetting(ledStatus);
                if(isBrightnessRunning){
                    handler.sendEmptyMessageDelayed(BRIGHT_TAG,INTERVAL);
                }
            }else if(msg.what == CCT_TAG){
                PHYApplication.getBandUtil().userLedSetting(ledStatus);
                if(isCctRunning){
                    handler.sendEmptyMessageDelayed(CCT_TAG,INTERVAL);
                }
            }else if(msg.what == EFFECT_TAG){
                PHYApplication.getBandUtil().userLedSetting(ledStatus);
                if(isEffectRunning){
                    handler.sendEmptyMessageDelayed(EFFECT_TAG,INTERVAL);
                }
            }else if(msg.what == SPEED_TAG){
                PHYApplication.getBandUtil().userLedSetting(ledStatus);
                if(isSpeedRunning){
                    handler.sendEmptyMessageDelayed(SPEED_TAG,INTERVAL);
                }
            }
        }
    };

    @Override
    public void initComponent() {
        setTitle(R.string.label_console);

        ledStatus=new LEDStatus();

        huesBar = findViewById(R.id.hues_seekbar);
        satuBar = findViewById(R.id.saturation_seekbar);
        brightBar = findViewById(R.id.brightness_seekbar);
        cctBar = findViewById(R.id.colorTemperature_seekbar);
        effectBar = findViewById(R.id.effect_seekbar);
        speedBar = findViewById(R.id.speed_seekbar);

        huesValue = findViewById(R.id.huesValue);
        saturationValue = findViewById(R.id.saturationValue);
        brightnessValue = findViewById(R.id.brightnessValue);
        cctValue = findViewById(R.id.cctValue);
        effectValue = findViewById(R.id.effectValue);
        speedValue = findViewById(R.id.speedValue);

        cctBar.setProgress(ledStatus.getColorTemperature());
        huesBar.setProgress(ledStatus.getHues());
        brightBar.setProgress(ledStatus.getBrightness());
        satuBar.setProgress(ledStatus.getSaturation());
        speedBar.setProgress(ledStatus.getEffectSpeed());
        effectBar.setProgress(ledStatus.getRgbEffectNo());
        huesValue.setText(ledStatus.getHues()+"");
        saturationValue.setText(ledStatus.getSaturation()+"");
        brightnessValue.setText(ledStatus.getBrightness()+"");
        cctValue.setText(ledStatus.getColorTemperature()*100+"");

        effectValue.setText(ledStatus.getRgbEffectNo()+"");
        setBarInfo(effectBar,Const.MIN_RGB_STYLE_NO,Const.MAX_RGB_STYLE_NO,ledStatus.getRgbEffectNo());
        speedValue.setText(ledStatus.getEffectSpeed()+"");

        huesBar.setOnSeekChangeListener(this);
        brightBar.setOnSeekChangeListener(this);
        satuBar.setOnSeekChangeListener(this);
        cctBar.setOnSeekChangeListener(this);
        effectBar.setOnSeekChangeListener(this);
        speedBar.setOnSeekChangeListener(this);

    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_led;
    }

    public void setBarInfo(IndicatorSeekBar bar, int min, int max, int progress){
        if(null==bar)
            return;
        int oriMin=bar.getProgress();
        bar.setVisibility(android.view.View.INVISIBLE);
        if (progress<oriMin){
            bar.setMin(min);
            bar.setProgress(min);
            bar.setMax(max);
            bar.setProgress(progress);
        }else{
            bar.setMax(max);
            bar.setProgress(max);
            bar.setMin(min);
            bar.setProgress(progress);
        }
        bar.setVisibility(android.view.View.VISIBLE);
    }

    @Override
    public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
        if (seekBar.getId() == R.id.hues_seekbar) {
//            huesData = progress;
            ledStatus.setRgbMode(true);
            ledStatus.setHues(progress);
            setBarInfo(effectBar,Const.MIN_RGB_STYLE_NO,Const.MAX_RGB_STYLE_NO,ledStatus.getRgbEffectNo());
            huesValue.setText(progress+"");
        } else if (seekBar.getId() == R.id.saturation_seekbar) {
            ledStatus.setRgbMode(true);
            ledStatus.setSaturation(progress);
            setBarInfo(effectBar,Const.MIN_RGB_STYLE_NO,Const.MAX_RGB_STYLE_NO,ledStatus.getRgbEffectNo());
            saturationValue.setText(progress+"");
        } else if (seekBar.getId() == R.id.brightness_seekbar) {
            brightnessData = progress;
            ledStatus.setBrightness(progress);
            brightnessValue.setText(progress+"");
        } else if (seekBar.getId() == R.id.colorTemperature_seekbar) {
            cctData = progress;
            ledStatus.setRgbMode(false);
            ledStatus.setColorTemperature(progress);
            cctValue.setText(progress*100+"");
            int cctNo=ledStatus.getCctEffectNo();
            if(cctNo<Const.MIN_COLOR_TEMP_STYLE_NO){
                cctNo=Const.MIN_COLOR_TEMP_STYLE_NO;
                ledStatus.setCctEffectNo(cctNo);
            }
            Log.d(getClass().getSimpleName(),"");
//            System.out.println("no:"+cctNo);
            setBarInfo(effectBar,Const.MIN_COLOR_TEMP_STYLE_NO,Const.MAX_COLOR_TEMP_STYLE_NO,cctNo);
            effectBar.setProgress(cctNo);
            effectValue.setText(cctNo-Const.MIN_COLOR_TEMP_STYLE_NO +"");
        } else if (seekBar.getId() == R.id.effect_seekbar) {
            if(ledStatus.isRgbMode()){
                ledStatus.setRgbEffectNo(progress);
                effectValue.setText(progress+"");
            }else{
                ledStatus.setCctEffectNo(progress);
//                System.out.println("progress:"+progress);
//                System.out.println("min:"+effectBar.getMin());
                if(progress>=Const.MIN_COLOR_TEMP_STYLE_NO)
                    effectValue.setText(progress- Const.MIN_COLOR_TEMP_STYLE_NO +"");
                else
                    effectValue.setText(progress+"");
            }
        } else if (seekBar.getId() == R.id.speed_seekbar) {
                ledStatus.setEffectSpeed(progress);
                speedValue.setText(progress+"");
        }
    }

    @Override
    public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {
    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {
        if(seekBar.getId() == R.id.hues_seekbar){
            isHuesRunning = true;
            handler.sendEmptyMessageDelayed(HUES_TAG,INTERVAL);
        }else if(seekBar.getId() == R.id.saturation_seekbar){
            isSaturationRunning = true;
            handler.sendEmptyMessageDelayed(SATURATION_TAG,INTERVAL);
        }else if(seekBar.getId() == R.id.brightness_seekbar){
            isBrightnessRunning = true;
            handler.sendEmptyMessageDelayed(BRIGHT_TAG,INTERVAL);
        }else if(seekBar.getId() == R.id.colorTemperature_seekbar){
            isCctRunning = true;
            handler.sendEmptyMessageDelayed(CCT_TAG,INTERVAL);
        }else if(seekBar.getId() == R.id.effect_seekbar){
            isEffectRunning = true;
            handler.sendEmptyMessageDelayed(EFFECT_TAG,INTERVAL);
        }else if(seekBar.getId() == R.id.speed_seekbar){
            isSpeedRunning = true;
            handler.sendEmptyMessageDelayed(SPEED_TAG,INTERVAL);
        }
    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

        if(seekBar.getId() == R.id.hues_seekbar){
            isHuesRunning = false;
            handler.sendEmptyMessage(HUES_TAG);
        }else if(seekBar.getId() == R.id.saturation_seekbar){
            isSaturationRunning = false;
            handler.sendEmptyMessage(SATURATION_TAG);
        }else if(seekBar.getId() == R.id.brightness_seekbar){
            isBrightnessRunning = false;
            handler.sendEmptyMessage(BRIGHT_TAG);
        }else if(seekBar.getId() == R.id.colorTemperature_seekbar){
            isCctRunning = false;
            handler.sendEmptyMessage(CCT_TAG);
        }else if(seekBar.getId() == R.id.effect_seekbar){
            isEffectRunning = false;
            handler.sendEmptyMessage(EFFECT_TAG);
        }else if(seekBar.getId() == R.id.speed_seekbar){
            isSpeedRunning = false;
            handler.sendEmptyMessage(SPEED_TAG);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

       handler.removeCallbacksAndMessages(null);
    }
}
