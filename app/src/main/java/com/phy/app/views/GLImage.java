package com.phy.app.views;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.phy.app.R;

/**
 * GLImage
 *
 * @author:zhoululu
 * @date:2018/5/3
 */

public class GLImage {

    public static Bitmap mBitmap1;
    public static Bitmap mBitmap2;
    public static Bitmap mBitmap3;
    public static Bitmap mBitmap4;
    public static Bitmap mBitmap5;
    public static Bitmap mBitmap6;

    public static void load(Resources resources) {
        /**
        mBitmap1 = BitmapFactory.decodeResource(resources, R.mipmap.signal_1);
        mBitmap2 = BitmapFactory.decodeResource(resources, R.mipmap.signal_2);
        mBitmap3 = BitmapFactory.decodeResource(resources, R.mipmap.signal_3);
        mBitmap4 = BitmapFactory.decodeResource(resources, R.mipmap.signal_4);
        mBitmap5 = BitmapFactory.decodeResource(resources, R.mipmap.signal_1);
        mBitmap6 = BitmapFactory.decodeResource(resources, R.mipmap.signal_2);
        **/
        mBitmap1 = BitmapFactory.decodeResource(resources, R.mipmap.a);
        mBitmap2 = BitmapFactory.decodeResource(resources, R.mipmap.b);
        mBitmap3 = BitmapFactory.decodeResource(resources, R.mipmap.c);
        mBitmap4 = BitmapFactory.decodeResource(resources, R.mipmap.d);
        mBitmap5 = BitmapFactory.decodeResource(resources, R.mipmap.e);
        mBitmap6 = BitmapFactory.decodeResource(resources, R.mipmap.f);

    }

}
