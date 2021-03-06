package com.phy.app.activity;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.phy.app.R;

import java.util.Objects;


/**
 * Created by zhoululu on 2017/4/14.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public Toolbar toolbar;

    public String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarLigntMode();

        setContentView(getContentLayout());
        toolbar = findViewById(R.id.TOOLBAR);

        if (toolbar!=null){
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        initComponent();

    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setStatusBarLigntMode(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    private void setStatusBarDarkMode(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }
    public  abstract void initComponent();
    public  abstract int getContentLayout();

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public void setStatusBarColorOnly(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    public void clearStatusBarColor() {
        setStatusBarColor(android.R.color.transparent);
        setStatusBarDarkMode();
    }


    public void setTitle(String t) {
        if (toolbar != null) {
            ((TextView) toolbar.findViewById(R.id.title)).setText(t);
        }
    }

    public void setTitle(int t) {
        setTitle(getString(t));
    }

    public void setNavigationIcon(int icon){
        if(toolbar != null){
            toolbar.setNavigationIcon(icon);
        }
    }

    public void setDisplayHomeAsUpEnabled(boolean f) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(f);
    }

    public void showToast(  String text){
        Toast.makeText(this, text,Toast.LENGTH_SHORT).show();
    }

    public void showToast(@StringRes int resId){
        showToast(getString(resId));
    }


    @Override
    public Resources getResources() {

        Resources res = super.getResources();
        Configuration config=new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config,res.getDisplayMetrics());
        return res;
    }

    /**
     * ??????????????????????????????.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //????????????????????????????????????
            View v = getCurrentFocus();      //???????????????????????????,ps:??????????????????????????????????????????????????????
            if (isShouldHideKeyboard(v, me)) { //??????????????????????????????????????????????????????
                hideKeyboard(v.getWindowToken());   //????????????
            }
        }
        return super.dispatchTouchEvent(me);
    }
    /**
     * ??????EditText???????????????????????????????????????????????????????????????????????????????????????????????????EditText??????????????????
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if ((v instanceof EditText)) {  //???????????????????????????????????????EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],    //????????????????????????????????????????????????
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            // ?????????????????????EditText??????????????????????????????????????????
            return !(event.getX() > left) || !(event.getX() < right)
                    || !(event.getY() > top) || !(event.getY() < bottom);
        }
        // ??????????????????EditText?????????
        return false;
    }
    /**
     * ??????InputMethodManager??????????????????
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
