package com.example.marcin.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by marcin on 21.03.18.
 */

public class SaveToFile implements Runnable, SensorEventListener {

    private boolean isRunning;
    private final ReentrantLock lock = new ReentrantLock();

    private View textView;
    private View textView2;
    private View textView3;
    private View QUATERNION;

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private Sensor senGyroscope;
    private Sensor senMagnetometer;
    private Sensor senOrientation;

    private float[] Q = new float[4];
    private float[] A = new float[4];
    private float[] G = new float[4];
    private float[] M = new float[4];

    private Queue<String> queue = new LinkedList<>();

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public SaveToFile(SensorManager senSensorManager) {
        this.senSensorManager = senSensorManager;
        this.isRunning = true;
    }

    public void getTextViews(View QUATERNION) {
        this.textView = textView;
        this.textView2 = textView2;
        this.textView3 = textView3;
        this.QUATERNION = QUATERNION;
    }

    public void registerListeners() {
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senOrientation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void unregisterListeners() {
        senSensorManager.unregisterListener(this);
    }

    public void init() {
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //get reference to sensor
        senGyroscope = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        senMagnetometer = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        senOrientation = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        registerListeners();
    }


    public void getValues(SensorEvent sensorEvent) {

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            A[0] = sensorEvent.values[0];
            A[1] = sensorEvent.values[1];
            A[2] = sensorEvent.values[2];
            A[3] = sensorEvent.timestamp;
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            G[0] = sensorEvent.values[0];
            G[1] = sensorEvent.values[1];
            G[2] = sensorEvent.values[2];
            G[3] = sensorEvent.timestamp;
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            M[0] = sensorEvent.values[0];
            M[1] = sensorEvent.values[1];
            M[2] = sensorEvent.values[2];
            M[3] = sensorEvent.timestamp;
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            SensorManager.getQuaternionFromVector(Q, sensorEvent.values);
            float qTimestamp = sensorEvent.timestamp;
            TextView text = (TextView) QUATERNION;
            text.setText("w = " + Q[0] + "\nx = " + Q[1] + "\ny = " + Q[2] + "\nz = " + Q[3] + "\ntimestamp = " + qTimestamp);

            String QuaternionString = "Q " + Q[0] + ", " + Q[1] + ", " + Q[2] + ", " + Q[3] + ", " + qTimestamp + "\n"
                    + "A " + A[0] + ", " + A[1] + ", " + A[2] + ", " + A[3] + "\n"
                    + "G " + G[0] + ", " + G[1] + ", " + G[2] + ", " + G[3] + "\n"
                    + "M " + M[0] + ", " + M[1] + ", " + M[2] + ", " + M[3] + "\n";

            lock.lock();
            try {
                queue.add(QuaternionString);
            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        getValues(sensorEvent);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    public void run() {
//        deleteFile("quaternion.txt");
        while (isRunning) {
            if (!queue.isEmpty()) {
                lock.lock();
                try {
                    generateNoteOnSD("quaternion.txt", queue.poll());
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public void generateNoteOnSD(String fileName, String fileBody) {

        try {
            File root = new File(Environment.getExternalStorageDirectory(), "IMU");
            if (!root.exists()) {
                root.mkdirs();
            }

            File txtFile = new File(root, fileName);

            FileWriter writer = new FileWriter(txtFile, true);
            writer.append(fileBody + "\n");
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteFile(String fileName) {
        File txtFile = new File(Environment.getExternalStorageDirectory() + "/IMU/" + fileName);
        txtFile.delete();
    }
}

