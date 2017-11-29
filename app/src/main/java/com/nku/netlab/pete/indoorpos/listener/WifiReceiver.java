package com.nku.netlab.pete.indoorpos.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

import com.nku.netlab.pete.indoorpos.MainActivity;

public class WifiReceiver extends BroadcastReceiver {
    private MainActivity mainActivity;

    public WifiReceiver(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setMainActivity( final MainActivity mainActivity ) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive( final Context context, final Intent intent ) {
        String action = intent.getAction();
        if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
//            List<ScanResult> result = m_wifiManager.getScanResults();
//            Calendar calendar = Calendar.getInstance();
//            StringBuilder sb = new StringBuilder();
//            sb.append(calendar.getTimeInMillis());
//            for(ScanResult record : result) {
//                sb.append(", ");
//                sb.append(record.BSSID + "&" + record.level);
//            }
//            sb.append('\n');
//            m_wifiRSSList.add(sb.toString());
//            // Restart scanning.
//            m_wifiManager.startScan();
        }
    }

}
