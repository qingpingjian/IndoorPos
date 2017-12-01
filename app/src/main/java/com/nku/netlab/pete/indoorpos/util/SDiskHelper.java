package com.nku.netlab.pete.indoorpos.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SDiskHelper {
    private static final String DIRECTORY_ROOT = "IndoorPos";
    private static final String DIRECTORY_WIFI_MAPPER = "wifimapper";
    private static final String FILE_EXTENTION = "csv";
    private static final String WIFI_MAPPER_HEADLINE = "floorid,coordx,coordy,timestamp,wifiinfos,orient\n";

    private void doCreateFile(File file) throws IOException {
        if (!file.exists()) {
            File folder = file.getParentFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
            file.createNewFile();
        }
    }

    private String getDirectory() {
        return String.format("%s/%s", Environment.getExternalStorageDirectory().toString(), DIRECTORY_ROOT);
    }

    private String getWifiMapperDirectory() {
        return String.format("%s/%s", getDirectory(), DIRECTORY_WIFI_MAPPER);
    }

    private String getCurrentTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(new Date());
    }

    public void toSaveWifiMapper(ArrayList<String> rssList)  throws Exception {
        String fileName = String.format("%s/%s_wifi.%s", getWifiMapperDirectory(), getCurrentTime(), FILE_EXTENTION);
        File wifiFile = new File(fileName);
        if (!wifiFile.exists()) {
            doCreateFile(wifiFile);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(WIFI_MAPPER_HEADLINE);
        for(String wifiRecord : rssList) {
            sb.append(wifiRecord);
        }
        FileWriter fStream = new FileWriter(wifiFile);
        BufferedWriter bufWriter = new BufferedWriter(fStream);
        bufWriter.write(sb.toString());
        bufWriter.close();
    }
}