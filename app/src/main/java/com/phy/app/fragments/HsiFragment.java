package com.phy.app.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.phy.app.R;
import com.phy.app.activity.ui.main.PageViewModel;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.Const;
import com.phy.app.beans.LEDStatus;
import com.phy.app.views.ColorView;
import com.warkiz.widget.IndicatorSeekBar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HsiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HsiFragment extends Fragment implements IndicatorSeekBar.OnSeekBarChangeListener ,ColorView.ColorChangeListener{
    private View root;
    private IndicatorSeekBar huesBar, satuBar, brightBar, cctBar, hsiEffectBar, cctEffectBar, speedBar;
    private TextView huesValue, saturationValue, brightnessValue, cctValue, hsiEffectValue, cctEffectValue, speedValue;
    private boolean isHuesRunning, isSaturationRunning, isBrightnessRunning, isCctRunning, isEffectRunning, isSpeedRunning;
    private ColorView colorView;
    private static final int HUES_TAG = 1;
    private static final int SATURATION_TAG = 2;
    private static final int BRIGHT_TAG = 3;
    private static final int CCT_TAG = 4;
    private static final int EFFECT_TAG = 5;
    private static final int SPEED_TAG = 7;
    private static final int INTERVAL = 300;

    private boolean isProgressUpdateByManual = false;
    LEDStatus ledStatus;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (ledStatus == null)
                ledStatus = PHYApplication.getLedStatus();
            if (msg.what == HUES_TAG) {
                if (isProgressUpdateByManual) {
                    ledStatus.setArrowIndex(Const.ARROW_AT_HUES);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                    colorView.setSelectedColor(ledStatus.getHues(),ledStatus.getSaturation()*0.01F);
                }
                if (isHuesRunning) {
                    handler.sendEmptyMessageDelayed(HUES_TAG, INTERVAL);
                }
            } else if (msg.what == SATURATION_TAG) {
                if (isProgressUpdateByManual) {
                    ledStatus.setArrowIndex(Const.ARROW_AT_SATURATION);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
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
                    ledStatus.setArrowIndex(Const.ARROW_AT_COLOR_TEMPERATURE);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                    colorView.clearSelected();
                }
                if (isCctRunning) {
                    handler.sendEmptyMessageDelayed(CCT_TAG, INTERVAL);
                }
            } else if (msg.what == EFFECT_TAG) {
                if (isProgressUpdateByManual) {
                    ledStatus.setArrowIndex(Const.ARROW_AT_EFFECT_NO);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                }
                if (isEffectRunning) {
                    handler.sendEmptyMessageDelayed(EFFECT_TAG, INTERVAL);
                }
            } else if (msg.what == SPEED_TAG) {
                if (isProgressUpdateByManual) {
                    ledStatus.setArrowIndex(Const.ARROW_AT_EFFECT_SPEED);
                    PHYApplication.getBandUtil().userLedSetting(ledStatus);
                }
                if (isSpeedRunning) {
                    handler.sendEmptyMessageDelayed(SPEED_TAG, INTERVAL);
                }
            }
            return false;
        }
    });

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HsiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HsiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HsiFragment newInstance(String param1, String param2) {
        HsiFragment fragment = new HsiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

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
            root = inflater.inflate(R.layout.fragment_hsi, container, false);
        huesBar = root.findViewById(R.id.new_hues_seekbar);
        satuBar = root.findViewById(R.id.new_saturation_seekbar);
        hsiEffectBar = root.findViewById(R.id.new_hsiEffect_seekbar);
        brightBar = root.findViewById(R.id.new_brightness_seekbar);
        speedBar = root.findViewById(R.id.new_speed_seekbar);
        colorView=root.findViewById(R.id.color_picker);



        huesValue = root.findViewById(R.id.new_huesValue);
        speedValue = root.findViewById(R.id.new_speedValue);
        saturationValue = root.findViewById(R.id.new_saturationValue);
        hsiEffectValue = root.findViewById(R.id.new_hisEffectValue);
        brightnessValue = root.findViewById(R.id.new_brightnessValue);

        huesBar.setOnSeekChangeListener(this);
        satuBar.setOnSeekChangeListener(this);
        hsiEffectBar.setOnSeekChangeListener(this);
        brightBar.setOnSeekChangeListener(this);
        speedBar.setOnSeekChangeListener(this);
        colorView.setColoChangeListener(this);

        updateDisplay(ledStatus);
        return root;

    }

    @Override
    public void onProgressChanged(IndicatorSeekBar seekBar, int progress, float progressFloat, boolean fromUserTouch) {
        if (ledStatus == null)
            ledStatus = PHYApplication.getLedStatus();
        if (seekBar.getId() == R.id.new_hues_seekbar) {
            ledStatus.setHues(progress);
            huesValue.setText(progress + "");
        } else if (seekBar.getId() == R.id.new_saturation_seekbar) {
            ledStatus.setSaturation(progress);
            saturationValue.setText(progress + "");
        } else if (seekBar.getId() == R.id.new_brightness_seekbar) {
            ledStatus.setBrightness(progress);
            brightnessValue.setText(progress + "");
        } else if (seekBar.getId() == R.id.new_colorTemperature_seekbar) {
            ledStatus.setColorTemperature(progress);
            ledStatus.setBrightness(ledStatus.getBrightness());
            cctValue.setText(progress * 100 + "");
            int cctNo = ledStatus.getCctEffectNo();
            if (cctNo < Const.MIN_COLOR_TEMP_STYLE_NO) {
                cctNo = Const.MIN_COLOR_TEMP_STYLE_NO;
                ledStatus.setCctEffectNo(cctNo);
            }
        } else if (seekBar.getId() == R.id.new_hsiEffect_seekbar) {
            ledStatus.setRgbEffectNo(progress);
            hsiEffectValue.setText(progress + "");
        } else if (seekBar.getId() == R.id.new_cctEffect_seekbar) {
            ledStatus.setCctEffectNo(progress);
            cctEffectValue.setText(progress - Const.MIN_COLOR_TEMP_STYLE_NO + "");
        } else if (seekBar.getId() == R.id.new_speed_seekbar) {
            ledStatus.setEffectSpeed(progress);
            speedValue.setText(progress + "");
        }
        ledStatus.setRgbMode(true);
    }

    @Override
    public void onSectionChanged(IndicatorSeekBar seekBar, int thumbPosOnTick, String textBelowTick, boolean fromUserTouch) {

    }

    @Override
    public void onStartTrackingTouch(IndicatorSeekBar seekBar, int thumbPosOnTick) {
        isProgressUpdateByManual = true;
        if (seekBar.getId() == R.id.new_hues_seekbar) {
            isHuesRunning = true;
            handler.sendEmptyMessageDelayed(HUES_TAG, INTERVAL);
        } else if (seekBar.getId() == R.id.new_saturation_seekbar) {
            isSaturationRunning = true;
            handler.sendEmptyMessageDelayed(SATURATION_TAG, INTERVAL);
        } else if (seekBar.getId() == R.id.new_brightness_seekbar) {
            isBrightnessRunning = true;
            handler.sendEmptyMessageDelayed(BRIGHT_TAG, INTERVAL);
        } else if (seekBar.getId() == R.id.new_colorTemperature_seekbar) {
            isCctRunning = true;
            handler.sendEmptyMessageDelayed(CCT_TAG, INTERVAL);
        } else if (seekBar.getId() == R.id.new_cctEffect_seekbar) {
            isEffectRunning = true;
            handler.sendEmptyMessageDelayed(EFFECT_TAG, INTERVAL);
        } else if (seekBar.getId() == R.id.new_hsiEffect_seekbar) {
            isEffectRunning = true;
            handler.sendEmptyMessageDelayed(EFFECT_TAG, INTERVAL);
        } else if (seekBar.getId() == R.id.new_speed_seekbar) {
            isSpeedRunning = true;
            handler.sendEmptyMessageDelayed(SPEED_TAG, INTERVAL);
        }
    }

    @Override
    public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
        isProgressUpdateByManual = true;
        if (seekBar.getId() == R.id.new_hues_seekbar) {
            isHuesRunning = false;
            handler.sendEmptyMessage(HUES_TAG);
        } else if (seekBar.getId() == R.id.new_saturation_seekbar) {
            isSaturationRunning = false;
            handler.sendEmptyMessage(SATURATION_TAG);
        } else if (seekBar.getId() == R.id.new_brightness_seekbar) {
            isBrightnessRunning = false;
            handler.sendEmptyMessage(BRIGHT_TAG);
        } else if (seekBar.getId() == R.id.new_colorTemperature_seekbar) {
            isCctRunning = false;
            handler.sendEmptyMessage(CCT_TAG);
        } else if (seekBar.getId() == R.id.new_cctEffect_seekbar) {
            isEffectRunning = false;
            handler.sendEmptyMessage(EFFECT_TAG);
        } else if (seekBar.getId() == R.id.new_hsiEffect_seekbar) {
            isEffectRunning = false;
            handler.sendEmptyMessage(EFFECT_TAG);
        } else if (seekBar.getId() == R.id.new_speed_seekbar) {
            isSpeedRunning = false;
            handler.sendEmptyMessage(SPEED_TAG);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        System.out.println(getClass().getSimpleName() + ":\t" + "HSI:" + ledStatus.getBrightness());
        updateDisplay(PHYApplication.getLedStatus());
    }

    public void updateDisplay(LEDStatus ledStatus) {
        if (!isProgressUpdateByManual) {
            if (huesBar != null) {
                huesBar.setProgress(ledStatus.getHues());
                satuBar.setProgress(ledStatus.getSaturation());
                hsiEffectBar.setProgress(ledStatus.getRgbEffectNo());
                brightBar.setProgress(ledStatus.getBrightness());
                speedBar.setProgress(ledStatus.getEffectSpeed());
            }
        }
        isProgressUpdateByManual = false;
    }

    public void syncBrightness(LEDStatus ledStatus) {
        if (brightBar != null)
            brightBar.setProgress(ledStatus.getBrightness());
    }

    @Override
    public void onColorChange(float[] hsvArray, int color) {
        huesBar.setProgress((int)hsvArray[0]);
        satuBar.setProgress((int)(hsvArray[1]*100));
        isProgressUpdateByManual=true;
        ledStatus.setRgbMode(true);
        isHuesRunning = false;
        handler.sendEmptyMessageDelayed(HUES_TAG,100);
    }
}
