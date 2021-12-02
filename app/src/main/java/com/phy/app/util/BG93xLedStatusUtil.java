package com.phy.app.util;

import com.phy.app.beans.Const;
import com.phy.app.beans.LEDStatus;

public class BG93xLedStatusUtil implements LedStatusUtil{
    /**
     *  解析BLE接收到的数据为Bean
     * @param values
     * @return
     */
    public  void parseHexBytes(byte[] values, LEDStatus ledStatus) {
        if (values == null || values.length != Const.LED_CTRL_COMMAND_PACKAGE_LEN)
            return ;
//        LEDStatus ledStatus = new LEDStatus();
        int data=0;
        int effectNo=0;
        System.out.println(getClass().getSimpleName()+":\t"+"Executed BG9xx");
        for (int i=0;i<values.length;i++){
            data=values[i]&0xFF;
            switch (i){
                case Const.BLE_PACKAGE_CMD_BYTE:
                    ledStatus.setCmd(data);
                    break;
                case Const.BLE_PACKAGE_MODE_BYTE:
                    ledStatus.setMode(data);
                    break;
                case Const.BLE_PACKAGE_BRIGHTNESS_BYTE:
                    ledStatus.setBrightness(data);
                    break;
                case Const.BLE_PACKAGE_CCT_BYTE:
                    ledStatus.setColorTemperature(data);
                    if(data!=0xff)
                        ledStatus.setRgbMode(false);
                    break;
                case Const.BLE_PACKAGE_HUES_lOW_BYTE:
                    ledStatus.setHues(data);
                    break;
                case Const.BLE_PACKAGE_HUES_HIGH_BYTE:
                    data=((data*256)|ledStatus.getHues())&0xffff;
                    ledStatus.setHues(data);
                    if(data!=0xffff){
                        ledStatus.setRgbMode(true);
                    }
                    break;
                case Const.BLE_PACKAGE_SATURATION_BYTE:
                    ledStatus.setSaturation(data);
                    if(data!=0xff){
                        ledStatus.setRgbMode(true);
                    }
                    break;
                case Const.BLE_PACKAGE_EFFECT_MODE_BYTE:
                    ledStatus.setEffectCategory(data);
                    break;
                case Const.BLE_PACKAGE_EFFECT_NO:
                    effectNo=data;
                    if(ledStatus.isRgbMode())
                        ledStatus.setRgbEffectNo(data);
                    else
                        ledStatus.setCctEffectNo(data);
                    break;
                case Const.BLE_PACKAGE_EFFECT_SPEED:
                    ledStatus.setEffectSpeed(data);
                    break;
                case Const.BLE_PACKAGE_ARROW_INDEX:
                    if((data&0x80)==0) {
                        ledStatus.setRgbMode(false);
                        ledStatus.setCctEffectNo(effectNo);
                    }else{
                        ledStatus.setRgbEffectNo(effectNo);
                        ledStatus.setRgbMode(true);
                    }
                    ledStatus.setArrowIndex(parseArrowIndex(data));
                    break;
            }
        }
//        System.out.println(ledStatus);

    }

    /**
     * 把Bean转换成BLE发送的Byte数据
     * @param ledStatus
     * @return
     */
    public  byte[] compressBeanToArray(LEDStatus ledStatus){
        byte[] data=new byte[Const.LED_CTRL_COMMAND_PACKAGE_LEN];
        if(ledStatus.isRgbMode()){
//            data[0]=(byte)0x02;
            data[0]=(byte)0x04;
            data[1]=(byte)0x00;
            data[2]=(byte)ledStatus.getBrightness();
            data[3]=(byte)0xFF;
            data[4]=(byte)(ledStatus.getHues()%256);
            data[5]=(byte)(ledStatus.getHues()/256);
            data[6]=(byte)(ledStatus.getSaturation());
            data[7]=(byte)0x01;
            data[8]=(byte)ledStatus.getRgbEffectNo();
            data[9]=(byte)ledStatus.getEffectSpeed();
            data[10]=(byte)convertBleArrowIndex(ledStatus.getArrowIndex());
        }
        else{
//            data[0]=(byte)0x01;
            data[0]=(byte)0x04;
            data[1]=(byte)0x00;
            data[2]=(byte)ledStatus.getBrightness();
            data[3]=(byte)ledStatus.getColorTemperature();
            data[4]=(byte)0xFF;
            data[5]=(byte)0xFF;
            data[6]=(byte)0xFF;
            data[7]=(byte)0x01;
            data[8]=(byte)ledStatus.getCctEffectNo();
            data[9]=(byte)ledStatus.getEffectSpeed();
            data[10]=(byte)convertBleArrowIndex(ledStatus.getArrowIndex());
        }
        return data;
    }

    /**
     *  所BLE接收到的Arrow Index信息,解析为APP中的ArrowIndex信息
     * @param data
     * @return
     */
    public  int parseArrowIndex(int data) {
        int index = 0;
        if ((data & 0x80) != 0) {
            index = data & 0x7F;
            if (index > Const.ARROW_AT_BRIGHTNESS) {
                if (index == 3) {
                    index = Const.ARROW_AT_HSI_EFFECT_NO;
                } else if (index == 4) {
                    index = Const.ARROW_AT_EFFECT_SPEED;
                }
            }
        } else {
            index = data & 0x7F;
            if (index ==  Const.TARGET_ARROW_AT_COLOR_TEMPERATURE) {
                index = Const.ARROW_AT_COLOR_TEMPERATURE;
            } else if (index == 3) {
                index = Const.ARROW_AT_CCT_EFFECT_NO;
            }
        }
        return index;
    }

    /**
     * 把APP中Index信息转换成BLE终端接收的ArrowIndex
     * @param data
     * @return
     */
    public  int convertBleArrowIndex(int data){
        switch(data){
            case Const.ARROW_AT_HUES:
            case Const.ARROW_AT_SATURATION:
                return data;
            case Const.ARROW_AT_COLOR_TEMPERATURE:
                return Const.TARGET_ARROW_AT_COLOR_TEMPERATURE;
            case Const.ARROW_AT_CCT_EFFECT_NO:
            case Const.ARROW_AT_HSI_EFFECT_NO:
            case Const.ARROW_AT_EFFECT_NO:
                return Const.TARGET_ARROW_AT_EFFECT_NO;
            case Const.ARROW_AT_EFFECT_SPEED:
                return Const.TARGET_ARROW_AT_EFFECT_SPEED;
            case Const.ARROW_AT_BRIGHTNESS:
                return Const.TARGET_ARROW_AT_BRIGHTNESS;
        }
        return data;
    }
}
