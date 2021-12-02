package com.phy.app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.phy.app.R;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.Const;
import com.phy.app.beans.LEDStatus;
import com.warkiz.widget.IndicatorSeekBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Bg5xxTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Bg5xxTestFragment extends Fragment implements IndicatorSeekBar.OnSeekBarChangeListener,View.OnClickListener,RadioGroup.OnCheckedChangeListener{
    private String TAG=getClass().getSimpleName();
    private View root;
    private IndicatorSeekBar  satuBar, brightBar, cctBar,huesBar;
    private TextView huesValue, saturationValue, brightnessValue, cctValue;
    private boolean isHuesRunning, isSaturationRunning, isBrightnessRunning, isCctRunning;
    private Button decBtn, incBtn,huesIncBtn,huesDecBtn,briDecBtn,briIncBtn,saturationIncBtn,saturationDecBtn;
    private RadioGroup radioGroup;
    private RadioButton redBtn, greedBtn, blueBtn;
//    private SeekBar huesBar;
    private int lastMsgWhat=0;
    private static final int HUES_TAG = 1;
    private static final int SATURATION_TAG = 2;
    private static final int BRIGHT_TAG = 3;
    private static final int CCT_TAG = 4;
    private static final int EFFECT_TAG = 5;
    private static final int SPEED_TAG = 7;
    private static final int INTERVAL = 300;
    private ToggleButton powerSwitch;

    private boolean isProgressUpdateByManual = false;
    LEDStatus ledStatus;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Bg5xxTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Bg5xxTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Bg5xxTestFragment newInstance(String param1, String param2) {
        Bg5xxTestFragment fragment = new Bg5xxTestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (ledStatus == null)
                ledStatus = PHYApplication.getLedStatus();
            lastMsgWhat=msg.what;
            if (msg.what == HUES_TAG) {
                if (isProgressUpdateByManual) {
                    ledStatus.setCmd(Const.BG5XX_CMD_MODE_HSI_MODE);
                    ledStatus.setArrowIndex(Const.ARROW_AT_HUES);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                }
                if (isHuesRunning) {
                    handler.sendEmptyMessageDelayed(HUES_TAG, INTERVAL);
                }
            } else if (msg.what == SATURATION_TAG) {
                if (isProgressUpdateByManual) {
                    ledStatus.setCmd(Const.BG5XX_CMD_MODE_HSI_MODE);
                    ledStatus.setArrowIndex(Const.ARROW_AT_SATURATION);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                }
                if (isSaturationRunning) {
                    handler.sendEmptyMessageDelayed(SATURATION_TAG, INTERVAL);
                }
            } else if (msg.what == BRIGHT_TAG) {
                if (isProgressUpdateByManual) {
                    ledStatus.setArrowIndex(Const.ARROW_AT_BRIGHTNESS);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                }
                if (isBrightnessRunning) {
                    handler.sendEmptyMessageDelayed(BRIGHT_TAG, INTERVAL);
                }
            } else if (msg.what == CCT_TAG) {
//                System.out.println(ledStatus);
                if (isProgressUpdateByManual) {
                    ledStatus.setCmd(Const.BG5XX_CMD_MODE_CCT_MODE);
                    ledStatus.setArrowIndex(Const.ARROW_AT_COLOR_TEMPERATURE);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                }
                if (isCctRunning) {
                    handler.sendEmptyMessageDelayed(CCT_TAG, INTERVAL);
                }
            }
            return false;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (ledStatus == null)
            ledStatus = PHYApplication.getLedStatus();
        if (root == null)
            root = inflater.inflate(R.layout.fragment_bg5xx_test, container, false);
        huesBar = root.findViewById(R.id.new_hues_seekbar);
        satuBar = root.findViewById(R.id.new_saturation_seekbar);
        brightBar = root.findViewById(R.id.new_brightness_seekbar);
        cctBar = root.findViewById(R.id.new_colorTemperature_seekbar);

        decBtn = root.findViewById(R.id.cctDecBtn);
        incBtn = root.findViewById(R.id.cctIncBtn);
        radioGroup = root.findViewById(R.id.radioGroup);
        redBtn = root.findViewById(R.id.redButton);
        greedBtn = root.findViewById(R.id.greenButton);
        blueBtn = root.findViewById(R.id.blueButton);
        huesIncBtn=root.findViewById(R.id.hsiIncBtn);
        huesDecBtn=root.findViewById(R.id.hsiDecBtn);
        briDecBtn=root.findViewById(R.id.brightnessDecBtn);
        briIncBtn=root.findViewById(R.id.brightnessIncBtn);
        saturationIncBtn=root.findViewById(R.id.satuIncBtn);
        saturationDecBtn=root.findViewById(R.id.satuDecBtn);
//        powerSwitch=root.findViewById(R.id.power_switch);

        huesValue = root.findViewById(R.id.new_huesValue);
        saturationValue = root.findViewById(R.id.new_saturationValue);
        brightnessValue = root.findViewById(R.id.new_brightnessValue);
        cctValue = root.findViewById(R.id.new_cctValue);


        radioGroup.setOnCheckedChangeListener(this);
        decBtn.setOnClickListener(this);
        incBtn.setOnClickListener(this);
//        huesBar.setOnSeekBarChangeListener(this);
        huesBar.setOnSeekChangeListener(this);
        satuBar.setOnSeekChangeListener(this);
        brightBar.setOnSeekChangeListener(this);
        cctBar.setOnSeekChangeListener(this);
        huesIncBtn.setOnClickListener(this);
        huesDecBtn.setOnClickListener(this);
        briDecBtn.setOnClickListener(this);
        briIncBtn.setOnClickListener(this);
        saturationIncBtn.setOnClickListener(this);
        saturationDecBtn.setOnClickListener(this);
//        powerSwitch.setOnCheckedChangeListener(this);
        updateDisplay(ledStatus);
        return root;
    }

    @Override
    public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
        if (ledStatus == null)
            ledStatus = PHYApplication.getLedStatus();
        if (seekBar.getId() == R.id.new_hues_seekbar) {
            if (isProgressUpdateByManual)
                ledStatus.setRgbMode(true);
            ledStatus.setHues(progress);
            huesValue.setText(progress + "");
        } else if (seekBar.getId() == R.id.new_saturation_seekbar) {
            if (isProgressUpdateByManual)
                ledStatus.setRgbMode(true);
            ledStatus.setSaturation(progress);
            saturationValue.setText(progress + "");
        } else if (seekBar.getId() == R.id.new_brightness_seekbar) {
            ledStatus.setBrightness(progress);
            brightnessValue.setText(progress + "");
        } else if (seekBar.getId() == R.id.new_colorTemperature_seekbar) {
            if (isProgressUpdateByManual){
                radioGroup.clearCheck();
                ledStatus.setRgbMode(false);
            }
            ledStatus.setColorTemperature(progress);
            cctValue.setText(progress * 100 + "");
        }
    }

    @Override
    public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {

    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {
//        isProgressUpdateByManual = true;
//        if (seekBar.getId() == R.id.new_hues_seekbar) {
//            isHuesRunning = true;
//            handler.sendEmptyMessageDelayed(HUES_TAG, INTERVAL);
//        } else if (seekBar.getId() == R.id.new_saturation_seekbar) {
//            isSaturationRunning = true;
//            handler.sendEmptyMessageDelayed(SATURATION_TAG, INTERVAL);
//        } else if (seekBar.getId() == R.id.new_brightness_seekbar) {
//            isBrightnessRunning = true;
//            handler.sendEmptyMessageDelayed(BRIGHT_TAG, INTERVAL);
//        } else if (seekBar.getId() == R.id.new_colorTemperature_seekbar) {
//            isCctRunning = true;
//            radioGroup.clearCheck();
//            handler.sendEmptyMessageDelayed(CCT_TAG, INTERVAL);
//        }
    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
        isProgressUpdateByManual = true;
        if (seekBar.getId() == R.id.new_hues_seekbar) {
            isHuesRunning = false;
            ledStatus.setRgbMode(true);
            handler.sendEmptyMessage(HUES_TAG);
        } else if (seekBar.getId() == R.id.new_saturation_seekbar) {
            isSaturationRunning = false;
            ledStatus.setRgbMode(true);
            handler.sendEmptyMessage(SATURATION_TAG);
        } else if (seekBar.getId() == R.id.new_brightness_seekbar) {
            isBrightnessRunning = false;
            handler.sendEmptyMessage(BRIGHT_TAG);
        } else if (seekBar.getId() == R.id.new_colorTemperature_seekbar) {
            isCctRunning = false;
            ledStatus.setRgbMode(false);
            radioGroup.clearCheck();
            handler.sendEmptyMessage(CCT_TAG);
        }
    }

    public void updateDisplay(LEDStatus ledStatus) {
        if (!isProgressUpdateByManual) {
            if (huesBar != null) {
                huesBar.setProgress(ledStatus.getHues());
                satuBar.setProgress(ledStatus.getSaturation());
                brightBar.setProgress(ledStatus.getBrightness());
                cctBar.setProgress(ledStatus.getColorTemperature());
//                powerSwitch.setChecked(ledStatus.isLedOn());
            }
        }
        isProgressUpdateByManual = false;
    }

    @Override
    public void onClick(View v) {
        int temp=0;
        if (v.getId() == R.id.cctDecBtn) {
            temp = cctBar.getProgress();
            isProgressUpdateByManual = true;
            temp = (temp <= ledStatus.getMinCctValue()) ? temp : (temp - 1);
            cctBar.setProgress(temp);
            handler.sendEmptyMessage(CCT_TAG);
            radioGroup.clearCheck();
        } else if (v.getId() == R.id.cctIncBtn) {
            temp = cctBar.getProgress();
            isProgressUpdateByManual = true;
            temp = (temp >= ledStatus.getMaxCctValue()) ? temp : (temp + 1);
            cctBar.setProgress(temp);
//            System.out.println("value="+cctBar.getProgress());
            handler.sendEmptyMessage(CCT_TAG);
            radioGroup.clearCheck();
        } else if (v.getId() == R.id.hsiIncBtn) {
            temp = huesBar.getProgress();
            isProgressUpdateByManual = true;
            temp=(temp<360)?(temp+1):0;
            huesBar.setProgress(temp);
            handler.sendEmptyMessage(HUES_TAG);
            radioGroup.clearCheck();
        } else if (v.getId() == R.id.hsiDecBtn) {
            temp = huesBar.getProgress();
            isProgressUpdateByManual = true;
            temp=(temp>=1)?(temp-1):360;
            huesBar.setProgress(temp);
            handler.sendEmptyMessage(HUES_TAG);
            radioGroup.clearCheck();
        } else if (v.getId() == R.id.brightnessDecBtn) {
            temp=brightBar.getProgress();
            isProgressUpdateByManual=true;
            if(temp!=0)
                temp--;
            brightBar.setProgress(temp);
            handler.sendEmptyMessage(BRIGHT_TAG);
        } else if (v.getId() == R.id.brightnessIncBtn) {
            temp=brightBar.getProgress();
            isProgressUpdateByManual=true;
            if(temp<100)
                temp++;
            brightBar.setProgress(temp);
            handler.sendEmptyMessage(BRIGHT_TAG);
        } else if (v.getId() == R.id.satuDecBtn) {
            temp=satuBar.getProgress();
            isProgressUpdateByManual=true;
            if(temp!=0)
                temp--;
            satuBar.setProgress(temp);
            if(temp!=100){
                radioGroup.clearCheck();                //清除三色按钮
            }
            handler.sendEmptyMessage(SATURATION_TAG);
        } else if (v.getId() == R.id.satuIncBtn) {
            temp=satuBar.getProgress();
            isProgressUpdateByManual=true;
            if(temp<100)
                temp++;
            satuBar.setProgress(temp);
            if(temp!=100){
                radioGroup.clearCheck();                //清除三色按钮
            }
            handler.sendEmptyMessage(SATURATION_TAG);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        System.out.println("id=" + checkedId);
        if (checkedId == R.id.redButton) {
//            System.out.println("result:"+redBtn.isChecked());
            if (redBtn.isChecked()) {
                isProgressUpdateByManual = true;
                huesBar.setProgress(0);
                handler.sendEmptyMessage(HUES_TAG);
            }
        } else if (checkedId == R.id.greenButton) {
            if (greedBtn.isChecked()) {
                isProgressUpdateByManual = true;
                huesBar.setProgress(120);
                handler.sendEmptyMessage(HUES_TAG);
            }
        } else if (checkedId == R.id.blueButton) {
            if (blueBtn.isChecked()) {
                isProgressUpdateByManual = true;
                huesBar.setProgress(240);
                handler.sendEmptyMessage(HUES_TAG);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            //界面可见
//            System.out.println("\n Bg5xxTestFragment show");
//            if (ledStatus == null)
//                ledStatus = PHYApplication.getLedStatus();
//            updateDisplay(ledStatus);
        } else {
            //界面不可见 相当于onpause
//            System.out.println("\n Bg5xxTestFragment hidden");
        }
    }

//    @Override
//    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if(buttonView.getId()==R.id.power_switch){
//            if (ledStatus == null)
//                ledStatus = PHYApplication.getLedStatus();
//            if(isChecked){
//                ledStatus.setLedOn(true);
//            }else{
//                ledStatus.setLedOn(false);
//            }
//            handler.sendEmptyMessage(lastMsgWhat);
//            isProgressUpdateByManual=true;
//                Log.d(TAG, "onCheckedChanged: "+lastMsgWhat);
//        }
//    }

}