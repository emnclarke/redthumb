package com.example.testapp;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class MessageSender extends AsyncTask<String, Void, Void>{
    Socket s;
    DataOutputStream dos;
    PrintWriter pw;

    @Override
    protected Void doInBackground(String... voids){

        String message = voids[0];
        try
        {
            s = new Socket("192.168.2.18",1234);
            pw = new PrintWriter(s.getOutputStream());
            pw.write(message);
            pw.flush();
            pw.close();
            s.close();

        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }
}
