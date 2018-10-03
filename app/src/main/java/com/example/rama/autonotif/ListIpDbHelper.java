package com.example.rama.autonotif;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ListIpDbHelper extends SQLiteOpenHelper{

    //private static final String TAG = "ListIpDbHelper";
    private static final String DATABASE_NAME = "autonotif_db";
    private static final int DATABASE_VERSION = 1;
    private static final String CREATE_TABLE = "create table "+ListIpEntity.ContactEntry.TABLE_NAME+
            "("+ListIpEntity.ContactEntry.IP+" text,"+ListIpEntity.ContactEntry.HOSTNAME+" text);";
    private static final String DROP_TABLE = "drop table if exists "+ListIpEntity.ContactEntry.TABLE_NAME;


    public ListIpDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //Log.d(TAG, "Database created...");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        //Log.d(TAG, "Table is created...");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        //Log.d(TAG, "Table is dropped...");
        onCreate(db);
    }

    public void addIp(String ip, String hostname, SQLiteDatabase db ){
        ContentValues cv = new ContentValues();
        cv.put(ListIpEntity.ContactEntry.IP, ip);
        cv.put(ListIpEntity.ContactEntry.HOSTNAME, hostname);
        db.insert(ListIpEntity.ContactEntry.TABLE_NAME, null, cv);
        //Log.d(TAG, "Data inserted...");
    }

    public Cursor getIp(SQLiteDatabase db){
        String[] projections = {
                ListIpEntity.ContactEntry.IP,
                ListIpEntity.ContactEntry.HOSTNAME
        };

        Cursor cursor = db.query(ListIpEntity.ContactEntry.TABLE_NAME, projections,
                null, null,null, null, null);

        return cursor;
    }

    public void deleteAllIp(SQLiteDatabase db){
        db.delete(ListIpEntity.ContactEntry.TABLE_NAME, null, null);
        //Log.d(TAG, "Delete all data...");
    }
}
