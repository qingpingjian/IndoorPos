package com.nku.netlab.pete.indoorpos.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;

import com.nku.netlab.pete.indoorpos.MainActivity;
import com.nku.netlab.pete.indoorpos.model.WifiScanConfig;
import com.nku.netlab.pete.indoorpos.util.SDiskHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver {
    private MainActivity mainActivity;
    private WifiScanConfig m_config;
    private boolean m_isScanning;
    private WifiManager wifiManager;
    private ArrayList<WifiRecord> m_wifiRecordList;
    private CountDownTimer m_scanWifiCDT;
    private SDiskHelper m_sdHelper;

    private class WifiRecord {
        String floorID;
        float coordX;
        float coordY;
        long timeStamp;
        String wifiInfos;
        double azimut;

        public void setFloorID(String floorID) {
            this.floorID = floorID;
        }

        public void setCoordX(float coordX) {
            this.coordX = coordX;
        }

        public void setCoordY(float coordY) {
            this.coordY = coordY;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public void setWifiInfos(String wifiInfos) {
            this.wifiInfos = wifiInfos;
        }

        public void setAzimut(double azimut) {
            this.azimut = azimut;
        }

        @Override
        public String toString() {
            // "userid,floorid,coordx,coordy,timestamp,wifiinfos,orient"
            StringBuilder sb = new StringBuilder();
            sb.append(Base64.encodeToString((Build.BOARD + "-" + Build.MODEL).getBytes(), Base64.DEFAULT).trim());
            sb.append(",");
            sb.append(floorID);
            sb.append(",");
            sb.append(String.format("%.3f", coordX));
            sb.append(",");
            sb.append(String.format("%.3f", coordY));
            sb.append(",");
            sb.append(timeStamp);
            sb.append(",");
            sb.append(wifiInfos);
            sb.append(",");
            sb.append(String.format("%.4f", azimut));
            sb.append("\n");
            return sb.toString();
        }
    }

    public WifiReceiver(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        m_config = null;
        m_isScanning = false;
        wifiManager = (WifiManager) this.mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        m_wifiRecordList = new ArrayList<>();
        m_sdHelper = new SDiskHelper();
    }

    @Override
    public void onReceive( final Context context, final Intent intent ) {
        String action = intent.getAction();
        if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            Log.i("WIFI", "Received one WiFi record.");
            getWifiScanResults();
        }
    }
    private void getWifiScanResults() {
        List<ScanResult> result = wifiManager.getScanResults();
        int wifiSize = result.size();
        if (wifiSize > 0) {
            // "floorid,coordx,coordy,timestamp,wifiinfos,orient"
            long currentTime = System.currentTimeMillis();
            long lastWifiTime = currentTime - WifiScanConfig.WIFI_SCAN_MIN_CYCLE;
            StringBuilder sb = new StringBuilder();
            ScanResult scanRecord = null;
            for (int i = 0; i < result.size(); i++) {
                scanRecord = result.get(i);
                sb.append(scanRecord.BSSID + "|" + scanRecord.level);
                if (i < result.size() - 1)
                    sb.append(";");
            }
            WifiRecord record = new WifiRecord();
            record.setFloorID(m_config.getFloorID());
            record.setCoordX(m_config.getCoordX());
            record.setCoordY(m_config.getCoordY());
            record.setTimeStamp(currentTime);
            record.setWifiInfos(sb.toString());
            record.setAzimut(mainActivity.getOrientByTimeStamp(lastWifiTime, currentTime));
            m_wifiRecordList.add(record);
            int recordNum = m_wifiRecordList.size();
            // To update the view of wifi fragment
            mainActivity.updateWifiScanStatus(recordNum);
            // We have enough wifi rss records
            if (recordNum >= m_config.getScanNum()) {
                stopScan();
                // Now we need to save the scan results.
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            ArrayList<String> wifiRecordStrList = new ArrayList<String>();
                            for(WifiRecord wifiInfo: m_wifiRecordList)
                                wifiRecordStrList.add(wifiInfo.toString());
                            m_sdHelper.toSaveWifiMapper(wifiRecordStrList);
                        }
                        catch (Exception ex) {
                            MainActivity.warn("WiFiReceiver failed to save rss data.", ex);
                        }
                    }
                }.start();
            }
            else {
                m_scanWifiCDT.start();
            }
        }
    }

    /**
     *  This should be called in non-UI thread and this method will block its owner thread.
     * */
    public void startScan(WifiScanConfig config) {
        synchronized (this) {
            m_config = config;
            m_wifiRecordList.clear();
            m_isScanning = true;
        }

        m_scanWifiCDT = new CountDownTimer(m_config.getScanCycle(), m_config.WIFI_SCAN_MIN_CYCLE) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                wifiManager.startScan();
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        try {
            this.mainActivity.registerReceiver(this, filter);
        }
        catch (Exception ex) {
            MainActivity.warn("WiFiReceiver failed to register.", ex);
        }

        this.wifiManager.startScan();
    }

    public void stopScan() {
        if (!m_isScanning)
            return;
        try {
            this.mainActivity.unregisterReceiver(this);
        }
        catch (Exception ex) {
            MainActivity.warn("WiFiReceiver failed to unregister.", ex);
        }
        m_isScanning = false;
    }


}
