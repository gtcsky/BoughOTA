package com.phy.app.ble.bean;

/**
 * HeartRateOriginalData
 *
 * @author:zhoululu
 * @date:2018/4/20
 */

public class HeartRateOriginalData {

    private int[] heartRate = new int[8];

    public HeartRateOriginalData(byte[] data) {
        genHeartRate(data);
    }

    public int[] getHeartRate() {
        return heartRate;
    }

    private void genHeartRate(byte[] data){
        heartRate[0] = (data[2] & 0xff) | (data[3] & 0xff)<<8;
        heartRate[1] = (data[4] & 0xff) | (data[5] & 0xff)<<8;
        heartRate[2] = (data[6] & 0xff) | (data[7] & 0xff)<<8;
        heartRate[3] = (data[8] & 0xff) | (data[9] & 0xff)<<8;
        heartRate[4] = (data[10] & 0xff) | (data[11] & 0xff)<<8;
        heartRate[5] = (data[12] & 0xff) | (data[13] & 0xff)<<8;
        heartRate[6] = (data[14] & 0xff) | (data[15] & 0xff)<<8;
        heartRate[7] = (data[16] & 0xff) | (data[17] & 0xff)<<8;
    }

    @Override
    public String toString() {
        return heartRate[0] + "*" + heartRate[1] + "*" + heartRate[2] + "*" + heartRate[3] + "*" + heartRate[4] + "*" + heartRate[5] + "*" + heartRate[6] + "*" + heartRate[7];
    }
}
