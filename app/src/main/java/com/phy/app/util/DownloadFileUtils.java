package com.phy.app.util;

import android.content.Context;

import com.phy.app.activity.UserInternetOtaActivity;
import com.phy.app.beans.Const;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFileUtils {
    public static void download(final Context context, final URL url) throws IOException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream is = null;
                FileOutputStream fos = null;
                File cacheFile = UrlUtil.getFileByUrl(context,url);
                try {
                    fos = new FileOutputStream(cacheFile);
                    connection = (HttpURLConnection) url.openConnection();
                    is = connection.getInputStream();
                    byte[] bytes=new byte[1024];
                    int len=0;
                    while((len=is.read(bytes))>0){
                        fos.write(bytes,0,len);
                    }
                    fos.flush();
                    ((UserInternetOtaActivity)context).getHandler().sendEmptyMessage(Const.FILE_DOWNLOAD_OVER);
                } catch (FileNotFoundException e) {
                    ((UserInternetOtaActivity)context).getHandler().sendEmptyMessage(Const.FILE_DOWNLOAD_OVER);
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(fos!=null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(is!=null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(connection!=null)
                        connection.disconnect();
                }

//                Log.d("DownloadFileUtils", "download: len=" + connection.getContentLength());
//                Log.d("DownloadFileUtils", "run: "+cacheFile.exists()+"\t len="+cacheFile.length());
            }
        });
        thread.start();
    }
}
