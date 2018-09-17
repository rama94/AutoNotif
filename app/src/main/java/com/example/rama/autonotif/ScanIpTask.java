package com.example.rama.autonotif;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ScanIpTask extends AsyncTask<String, String, Void> {

    private static final int lower = 1;
    private static final int upper = 255;
    private static final int timeout = 100;
    private static final String TAG = "AsyncScanIP";

    @Override
    protected Void doInBackground(String... params) {

        String subnet = params[0].substring(0, (params[0].indexOf(".",(params[0].indexOf(".", params[0].indexOf(".") + 1))+1))+1);

        for (int i = lower; i <= upper; i++) {
            String host = subnet + i;
            Log.e(TAG, "ip : "+host);
            try {
                InetAddress inetAddress = InetAddress.getByName(host);
                if (inetAddress.isReachable(timeout)){
                    Log.e(TAG,"host : "+inetAddress.getHostAddress()+ ", name : "+inetAddress.getHostName());
                }
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.e(TAG, "Done searching");

        return null;
    }
}
