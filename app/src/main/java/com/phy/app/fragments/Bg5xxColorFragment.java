package com.phy.app.fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phy.app.R;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.Const;
import com.phy.app.beans.LEDStatus;
import com.phy.app.views.ColorView;
import com.warkiz.widget.IndicatorSeekBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Bg5xxColorFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Bg5xxColorFragment extends Fragment implements IndicatorSeekBar.OnSeekBarChangeListener,ColorView.ColorChangeListener {
    private View root;
    private IndicatorSeekBar huesBar, satuBar, brightBar, cctBar;
    private TextView huesValue, saturationValue, brightnessValue, cctValue;
    private boolean isHuesRunning, isSaturationRunning, isBrightnessRunning, isCctRunning;
    private ColorView colorView;
    private static final int HUES_TAG = 1;
    private static final int SATURATION_TAG = 2;
    private static final int BRIGHT_TAG = 3;
    private static final int CCT_TAG = 4;
    private static final int EFFECT_TAG = 5;
    private static final int SPEED_TAG = 7;
    private static final int INTERVAL = 300;

    private boolean isProgressUpdateByManual = false;
    private boolean isUpdatedByColorPicker;
    LEDStatus ledStatus;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Bg5xxColorFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Bg5xxColorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Bg5xxColorFragment newInstance(String param1, String param2) {
        Bg5xxColorFragment fragment = new Bg5xxColorFragment();
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
//            System.out.println("out:"+ledStatus);
            if (msg.what == HUES_TAG) {
                if (isProgressUpdateByManual) {
                    ledStatus.setCmd(Const.BG5XX_CMD_MODE_HSI_MODE);
                    ledStatus.setArrowIndex(Const.ARROW_AT_HUES);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                    if(!isUpdatedByColorPicker)
                        colorView.setSelectedColor(ledStatus.getHues(),ledStatus.getSaturation()*0.01F);
                }
                if (isHuesRunning) {
                    handler.sendEmptyMessageDelayed(HUES_TAG, INTERVAL);
                }
            } else if (msg.what == SATURATION_TAG) {
                if (isProgressUpdateByManual) {
                    ledStatus.setCmd(Const.BG5XX_CMD_MODE_HSI_MODE);
                    ledStatus.setArrowIndex(Const.ARROW_AT_SATURATION);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                    if(!isUpdatedByColorPicker)
                        colorView.setSelectedColor(ledStatus.getHues(),ledStatus.getSaturation()*0.01F);
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
                if (isProgressUpdateByManual) {
                    ledStatus.setCmd(Const.BG5XX_CMD_MODE_CCT_MODE);
                    ledStatus.setArrowIndex(Const.ARROW_AT_COLOR_TEMPERATURE);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                    colorView.clearSelected();
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
            root = inflater.inflate(R.layout.fragment_bg5xx_color, container, false);
        huesBar = root.findViewById(R.id.new_hues_seekbar);
        satuBar = root.findViewById(R.id.new_saturation_seekbar);
        brightBar = root.findViewById(R.id.new_brightness_seekbar);
        cctBar = root.findViewById(R.id.new_colorTemperature_seekbar);
        huesValue = root.findViewById(R.id.new_huesValue);
        saturationValue = root.findViewById(R.id.new_saturationValue);
        brightnessValue = root.findViewById(R.id.new_brightnessValue);
        cctValue = root.findViewById(R.id.new_cctValue);
        colorView=root.findViewById(R.id.color_picker);

        huesBar.setOnSeekChangeListener(this);
        satuBar.setOnSeekChangeListener(this);
        brightBar.setOnSeekChangeListener(this);
        cctBar.setOnSeekChangeListener(this);
        colorView.setColoChangeListener(this);
        updateDisplay(ledStatus);
        return root;
    }

    @Override
    public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
        if (ledStatus == null)
            ledStatus = PHYApplication.getLedStatus();
        if (seekBar.getId() == R.id.new_hues_seekbar) {
            if(isProgressUpdateByManual)
                ledStatus.setRgbMode(true);
            ledStatus.setHues(progress);
            huesValue.setText(progress + "");
//            huesBar.bar(Color.HSVToColor(new float[]{progress,ledStatus.getSaturation()*0.01F,ledStatus.getBrightness()*0.01F}));
        } else if (seekBar.getId() == R.id.new_saturation_seekbar) {
            if(isProgressUpdateByManual)
                ledStatus.setRgbMode(true);
            ledStatus.setSaturation(progress);
            saturationValue.setText(progress + "");
        } else if (seekBar.getId() == R.id.new_brightness_seekbar) {
            ledStatus.setBrightness(progress);
            brightnessValue.setText(progress + "");
        } else if (seekBar.getId() == R.id.new_colorTemperature_seekbar) {
            if(isProgressUpdateByManual)
                ledStatus.setRgbMode(false);
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
//            handler.sendEmptyMessageDelayed(CCT_TAG, INTERVAL);
//        }
    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
        isProgressUpdateByManual = true;
        isUpdatedByColorPicker=false;
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
            ledStatus.setRgbMode(false);
            isCctRunning = false;
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
            }
        } else {
//            colorView.clearSelected();
//            isUpdatedByColorPicker = false;
        }
        isProgressUpdateByManual = false;
    }

//    setUserVisibleHint


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            //界面可见
//            if (ledStatus == null)
//                ledStatus = PHYApplication.getLedStatus();
//            updateDisplay(ledStatus);
        } else {
            //界面不可见 相当于onpause
//            System.out.println("\n Bg5xxColorFragment hidden");
        }
    }

    public View getRoot() {
        return root;
    }

    public void setRoot(View root) {
        this.root = root;
    }

    public IndicatorSeekBar getHuesBar() {
        return huesBar;
    }

    public void setHuesBar(IndicatorSeekBar huesBar) {
        this.huesBar = huesBar;
    }

    public IndicatorSeekBar getSatuBar() {
        return satuBar;
    }

    public void setSatuBar(IndicatorSeekBar satuBar) {
        this.satuBar = satuBar;
    }

    public IndicatorSeekBar getBrightBar() {
        return brightBar;
    }

    public void setBrightBar(IndicatorSeekBar brightBar) {
        this.brightBar = brightBar;
    }

    public IndicatorSeekBar getCctBar() {
        return cctBar;
    }

    public void setCctBar(IndicatorSeekBar cctBar) {
        this.cctBar = cctBar;
    }

    public TextView getHuesValue() {
        return huesValue;
    }

    public void setHuesValue(TextView huesValue) {
        this.huesValue = huesValue;
    }

    public TextView getSaturationValue() {
        return saturationValue;
    }

    public void setSaturationValue(TextView saturationValue) {
        this.saturationValue = saturationValue;
    }

    public TextView getBrightnessValue() {
        return brightnessValue;
    }

    public void setBrightnessValue(TextView brightnessValue) {
        this.brightnessValue = brightnessValue;
    }

    public TextView getCctValue() {
        return cctValue;
    }

    public void setCctValue(TextView cctValue) {
        this.cctValue = cctValue;
    }

    @Override
    public void onColorChange(float[] hsvArray, int color) {
        huesBar.setProgress((int)hsvArray[0]);
        satuBar.setProgress((int)(hsvArray[1]*100));
        isProgressUpdateByManual=true;
        ledStatus.setRgbMode(true);
        isHuesRunning = false;
        isUpdatedByColorPicker=true;
        handler.sendEmptyMessageDelayed(HUES_TAG,100);
    }
}