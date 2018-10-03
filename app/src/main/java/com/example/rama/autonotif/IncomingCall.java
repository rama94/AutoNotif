package com.example.rama.autonotif;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class IncomingCall extends BroadcastReceiver {

    private static final String TAG = "IncomingCall";

    @Override
    public void onReceive(final Context context, Intent intent) {
        boolean isCallChecked = context.getSharedPreferences("isCallChecked",Context.MODE_PRIVATE).getBoolean("isCallChecked", false);
        if(isCallChecked){

            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)){

                //Log.e(TAG, "Inside EXTRA_STATE_RINGING");
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                //Log.e(TAG, "INCOMING incoming number : "+number+" or "+number2);
                new sendNotifTask(context).execute("call|"+number);
                //Toast.makeText(context,"You get incoming call from "+number, Toast.LENGTH_LONG).show();
            }

        } else {
            Log.e(TAG, "doesn't detect call because not activated!");
        }
    }

    public class sendNotifTask extends AsyncTask<String, Void, Void> {

        private static final int timeout = 500;
        private static final String TAG = "sendNotifTask";

        private Socket client;
        private static final int port = 51094;

        private Context context;

        protected sendNotifTask (Context mContext){
            context = mContext;
        }

        @Override
        protected Void doInBackground(String... params) {
            ListIpDbHelper listIpDbHelper = new ListIpDbHelper(context);
            SQLiteDatabase db = listIpDbHelper.getReadableDatabase();

            Cursor cursor = listIpDbHelper.getIp(db);
            while (cursor.moveToNext()) {
                String hostaddr = cursor.getString(cursor.getColumnIndex(ListIpEntity.ContactEntry.IP));
                //Log.d(TAG,"Contacting ip "+hostaddr+"...");
                if(isAddressReachable(hostaddr)) sendNotif(params[0]);
            }

            listIpDbHelper.close();

            return null;
        }

        private boolean isAddressReachable(String addr) {
            try {
                client = new Socket();
                client.connect(new InetSocketAddress(addr,port),timeout);
                //Log.d(TAG,"Connection to "+addr+" is established with port "+port);
                return true;
            } catch (IOException e){
                Log.e(TAG,"Can't connect to "+addr+" port "+port);
                try {
                    client.close();
                } catch (IOException e1) {
                    Log.e(TAG,"no need to close socket");
                }
                return false;
            }
        }

        private void sendNotif(String message) {
            //Log.d(TAG, "Try sending message to PC");
            try {
                PrintWriter printwriter;
                printwriter = new PrintWriter(client.getOutputStream(),true);
                printwriter.write(message);  //write the message to output stream
                Log.d(TAG, message);
                printwriter.flush();
                printwriter.close();
                client.close();   //closing the connection
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.e(TAG, "Unknown Host when sending the message");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Another error!");
            }
        }
    }

}
