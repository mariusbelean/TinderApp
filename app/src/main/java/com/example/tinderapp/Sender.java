package com.example.tinderapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Sender extends AppCompatActivity {
    private Socket s;
    //private EditText et;
    private String txt;
    private PrintWriter writer;

    //nnn
    public void Send_data(String stg){
        String message = stg;

        BackgroundTask bt = new BackgroundTask();
        bt.execute(message);
    }

    class BackgroundTask extends AsyncTask<String,Void,Void> {
        Socket s;
        PrintWriter writer;

        @Override
        protected Void doInBackground(String... voids) {

            try {
                String message = voids[0];
                s = new Socket("192.168.43.65", 5100);
                writer = new PrintWriter(s.getOutputStream());
                writer.write(message);
                writer.flush();
                writer.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
