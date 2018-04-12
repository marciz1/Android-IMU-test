package com.example.marcin.sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends Activity {

    private SensorManager senSensorManager;
    private SaveToFile saveToFile;

    private Socket socket;
    private static final String SERVER_IP = "192.198.0.103";
    private static final int SERVERPORT = 4444;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View textView = findViewById(R.id.textView);
        View textView2 = findViewById(R.id.textView2);
        View textView3 = findViewById(R.id.textView3);
        View QUATERNION = findViewById(R.id.QUATERNION);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); //initialize sensorManager(acces)

        saveToFile = new SaveToFile(senSensorManager);
        saveToFile.deleteFile("quaternion.txt");
        saveToFile.getTextViews(textView, textView2, textView3, QUATERNION);
        saveToFile.init();

        new Thread(new ClientThread()).start();
        new Thread(saveToFile).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        saveToFile.deleteFile("quaternion.txt");
        saveToFile.registerListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveToFile.unregisterListeners();
    }

    public class ClientThread implements Runnable{

        @Override
        public void run(){


                try {
                    socket = new Socket(SERVER_IP, SERVERPORT);
                } catch (IOException e2) {
                    e2.printStackTrace();
                }


                while(true) {
                    try {
                        InputStream in = socket.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));

                        String test = br.readLine();
                        Log.w("SERVER: ", test);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
    }
}


