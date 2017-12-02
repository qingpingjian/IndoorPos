package com.nku.netlab.pete.indoorpos.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.nku.netlab.pete.indoorpos.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class SensorCollector implements SensorEventListener {
    public static final float ORIENTATION_DISABLE = -100f;

    private MainActivity mainActivity;
    private SensorManager sensorManager;
    private Sensor [] m_sensors;    // 0 Accelerometer, 1 Gyroscope, 2 Magnetometer, 3 Compass
    private ArrayList<String>[] m_sensorValueLists;
    // For orientation calculation, we keep the last sensor values.
    private float [] m_lastAcceValue;
    private float [] m_lastMagnValue;


    public SensorCollector(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.sensorManager = (SensorManager) this.mainActivity.getSystemService(Context.SENSOR_SERVICE);
        m_sensors = new Sensor[3];
        m_sensors[0] = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        m_sensors[1] = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        m_sensors[2] = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        m_sensorValueLists = new ArrayList[4];
        for (int i = 0; i < 4; i++) {
            m_sensorValueLists[i] = new ArrayList<>();
        }
        m_lastAcceValue = new float[3];
        m_lastMagnValue = new float[3];
    }

    public void registerEventListener() {
        synchronized (this) {
            for (int i = 0; i < 4; i++) {
                m_sensorValueLists[i].clear();
            }
        }
        if (this.sensorManager != null) {
            for (int i = 0; i < 3; i++) {
                this.sensorManager.registerListener(this, m_sensors[i], SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    public void unregisterEventListener() {
        if (this.sensorManager != null) {
            for (int i = 0; i < 3; i++) {
                this.sensorManager.unregisterListener(this, m_sensors[i]);
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor == null)
            return;
        StringBuilder sb = new StringBuilder();
        sb.append("1305");
        sb.append(",");
        Calendar calendar = Calendar.getInstance();
        Long timeStamp = calendar.getTimeInMillis();
        sb.append(timeStamp);
        sb.append(",");
        int sensorType = sensor.getType();
        // Accelerometer - Oriention and Step Counter
        if (sensorType == sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, m_lastAcceValue, 0, event.values.length);
            sb.append(event.values[0]);
            sb.append(",");
            sb.append(event.values[1]);
            sb.append(",");
            sb.append(event.values[2]);
            sb.append('\n');
            m_sensorValueLists[0].add(sb.toString());
            if (m_sensorValueLists[2].size() > 0) {
                float azimut = calculateOrientation();
                if (azimut != ORIENTATION_DISABLE) {
                    sb.setLength(0);
                    sb.append("1305");
                    sb.append(",");
                    sb.append(timeStamp);
                    sb.append(",");
                    sb.append(azimut);
                    sb.append(",0,0\n");
                    m_sensorValueLists[3].add(sb.toString());
                    // Update UI orientation
                    mainActivity.updateFragmentOrientation(Math.toDegrees(azimut + Math.PI * 2.0) % 360);
                }
            }
        }
        else if (sensorType == sensor.TYPE_GYROSCOPE) {
            sb.append(event.values[0]);
            sb.append(",");
            sb.append(event.values[1]);
            sb.append(",");
            sb.append(event.values[2]);
            sb.append('\n');
            m_sensorValueLists[1].add(sb.toString());
        }
        else if (sensorType == sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, m_lastMagnValue, 0, event.values.length);
            sb.append(event.values[0]);
            sb.append(",");
            sb.append(event.values[1]);
            sb.append(",");
            sb.append(event.values[2]);
            sb.append('\n');
            m_sensorValueLists[2].add(sb.toString());
            if (m_sensorValueLists[0].size() > 0) {
                float azimut = calculateOrientation();
                if (azimut != ORIENTATION_DISABLE) {
                    sb.setLength(0);
                    sb.append("1305");
                    sb.append(",");
                    sb.append(timeStamp);
                    sb.append(",");
                    sb.append(azimut);
                    sb.append(",0,0\n");
                    m_sensorValueLists[3].add(sb.toString());
                    // Update UI orientation
                    mainActivity.updateFragmentOrientation(Math.toDegrees(azimut + Math.PI * 2.0) % 360);
                }
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