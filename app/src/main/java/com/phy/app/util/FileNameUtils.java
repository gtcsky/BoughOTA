package com.phy.app.util;

import static android.text.TextUtils.isEmpty;

public class FileNameUtils {
    /**
     * 从标准的FileName中截取对应的model型号
     *
     * @param fileName  OTA文件名称:如BG584M(LB160) Release 20211021中取出BG584M
     * @return          ModelNo 如:BG584M
     */
    public static String getModelNo(String fileName) {
        if (isEmpty(fileName) || isEmpty(fileName.trim()) || fileName.trim().length() < 6)
            return null;
        String fileNo = fileName.substring(0, 6);
        if (fileNo.contains("(") || fileNo.contains(" ") || fileNo.contains("_") || fileNo.contains("-"))
            fileNo = fileNo.substring(0, 5);
        return fileNo;
    }
}
