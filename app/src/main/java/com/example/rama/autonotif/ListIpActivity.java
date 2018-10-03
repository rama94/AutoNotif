package com.example.rama.autonotif;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ListIpActivity extends AppCompatActivity {

    private ListView listView;
    private static final String TAG = "ListIpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_ip);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        //getSupportActionBar().setTitle(R.string.list_ip);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#ffffff'>List Connected PC </font>"));

        listView = findViewById(R.id.lv_list_ip);
        listView.setTextFilterEnabled(true);

        getListIp();

        final Button button= findViewById(R.id.bn_activate);

        SharedPreferences isCallChecked = getApplicationContext().getSharedPreferences("isCallChecked", MODE_PRIVATE);
        SharedPreferences.Editor isCallCheckedEditor = isCallChecked.edit();
        isCallCheckedEditor.putBoolean("isCallChecked", false);
        isCallCheckedEditor.commit();
        isCallCheckedEditor.clear();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Log.d(TAG, "Start button is clicked;");

                getPermission(1); // get phone permission
                getPermission(2); // get call log permission

                SharedPreferences isCallChecked = getApplicationContext().getSharedPreferences("isCallChecked", MODE_PRIVATE);
                SharedPreferences.Editor isCallCheckedEditor = isCallChecked.edit();
                if (!isCallChecked.getBoolean("isCallChecked", false)) {
                    isCallCheckedEditor.putBoolean("isCallChecked", true);
                    //Log.d(TAG, "On going call is activated");
                    button.setText("DEACTIVATE");
                } else {
                    isCallCheckedEditor.putBoolean("isCallChecked", false);
                   // Log.d(TAG, "On going call is not activated");
                    button.setText("ACTIVATE");
                }
                isCallCheckedEditor.commit();
                isCallCheckedEditor.clear();

                //Log.d(TAG, "End button is clicked;");

            }
        });

        Log.d(TAG, "End of end create;");
    }

    private void getPermission(int id){
        switch (id) {
            case 1 :
                if (ContextCompat.checkSelfPermission(ListIpActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ListIpActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                        ActivityCompat.requestPermissions(ListIpActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                    } else {
                        ActivityCompat.requestPermissions(ListIpActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                    }
                } break;
            case 2 :
                if (ContextCompat.checkSelfPermission(ListIpActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(ListIpActivity.this, Manifest.permission.READ_CALL_LOG)) {
                        ActivityCompat.requestPermissions(ListIpActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 2);
                    } else {
                        ActivityCompat.requestPermissions(ListIpActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 2);
                    }
                }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(ListIpActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this,"Permission Granted!", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(this,"No permission granted!!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 2:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(ListIpActivity.this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this,"Permission Granted!", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(this,"No permission granted!!", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_ip,menu);
        //return super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_refresh) {
            item.setEnabled(false);
            getListIp();
            item.setEnabled(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getListIp(){
        ListIpDbHelper listIpDbHelper = new ListIpDbHelper(getApplicationContext());
        SQLiteDatabase database = listIpDbHelper.getWritableDatabase();
        listIpDbHelper.deleteAllIp(database);
        listIpDbHelper.close();

        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {

            WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            //Log.e(TAG, "my ip is : " + ip);
            new scanIP().execute(ip);

        } else {
            Toast.makeText(this,"Not connected to wifi", Toast.LENGTH_SHORT).show();
        }
    }

    public class scanIP extends AsyncTask<String, Void, ArrayList<InetAddress>> {

        private static final int lower = 1;
        private static final int upper = 255;
        private static final int timeout = 100;
        private static final String TAG = "AsyncScanIP";

        private Socket client;
        private static final int port = 51094;

        @Override
        protected ArrayList<InetAddress> doInBackground(String...params) {
            ArrayList<InetAddress> listAddr = new ArrayList<InetAddress>(){};

            String[] parts = params[0].split("\\.");
            String subnet = parts[0]+"."+parts[1]+"."+parts[2]+".";

            Log.d(TAG, "Scanning...");

            ListIpDbHelper listIpDbHelper = new ListIpDbHelper(getApplicationContext());
            SQLiteDatabase database = listIpDbHelper.getWritableDatabase();

            for (int i = lower; i <= upper; i++) {
                String host = subnet + i;
                try {
                    InetAddress inetAddress = InetAddress.getByName(host);
                    String hostname = inetAddress.getHostName();
                    if (!host.equals(hostname) && isAddressReachable(host))
                    {
                        Log.d(TAG, "You can connect to "+hostname+"with IP "+host);
                        listIpDbHelper.addIp(host,hostname,database);

                        listAddr.add(inetAddress);

                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
            listIpDbHelper.close();
            return listAddr;
        }

        @Override
        protected void onPostExecute(ArrayList<InetAddress> listAddr) {
            ArrayList<String> hostnames = new ArrayList<>();

            for (int i = 0; i < listAddr.size(); i++) {
                hostnames.add(listAddr.get(i).getHostName());
            }

            listView.setAdapter(new ArrayAdapter<>(ListIpActivity.this, android.R.layout.simple_list_item_1, hostnames));
        }

        private boolean isAddressReachable(String addr) {
            try {
                client = new Socket();
                client.connect(new InetSocketAddress(addr,port),timeout);
                //Log.d(TAG,"Connection to "+addr+" is established");
                sendNotif();
                return true;
            } catch (IOException e){
                //Log.e(TAG,"Can't connect to "+addr+"");
                try {
                    client.close();
                } catch (IOException e1) {
                    Log.e(TAG,"no need to close socket");
                }
                return false;
            }
        }

        private void sendNotif() {
            Log.d(TAG, "Try sending message to PC");
            try {
                PrintWriter printwriter;
                printwriter = new PrintWriter(client.getOutputStream(),true);
                printwriter.write("Establishing connection");  //write the message to output stream

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
