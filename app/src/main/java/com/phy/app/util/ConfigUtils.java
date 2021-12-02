package com.phy.app.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtils {
    private static String TAG="ConfigUtils";

    public static int loadDefault(Properties pp,String dir) {
        String path = dir + "//config";
        File file = new File(path);
        if (file.isDirectory() == false)
            file.mkdir();
        if (!file.exists())
            System.out.println("配置文件不存在");
        File configFile = new File(path + "//config.xml");
//        Log.d(TAG, "loadDefault: "+configFile.getAbsolutePath()+"\t"+configFile.length());

        FileInputStream fis = null;
        try {
            if (configFile.exists()) {
                fis = new FileInputStream(configFile);
                pp.loadFromXML(fis);
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return 0;
    }


    public static int storeConfig(Properties pp,String fileDir) {

        String path = fileDir + "//config";
        File file = new File(path);
        if (file.isDirectory() == false)
            file.mkdir();
		if (!file.exists()){
			System.out.println("创建文件夹失败");
        }

        File configFile = new File(path + "//config.xml");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(configFile);
            pp.storeToXML(fos, "userConfig");

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
//        Log.d(TAG, "storeConfig: "+configFile.getAbsolutePath()+"\t size:"+configFile.length());
        return 0;
    }


}
