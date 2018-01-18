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
    private static final String DIRECTORY_PDR_SENSOR = "pdrsensor";
    private static final String FILE_EXTENTION = "csv";
    private static final String WIFI_MAPPER_HEADLINE = "userid,floorid,coordx,coordy,timestamp,wifiinfos,orient\n";
    private static final String ACCE_PDR_HEADLINE = "userid,floorid,timestamp,acce_x,acce_y,acce_z\n";
    private static final String GYRO_PDR_HEADLINE = "userid,floorid,timestamp,gyro_x,gyro_y,gyro_z\n";
    private static final String MAGN_PDR_HEADLINE = "userid,floorid,timestamp,magn_x,magn_y,magn_z\n";
    private static final String COMP_PDR_HEADLINE = "userid,floorid,timestamp,azimut,pitch,roll\n";

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

    public void toSaveAcceSensor() throws Exception {
        File acceFile = new File("acce.csv");
        if (!acceFile.exists()) {
            doCreateFile(acceFile);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(ACCE_PDR_HEADLINE);

        FileWriter fStream = new FileWriter(acceFile);
        BufferedWriter bufWriter = new BufferedWriter(fStream);
        bufWriter.write(sb.toString());
        bufWriter.close();
    }
}