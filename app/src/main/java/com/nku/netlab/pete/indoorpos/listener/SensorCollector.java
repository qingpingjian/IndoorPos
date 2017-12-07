package com.nku.netlab.pete.indoorpos.listener;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.nku.netlab.pete.indoorpos.MainActivity;

public class SensorCollector implements SensorEventListener {
    private static final float ORIENTATION_DISABLE = -100.0f;

    private MainActivity mainActivity;
    private SensorManager sensorManager;
    private Sensor [] m_sensors;    // 0 Accelerometer, 1 Gyroscope, 2 Magnetometer, "3 Compass"
    private boolean m_sensorFlag;
    private ArrayList<String>[] m_sensorValueLists;
    private ArrayList<OrientRecord> m_orientValueList;
    // For orientation calculation, we keep the last sensor values.
    private float [] m_lastAcceValue;
    private float [] m_lastMagnValue;
    private Timer m_orientTimer;
    private TimerTask m_orientTimerTask;
    private OrientUpdateHandler m_orientHandler;
    private double m_currentOrientInDegree;

    private class OrientRecord {
        long timeStamp;
        float azimut;

        public long getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }

        public float getAzimut() {
            return azimut;
        }

        public void setAzimut(float azimut) {
            this.azimut = azimut;
        }
    }

    private class OrientUpdateHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            mainActivity.updateFragmentOrientation(m_currentOrientInDegree);
            super.handleMessage(msg);
        }
    }

    public SensorCollector(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        this.sensorManager = (SensorManager) this.mainActivity.getSystemService(Context.SENSOR_SERVICE);
        m_sensors = new Sensor[3];
        m_sensors[0] = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        m_sensors[1] = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        m_sensors[2] = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        m_sensorFlag = false;
        m_sensorValueLists = new ArrayList[4];
        m_orientValueList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            m_sensorValueLists[i] = new ArrayList<>();
        }
        m_lastAcceValue = new float[3];
        m_lastMagnValue = new float[3];
        m_orientHandler = new OrientUpdateHandler();
    }

    private void startOrientUpdate() {
        if (m_orientTimer == null) {
            m_orientTimer = new Timer();
        }
        if (m_orientTimerTask == null) {
            m_orientTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Message message = new Message();
                    message.what = 1;
                    m_orientHandler.sendMessage(message);
                }
            };
        }
        if (m_orientTimer != null && m_orientTimerTask != null) {
            // Start updating the orientation view for users
            m_orientTimer.schedule(m_orientTimerTask, 100, 200);
        }
    }

    private void stopOrientUpdate() {
        if (m_orientTimer != null) {
            m_orientTimer.cancel();
            m_orientTimer = null;
        }
        if (m_orientTimerTask != null) {
            m_orientTimerTask.cancel();
            m_orientTimerTask = null;
        }
    }

    public void registerEventListener() {
        if (sensorManager != null) {
            for (int i = 0; i < 3; i++) {
                sensorManager.registerListener(this, m_sensors[i], SensorManager.SENSOR_DELAY_GAME);
            }
        }
        // Start the orientation update in the meanwhile
        startOrientUpdate();
    }

    public void unregisterEventListener() {
        // Stop the orientation update firstly
        stopOrientUpdate();
        if (sensorManager != null) {
            for (int i = 0; i < 3; i++) {
                sensorManager.unregisterListener(this, m_sensors[i]);
            }
        }
    }

    public void startRecordSensors() {
        synchronized (this) {
            for (int i = 0; i < 4; i++) {
                m_sensorValueLists[i].clear();
            }
            m_orientValueList.clear();
            m_sensorFlag = true;
        }
    }

    public void stopRecordSensors() {
        synchronized (this) {
            m_sensorFlag = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor == null)
            return;

        // Record sensor values to realize all the position algorithm
        recordSensorValues(event);

        // To update the orientation in views of different fragments
        int sensorType = sensor.getType();
        if (sensorType == sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, m_lastAcceValue, 0, event.values.length);
            float azimut = calculateOrientation();
            if (azimut != ORIENTATION_DISABLE) {
                m_currentOrientInDegree = Math.toDegrees(azimut + Math.PI * 2.0) % 360;
            }
        }
        else if (sensorType == sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, m_lastMagnValue, 0, event.values.length);
            float azimut = calculateOrientation();
            if (azimut != ORIENTATION_DISABLE) {
                m_currentOrientInDegree = Math.toDegrees(azimut + Math.PI * 2.0) % 360;
            }
        }
    }

    private void recordSensorValues(SensorEvent event) {
        if (!m_sensorFlag)
            return;
        Sensor sensor = event.sensor;
        int sensorType = sensor.getType();
        StringBuilder sb = new StringBuilder();
        sb.append("1305");
        sb.append(",");
        Long timeStamp = System.currentTimeMillis();
        sb.append(timeStamp);
        sb.append(",");
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
                OrientRecord or = new OrientRecord();
                or.setTimeStamp(timeStamp);
                azimut = azimut > 0 ? azimut : (float)(azimut + Math.PI * 2.0);
                or.setAzimut(azimut);
                m_orientValueList.add(or);
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
                OrientRecord or = new OrientRecord();
                or.setTimeStamp(timeStamp);
                azimut = azimut > 0 ? azimut : (float)(azimut + Math.PI * 2.0);
                or.setAzimut(azimut);
                m_orientValueList.add(or);
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

    /**
     * Get mean of all azimute from > =startTimeInMills to <=endTimeInMills
     * **/
    public double getOrientation(long startTimeInMills, long endTimeInMills) {
        if (endTimeInMills < startTimeInMills)
            return 0.0;
        int startIndex = 0;
        int endIndex = 0;
        int size = m_orientValueList.size();
        long timeStamp;
        for (int i = size - 1; i >= 0; i--) {
            timeStamp = m_orientValueList.get(i).getTimeStamp();
            if (endTimeInMills >= timeStamp){
                endIndex = i;
                break;
            }
        }
        for (int j = endIndex; j >= 0; j--) {
            timeStamp = m_orientValueList.get(j).getTimeStamp();
            if (timeStamp < startTimeInMills) {
                startIndex = j + 1;
                break;
            }
        }
        // Calculate the average of a set of circular data
        double x = 0;
        double y = 0;
        for (int k = startIndex; k <= endIndex; k++) {
            x += Math.cos(m_orientValueList.get(k).getAzimut());
            y += Math.sin(m_orientValueList.get(k).getAzimut());
        }
        double azimutAvg = Math.atan2(y, x);
        azimutAvg = azimutAvg < 0 ? azimutAvg + Math.PI * 2.0 : azimutAvg;
        return azimutAvg;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}