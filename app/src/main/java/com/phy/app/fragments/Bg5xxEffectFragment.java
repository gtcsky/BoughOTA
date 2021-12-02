package com.phy.app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.phy.app.R;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.Const;
import com.phy.app.beans.LEDStatus;
import com.phy.app.java.FragmentListener;
import com.phy.app.util.UserConst;
import com.phy.app.views.SelectorView;
import com.warkiz.widget.IndicatorSeekBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Bg5xxEffectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Bg5xxEffectFragment extends Fragment implements IndicatorSeekBar.OnSeekBarChangeListener,View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private String TAG=getClass().getSimpleName();
    private View root;
    private IndicatorSeekBar huesBar, satuBar, brightBar, cctBar,speedBar,repeatBar;
    private TextView huesValue, saturationValue, brightnessValue, cctValue,speedValue,repeatValue;
    private boolean isHuesRunning, isSaturationRunning, isBrightnessRunning, isCctRunning,isSpeedRunning,isRepeatRunning;
    private ToggleButton toggleButton,powerSwitch;
    private boolean isSelectViewVisible=false;
    private int lastMsgWhat=0;

    private static final int HUES_TAG = 1;
    private static final int SATURATION_TAG = 2;
    private static final int BRIGHT_TAG = 3;
    private static final int CCT_TAG = 4;
    private static final int EFFECT_TAG = 5;
    private static final int REPEAT_TAG = 6;
    private static final int SPEED_TAG = 7;
    private static final int EFFECT_CATEGORY = 8;

    private static final int MUSIC_DEMO=111;
    private static final int INTERVAL = 300;
//    private FragmentListener fragmentListener;

    private SelectorView selectorView;
    List<String> list;

    private boolean isProgressUpdateByManual = false;
    LEDStatus ledStatus;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Bg5xxEffectFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Bg5xxEffectFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Bg5xxEffectFragment newInstance(String param1, String param2) {
        Bg5xxEffectFragment fragment = new Bg5xxEffectFragment();
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
                    ledStatus.setArrowIndex(Const.ARROW_AT_HUES);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                }
                if (isHuesRunning) {
                    handler.sendEmptyMessageDelayed(HUES_TAG, INTERVAL);
                }
            } else if (msg.what == SATURATION_TAG) {
                if (isProgressUpdateByManual) {
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
                    ledStatus.setArrowIndex(Const.ARROW_AT_COLOR_TEMPERATURE);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                }
                if (isCctRunning) {
                    handler.sendEmptyMessageDelayed(CCT_TAG, INTERVAL);
                }
            }else if(REPEAT_TAG==msg.what){
                if (isProgressUpdateByManual) {
                    ledStatus.setArrowIndex(Const.ARROW_AT_PRESET_EFFECT_MODE);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                }
                if(isRepeatRunning){
                    handler.sendEmptyMessageDelayed(REPEAT_TAG, INTERVAL);
                }
            }else if(msg.what==SPEED_TAG){
                if (isProgressUpdateByManual) {
                    ledStatus.setArrowIndex(Const.ARROW_AT_PRESET_EFFECT_MODE);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                }
                if(isSpeedRunning){
                    handler.sendEmptyMessageDelayed(SPEED_TAG, INTERVAL);
                }
            }else if(msg.what==EFFECT_CATEGORY){
                ledStatus.setArrowIndex(Const.ARROW_AT_PRESET_EFFECT_MODE);
                PHYApplication.getBandUtil().userLedSetting(ledStatus);
            }else if(msg.what==EFFECT_TAG){
                ledStatus.setArrowIndex(Const.ARROW_AT_PRESET_EFFECT_MODE);
                PHYApplication.getBandUtil().userLedSetting(ledStatus);
            }else if(MUSIC_DEMO==msg.what){
                Random random=new Random();
                ledStatus.setBrightness(random.nextInt(100));
                ledStatus.setArrowIndex(Const.ARROW_AT_PRESET_EFFECT_MODE);
                PHYApplication.getBandUtil().userLedSetting(ledStatus);
                handler.sendEmptyMessageDelayed(MUSIC_DEMO,50);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_bg5xx_effect, container, false);
        if (ledStatus == null)
            ledStatus = PHYApplication.getLedStatus();
        System.out.println(ledStatus);
        if (root == null)
            root = inflater.inflate(R.layout.fragment_bg5xx_effect, container, false);
        huesBar = root.findViewById(R.id.new_hues_seekbar);
        satuBar = root.findViewById(R.id.new_saturation_seekbar);
        brightBar = root.findViewById(R.id.new_brightness_seekbar);
        cctBar = root.findViewById(R.id.new_colorTemperature_seekbar);
        speedBar = root.findViewById(R.id.speed_seekbar);
        repeatBar = root.findViewById(R.id.repeat_seekbar);
        toggleButton=root.findViewById(R.id.toggleButton);
        powerSwitch=root.findViewById(R.id.power_switch);

        huesValue = root.findViewById(R.id.new_huesValue);
        saturationValue = root.findViewById(R.id.new_saturationValue);
        brightnessValue = root.findViewById(R.id.new_brightnessValue);
        cctValue = root.findViewById(R.id.new_cctValue);
        speedValue= root.findViewById(R.id.new_speedValue);
        repeatValue= root.findViewById(R.id.new_repeatValue);

        huesBar.setOnSeekChangeListener(this);
        satuBar.setOnSeekChangeListener(this);
        brightBar.setOnSeekChangeListener(this);
        cctBar.setOnSeekChangeListener(this);
        speedBar.setOnSeekChangeListener(this);
        repeatBar.setOnSeekChangeListener(this);
        toggleButton.setOnCheckedChangeListener(this);
        powerSwitch.setOnCheckedChangeListener(this);
        updateDisplay(ledStatus);

        selectorView = (SelectorView) root.findViewById(R.id.selector);

        list = new ArrayList<>();
        for (int i = 0; i < UserConst.MAX_VIEW_NO; i++) {
            list.add(i+1+"");
        }

        selectorView.setAdapter(adapter);

        selectorView.setOnItemCheckListener(new SelectorView.OnItemCheckListener() {
            @Override
            public void onItemChecked(int position) {
                Log.i(TAG, "onItemChecked: "+position);
//                show.setText(list.get(position));
//                ledStatus.setCmd(Const.BG5XX_CMD_MODE_PRESET_MODE);
//                ledStatus.setPresetEffectNo(position);
//                handler.sendEmptyMessage(EFFECT_TAG);
//                ledStatus.setCmd(Const.BG5XX_CMD_MODE_PRESET_MODE);
//                ledStatus.setPresetEffectNo(position);
//                handler.sendEmptyMessage(EFFECT_TAG);
            }

            @Override
            public void onScrolled(int position) {
                if (isSelectViewVisible) {
                    Log.i(TAG, "onScrolled: " + position);
//                show.setText(list.get(position));
                    ledStatus.setCmd(Const.BG5XX_CMD_MODE_PRESET_MODE);
                    ledStatus.setPresetEffectNo(position + 1);
                    ledStatus.setMode(position + 1);
                    handler.sendEmptyMessage(EFFECT_TAG);
                }
            }
        });

        return root;
    }

    public void left(View view){
        selectorView.left();
    }

    public void right(View view){
        selectorView.right();
    }

    SelectorView.SeletcorAdapter adapter = new SelectorView.SeletcorAdapter(){
        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public void setView(View view, int position) {
            ((TextView)view).setText(list.get(position));
        }

    };



    @Override
    public void onClick(View v) {

    }

    @Override
    public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
        if (ledStatus == null)
            ledStatus = PHYApplication.getLedStatus();
        if(toggleButton.isChecked()){
            ledStatus.setEffectCategory(Const.BG5XX_CATEGORY_GRADUAL);
        }else{
            ledStatus.setEffectCategory(Const.BG5XX_CATEGORY_FLASH);
        }
        if(isProgressUpdateByManual){
            ledStatus.setCmd(Const.BG5XX_CMD_MODE_CUSTOMIZE_MODE);
//            System.out.println("update cmd by EffectFragment");
        }
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
                ledStatus.setRgbMode(false);
            }
            ledStatus.setColorTemperature(progress);
            cctValue.setText(progress * 100 + "");
        } else if (seekBar.getId() == R.id.speed_seekbar) {
            ledStatus.setEffectFreq(progress);
            speedValue.setText(progress+"");
        } else if (seekBar.getId() == R.id.repeat_seekbar) {
            ledStatus.setEffectRepTimes(progress);
            repeatValue.setText(progress+"");
        }
    }

    @Override
    public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {

    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {
//        ledStatus.setCmd(Const.BG5XX_CMD_MODE_CUSTOMIZE_MODE);
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
//            handler.sendEmptyMessageDelayed(CCT_TAG, INTERVAL);
//        } else if (seekBar.getId() == R.id.speed_seekbar) {
//            isSpeedRunning = true;
//            handler.sendEmptyMessageDelayed(SPEED_TAG, INTERVAL);
//        } else if (seekBar.getId() == R.id.repeat_seekbar) {
//            isRepeatRunning=true;
//            handler.sendEmptyMessageDelayed(REPEAT_TAG, INTERVAL);
//    }
    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
        ledStatus.setCmd(Const.BG5XX_CMD_MODE_CUSTOMIZE_MODE);
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
//            System.out.println("\n1:"+ledStatus);
            handler.sendEmptyMessage(CCT_TAG);
        } else if (seekBar.getId() == R.id.speed_seekbar) {
            isSpeedRunning = false;
            handler.sendEmptyMessage(SPEED_TAG);
        } else if (seekBar.getId() == R.id.repeat_seekbar) {
            isRepeatRunning=false;
            handler.sendEmptyMessage(REPEAT_TAG);
        }
    }

    public void updateDisplay(LEDStatus ledStatus) {
        if (!isProgressUpdateByManual) {
            if (huesBar != null) {
                huesBar.setProgress(ledStatus.getHues());
                satuBar.setProgress(ledStatus.getSaturation());
                brightBar.setProgress(ledStatus.getBrightness());
                cctBar.setProgress(ledStatus.getColorTemperature());
                repeatBar.setProgress(ledStatus.getEffectRepTimes());
                speedBar.setProgress(ledStatus.getEffectFreq());
                powerSwitch.setChecked(ledStatus.isLedOn());
            }
        }
        isProgressUpdateByManual = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
//            Log.d(TAG, "setUserVisibleHint: Resume");
            //界面可见
//            if (ledStatus == null)
//                ledStatus = PHYApplication.getLedStatus();
//            updateDisplay(ledStatus);
//            if(fragmentListener!=null){
//                fragmentListener.fragmentResume();
            isSelectViewVisible=true;
//            }
        } else {
//            Log.d(TAG, "setUserVisibleHint: hidden");
            isSelectViewVisible=false;
//            if(fragmentListener!=null) {
//                fragmentListener.fragmentHidden();
//            }
            //界面不可见 相当于onpause
//            System.out.println("\n Bg5xxEffectFragment hidden");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        System.out.println("onChecked");
            if(buttonView.getId()==R.id.toggleButton){
                if (ledStatus == null)
                    ledStatus = PHYApplication.getLedStatus();
                if(isChecked){
                    ledStatus.setEffectCategory(Const.BG5XX_CATEGORY_GRADUAL);
//                    handler.removeMessages(MUSIC_DEMO);
                }else{
                    ledStatus.setEffectCategory(Const.BG5XX_CATEGORY_FLASH);
//                    handler.sendEmptyMessage(MUSIC_DEMO);
                }
                isProgressUpdateByManual=true;
                handler.sendEmptyMessage(EFFECT_CATEGORY);

//                System.out.println(ledStatus);

            }else if(buttonView.getId()==R.id.power_switch){
                if(isChecked){
                    ledStatus.setLedOn(true);
                }else{
                    ledStatus.setLedOn(false);
                }
                handler.sendEmptyMessage(lastMsgWhat);
                isProgressUpdateByManual=true;
            }
    }

//    public void setFragmentListener(FragmentListener fragmentListener) {
//        this.fragmentListener = fragmentListener;
//    }
}

