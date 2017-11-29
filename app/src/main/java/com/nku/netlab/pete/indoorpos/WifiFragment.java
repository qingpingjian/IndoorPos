package com.nku.netlab.pete.indoorpos;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class WifiFragment extends Fragment implements View.OnClickListener{
    // the fragment initialization parameters
    private static final String ARG_SCANTYPE = "wifi_scan_type";
    private static final String ARG_SCANNUM = "wifi_scan_num";
    private RadioGroup m_rgpScanType;
    private RadioButton m_rbDataChange, m_rbFixTime;
    private EditText m_edtScanNum;
    private EditText m_edtX;
    private EditText m_edtY;
//    private EditText m_edtOrient;
    private ProgressBar m_pbScanStatus;
    private Button m_btnScan;
    private Button m_btnStop;

    public static WifiFragment newInstance(int scanType, int scanNum) {
        WifiFragment wifiFrag = new WifiFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCANTYPE, scanType);
        args.putInt(ARG_SCANNUM, scanNum);
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
        m_rbDataChange = view.findViewById(R.id.rbDataChange);
        m_rbFixTime = view.findViewById(R.id.rbFixTime);
        m_edtScanNum = (EditText)view.findViewById(R.id.edtScanNum);
        m_edtX = (EditText) view.findViewById(R.id.edtX);
        m_edtY = (EditText) view.findViewById(R.id.edtY);
//        m_edtOrient = (EditText) view.findViewById(R.id.edtOrient);
        m_pbScanStatus = view.findViewById(R.id.pbScanStatus);
        m_btnScan = (Button) view.findViewById(R.id.btnScan);
        m_btnStop = (Button) view.findViewById(R.id.btnStop);
        m_btnScan.setOnClickListener(this);
        m_btnStop.setOnClickListener(this);
        Bundle args = getArguments();
        if (args != null) {
            int scanType = args.getInt(ARG_SCANTYPE);
            if (scanType == 0)
                m_rbDataChange.setChecked(true);
            else
                m_rbFixTime.setChecked(true);
            int scanNum = args.getInt(ARG_SCANNUM);
            m_edtScanNum.setText("" + scanNum);
            m_pbScanStatus.setMax(scanNum);
        }
        MainActivity.info("WiFi: createView.");
        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.btnScan) {
            m_btnScan.setEnabled(false);
            m_btnStop.setEnabled(true);

            MainActivity.info("Scan Button clicked.");
            Toast.makeText(getActivity().getApplicationContext(), "Scan Button clicked",
                    Toast.LENGTH_LONG).show();
        }
        else if (id == R.id.btnStop) {
            m_btnStop.setEnabled(false);
            m_btnScan.setEnabled(true);
            MainActivity.info("Stop Button clicked.");
            Toast.makeText(getActivity().getApplicationContext(), "Stop Button clicked",
                    Toast.LENGTH_LONG).show();
        }
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
