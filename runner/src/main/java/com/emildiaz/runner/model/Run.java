package com.emildiaz.runner.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class Run extends SugarRecord<Run> {

    private static final double KM_TO_MILES = 0.621371;

    Date startDateTime = new Date();
    Date endDateTime;

    @Ignore
    List<GeoPoint> points;

    public Run() {}

    public Run(DateTime startDateTime) {
        this.startDateTime = new Date(startDateTime.getMilliseconds(TimeZone.getDefault()));
    }

    public Run(DateTime startDateTime, DateTime endDateTime) {
        this.startDateTime = new Date(startDateTime.getMilliseconds(TimeZone.getDefault()));
        this.endDateTime = new Date(endDateTime.getMilliseconds(TimeZone.getDefault()));;
    }

    public void addPoint(LatLng latLng) {
        GeoPoint point = GeoPoint.fromLatLng(this, latLng);
        point.save();
    }

    public DateTime getStartDateTime() {
        return DateTime.forInstant(startDateTime.getTime(), TimeZone.getDefault());
    }

    public DateTime getEndDateTime() {
        Date endDateTime = this.endDateTime;
        if (endDateTime == null) {
            endDateTime = new Date();
        }
        return DateTime.forInstant(endDateTime.getTime(), TimeZone.getDefault());
    }

    public List<GeoPoint> getPoints() {
        if (points == null) {
            points = GeoPoint.find(GeoPoint.class, "run = ?", this.getId().toString());
        }
        return points;
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

    public long calculateDuration() {
        DateTime startDateTime = getStartDateTime();
        DateTime endDateTime = getEndDateTime();
        return endDateTime.numSecondsFrom(startDateTime);
    }

    public double calculateAveragePace() {
        return calculateDuration() / calculateDistance();
    }

    public double calculateAverageSpeed() {
        return calculateDistance() / calculateDuration();
    }

    @Override
    public void save() {
        if (endDateTime == null) {
            endDateTime = new Date();
        }
        super.save();
    }
}
