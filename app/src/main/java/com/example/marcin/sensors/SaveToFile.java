package com.example.marcin.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import static android.content.Context.MODE_PRIVATE;
import static android.os.ParcelFileDescriptor.MODE_APPEND;


/**
 * Created by marcin on 21.03.18.
 */

public class SaveToFile implements Runnable, SensorEventListener {

    private Context context;
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

    private float alastX, alastY, alastZ;
    private float glastX, glastY, glastZ;
    private float mlastX, mlastY, mlastZ;

    private float aTimestamp = 0;
    private float gTimestamp = 0;
    private float mTimestamp = 0;

    float[] Q = new float[4];
    private String QuaternionString;

    private Queue<String> queue = new LinkedList<>();
    private boolean deleteMarker;

    public SaveToFile(Context context, SensorManager senSensorManager) {
        this.context = context;
        this.senSensorManager = senSensorManager;
        this.isRunning = true;
    }

    public void getTextViews(View textView, View textView2, View textView3, View QUATERNION) {
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
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            alastX = x;
            alastY = y;
            alastZ = z;
            aTimestamp = sensorEvent.timestamp;

            TextView text = (TextView) textView;
            text.setText("ACCELEROMETER: x = " + alastX + " y = " + alastY + " z = " + alastZ + " timestamp = " + aTimestamp);

        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            glastX = x;
            glastY = y;
            glastZ = z;
            gTimestamp = sensorEvent.timestamp;

            TextView text = (TextView) textView2;
            text.setText("GYROSCOPE: x = " + glastX + " y = " + glastY + " z = " + glastZ + " timestamp = " + gTimestamp);
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            mlastX = x;
            mlastY = y;
            mlastZ = z;
            mTimestamp = sensorEvent.timestamp;

            TextView text = (TextView) textView3;
            text.setText("MAGNETOMETER: x = " + mlastX + " y = " + mlastY + " z = " + mlastZ + " timestamp = " + mTimestamp);

        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

            SensorManager.getQuaternionFromVector(Q, sensorEvent.values);

            TextView text = (TextView) QUATERNION;
            text.setText("QUATERION: w = " + Q[0] + " x = " + Q[1] + " y = " + Q[2] + " z = " + Q[3]);

            QuaternionString = Q[0] + ", " + Q[1] + ", " + Q[2] + ", " + Q[3];

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
        deleteMarker = false;
        while (isRunning) {
            if (!queue.isEmpty()) {

                lock.lock();
                try {
//                  Log.w("Quaternion", queue.poll());
                    generateNoteOnSD("quaternion.txt", queue.poll());
                } finally {
                    lock.unlock();
                }
            }
        }
    }


    public void generateNoteOnSD(String fileName, String fileBody) {

        try {
            File root = new File(Environment.getExternalStorageDirectory(), "ImuTest1");
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

    public void deleteFile(){
        File txtFile = new File(Environment.getExternalStorageDirectory() + "/ImuTest1", "quaternion.txt");
        txtFile.delete();
    }
}

