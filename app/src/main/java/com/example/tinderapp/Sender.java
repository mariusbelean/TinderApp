package com.example.tinderapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Sender extends AsyncTask<String,Void,Void> {
    private Socket s;
    //private EditText et;
   // private String txt;
    private PrintWriter writer;

    //nnn
//    public void Send_data(String stg){
//        String message = stg;
//
//        BackgroundTask bt = new BackgroundTask();
//        bt.execute(message);
//    }

//    class BackgroundTask extends AsyncTask<String,Void,Void> {
//        Socket s;
//        PrintWriter writer;

        @Override
        protected Void doInBackground(String... voids) {
            String message = voids[0];
            try {
                s = new Socket("172.20.10.2", 5100);
                writer = new PrintWriter(s.getOutputStream());
                writer.write(message);
                writer.flush();
                writer.close();
                s.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

