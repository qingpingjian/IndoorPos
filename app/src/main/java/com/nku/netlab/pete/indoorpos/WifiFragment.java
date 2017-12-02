package com.nku.netlab.pete.indoorpos;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import com.nku.netlab.pete.indoorpos.listener.UIOrientationUpdater;
import com.nku.netlab.pete.indoorpos.model.WifiScanConfig;


/**
 * A simple {@link Fragment} subclass.
 */
public class WifiFragment extends Fragment implements View.OnClickListener, UIOrientationUpdater {
    // the fragment initialization parameters
    private static final String ARG_WIFI_SCAN = "wifi_scan_config";

    private OnFragmentWiFiListener m_WifiListener;
    private RadioButton m_rbDataChange, m_rbFixTime;
    private EditText m_edtScanNum;
    private EditText m_edtX;
    private EditText m_edtY;
    private EditText m_edtOrient;
    private ProgressBar m_pbScanStatus;
    private Button m_btnScan;
    private Button m_btnStop;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentWiFiListener {
        // TODO: Update argument type and name
        public boolean onStartScanWifi(WifiScanConfig config);
        public void onStopScanWifi();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        m_WifiListener = null;
        if (context instanceof OnFragmentWiFiListener) {
            m_WifiListener = (OnFragmentWiFiListener)context;
        }
        else if (getActivity() instanceof OnFragmentWiFiListener) {
            m_WifiListener = (OnFragmentWiFiListener)getActivity();
        }
    }

    public static WifiFragment newInstance() {
        WifiFragment wifiFrag = new WifiFragment();
        Bundle args = new Bundle();
        WifiScanConfig config = new WifiScanConfig();
        args.putSerializable(ARG_WIFI_SCAN, config);
        wifiFrag.setArguments(args);
        return wifiFrag;
    }

    public WifiFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.info("WiFi: create.");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_wifi, container, false);
        m_rbDataChange = (RadioButton) view.findViewById(R.id.rbDataChange);
        m_rbFixTime = (RadioButton) view.findViewById(R.id.rbFixTime);
        m_edtScanNum = (EditText)view.findViewById(R.id.edtScanNum);
        m_edtX = (EditText) view.findViewById(R.id.edtX);
        m_edtY = (EditText) view.findViewById(R.id.edtY);
        m_edtOrient = (EditText) view.findViewById(R.id.edtOrient);
        m_pbScanStatus = (ProgressBar) view.findViewById(R.id.pbScanStatus);
        m_btnScan = (Button) view.findViewById(R.id.btnScan);
        m_btnStop = (Button) view.findViewById(R.id.btnStop);
        m_btnScan.setOnClickListener(this);
        m_btnStop.setOnClickListener(this);
        Bundle args = getArguments();
        if (args != null) {
            WifiScanConfig config = (WifiScanConfig) args.getSerializable(ARG_WIFI_SCAN);
            if (config.getScanType() == WifiScanConfig.WIFI_SCAN_DATA_CHANGE)
                m_rbDataChange.setChecked(true);
            else
                m_rbFixTime.setChecked(true);
            m_edtScanNum.setText("" + config.getScanNum());
            m_pbScanStatus.setMax(config.getScanNum());
        }
        MainActivity.info("WiFi: createView.");
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnScan) {
            int scanNum = Integer.parseInt(m_edtScanNum.getText().toString().trim());
            WifiScanConfig config = new WifiScanConfig();
            config.setScanType(m_rbDataChange.isChecked() ? WifiScanConfig.WIFI_SCAN_DATA_CHANGE : WifiScanConfig.WIFI_SCAN_FIX_TIME);
            config.setScanNum(scanNum);
            config.setCoordX(Float.parseFloat(m_edtX.getText().toString().trim()));
            config.setCoordY(Float.parseFloat(m_edtY.getText().toString().trim()));
            if (m_WifiListener.onStartScanWifi(config)) {
                m_btnScan.setEnabled(false);
                m_btnStop.setEnabled(true);
                m_pbScanStatus.setMax(scanNum);
                m_pbScanStatus.setVisibility(View.VISIBLE);
                m_pbScanStatus.setProgress(0);
            }
            MainActivity.info("Scan Button clicked.");
        }
        else if (id == R.id.btnStop) {
            m_btnStop.setEnabled(false);
            m_btnScan.setEnabled(true);
            m_pbScanStatus.setVisibility(View.INVISIBLE);
            m_WifiListener.onStopScanWifi();
            MainActivity.info("Stop Button clicked.");
        }
    }

    public void updateWifiScanStatus(int wifiNum) {
        int scanNum = m_pbScanStatus.getMax();
        if (wifiNum <= 0 || wifiNum > scanNum)
            return;
        m_pbScanStatus.setProgress(wifiNum);
        if (wifiNum == scanNum) {
            m_btnStop.setEnabled(false);
            m_btnScan.setEnabled(true);
            m_pbScanStatus.setVisibility(View.INVISIBLE);
            MainActivity.info("Scan Finished.");
            Context context = getActivity().getApplicationContext();
            Toast.makeText(context,
                    String.format(context.getString(R.string.wifi_scan_finished), scanNum),
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onOrientationChanged(double orientInDegree) {
        m_edtOrient.setText(String.format("%.5f", orientInDegree));
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity.info("WiFi: start.");
    }

    @Override
    public void onResume() {
        super.onResume();
        MainActivity.info("WiFi: resume.");
    }

    @Override
    public void onPause() {
        MainActivity.info("WiFi: pause.");
        super.onPause();
    }

    @Override
    public void onStop() {
        MainActivity.info("WiFi: stop.");
        super.onStop();
    }

    @Override
    public void onDetach() {
        MainActivity.info("WiFi: detach.");
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        MainActivity.info("WiFi: destroy.");
        super.onDestroy();
    }
}
