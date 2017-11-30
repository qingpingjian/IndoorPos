package com.nku.netlab.pete.indoorpos.model;

import java.io.Serializable;

public class WifiScanConfig implements Serializable{
    public static final int WIFI_SCAN_DATA_CHANGE = 0;
    public static final int WIFI_SCAN_FIX_TIME = 1;
    private int scanType;
    private int scanNum;
    private float coordX;
    private float coordY;

    public WifiScanConfig() {
        scanType = WIFI_SCAN_FIX_TIME;
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

    public int getScanNum() {
        return scanNum;
    }

    public void setScanNum(int scanNum) {
        this.scanNum = scanNum;
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