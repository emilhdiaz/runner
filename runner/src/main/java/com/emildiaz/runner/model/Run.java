package com.emildiaz.runner.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.orm.SugarRecord;

import java.util.List;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class Run extends SugarRecord<Run> {

    private static final double KM_TO_MILES = 0.621371;

    DateTime startDateTime = DateTime.now(TimeZone.getDefault());
    DateTime endDateTime;

    public Run() {}

    public Run(DateTime startDateTime, DateTime endDateTime) {
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public void addPoint(LatLng latLng) {
        GeoPoint point = GeoPoint.fromLatLng(this, latLng);
        point.save();
    }

    public DateTime getStartDateTime() {
        return startDateTime;
    }

    public DateTime getEndDateTime() {
        return endDateTime;
    }

    public List<GeoPoint> getPoints() {
        return GeoPoint.find(GeoPoint.class, "run = ?", this.getId().toString());
    }

    public double calculateDistance() {
        double distance = 0.0;
        List<GeoPoint> points = getPoints();

        GeoPoint last = null;
        for(GeoPoint current : points) {
            if (last != null) {
                distance = distance + SphericalUtil.computeDistanceBetween(last.getLatLng(), current.getLatLng());
            }
            last = current;
        }

        return (distance / 1000) * KM_TO_MILES;
    }
}
