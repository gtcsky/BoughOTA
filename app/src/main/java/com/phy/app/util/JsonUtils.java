package com.phy.app.util;


import com.phy.app.ble.bean.OtaFileInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {
    public static List<OtaFileInfo> parseJsonString(String str) {
        List<OtaFileInfo> list = new ArrayList<>();
        String temp;
        try {
            JSONArray jsonArray = new JSONArray(str);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                OtaFileInfo otaFileInfo = new OtaFileInfo();
                otaFileInfo.setDescription(object.getString("description"));
                otaFileInfo.setModel(object.getString("model"));
                try{
                    otaFileInfo.setMD5(object.getString("MD5"));
                }catch (Exception e){
//                    throw new RuntimeException("md5 not exist");
                }
                otaFileInfo.setUrl(object.getString("url"));
                otaFileInfo.setVersion(object.getString("version"));
                list.add(otaFileInfo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
