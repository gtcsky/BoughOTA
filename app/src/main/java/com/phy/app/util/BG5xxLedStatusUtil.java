package com.phy.app.util;

import com.phy.app.beans.Const;
import com.phy.app.beans.LEDStatus;

public class BG5xxLedStatusUtil implements LedStatusUtil {
    private String TAG=getClass().getSimpleName();
    /**
     *  解析BLE接收到的数据为Bean
     * @param values
     * @return
     */
    @Override
    public void parseHexBytes(byte[] values, LEDStatus ledStatus) {
        if (values == null || values.length < Const.LED_CTRL_COMMAND_PACKAGE_LEN-1)
            return;
        int data = 0;
        int effectNo = 0;
        for (int i = 0; i < values.length; i++) {
            data = values[i] & 0xFF;
            if((values[0] & 0xFF)==Const.BG5XX_CMD_MODE_CUSTOMIZE_MODE)
                return;                                         //目标端的BG5XX_CMD_MODE_CUSTOMIZE_MODE信号,均来自于APP下发数据的响应
            switch (i) {
                case Const.BLE_PACKAGE_CMD_BYTE:
                    ledStatus.setCmd(data);
                    if(ledStatus.getCmd()== Const.BG5XX_CMD_MODE_CCT_MODE)
                            ledStatus.setRgbMode(false);
                    else if(ledStatus.getCmd()== Const.BG5XX_CMD_MODE_HSI_MODE)
                            ledStatus.setRgbMode(true);
                    break;
                case Const.BLE_PACKAGE_MODE_BYTE:
                    ledStatus.setMode(data);
                    break;
                case Const.BLE_PACKAGE_BRIGHTNESS_BYTE:
                    ledStatus.setBrightness(data);
                    break;
                case Const.BLE_PACKAGE_CCT_BYTE:

                    ledStatus.setColorTemperature(data);
                    if (ledStatus.getCmd() == Const.BG5XX_CMD_MODE_CUSTOMIZE_MODE) {
                        if (data != 0xff)
                            ledStatus.setRgbMode(false);
                    }
                    break;
                case Const.BLE_PACKAGE_HUES_lOW_BYTE:
                    ledStatus.setHues(data);
                    break;
                case Const.BLE_PACKAGE_HUES_HIGH_BYTE:
                    data = ((data * 256) | ledStatus.getHues()) & 0xffff;
                    ledStatus.setHues(data);
                    if (ledStatus.getCmd() == Const.BG5XX_CMD_MODE_CUSTOMIZE_MODE) {
                        if (data != 0xffff) {
                            ledStatus.setRgbMode(true);
                        }
                    }
                    break;
                case Const.BLE_PACKAGE_SATURATION_BYTE:
                    ledStatus.setSaturation(data);
                    if (ledStatus.getCmd() == Const.BG5XX_CMD_MODE_CUSTOMIZE_MODE) {
                        if (data != 0xff) {
                            ledStatus.setRgbMode(true);
                        }
                    }
                    break;
                case Const.BLE_PACKAGE_EFFECT_MODE_BYTE:
                    ledStatus.setEffectCategory(data);
                    break;
                case Const.BLE_PACKAGE_EFFECT_TIMES:
                    ledStatus.setEffectRepTimes(data);
                    break;
                case Const.BLE_PACKAGE_EFFECT_FREQ:
                    ledStatus.setEffectFreq(data);
                    break;
                case Const.BLE_PACKAGE_ARROW_INDEX:
                    ledStatus.setArrowIndex(parseArrowIndex(data));
                    break;
            }
        }
//        System.out.println("parseHexBytes"+ledStatus);
    }
    /**
     * 把Bean转换成BLE发送的Byte数据
     * @param ledStatus
     * @return
     */
    @Override
    public byte[] compressBeanToArray(LEDStatus ledStatus) {
        byte[] data = new byte[Const.LED_CTRL_COMMAND_PACKAGE_LEN];
    if(ledStatus.isLedOn()){
        data[0] = (byte) ledStatus.getCmd();
//        if(data[0]==0x04)
//            data[0]=0x05;
    }else{
        data[0]=0x00;
    }
//        Log.d(TAG, "compressBeanToArray: "+data[0]);
        if (data[0] != Const.BG5XX_CMD_MODE_PRESET_MODE)
            data[1] = (byte) 0;
        else
            data[1] = (byte) ledStatus.getMode();
        data[2] = (byte) ledStatus.getBrightness();
        if (ledStatus.isRgbMode()) {
            data[3] = (byte) 0xFF;
            data[4] = (byte) (ledStatus.getHues() % 256);
            data[5] = (byte) (ledStatus.getHues() / 256);
            data[6] = (byte) (ledStatus.getSaturation());
            data[7] = (byte) ledStatus.getEffectCategory();
            data[8] = (byte) ledStatus.getEffectRepTimes();
            data[9] = (byte) ledStatus.getEffectFreq();
            data[10] = (byte) convertBleArrowIndex(ledStatus.getArrowIndex());
        } else {
            data[3] = (byte) ledStatus.getColorTemperature();
            data[4] = (byte) 0xFF;
            data[5] = (byte) 0xFF;
            data[6] = (byte) 0xFF;
            data[7] = (byte) ledStatus.getEffectCategory();
            data[8] = (byte) ledStatus.getEffectRepTimes();
            data[9] = (byte) ledStatus.getEffectFreq();
            data[10] = (byte) convertBleArrowIndex(ledStatus.getArrowIndex());
        }
        return data;
    }
    /**
     *  所BLE接收到的Arrow Index信息,解析为APP中的ArrowIndex信息
     * @param data
     * @return
     */
    @Override
    public int parseArrowIndex(int data) {
        return data;
    }
    /**
     * 把APP中Index信息转换成BLE终端接收的ArrowIndex
     * @param data
     * @return
     */
    @Override
    public int convertBleArrowIndex(int data) {
        return data;
    }
}
