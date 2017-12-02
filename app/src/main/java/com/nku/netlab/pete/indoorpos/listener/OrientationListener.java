package com.nku.netlab.pete.indoorpos.listener;

/**
 * Update the orientation in UI thread, all fragments that includes orientation view
 * should implement this listener.
 * **/
public interface OrientationListener {
    public void onOrientationChanged(double orient);
}