package com.nku.netlab.pete.indoorpos.model;

import java.io.Serializable;

public class WifiScanConfig implements Serializable{
    public static final String WIFI_SCAN_CYCLE_SETTING = "wifi_scan_cycle_setting";
    public static final String WIFI_SCAN_NUM_SETTING = "wifi_scan_num_setting";
    public static final int WIFI_SCAN_MIN_CYCLE = 100; // milliseconds
    public static final int WIFI_SCAN_DEFAULT_CYCLE = 500; // milliseconds
    public static final int WIFI_SCAN_DEFAULT_NUM = 50;
    private int scanCycle;
    private int scanNum;
    private float coordX;
    private float coordY;

    public WifiScanConfig() {
        scanCycle = WIFI_SCAN_DEFAULT_CYCLE;
        scanNum = 10;
        coordX = 0.0f;
        coordY = 0.0f;
    }

    public int getScanCycle() {
        return scanCycle;
    }

    public void setScanCycle(int scanCycle) {
        this.scanCycle = scanCycle;
    }

    public int getScanNum() {
        return scanNum;
    }

    public void setScanNum(int scanNum) {
        this.scanNum = scanNum;
    }
    // TODO: we need to add identifier to building maps
    public String getFloorID() {
        return "1305";
    }

    public float getCoordX() {
        return coordX;
    }

    public void setCoordX(float coordX) {
        this.coordX = coordX;
    }

    public float getCoordY() {
        return coordY;
    }

    public void setCoordY(float coordY) {
        this.coordY = coordY;
    }
}