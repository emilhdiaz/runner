package com.emildiaz.runner.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

import com.emildiaz.runner.db.GeoPointTable;
import com.google.android.gms.maps.model.LatLng;

public class GeoPoint {

    Context context;
    Uri uri;
    double latitude;
    double longitude;
    Run run;

    public GeoPoint(Context context, Run run, double latitude, double longitude) {
        init(context, run, latitude, longitude);
    }

    public GeoPoint(Context context, Run run, LatLng latLng) {
        init(context, run, latLng.latitude, latLng.longitude);
    }

    private void init (Context context, Run run, double latitude, double longitude) {
        this.context = context;
        this.run = run;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }

    public void save() {
        ContentValues values = new ContentValues();
        values.put(GeoPointTable.COLUMN_LATITUDE, latitude);
        values.put(GeoPointTable.COLUMN_LONGITUDE, longitude);
        ContentResolver contentResolver = context.getContentResolver();

        if (uri == null) {
            uri = contentResolver.insert(Uri.parse(run.getUri().toString() + "/points"), values);
        }
    }
}
