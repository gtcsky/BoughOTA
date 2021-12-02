package com.phy.app.views;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * MyGLRenderer2
 *
 * @author:zhoululu
 * @date:2018/5/3
 */

public class MyGLRenderer2 implements GLSurfaceView.Renderer {

    private int one = 0x10000;
    private int[] quarter = {-one,-one,one,
            one,-one,one,
            one,one,one,
            -one,one,one,

            -one,-one,-one,
            -one,one,-one,
            one,one,-one,
            one,-one,-one,

            -one,one,-one,
            -one,one,one,
            one,one,one,
            one,one,-one,

            -one,-one,-one,
            one,-one,-one,
            one,-one,one,
            -one,-one,one,

            one,-one,-one,
            one,one,-one,
            one,one,one,
            one,-one,one,

            -one,-one,-one,
            -one,-one,one,
            -one,one,one,
            -one,one,-one,};

    private int[] texCoords = {one,0,0,0,0,one,one,one,
            0,0,0,one,one,one,one,0,
            one,one,one,0,0,0,0,one,
            0,one,one,one,one,0,0,0,
            0,0,0,one,one,one,one,0,
            one,0,0,0,0,one,one,one,};

    //准备正方体顶点
    private IntBuffer quarterBuffer = BufferUtil.iBuffer(quarter);
    //纹理映射数据
    private IntBuffer texCoordsBuffer = BufferUtil.iBuffer(texCoords);

    //        private IntBuffer indicesBuffer = BufferUtil.iBuffer(indices);
    ByteBuffer indicesBuffer = ByteBuffer.wrap(new byte[]{
            0,1,3,2,
            4,5,7,6,
            8,9,11,10,
            12,13,15,14,
            16,17,19,18,
            20,21,23,22,
    });

    private float rotateX; //用于正方体x轴的旋转；
    private float rotateY; //用于正方体y轴的旋转；
    private float rotateZ; //用于正方体z轴的旋转；

    private Context context;
    int[] texture;

    public MyGLRenderer2(Context context) {
        this.context = context;
    }

    //咳，咳现在开始画图了
    @Override
    public void onDrawFrame(GL10 gl) {

        //清楚屏幕和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        //重置当前的观察模型矩阵
        gl.glLoadIdentity();
        //现将屏幕向里移动，用来画正方体
        gl.glTranslatef(0.0f, 0.0f, -6.0f);
        //设置3个方向的旋转
        gl.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);

        //通知opnegl将文理名字texture绑定到指定的纹理目标上GL10.GL_TEXTURE_2D
       // gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        //纹理的使用与开启颜色渲染一样，需要开启纹理功能
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        //设置正方体 各顶点
        gl.glVertexPointer(3, GL10.GL_FIXED, 0, quarterBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FIXED, 0, texCoordsBuffer);

        //绘制
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, 24,  GL10.GL_UNSIGNED_BYTE, indicesBuffer);
        /*for(int i=0; i<6; i++){
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[i]);
            gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, i*4, 4);
        }*/
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

    }

    //当窗口改变时，调用，至少在创建窗口时调用一次，这边设置下场景大小
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub
        //设置OpenGL场景大小
        float ratio = (float) width / height;
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);//设置为投影矩阵模式
        gl.glLoadIdentity();//重置
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);//设置视角
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    //当窗口被创建时我们可以做些初始化工作
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置清除屏幕时所用的颜色，参数依次为红、绿、蓝、Alpha值
        gl.glClearColor(1, 1, 1, 1);
        gl.glEnable(GL10.GL_CULL_FACE);
        //启用阴影平滑
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);//启用深度测试

        //以下是关于深度缓存的设置，非常重要
        gl.glClearDepthf(1.0f);//设置深度缓存
        gl.glDepthFunc(GL10.GL_LEQUAL);//所做深度测试的类型

        //告诉系统对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
        //允许2D贴图
        gl.glEnable(GL10.GL_TEXTURE_2D);

        /*IntBuffer intBuffer = IntBuffer.allocate(1);
        //创建纹理
        gl.glGenTextures(1, intBuffer);
        texture = intBuffer.get();
        // 将生成的空纹理绑定到当前2D纹理通道
        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);
        // 设置2D纹理通道当前绑定的纹理的属性
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.signal_4);

        // 将bitmap应用到2D纹理通道当前绑定的纹理中
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);*/

        IntBuffer textureBuffer = IntBuffer.allocate(6);
        gl.glGenTextures(6, textureBuffer);
        texture = textureBuffer.array();

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[0]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.mBitmap1, 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[1]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.mBitmap2, 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[2]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.mBitmap3, 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[3]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.mBitmap4, 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[4]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.mBitmap5, 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[5]);
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, GLImage.mBitmap6, 0);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);

    }

    public void setRotateX(float X){
        rotateX+=X;
    }

    public void setRotateY(float y){
        rotateY+=y;
    }


    public void modify(float sonsorX,float sonsorY,float sonsorZ){

        float angleY = 0;
        float angleX = 0;

        if(sonsorX <0 && sonsorY >0){
            angleY = Math.abs(sonsorX)*0.09f;
        }else if(sonsorX<0 && sonsorY<0){
            angleY = 180-Math.abs(sonsorX)*0.09f;
        }else if(sonsorX>0 && sonsorY<0){
            angleY = 180+Math.abs(sonsorX)*0.09f;
        }else if(sonsorX>0 && sonsorY>0){
            angleY = 360-Math.abs(sonsorX)*0.09f;
        }

        if(rotateY < 0){
            rotateY = -angleY;
        }else{
            rotateY = (360-angleY);
        }

        if(sonsorZ <0 && sonsorY >0){
            angleX = 90-Math.abs(sonsorY)*0.09f;
        }else if(sonsorZ<0 && sonsorY<0){
            angleX = 90+Math.abs(sonsorY)*0.09f;
        }else if(sonsorZ>0 && sonsorY<0){
            angleX = 270-Math.abs(sonsorY)*0.09f;
        }else if(sonsorZ>0 && sonsorY>0){
            angleX = 270+Math.abs(sonsorY)*0.09f;
        }

        if(rotateX < 0){
            rotateX = -(360-angleX);
        }else{
            rotateX = angleX;
        }
    }

}





