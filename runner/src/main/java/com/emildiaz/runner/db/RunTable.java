package com.emildiaz.runner.db;

import android.database.sqlite.SQLiteDatabase;

public class RunTable {
    public static final String TABLE_NAME = "run";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String[] COLUMNS = {
        COLUMN_ID,
        COLUMN_DATE,
        COLUMN_DISTANCE
    };

    private static final String DATABASE_DROP =
        "DROP TABLE IF EXISTS " + TABLE_NAME;
    private static final String DATABASE_CREATE =
        "CREATE TABLE " + TABLE_NAME + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_DATE + " INTEGER NOT NULL," +
            COLUMN_DISTANCE + " INTEGER NOT NULL" +
        ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL(DATABASE_DROP);
        onCreate(database);
    }
}