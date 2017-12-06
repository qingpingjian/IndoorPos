package com.nku.netlab.pete.indoorpos.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.nku.netlab.pete.indoorpos.MainActivity;

public class OrientationListener implements SensorEventListener {
    private static final float ORIENTATION_DISABLE = -100f;
    private MainActivity mainActivity;
    private SensorManager sensorManager;
    private Sensor [] m_sensors;    // 0 Accelerometer, 1 Magnetometer
    // For orientation calculation, we keep the last sensor values.
    private float [] m_lastAcceValue;
    private float [] m_lastMagnValue;

    public OrientationListener(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        sensorManager = (SensorManager) this.mainActivity.getSystemService(Context.SENSOR_SERVICE);
        m_sensors = new Sensor[2];
        m_sensors[0] = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        m_sensors[1] = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        m_lastAcceValue = new float[3];
        m_lastMagnValue = new float[3];
    }

    public void registerEventListener() {
        if (sensorManager != null) {
            sensorManager.registerListener(this, m_sensors[0], SensorManager.SENSOR_DELAY_NORMAL);
            sensorManager.registerListener(this, m_sensors[1], SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void unregisterEventListener() {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this, m_sensors[0]);
            sensorManager.unregisterListener(this, m_sensors[1]);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor == null)
            return;
        int sensorType = sensor.getType();
        if (sensorType == sensor.TYPE_ACCELEROMETER || sensorType == sensor.TYPE_MAGNETIC_FIELD) {
            if (sensorType == sensor.TYPE_ACCELEROMETER)
                System.arraycopy(event.values, 0, m_lastAcceValue, 0, event.values.length);
            else
                System.arraycopy(event.values, 0, m_lastMagnValue, 0, event.values.length);

            float azimut = calculateOrientation();
            if (azimut != ORIENTATION_DISABLE) {
                // Update UI orientation
                mainActivity.updateFragmentOrientation(Math.toDegrees(azimut + Math.PI * 2.0) % 360);
            }
        }
    }
    private float calculateOrientation() {
        float R[] = new float[9];
        float I[] = new float[9];

        float azimut = ORIENTATION_DISABLE;
        boolean success = SensorManager.getRotationMatrix(R, I, m_lastAcceValue, m_lastMagnValue);
        if (success) {
            float orientation[] = new float[3];
            SensorManager.getOrientation(R, orientation);
            azimut = orientation[0];
        }
        return azimut;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}