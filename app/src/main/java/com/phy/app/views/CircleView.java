package com.phy.app.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.phy.app.R;

/**
 * Created by zhoululu on 17/4/20.
 */

public class CircleView extends View{


    private Paint circlePaint;
    private Paint processPaint;
    private Paint textPaint;

    private RectF oval; // RectF对象（圆环）

    private int height;//控件高
    private int width;//控件宽
    private int circleWidth;//圆环宽

    private int centerPoint;

    private int heartRateTextSize = 250;
    private int unitTextSize = 50;

    private int strokeWidth = 40;

    private int heartRate = 0;
    private String heartRateUnit = "BPM";

    public CircleView(Context context) {
        this(context,null,0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        //TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SportCircleView);
       // stepsTextSize = typedArray.getDimensionPixelSize(R.styleable.SportCircleView_stepsTextSize,100);
        //unitTextSize = typedArray.getDimensionPixelSize(R.styleable.SportCircleView_unitTextSize,30);
        //descTextSize = typedArray.getDimensionPixelSize(R.styleable.SportCircleView_descTextSize,50);
        //calTextSize = typedArray.getDimensionPixelSize(R.styleable.SportCircleView_calTextSize,50);

        init();
    }

    private void init(){
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.parseColor("#2ead4d"));
        circlePaint.setStrokeWidth(2); // 线宽
        circlePaint.setStyle(Paint.Style.FILL);

        processPaint = new Paint();
        processPaint.setAntiAlias(true);
        processPaint.setColor(Color.parseColor("#0df145"));
        processPaint.setStrokeWidth(strokeWidth);
        processPaint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.parseColor("#ffffff"));
        textPaint.setStrokeWidth(2);
        textPaint.setStyle(Paint.Style.FILL);

        //Typeface numberTypeFace = Typeface.createFromAsset(getContext().getAssets(),"fonts/dincond-bold.otf");
        //textPaint.setTypeface(numberTypeFace);
        oval = new RectF();

        //stepsUnit = getResources().getString(R.string.sport_view_step_unit);
       // calUnit = getResources().getString(R.string.sport_view_cal_unit);
        //distanceUnit = getResources().getString(R.string.sprot_view_distance_unit);
        //todaySteps = getResources().getString(R.string.sport_view_today_steps_des);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //计算最小宽度
        height = MeasureSpec.getSize(heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);

        if(width >= height){
            circleWidth = height;
        }else{
            circleWidth = width;
        }

        setMeasuredDimension(circleWidth,circleWidth);

        oval.left = strokeWidth / 2; // 左边
        oval.top = strokeWidth / 2; // 上边
        oval.right = circleWidth - strokeWidth / 2; // 右边
        oval.bottom = circleWidth - strokeWidth / 2; // 下边

        centerPoint = circleWidth/2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(centerPoint,centerPoint,circleWidth/2,circlePaint);
        canvas.drawArc(oval, 0, 360, false, processPaint);// 绘制圆弧

        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(heartRateTextSize);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float textHeight = fontMetrics.bottom-fontMetrics.top;

        float top = centerPoint-textHeight/2;
        float baseY = top-fontMetrics.top;

        canvas.drawText(heartRate+"",centerPoint,baseY,textPaint);


        //绘制运动距离和卡路里
        textPaint.setTextSize(unitTextSize);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(heartRateUnit,centerPoint,centerPoint+centerPoint/2,textPaint);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_heart);
        Bitmap scaleBitmap = zoomImg(bitmap,scal);
        canvas.drawBitmap(scaleBitmap,centerPoint-scaleBitmap.getWidth()/2,centerPoint/2-scaleBitmap.getHeight()/2,textPaint);
    }

    public void setData(int heartRate){
        this.heartRate = heartRate;

        invalidate();
    }

    // 等比缩放图片
    private static Bitmap zoomImg(Bitmap bm, float scale){
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();

        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newbm;
    }

    float scal = 0.8f;
    ValueAnimator animator;
    public void startAnim() {
        animator = ValueAnimator.ofFloat(0.8f, 1.0f);
        animator.setDuration(600);
        animator.setInterpolator(new LinearInterpolator()); //插值器
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float distance = (float) animation.getAnimatedValue();
                scal = distance;
                invalidate();
            }
        });
        animator.start();
    }

    public void stop(){
        if(animator != null && animator.isRunning()){
            animator.cancel();
        }
        animator = null;
    }

}
