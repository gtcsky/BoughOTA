package com.phy.app.util;

import android.util.Log;

import com.phy.app.beans.GattUuidInfo;

import java.util.UUID;

public class UuidUtils {

    public static String getServiceName(UUID uuid) {
        if (uuid == null)
            return "Unknown Service";
        String uuidStr = uuid.toString();
        String first;
        try {
            first = uuidStr.split("-")[0];
        } catch (Exception e) {
            return "Unknown Service";
        }
        if (first.length() == 8) {
            first = first.substring(4);
//            Log.d("UuidUtils", "uuid " + first);
            String name = GattUuidInfo.uuidMap.get(first);
            if (name != null) {
//                    Log.d("UuidUtils", "name "+name);
                return name;
            } else
                return "Unknown Service";
        } else {
            return "Unknown Service";
        }
    }


    public static String getCharacterName(UUID uuid){
        if (uuid == null)
            return "Unknown Characteristic";
        String uuidStr = uuid.toString();
        String first;
        try {
            first = uuidStr.split("-")[0];
        } catch (Exception e) {
            return "Unknown Service";
        }
        if (first.length() == 8) {
            first = first.substring(4);
            String name = GattUuidInfo.charUuidMap.get(first);
            if (name != null) {
                return name;
            } else
                return "Unknown Characteristic";
        } else {
            return "Unknown Characteristic";
        }
    }
}
