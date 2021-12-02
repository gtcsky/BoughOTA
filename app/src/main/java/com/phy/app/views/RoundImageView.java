package com.phy.app.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;


import com.phy.app.R;

public class RoundImageView extends ImageView {

    private Bitmap mBitmap;
    private Rect mRect = new Rect();
    private PaintFlagsDrawFilter pdf = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG);
    private Paint mPaint = new Paint();
    private Path mPath = new Path();

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    //传入一个Bitmap对象
    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }


    private void init() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);// 抗锯齿
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mBitmap == null) {
            return;
        }
        mRect.set(0, 0, getWidth(), getHeight());
        Log.d("RoundImage", "Width= "+getWidth()+"Height="+getWidth());
        canvas.save();
        canvas.setDrawFilter(pdf);
        mPath.addCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2, Path.Direction.CCW);
        canvas.clipPath(mPath, Region.Op.REPLACE);

        mRect.set(10, 10, getWidth()-10, getHeight()-10);
        canvas.drawBitmap(mBitmap, null, mRect, mPaint);

        Bitmap aBitmap= BitmapFactory.decodeResource(getResources(), R.mipmap.circle);
        mRect.set(1, 1, getWidth()-1, getHeight()-2);
        canvas.drawBitmap(aBitmap, null, mRect, mPaint);
        canvas.restore();
    }
}
