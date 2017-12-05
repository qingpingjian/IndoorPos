package com.nku.netlab.pete.indoorpos.model;

import java.io.Serializable;

public class WifiScanConfig implements Serializable{
    public static final int WIFI_SCAN_DATA_CHANGE = 0;
    public static final int WIFI_SCAN_FIX_TIME = 1;
    public static final int WIFI_SCAN_MIN_FREQUENCY = 100; // milliseconds
    public static final int WIFI_SCAN_DEFAULT_FREQUENCY = 500; // milliseconds
    private int scanType;
    private int scanCycle;
    private int scanNum;
    private float coordX;
    private float coordY;

    public WifiScanConfig() {
        scanType = WIFI_SCAN_FIX_TIME;
        scanCycle = WIFI_SCAN_DEFAULT_FREQUENCY;
        scanNum = 4;
        coordX = 0.0f;
        coordY = 0.0f;
    }
    public int getScanType() {
        return scanType;
    }

    public void setScanType(int scanType) {
        this.scanType = scanType;
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