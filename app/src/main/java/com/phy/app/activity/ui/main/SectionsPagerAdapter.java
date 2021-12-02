package com.phy.app.activity.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.phy.app.R;
import com.phy.app.app.PHYApplication;
import com.phy.app.fragments.Bg5xxColorFragment;
import com.phy.app.fragments.Bg5xxEffectFragment;
import com.phy.app.fragments.Bg5xxTestFragment;
import com.phy.app.fragments.CctFragment;
import com.phy.app.fragments.HsiFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {
    public static List<Fragment> fgs;
    static {
        fgs=new ArrayList<Fragment>();
    }

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private static final int[] BG5xx_TAB_TITLES = new int[]{R.string.color_ctrl, R.string.effect_ctrl,R.string.test_title};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        Fragment fragment=null;
        String modelNo=PHYApplication.getLedStatus().getModelNumber();
        switch (position){
            case 0:
                if(modelNo.contains("BG93"))
                    fragment=new HsiFragment();
                else
                    fragment=new Bg5xxColorFragment();
                break;
            case 1:
                if(modelNo.contains("BG93"))
                    fragment=new CctFragment();
                else
                    fragment=new Bg5xxEffectFragment();
                break;
            case 2:
                fragment=new Bg5xxTestFragment();
                break;
        }
        fgs.add(fragment);
        return fragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (PHYApplication.getLedStatus().getModelNumber().contains("BG93")){
            return mContext.getResources().getString(TAB_TITLES[position]);
        }
        else{
            return mContext.getResources().getString(BG5xx_TAB_TITLES[position]);
        }
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        if (PHYApplication.getLedStatus().getModelNumber().contains("BG93")) {
            return 2;
        } else {
            return 3;
        }
    }

    public static List<Fragment> getFgs() {
        return fgs;
    }

    public static void setFgs(List<Fragment> fgs) {
        SectionsPagerAdapter.fgs = fgs;
    }
}