package com.nku.netlab.pete.indoorpos.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;

import com.nku.netlab.pete.indoorpos.MainActivity;
import com.nku.netlab.pete.indoorpos.model.WifiScanConfig;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class WifiReceiver extends BroadcastReceiver {
    private MainActivity mainActivity;
    private WifiManager wifiManager;
    private ArrayList<String> m_wifiRSSList;
    private Timer m_scanTimer;
    private TimerTask m_scanTimerTask ;
    private Handler m_scanHandler;
    private WifiScanConfig m_config;

    private class ScanHandler extends Handler {
        public void handleMessage(Message msg) {
            getWifiScanResults();
            super.handleMessage(msg);
        }
    }

    public WifiReceiver(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.wifiManager = (WifiManager) this.mainActivity.getSystemService(Context.WIFI_SERVICE);
        this.m_wifiRSSList = new ArrayList<>();
        this.m_scanTimer = null;
        this.m_scanTimerTask = null;
        this.m_scanHandler = new ScanHandler();
    }

    @Override
    public void onReceive( final Context context, final Intent intent ) {
        String action = intent.getAction();
        if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
            getWifiScanResults();
            this.mainActivity.updateWifiScanStatus(m_wifiRSSList.size());
            // we have enough wifi rss records
            if (m_wifiRSSList.size() >= m_config.getScanNum()) {
                stopScan();
                // TODO: Now we need to say the scan results.

            }
            else {
                wifiManager.startScan();
            }
        }
    }

    private void getWifiScanResults() {
        List<ScanResult> result = wifiManager.getScanResults();
        int wifiSize = result.size();
        if (wifiSize > 0) {
            Calendar calendar = Calendar.getInstance();
            StringBuilder sb = new StringBuilder();
            sb.append(calendar.getTimeInMillis());
            sb.append(",");
            ScanResult wifiRecord = null;
            for (int i = 0; i < result.size(); i++) {
                wifiRecord = result.get(i);
                sb.append(wifiRecord.BSSID + ":" + wifiRecord.level);
                if (i < result.size() - 1)
                    sb.append("|");
            }
            m_wifiRSSList.add(sb.toString());
        }
    }

    private void startTimer() {
        if (m_scanTimer == null) {
            m_scanTimer = new Timer();
        }
        if (m_scanTimerTask == null) {
            m_scanTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    m_scanHandler.sendMessage(message);
                }
            };
        }
        if (m_scanTimer != null && m_scanTimerTask != null) {
            // give 100ms for wifi interface
            m_scanTimer.schedule(m_scanTimerTask, 100, 1000); // get scan results for each second
        }
    }

    private void stopTimer() {
        if (m_scanTimer != null) {
            m_scanTimer.cancel();
            m_scanTimer = null;
        }
        if (m_scanTimerTask != null) {
            m_scanTimerTask.cancel();
            m_scanTimerTask = null;
        }
    }

    /**
     *  This should be called in non-UI thread and this method will block its owner thread.
     * */
    public void startScan(WifiScanConfig config) {
        synchronized (this) {
            m_config = config;
            m_wifiRSSList.clear();
        }

        // data change
        if (m_config.getScanType() == 0) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
            try {
                this.mainActivity.registerReceiver(this, filter);
            }
            catch (Exception ex) {
                MainActivity.warn("WiFiReceiver failed to register.", ex);
            }
        }
        else { // fix time
            startTimer();
        }
        this.wifiManager.startScan();
    }

    public void stopScan() {
        if (m_config.getScanType() == 0) {
            try {
                this.mainActivity.unregisterReceiver(this);
            }
            catch (Exception ex) {
                MainActivity.warn("WiFiReceiver failed to unregister.", ex);
            }
        }
        stopTimer();
    }
}