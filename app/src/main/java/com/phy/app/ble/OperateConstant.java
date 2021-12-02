package com.phy.app.ble;

/**
 * Created by zhoululu on 2017/6/21.
 */

public class OperateConstant {
    public static final String SERVICE_UUID = "0000fff0-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_WRITE_UUID = "0000fff3-0000-1000-8000-00805f9b34fb";
//    public static final String CHARACTERISTIC_READ_UUID = "0000ff10-0000-1000-8000-00805f9b34fb";
    public static final String NOTIFICATION_INDICATE_UUID = "0000ffF4-0000-1000-8000-00805f9b34fb";
    public static final String NOTIFICATION_USER_MANUAL_INFO_UUID = "0000fff6-0000-1000-8000-00805f9b34fb";
    public static final String WRITE_USER_MANUAL_INFO_UUID = "0000fff1-0000-1000-8000-00805f9b34fb";

    public static final String DESCRIPTOR_UUID = "00002902-0000-1000-8000-00805f9b34fb";

    public static final String SERVICE_BATTERY_UUID = "0000180f-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_BATTERY_READ_UUID = "00002a19-0000-1000-8000-00805f9b34fb";
    public static final String USER_CURRENT_BATTERY_READ_UUID = "00002a1a-0000-1000-8000-00805f9b34fb";
    public static final String USER_REAL_BATTERY_READ_UUID = "00002a1b-0000-1000-8000-00805f9b34fb";

    public static final String SERVICE_OTA_UUID = "5833ff01-9b8b-5191-6142-22a4536ef123";
    public static final String CHARACTERISTIC_OTA_WRITE_UUID = "5833ff02-9b8b-5191-6142-22a4536ef123";
    public static final String CHARACTERISTIC_OTA_INDICATE_UUID = "5833ff03-9b8b-5191-6142-22a4536ef123";
    public static final String CHARACTERISTIC_OTA_DATA_WRITE_UUID = "5833ff04-9b8b-5191-6142-22a4536ef123";

    public static final String SERVICE_DEVICE_INFO_UUID="0000180a-0000-1000-8000-00805f9b34fb";
    public static final String MODULE_NUMBER_UUID="00002a24-0000-1000-8000-00805f9b34fb";
    public static final String SERIAL_NUMBER_UUID="00002a25-0000-1000-8000-00805f9b34fb";
    public static final String FIRMWARE_VERSION_UUID="00002a26-0000-1000-8000-00805f9b34fb";

    public static final String SERVICE_GENERIC_ACCESS_UUID="00001800-0000-1000-8000-00805f9b34fb";
    public static final String CHARACTERISTIC_DEVICE_NAME_UUID="00002a00-0000-1000-8000-00805f9b34fb";

//    public static final String GET_VERSION = "GET_VERSION";
    public static final String SYNC_TIME = "SYNC_TIME";
    public static final String GET_TIME = "GET_TIME";
    public static final String START_GSENSOR = "START_GSENSOR";
    public static final String STOP_GSENSOR = "STOP_GSENSOR";
    public static final String GSENSOR_DATA = "GSENSOR_DATA";
    public static final String GET_BATTERY = "GET_BATTERY";
    public static final String START_HEART_RATE = "START_HEART_RATE";
    public static final String HEART_RATE_DATA = "HEART_RATE_DATA";
    public static final String HEART_RATE_LAST_DATA = "HEART_RATE_LAST_DATA";
    public static final String SEND_MESSAGE = "SEND_MESSAGE";
    public static final String LED_SETTING = "LED_SETTING";


    public static final String START_OTA = "START_OTA";
    public static final String BOOT_LOAD_VERSION = "BOOT_LOAD_VERSION";
    public static final String FIRMWARE_VERSION = "FIRMWARE_VERSION";

}
