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

//    private static final String SERVER_IP = "192.168.100.105";
    private static final String SERVER_IP = "192.168.8.102";

    private static final int SERVERPORT = 4444;

    private Thread save;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.w("SERVER: ", "logtest");
        View QUATERNION = findViewById(R.id.QUATERNION);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        saveToFile = new SaveToFile(senSensorManager);
        saveToFile.deleteFile("quaternion.txt");
        saveToFile.getTextViews(QUATERNION);
        saveToFile.init();

        new Thread(new ClientThread()).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        saveToFile.registerListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveToFile.unregisterListeners();
    }

    public class ClientThread implements Runnable {
        @Override
        public void run() {
            boolean bln = false;
            while (true) {

                if(!bln){
                    try {
                        socket = new Socket(SERVER_IP, SERVERPORT);
                        bln = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                InputStream in = null;
                if (socket != null) {
                    try {
                        in = socket.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String message = br.readLine();
                        Log.w("SERVER: ", message);
                        if (message.equals("START")) {
                            save = new Thread(saveToFile);
                            save.start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        saveToFile.setRunning(false);
                    }
                }
            }
        }
    }
}
