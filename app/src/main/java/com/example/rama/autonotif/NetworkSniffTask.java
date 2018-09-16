package com.example.rama.autonotif;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.net.InetAddress;

public class NetworkSniffTask extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "NetworkSniffTask";
    private WeakReference<Context> mContextRef;

    protected NetworkSniffTask(Context context) {
        mContextRef = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.e(TAG, "Let's sniff the network");
        try {
            Context context = mContextRef.get();
            if (context != null) {
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo connectionInfo = wm.getConnectionInfo();
                int ipAddress = connectionInfo.getIpAddress();
                String ipString = Formatter.formatIpAddress(ipAddress);
                Log.e(TAG, "activeNetwork: " + String.valueOf(activeNetwork));
                Log.e(TAG, "ipString: " + String.valueOf(ipString));
                String prefix = ipString.substring(0, ipString.lastIndexOf(".") + 1);
                Log.e(TAG, "prefix: " + prefix);
                for (int i = 0; i < 255; i++) {
                    String testIp = prefix + String.valueOf(i);
                    InetAddress name = InetAddress.getByName(testIp);
                    String hostName = name.getCanonicalHostName();
                    if (name.isReachable(2))
                        Log.e(TAG, "Host:" + hostName);
                }
            }
            Log.e(TAG, "done ");
        } catch (Throwable t) {
            Log.e(TAG, "Well that's not good.", t);
        }
        return null;
    }
}
