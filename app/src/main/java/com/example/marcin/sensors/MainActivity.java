package com.example.marcin.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.nio.file.Files;


public class MainActivity extends Activity {

    private SensorManager senSensorManager;
    private SaveToFile saveToFile;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View textView = findViewById(R.id.textView);
        View textView2 = findViewById(R.id.textView2);
        View textView3 = findViewById(R.id.textView3);
        View QUATERNION = findViewById(R.id.QUATERNION);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); //initialize sensorManager(acces)
        saveToFile = new SaveToFile(getApplicationContext(), senSensorManager);
        saveToFile.deleteFile();
        saveToFile.getTextViews(textView, textView2, textView3, QUATERNION);
        saveToFile.init();



        Thread thread = new Thread(saveToFile);
        thread.start();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveToFile.deleteFile();

    }

    @Override
    protected void onResume() {
        super.onResume();
        saveToFile.deleteFile();
        saveToFile.registerListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveToFile.unregisterListeners();
    }
}