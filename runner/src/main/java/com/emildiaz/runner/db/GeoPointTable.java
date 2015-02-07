package com.emildiaz.runner.db;

import android.database.sqlite.SQLiteDatabase;

public class GeoPointTable {
    public static final String TABLE_NAME = "geo_point";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_RUN_ID = "run_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String[] COLUMNS = {
        COLUMN_ID,
        COLUMN_RUN_ID,
        COLUMN_LATITUDE,
        COLUMN_LONGITUDE
    };

    private static final String DATABASE_DROP =
        "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String DATABASE_CREATE =
        "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_RUN_ID + " INTEGER NOT NULL," +
            COLUMN_LATITUDE + " NUMBER NOT NULL," +
            COLUMN_LONGITUDE + " NUMBER NOT NULL" +
        ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(DATABASE_DROP);
        onCreate(database);
    }
}