package com.example.marcin.sensors;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by marcin on 21.03.18.
 */

public class SaveToFile implements Runnable {

    private String data;
    private Context context;

    public SaveToFile(String data, Context context) {
        this.data = data;
        this.context = context;
    }



    public void run() {
        generateNoteOnSD(this.context, "Imu.txt", data);
        Log.w("Exeption", "Hello from thread");
    }


    public void generateNoteOnSD(Context context, String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }

            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
