package com.nku.netlab.pete.indoorpos;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.nku.netlab.pete.indoorpos.listener.UIOrientationListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class PdrFragment extends Fragment implements UIOrientationListener {
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
        setHasOptionsMenu(true);
        m_tvOrient = (TextView) view.findViewById(R.id.tvOrient);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.pdr_online_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Context context = getActivity().getApplicationContext();
        if (id == R.id.pdr_online_item1_setting) {
            Toast.makeText(context,
                    "PDR online PDR Update Clicked",
                    Toast.LENGTH_SHORT).show();
        }
        else if (id == R.id.pdr_online_item2_setting) {
            Toast.makeText(context,
                    "PDR online sensor type Clicked",
                    Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    public void onOrientationChanged(double orient) {
        m_tvOrient.setText(String.format("%.4f", orient));
    }
}
