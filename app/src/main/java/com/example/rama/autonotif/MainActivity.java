package com.example.rama.autonotif;

import android.content.Context;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

//    ArrayList<String> ipList;
//    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getListIp(View view){
        Log.d("message1","The button is pushed");

        //new NetworkSniffTask(getBaseContext()).execute();
        new ScanIpTask().execute();
//        String netAddress = null;
//        try
//        {
//            netAddress = new NetworkSniffTask();
//        }
//        catch (Exception e1)
//        {
//            e1.printStackTrace();
//        }
    }

    private class ScanIpTask extends AsyncTask<Void, String, Void> {

        /*
        Scan IP 192.168.1.100~192.168.1.110
        you should try different timeout for your network/devices
         */
        static final String subnet = "192.168.1.";
        static final int lower = 100;
        static final int upper = 110;
        static final int timeout = 5000;

//        @Override
//        protected void onPreExecute() {
//            ipList.clear();
//            adapter.notifyDataSetInvalidated();
//            Toast.makeText(MainActivity.this, "Scan IP...", Toast.LENGTH_LONG).show();
//        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = lower; i <= upper; i++) {
                String host = subnet + i;
                Log.e("ScanIpTask", "ip : "+host);
                try {
                    InetAddress inetAddress = InetAddress.getByName(host);
                    if (inetAddress.isReachable(timeout)){
                        //publishProgress(inetAddress.toString());
                        Log.e("ScanIpTask","host : "+inetAddress.toString());
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }
}
