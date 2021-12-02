package com.phy.app.util;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import com.phy.app.ble.OperateConstant;

import java.util.Arrays;
import java.util.UUID;

/**
 * Created by zhoululu on 2017/6/22.
 */

public class Util {

    private static byte csn = 0x00;

    public static byte getCSN(){
        if(csn < 0xff){
            csn += 1;
        }else{
            csn = 0x00;
        }
        return csn;
    }

    public static byte genVerifyByte(byte method,byte csn){
        return (byte)(method ^ csn);
    }

    public static byte genVerifyByte(byte method,byte csn,byte[] command){

        int t = method ^ csn;
        for (int i=0;i<command.length;i++){
            t = t ^ command[i];
        }

        return (byte) t;
    }

    public static boolean containByte(byte[] source,byte[] des){

        for (int i=0;i<source.length/des.length;i++){
            byte[] bytes = new byte[des.length];
            System.arraycopy(source,i*des.length,bytes,0,des.length);

            if(Arrays.equals(bytes,des)){
                return true;
            }
        }
        return false;
    }

    public static byte[] replaceByte(byte[] source,byte[] des,byte[] replace){

        byte[] result = new byte[source.length+replace.length-des.length];

        for (int i=0;i<source.length/des.length;i++){
            byte[] bytes = new byte[des.length];
            System.arraycopy(source,i*des.length,bytes,0,des.length);

            if(Arrays.equals(bytes,des)){
                System.arraycopy(source,0,result,0,i*des.length);
                System.arraycopy(replace,0,result,i*des.length,replace.length);
                System.arraycopy(source,i*des.length+des.length,result,i*des.length+replace.length,source.length-i*des.length-des.length);

                return result;
            }
        }

        return source;
    }

    public static byte[] replaceAllByte(byte[] source,byte[] des,byte[] replace){

        byte[] result =  replaceByte(source,des,replace);

        while (containByte(result,des)){
            result =  replaceByte(source,des,replace);
        }

        return result;
    }

    public static String strAdd0(String str,int lenth){
        int strLength = str.length();
        StringBuffer result = new StringBuffer("");
        for (int i=0;i<lenth-strLength;i++){
            result.append("0");
        }

        return result.append(str).toString();
    }

    public static String translateStr(String str){
        String result = "";
        for (int i =0;i<str.length()/2;i++){
            result = str.substring(i*2,i*2+2) + result;
        }

        return  result;
    }

    public static boolean checkIsOTA(BluetoothGatt bluetoothGatt){

        if(bluetoothGatt == null){
            return false;
        }

        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(OperateConstant.SERVICE_OTA_UUID));

        if(bluetoothGattService == null){
            return false;
        }

        BluetoothGattCharacteristic bluetoothGattCharacteristic = bluetoothGattService.getCharacteristic(UUID.fromString(OperateConstant.CHARACTERISTIC_OTA_DATA_WRITE_UUID));
        if(bluetoothGattCharacteristic != null){
            return true;
        }else {
            return false;
        }
    }

    public static boolean checkIsCanOTA(BluetoothGatt bluetoothGatt) {

        if (bluetoothGatt == null) {
            return false;
        }

        BluetoothGattService bluetoothGattService = bluetoothGatt.getService(UUID.fromString(OperateConstant.SERVICE_OTA_UUID));

        if (bluetoothGattService == null) {
            return false;
        }

        return true;
    }

    /**
     *          3031323334->01234
     * @param hexByteIn
     * @return
     */
    public static String Hex2Str(byte[] hexByteIn) {
        int len = hexByteIn.length;
        StringBuilder sb = new StringBuilder();
        for (byte b : hexByteIn) {
//            sb.append(String.format( "%02x",b ));
                sb.append(String.format("%c", b));
        }
        return sb.toString();
    }

    /**
     *        Byte数组转换成字符串显示
     * @param hexByteIn
     * @return
     */
    public static String hex2AsciiStr(byte[] hexByteIn){
        int len = hexByteIn.length;
        StringBuilder sb=new StringBuilder();
        for (byte b : hexByteIn)
        {
            sb.append(String.format( "%02x ",b ));
        }
        return sb.toString();
    }

//    /**
//     *              合并任意类型数组
//     * @param first
//     * @param second
//     * @param <T>
//     * @return
//     *
//     */
//    public static <T> T[] arrayConcat(T[] first,T[] second){
//
//        T[] result = Arrays.copyOf(first, first.length + second.length);
//        System.arraycopy(second, 0, result, first.length, second.length);
//        return result;
//    }
    public static byte[] arrayConcat(byte[] first,byte[] second){

        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
