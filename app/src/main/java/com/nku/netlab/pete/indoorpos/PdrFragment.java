package com.nku.netlab.pete.indoorpos;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nku.netlab.pete.indoorpos.listener.UIOrientationUpdater;


/**
 * A simple {@link Fragment} subclass.
 */
public class PdrFragment extends Fragment implements UIOrientationUpdater {
    private TextView m_tvOrient;

    public static PdrFragment newInstance() {
        PdrFragment pdrFrag = new PdrFragment();
        Bundle args = new Bundle();
        pdrFrag.setArguments(args);
        return pdrFrag;
    }

    public PdrFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_pdr, container, false);
        m_tvOrient = (TextView) view.findViewById(R.id.tvOrient);
        return view;
    }

    @Override
    public void onOrientationChanged(double orient) {
        m_tvOrient.setText(String.format("%.4f", orient));
    }
}
