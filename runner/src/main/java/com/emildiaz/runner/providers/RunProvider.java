package com.emildiaz.runner.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.emildiaz.runner.db.DatabaseHelper;
import com.emildiaz.runner.db.GeoPointTable;
import com.emildiaz.runner.db.RunTable;

public class RunProvider extends ContentProvider {
    private DatabaseHelper helper;
    private UriMatcher uriMatcher;

    private static final int CODE_RUNS = 10;
    private static final int CODE_RUN = 20;
    private static final int CODE_POINTS = 30;
    private static final int CODE_POINT = 40;
    private static final String AUTHORITY = "com.emildiaz.runner.providers";
    private static final String BASE_PATH = "runs";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH);

    @Override
    public boolean onCreate() {
        helper = new DatabaseHelper(getContext());
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, BASE_PATH, CODE_RUNS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", CODE_RUN);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#/points", CODE_POINTS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#/points/#", CODE_POINT);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();

        switch (uriMatcher.match(uri)) {
            case CODE_RUNS:
                builder.setTables(RunTable.TABLE_NAME);
                break;
            case CODE_RUN:
                builder.setTables(RunTable.TABLE_NAME);
                builder.appendWhere(RunTable.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case CODE_POINTS:
                builder.setTables(GeoPointTable.TABLE_NAME);
                builder.appendWhere(GeoPointTable.COLUMN_RUN_ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase database = helper.getReadableDatabase();
        Cursor cursor = builder.query(database, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String table = null;
        switch (uriMatcher.match(uri)) {
            case CODE_RUNS:
                table = RunTable.TABLE_NAME;
                break;
            case CODE_POINTS:
                table = GeoPointTable.TABLE_NAME;
                values.put(GeoPointTable.COLUMN_RUN_ID, uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase database = helper.getWritableDatabase();
        long id = database.insert(table, null, values);
        getContext().getContentResolver().notifyChange(uri, null);

        switch (uriMatcher.match(uri)) {
            case CODE_RUNS:
                return Uri.parse(CONTENT_URI.toString() + "/" + id);
            case CODE_POINTS:
                return Uri.parse(CONTENT_URI.toString() + "/" + values.getAsString(GeoPointTable.COLUMN_RUN_ID) + "/points/" + id);
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String table = null;
        String where = null;
        switch (uriMatcher.match(uri)) {
            case CODE_RUN:
                table = RunTable.TABLE_NAME;
                where = RunTable.COLUMN_ID + "=" + uri.getLastPathSegment();
                break;
            case CODE_POINT:
                table = GeoPointTable.TABLE_NAME;
                where = GeoPointTable.COLUMN_RUN_ID + "=" + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase database = helper.getWritableDatabase();
        int countUpdated = database.update(table, values, where, null);
        getContext().getContentResolver().notifyChange(uri, null);
        return countUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String table = null;
        String where = null;
        switch (uriMatcher.match(uri)) {
            case CODE_RUN:
                table = RunTable.TABLE_NAME;
                where = RunTable.COLUMN_ID + "=" + uri.getLastPathSegment();
                break;
            case CODE_POINT:
                table = GeoPointTable.TABLE_NAME;
                where = GeoPointTable.COLUMN_RUN_ID + "=" + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase database = helper.getWritableDatabase();
        int countDeleted = database.delete(table, where, null);
        getContext().getContentResolver().notifyChange(uri, null);
        return countDeleted;
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
