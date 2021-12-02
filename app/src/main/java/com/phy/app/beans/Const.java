package com.phy.app.beans;

public class Const {

    public final static int MIN_RGB_STYLE_NO = 0;
    public final static int MAX_RGB_STYLE_NO = 59;
    public final static int MIN_COLOR_TEMP_STYLE_NO = 60;
    public final static int MAX_COLOR_TEMP_STYLE_NO = 75;
    public final static int LED_CTRL_COMMAND_PACKAGE_LEN = 11;

    public final static int ARROW_AT_HUES = 0;
    public final static int ARROW_AT_SATURATION = 1;
    public final static int ARROW_AT_BRIGHTNESS = 2;
    public final static int ARROW_AT_COLOR_TEMPERATURE = 3;
    public final static int ARROW_AT_HSI_EFFECT_NO = 4;
    public final static int ARROW_AT_CCT_EFFECT_NO = 5;
    public final static int ARROW_AT_RESERVE_EFFECT_NO = 6;
    public final static int ARROW_AT_PRESET_EFFECT_MODE = 7;
    public final static int ARROW_AT_EFFECT_GRADUAL_OR_FLASH = 8;
    public final static int ARROW_AT_EFFECT_TIMES = 9;
    public final static int ARROW_AT_EFFECT_NO = 9;
    public final static int ARROW_AT_EFFECT_FREQ = 10;
    public final static int ARROW_AT_EFFECT_SPEED = 10;

    public final static int TAGERT_ARROW_AT_HUES = 0;
    public final static int TARGET_ARROW_AT_SATURATION = 1;
    public final static int TARGET_ARROW_AT_COLOR_TEMPERATURE = 2;
    public final static int TARGET_ARROW_AT_EFFECT_NO = 3;
    public final static int TARGET_ARROW_AT_EFFECT_SPEED = 4;
    public final static int TARGET_ARROW_AT_BRIGHTNESS = 0x0f;

    public final static int BLE_PACKAGE_CMD_BYTE = 0;
    public final static int BLE_PACKAGE_MODE_BYTE = 1;
    public final static int BLE_PACKAGE_BRIGHTNESS_BYTE = 2;
    public final static int BLE_PACKAGE_CCT_BYTE = 3;
    public final static int BLE_PACKAGE_HUES_lOW_BYTE = 4;
    public final static int BLE_PACKAGE_HUES_HIGH_BYTE = 5;
    public final static int BLE_PACKAGE_SATURATION_BYTE = 6;
    public final static int BLE_PACKAGE_EFFECT_MODE_BYTE = 7;
    public final static int BLE_PACKAGE_EFFECT_TIMES = 8;
    public final static int BLE_PACKAGE_EFFECT_NO = 8;                //for BG930
    public final static int BLE_PACKAGE_EFFECT_FREQ = 9;
    public final static int BLE_PACKAGE_EFFECT_SPEED = 9;            //for BG930& BG931
    public final static int BLE_PACKAGE_ARROW_INDEX = 10;            //for BG930& BG931

    public final static int HSI_FRAGMENT_INDEX = 1;
    public final static int CCT_FRAGMENT_INDEX = 2;

    public final static int BG5XX_CMD_MODE_OFF = 0;
    public final static int BG5XX_CMD_MODE_CCT_MODE = 1;
    public final static int BG5XX_CMD_MODE_HSI_MODE = 2;
    public final static int BG5XX_CMD_MODE_PRESET_MODE = 3;
    public final static int BG5XX_CMD_MODE_CUSTOMIZE_MODE = 4;

    public final static int BG5XX_CATEGORY_GRADUAL = 1;
    public final static int BG5XX_CATEGORY_FLASH = 2;
    public final static int BG5XX_CATEGORY_ONE_SHOT = 3;

    public final static int ACTIVITY_RESULT_SEARCH_DEVICE=200;
    public final static int ACTIVITY_RESULT_OTA=300;

    public final static byte UPDATE_MODEL_NUMBER_COMMAND = (byte) 0xAB;
    public final static byte UPDATE_DEVICE_NAME_COMMAND = (byte) 0xA1;
    public final static byte UPDATE_SERIAL_NUMBER_COMMAND = (byte) 0xAC;
    public final static byte UPDATE_LOCAL_NAME_COMMAND = (byte) 0xAD;
    public final static byte LOAD_TARGET_DEFAULT_SETTING = (byte) 0xAF;

    public final static int FILE_DOWNLOAD_OVER = 10011;
    public final static int HTTP_RESPOND = 10010;
    public final static int SWITCH_NOTIFY_TIMEOUT=10012;
    public final static int OTA_INFO_NOTIFY_TIMEOUT=10013;
    public final static int HTTP_NO_RESPOND = 10014;
    public final static int READ_VERSION=10020;
    public final static int AUTO_EXIT=17777;


    public final static char OTA_COMMAND_START_BYTE='B';
    public final static char USER_MANUAL_COMMAND_START_BYTE='A';

    public static final String PRE_ADDRESS = "http://bough2.host.com263.cn/BoughOTAFiles/";
}
