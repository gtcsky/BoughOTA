package com.phy.app.ble;

import android.graphics.Path;
import android.util.Log;

import com.phy.app.ble.bean.Gsonsor;
import com.phy.app.ble.bean.HeartRateLastData;
import com.phy.app.ble.bean.HeartRateOriginalData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by zhoululu on 2017/6/22.
 */

public class BleAnalyze {

    public static void bleDataAnalysis(byte[] data){
        switch (data[0]){

            case 0x01:
                Log.e("version=",data[2]+""+data[3]+""+data[4]);
                break;

            case 0x02:
                BandUtil.bandBleCallBack.onResponse(OperateConstant.SYNC_TIME,null);
                break;

            case 0x03:
                Calendar calendar = Calendar.getInstance();
                calendar.set(data[2]+2000,data[3]-1,data[4],data[5],data[6],data[7]);
                Date date = calendar.getTime();
                BandUtil.bandBleCallBack.onResponse(OperateConstant.GET_TIME,date);
                break;

            case 0x21:
                BandUtil.bandBleCallBack.onResponse(OperateConstant.START_HEART_RATE,null);

            case 0x23:
                BandUtil.bandBleCallBack.onResponse(OperateConstant.START_GSENSOR,null);
                break;

            case 0x24:
                BandUtil.bandBleCallBack.onResponse(OperateConstant.STOP_GSENSOR,null);
                break;

            case 0x30:
                BandUtil.bandBleCallBack.onResponse(OperateConstant.LED_SETTING,null);
                break;

            case 0x38:
                BandUtil.bandBleCallBack.onResponse(OperateConstant.SEND_MESSAGE,null);
                break;

            case (byte) 0x81:
                HeartRateLastData rateLastData = new HeartRateLastData(data[2]);
                BandUtil.bandBleCallBack.onResponse(OperateConstant.HEART_RATE_LAST_DATA,rateLastData);
                break;

            case (byte) 0x85:
                BandUtil.bandBleCallBack.onResponse(OperateConstant.HEART_RATE_DATA,new HeartRateOriginalData(data));

            case (byte) 0x86:

                BandUtil.bandBleCallBack.onResponse(OperateConstant.GSENSOR_DATA,new Gsonsor(data));

                break;

        }
    }

    public static void batteryDataAnalysis(byte[] data){
        BandUtil.bandBleCallBack.onResponse(OperateConstant.GET_BATTERY,data[0] & 0xff);
    }

    public static void bootLoadDataAnalysis(byte[] data){
        if(data[0] == 0x00){
            BandUtil.bandBleCallBack.onResponse(OperateConstant.BOOT_LOAD_VERSION,data);
        }
    }

   public static void firmwareVersionAnalysis(int data) {
        if(data!=0){
            BandUtil.bandBleCallBack.onResponse(OperateConstant.FIRMWARE_VERSION,data);
        }
   }

    /*public static void BCTCDataAnalysis(List<byte[]> data){
        int length = 0;
        for(int i=0;i<data.size();i++){
            length += data.get(i).length;
        }

        byte[] result = new byte[length];

        for(int i=0;i<data.size();i++){
            System.arraycopy(data.get(i),0,result,i*20,data.get(i).length);
        }

        BandUtil.bandBleCallBack.onResponse(OperateConstant.BCTC_EXEC,result);
    }*/

}
