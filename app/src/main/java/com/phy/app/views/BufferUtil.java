package com.phy.app.views;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * BufferUtil
 *
 * @author:zhoululu
 * @date:2018/5/3
 */

public class BufferUtil {
    public static IntBuffer intBuffer;

    public static IntBuffer iBuffer(int[] a) {
        // 先初始化buffer,数组的长度*4,因为一个float占4个字节
        ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
        // 数组排列用nativeOrder
        mbb.order(ByteOrder.nativeOrder());
        intBuffer = mbb.asIntBuffer();
        intBuffer.put(a);
        intBuffer.position(0);
        return intBuffer;
    }
}