package com.example.marcin.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends Activity implements SensorEventListener {

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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); //initialize sensorManager(acces)

        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //get reference to sensor
        senGyroscope = senSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        senMagnetometer = senSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        senOrientation = senSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST); //context, sensor, registration time
        senSensorManager.registerListener(this, senGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senOrientation, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //long curTime = System.currentTimeMillis(); //system time

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            alastX = x;
            alastY = y;
            alastZ = z;
            aTimestamp = sensorEvent.timestamp;

            TextView text = (TextView) findViewById(R.id.textView);
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

            TextView text = (TextView) findViewById(R.id.textView2);
            text.setText("GYROSCOPE: x = " + glastX + " y = " + glastY + " z = " + glastZ+ " timestamp = " + gTimestamp);
        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            mlastX = x;
            mlastY = y;
            mlastZ = z;
            mTimestamp = sensorEvent.timestamp;

            TextView text = (TextView) findViewById(R.id.textView3);
            text.setText("MAGNETOMETER: x = " + mlastX + " y = " + mlastY + " z = " + mlastZ+ " timestamp = " + mTimestamp);

        }

        if (sensorEvent.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            SensorManager.getQuaternionFromVector(Q, sensorEvent.values);

            TextView text = (TextView) findViewById(R.id.QUATERNION);
            text.setText("QUATERION: w = " + Q[0] + " x = " + Q[1] + " y = " + Q[2]+ " z = " + Q[3]);

            QuaternionString = "QUATERION: w = " + Q[0] + " x = " + Q[1] + " y = " + Q[2]+ " z = " + Q[3];
        }


        Thread thread = new Thread(new SaveToFile(QuaternionString, getApplicationContext()));
        thread.start();


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senGyroscope, SensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senMagnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        senSensorManager.registerListener(this, senOrientation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        senSensorManager.unregisterListener(this);
    }



}