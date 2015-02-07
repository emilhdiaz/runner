package com.emildiaz.runner.model;

import com.google.android.gms.maps.model.LatLng;
import com.orm.SugarRecord;

public class GeoPoint extends SugarRecord<GeoPoint> {

    Run run;
    double latitude;
    double longitude;

    public GeoPoint() {}

    public GeoPoint(Run run, double latitude, double longitude) {
        this.run = run;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static GeoPoint fromLatLng(Run run, LatLng latLng) {
        return new GeoPoint(run, latLng.latitude, latLng.longitude);
    }

    public LatLng getLatLng() {
        return new LatLng(latitude, longitude);
    }
}
