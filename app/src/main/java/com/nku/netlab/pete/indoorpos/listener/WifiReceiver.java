package com.nku.netlab.pete.indoorpos.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.util.Log;

import com.nku.netlab.pete.indoorpos.MainActivity;
import com.nku.netlab.pete.indoorpos.model.WifiScanConfig;
import com.nku.netlab.pete.indoorpos.util.SDiskHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class WifiReceiver extends BroadcastReceiver {
    private MainActivity mainActivity;
    private boolean m_isScanning;
    private WifiManager wifiManager;
    private ArrayList<String> m_wifiRSSList;
    private ArrayList<WifiRecord> m_wifiRecordList;
//    private Timer m_scanTimer;
//    private TimerTask m_scanTimerTask ;
    private CountDownTimer m_scanWifiCDT;
    private WifiScanConfig m_config;
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

        public long getTimeStamp() {
            return timeStamp;
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
            // "floorid,coordx,coordy,timestamp,wifiinfos,orient"
            StringBuilder sb = new StringBuilder();
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
            sb.append(String.format("%.3f", azimut));
            sb.append("\n");
            return sb.toString();
        }
    }
//    private class ScanHandler extends Handler {
//        public void handleMessage(Message msg) {
//            getWifiScanResults();
//            super.handleMessage(msg);
//        }
//    }

    public WifiReceiver(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.m_isScanning = false;
        this.wifiManager = (WifiManager) this.mainActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        this.m_wifiRSSList = new ArrayList<>();
        this.m_wifiRecordList = new ArrayList<>();
//        this.m_scanTimer = null;
//        this.m_scanTimerTask = null;
//        this.m_scanHandler = new ScanHandler();
        this.m_config = null;
        this.m_sdHelper = new SDiskHelper();
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
            Calendar calendar = Calendar.getInstance();
            long currentTime = calendar.getTimeInMillis();
            long lastWifiTime = 0;
            if (m_wifiRecordList.size() > 0) {
                lastWifiTime = m_wifiRecordList.get(m_wifiRecordList.size() - 1).getTimeStamp();
            }
            else {
                lastWifiTime = currentTime - WifiScanConfig.WIFI_SCAN_MIN_FREQUENCY;
            }
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
            m_wifiRSSList.add(record.toString());
            m_wifiRecordList.add(record);
            int recordNum = m_wifiRSSList.size();
            // To update the view of wifi fragment
            this.mainActivity.updateWifiScanStatus(recordNum);
            // We have enough wifi rss records
            if (recordNum >= m_config.getScanNum()) {
                stopScan();
                // Now we need to save the scan results.
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            m_sdHelper.toSaveWifiMapper(m_wifiRSSList);
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
//
//    private void startTimer() {
//        if (m_scanTimer == null) {
//            m_scanTimer = new Timer();
//        }
//        if (m_scanTimerTask == null) {
//            m_scanTimerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    Message message = new Message();
//                    message.what = 1;
//                    m_scanHandler.sendMessage(message);
//                }
//            };
//        }
//        if (m_scanTimer != null && m_scanTimerTask != null) {
//            // give 100ms for wifi interface
//            m_scanTimer.schedule(m_scanTimerTask, 100, 1000); // get scan results for each second
//        }
//    }
//
//    private void stopTimer() {
//        if (m_scanTimer != null) {
//            m_scanTimer.cancel();
//            m_scanTimer = null;
//        }
//        if (m_scanTimerTask != null) {
//            m_scanTimerTask.cancel();
//            m_scanTimerTask = null;
//        }
//    }

    /**
     *  This should be called in non-UI thread and this method will block its owner thread.
     * */
    public void startScan(WifiScanConfig config) {
        synchronized (this) {
            m_config = config;
            m_wifiRSSList.clear();
            m_wifiRecordList.clear();
            m_isScanning = true;
        }

        int scanCycle = m_config.getScanCycle();
        m_scanWifiCDT = new CountDownTimer(scanCycle, m_config.WIFI_SCAN_MIN_FREQUENCY) {
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



        // data change
//        if (m_config.getScanType() == WifiScanConfig.WIFI_SCAN_DATA_CHANGE) {
//
//        }
//        else { // fix time
//            startTimer();
//        }
        this.wifiManager.startScan();
    }

    public void stopScan() {
        if (!m_isScanning)
            return;
        if (m_config.getScanType() == WifiScanConfig.WIFI_SCAN_DATA_CHANGE) {
            try {
                this.mainActivity.unregisterReceiver(this);
            }
            catch (Exception ex) {
                MainActivity.warn("WiFiReceiver failed to unregister.", ex);
            }
        }
//        stopTimer();
        m_isScanning = false;
    }


}
