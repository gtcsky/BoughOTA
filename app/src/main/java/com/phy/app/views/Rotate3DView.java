package com.phy.app.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.phy.app.R;

/**
 * Rotate3DView
 *
 * @author:zhoululu
 * @date:2018/5/2
 */

public class Rotate3DView extends View {

    /* 触摸时作用在Camera的矩阵 */
    private Matrix mCameraMatrix;
    /* 照相机，用于旋转时钟实现3D效果 */
    private Camera mCamera;
    // camera绕X轴旋转的角度
    private float mCameraRotateX;
    // camera绕Y轴旋转的角度
    private float mCameraRotateY;

    /* 画布 */
    private Canvas mCanvas;
    /* 刻度圆弧画笔 */
    private Paint mPaint;

    private float X;
    private float Y;

    private Bitmap mBitmap;

    public Rotate3DView(Context context) {
        this(context,null);
    }

    public Rotate3DView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public Rotate3DView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mCameraMatrix = new Matrix();
        mCamera = new Camera();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureDimension(widthMeasureSpec), measureDimension(heightMeasureSpec));
    }

    private int measureDimension(int measureSpec) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = 800;
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ring);

        mCanvas = canvas;
        setCameraRotate();

        canvas.drawBitmap(mBitmap,0,0,mPaint);

    }

    /**
     * 设置3D时钟效果，触摸矩阵的相关设置、照相机的旋转大小
     * 应用在绘制图形之前，否则无效
     */
    private void setCameraRotate() {
        mCameraMatrix.reset();
        mCamera.save();
        mCamera.rotateX(mCameraRotateX);//绕x轴旋转角度
        mCamera.rotateY(mCameraRotateY);//绕y轴旋转角度
        mCamera.getMatrix(mCameraMatrix);//相关属性设置到matrix中
        mCamera.restore();
        //camera在view左上角那个点，故旋转默认是以左上角为中心旋转
        //故在动作之前pre将matrix向左移动getWidth()/2长度，向上移动getHeight()/2长度
        mCameraMatrix.preTranslate(-mBitmap.getWidth() / 2, -mBitmap.getHeight() / 2);
        //在动作之后post再回到原位
        mCameraMatrix.postTranslate(mBitmap.getWidth() / 2, mBitmap.getHeight() / 2);
        mCanvas.concat(mCameraMatrix);//matrix与canvas相关联
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                /**
                getCameraRotate(event);
                getCanvasTranslate(event);

                invalidate();
                */
                X = event.getX();
                Y = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //根据手指坐标计算camera应该旋转的大小
                getCameraRotate(event);
                getCanvasTranslate(event);

                invalidate();
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    /**
     * 获取camera旋转的大小
     *
     * @param event motionEvent
     */
    private void getCameraRotate(MotionEvent event) {
        //float rotateX = -(event.getY() - getHeight() / 2);
        //float rotateY = (event.getX() - getWidth() / 2);
        //求出此时旋转的大小与半径之比
        //float[] percentArr = getPercent(rotateX, rotateY);
        //最终旋转的大小按比例匀称改变
        //mCameraRotateX = percentArr[0] * mMaxCameraRotate;
        //mCameraRotateY = percentArr[1] * mMaxCameraRotate;

        float rotateX = event.getY() - Y;
        float rotateY = event.getX() - X;
        //mCameraRotateX = rotateX;
        mCameraRotateY = rotateY;
    }

    /**
     * 当拨动时钟时，会发现时针、分针、秒针和刻度盘会有一个较小的偏移量，形成近大远小的立体偏移效果
     * 一开始我打算使用 matrix 和 camera 的 mCamera.translate(x, y, z) 方法改变 z 的值
     * 但是并没有效果，所以就动态计算距离，然后在 onDraw()中分零件地 mCanvas.translate(x, y)
     *
     * @param event motionEvent
     */
    private void getCanvasTranslate(MotionEvent event) {
        float translateX = (event.getX() - getWidth() / 2);
        float translateY = (event.getY() - getHeight() / 2);
        //求出此时位移的大小与半径之比
        //float[] percentArr = getPercent(translateX, translateY);
        //最终位移的大小按比例匀称改变
       // mCanvasTranslateX = percentArr[0] * mMaxCanvasTranslate;
       // mCanvasTranslateY = percentArr[1] * mMaxCanvasTranslate;
    }

    /**
     * 获取一个操作旋转或位移大小的比例
     *
     * @param x x大小
     * @param y y大小
     * @return 装有xy比例的float数组
     */
    /*private float[] getPercent(float x, float y) {
        float[] percentArr = new float[2];
        float percentX = x / mRadius;
        float percentY = y / mRadius;
        if (percentX > 1) {
            percentX = 1;
        } else if (percentX < -1) {
            percentX = -1;
        }
        if (percentY > 1) {
            percentY = 1;
        } else if (percentY < -1) {
            percentY = -1;
        }
        percentArr[0] = percentX;
        percentArr[1] = percentY;
        return percentArr;
    }*/

}
