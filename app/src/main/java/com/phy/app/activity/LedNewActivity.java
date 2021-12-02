package com.phy.app.activity;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;

import com.phy.app.R;
import com.phy.app.activity.ui.main.SectionsPagerAdapter;
import com.phy.app.app.PHYApplication;
import com.phy.app.beans.Connect;
import com.phy.app.beans.LEDStatus;
import com.phy.app.fragments.Bg5xxColorFragment;
import com.phy.app.fragments.Bg5xxEffectFragment;
import com.phy.app.fragments.Bg5xxTestFragment;
import com.phy.app.fragments.CctFragment;
import com.phy.app.fragments.HsiFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

//public class LedNewActivity extends AppCompatActivity  {
public class LedNewActivity extends EventBusBaseActivity  {
    private static int currentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_led_new);
        PHYApplication.setIsLedCtrlMode(true);
        final SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        final ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentPage=tab.getPosition();
                if (PHYApplication.getLedStatus().getModelNumber().contains("BG93")) {
                    updateBrightnessOnly(tab.getPosition(),PHYApplication.getLedStatus());
                }else{
                    Log.d(TAG, "onTabSelected: "+PHYApplication.getLedStatus().isLedOn()+"  position:"+tab.getPosition());
                    updateGuiShow(PHYApplication.getLedStatus());
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void initComponent() {

    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_led_new;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Connect connect) {
        if(connect!=null){
            if(!connect.isConnect())
                finish();
            Log.d(getClass().getSimpleName(), "*************connect message**************");
        }else{      //disconnect event
            Log.d(getClass().getSimpleName(), "*************null**************");
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LEDStatus ledStatus) {
        PHYApplication.setLedStatus(ledStatus);
        if (PHYApplication.isIsLedCtrlMode()) {
           updateGuiShow(ledStatus);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        PHYApplication.setIsLedCtrlMode(false);
        ArrayList<Fragment> pfs = (ArrayList<Fragment>) SectionsPagerAdapter.getFgs();
        if (pfs == null || pfs.isEmpty()) {
            Log.d(TAG, "pfs is empty");
        } else {
            pfs.clear();
            SectionsPagerAdapter.setFgs(pfs);
        }
//        pfs = null;
    }

    public  void updateGuiShow(LEDStatus ledStatus) {
        if (PHYApplication.getLedStatus().getModelNumber().contains("BG93")) {
            int index = 1;
            if (ledStatus.isRgbMode())
                index = 0;
            if (SectionsPagerAdapter.getFgs() != null) {
                if (ledStatus.isRgbMode())
                    ((HsiFragment) SectionsPagerAdapter.getFgs().get(index)).updateDisplay(ledStatus);
                else
                    ((CctFragment) SectionsPagerAdapter.getFgs().get(index)).updateDisplay(ledStatus);
            }
        } else {
            switch (currentPage) {
                case 0:
                    ((Bg5xxColorFragment) SectionsPagerAdapter.getFgs().get(currentPage)).updateDisplay(ledStatus);
                    break;
                case 1:
                    ((Bg5xxEffectFragment) SectionsPagerAdapter.getFgs().get(currentPage)).updateDisplay(ledStatus);
                    break;
                case 2:
                    if (SectionsPagerAdapter.getFgs().size() > 2)
                        ((Bg5xxTestFragment) SectionsPagerAdapter.getFgs().get(currentPage)).updateDisplay(ledStatus);
                    break;
            }
        }
    }

    public static void updateBrightnessOnly(int index, LEDStatus ledStatus) {
        if (PHYApplication.getLedStatus().getModelNumber().contains("BG93")) {
            if (index == 0) {
                ((HsiFragment) SectionsPagerAdapter.getFgs().get(index)).syncBrightness(ledStatus);
            } else {
                ((CctFragment) SectionsPagerAdapter.getFgs().get(index)).syncBrightness(ledStatus);
            }
        }
    }



}