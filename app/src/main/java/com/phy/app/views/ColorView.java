package com.phy.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * Created by Sky @Bough.com on 17/8/21.
 */

public class ColorView extends View {
    private final float INVALID_TOUCH_POSITION=5000;
    private String TAG=getClass().getSimpleName();
    private Paint paint,touchPaint,circlePaint;
    private float canvasWidth,canvasHeight,radius;
    private float touchX=INVALID_TOUCH_POSITION,touchY=INVALID_TOUCH_POSITION;
    private static final int[] LINE_COLORS = new int[]{0xFFFF0000, 0xFFFF00FF,0xFF0000FF, 0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000};
    private float[] hsvArray=new float[3];
    private int selectedColor=0xFFFFFFFF;
    private ColorChangeListener colorChangeListener;

    public ColorView(Context context) {
        super(context);
        init();
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        paint=new Paint();
        paint.setAntiAlias(true);

        touchPaint=new Paint();
        touchPaint.setAntiAlias(true);
        touchPaint.setStyle(Paint.Style.FILL);

        circlePaint=new Paint();
        circlePaint.setColor(Color.BLACK);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(paint==null||canvas==null)
            return;
        canvasWidth=getWidth();
        canvasHeight=getHeight();
        canvas.translate(canvasWidth/2,canvasHeight/2);
        if(canvasHeight==0||canvasWidth==0)
            return;
        radius=canvasHeight*0.4F;
        Shader sweepGradient=new SweepGradient(0,0,LINE_COLORS,null);
        paint.setShader(sweepGradient);

        Shader radialGradient=new RadialGradient(0,0,radius,0xFFFFFFFF,0x00FFFFFF,Shader.TileMode.CLAMP);
        Shader composeShader=new ComposeShader(sweepGradient,radialGradient, PorterDuff.Mode.SRC_OVER);
        paint.setShader(composeShader);
        canvas.drawCircle(0,0,radius,paint);

        if(touchY==INVALID_TOUCH_POSITION||touchX==INVALID_TOUCH_POSITION){
            return;
        }
        float interRadius=canvasHeight*0.025F;
        circlePaint.setStrokeWidth(interRadius*0.1F);
        canvas.drawCircle(touchX,touchY,interRadius+4,circlePaint);
        canvas.drawCircle(touchX,touchY,interRadius,touchPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - canvasWidth / 2;
        float y = event.getY() - canvasHeight / 2;

        if (x * x + y * y <= radius * radius) {
            touchY=y;
            touchX=x;
            selectedColor=calcColor(touchX,touchY*-1F);
            touchPaint.setColor(selectedColor);
            this.colorChangeListener.onColorChange(hsvArray,selectedColor);
//            Log.d(TAG, Integer.toHexString(selectedColor));
            invalidate();
        }else {
            if(event.getAction()==MotionEvent.ACTION_MOVE){
                float r=(float) Math.sqrt(y*y+x*x);
//                Log.d(TAG, "onTouchEvent: r="+r+"\t radius:"+radius);
                float degree=calcDegree(x,y);
                touchY=(float)(radius*Math.sin(degree/360*2*Math.PI));
                touchX=(float)(radius*Math.cos(degree/360*2*Math.PI));
                selectedColor=calcColor(touchX,touchY*-1F);
                touchPaint.setColor(selectedColor);
                this.colorChangeListener.onColorChange(hsvArray,selectedColor);
//                Log.d(TAG, Integer.toHexString(selectedColor));
                invalidate();
            }
        }
        return true;
    }

    private float calcDegree(float x,float y){
        float r=(float) Math.sqrt(y*y+x*x);
        if(r==0)
            return 0;

        float degree=(float) Math.toDegrees(Math.asin(y/r));
        if(degree>=0){
            if(x<0)
                degree=180-degree;
        }else{
            if(x>=0)
                degree+=360;
            else{
                degree*=-1F;
                degree+=180;
            }
        }
        return degree;
    }


    /**
     *          根据传入的X，Y坐标，计算出0xXXXXXXXX格式的Color
     * @param x
     * @param y
     * @return
     */
    private int calcColor(float x,float y){
        float r=(float) Math.sqrt(y*y+x*x);
        if(r!=0){
            hsvArray[1]=r/radius;   //saturation
        }else{
            hsvArray[1]=0;          //saturation
        }

        float degree=(float) Math.toDegrees(Math.asin(y/r));
        if(degree>=0){
            if(x<0)
                degree=180-degree;
        }else{
            if(x>=0)
                degree+=360;
            else{
                degree*=-1F;
                degree+=180;
            }
        }
        hsvArray[0]=degree;         //Hues
        hsvArray[2]=1.0F;           //Brightness
        return Color.HSVToColor(hsvArray);

    }

    public float[] getHsvArray() {
        return hsvArray;
    }

    public int getSelectedColor() {
        return selectedColor;
    }


    public interface ColorChangeListener{
        public void onColorChange(float[] hsvArray,int color);
    }

    public void setColoChangeListener(ColorChangeListener listener){
        this.colorChangeListener=listener;
    }

    public void clearSelected(){
        touchY=INVALID_TOUCH_POSITION;
        touchX=INVALID_TOUCH_POSITION;
        invalidate();
    }

    /**
     *          根据传入的Hues和Saturation值,设定色盘中取色点的位置
     * @param hues          取值范围:>=0
     * @param saturation    取值范围:0~1.0
     */
    public void setSelectedColor(float hues,float saturation) {
        if(saturation>1.0)
            saturation=1.0F;
        if (hues < 0 || saturation > 1.0 || saturation < 0)
            return;
        hues %= 360;
        float arc = (float)(hues/360*Math.PI*2);
        float r = radius * saturation;
        touchY = (float) Math.sin(arc) * r*-1.0F;
        touchX = (float) Math.cos(arc) * r;

        selectedColor=calcColor(touchX,touchY*-1F);
        touchPaint.setColor(selectedColor);
        invalidate();
    }
}

