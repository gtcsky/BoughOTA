package com.phy.app.util;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhoululu on 2017/7/6.
 */

public class LogUtil {

    File logFile;
    String time ;

    int fileIndex = 0;
    int logSumIndex = 0;

    public static long logTime;

    private static LogUtil logUtil;

    private LogUtil() {

    }

    public static LogUtil getLogUtilInstance(){
        if(logUtil == null){
            logUtil = new LogUtil();
        }

        return logUtil;
    }

    public void createLogFile(){

        time = getCurrentTime();

        String path = Environment.getExternalStorageDirectory().getPath() + "/phy";

        File file = new File(path);
        if(!file.exists()){
            file.mkdir();
        }

        logFile = new File(path + "/" + ("phy_" + time + "_" + ".txt"));
    }

    public void saveApdu(String content){
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(logFile, true);

            fileWriter.append(getCurrentTime()+" ");
            fileWriter.append(content);
            fileWriter.append("\n");

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            if (fileWriter != null) {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e1) { /* fail silently */ }
            }
        }
    }

    public void save(String content){

        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(logFile, true);

            fileWriter.append(content);
            fileWriter.append("\n");

            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            if (fileWriter != null) {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e1) { /* fail silently */ }
            }
        }finally {
            logTime = System.currentTimeMillis();
            logSumIndex ++;
        }
    }

    public static String getCurrentTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());

        return time;
    }


}
