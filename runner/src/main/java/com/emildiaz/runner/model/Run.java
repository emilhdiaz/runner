package com.emildiaz.runner.model;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.emildiaz.runner.db.GeoPointTable;
import com.emildiaz.runner.db.RunTable;
import com.emildiaz.runner.providers.RunProvider;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class Run {

    private static final double KM_TO_MILES = 0.621371;

    Context context;
    Uri uri;
    DateTime date = DateTime.now(TimeZone.getDefault());
    double distance = 0.0;
    List<GeoPoint> points = new ArrayList<>();

    private Run() {}

    public Run(Context context, LatLng[] latLngs) {
        GeoPoint[] points = new GeoPoint[latLngs.length];
        for (int i = 0; i < latLngs.length; i++) {
            points[i] = new GeoPoint(context, this, latLngs[i]);
        }
        init(context, points);
    }

    public Run(Context context, GeoPoint[] points) {
        init(context, points);
    }

    private void init(Context context, GeoPoint[] points) {
        this.context = context;
        this.points = new ArrayList(Arrays.asList(points));
        this.distance = calculateDistance(this.points);
    }

    public Uri getUri() {
        return uri;
    }

    public DateTime getDate() {
        return date;
    }

    public double getDistance() {
        return distance;
    }

    public List<GeoPoint> getPoints() {
        return points;
    }

    public double getAvgPace() {
        return 0.0;
    }

    public void save() {
        ContentValues values = new ContentValues();
        values.put(RunTable.COLUMN_DATE, date.getMilliseconds(TimeZone.getDefault()));
        values.put(RunTable.COLUMN_DISTANCE, distance);
        ContentResolver contentResolver = context.getContentResolver();

        if (uri == null) {
            uri = contentResolver.insert(RunProvider.CONTENT_URI, values);
        }
        else {
            contentResolver.update(uri, values, null, null);
        }

        for(GeoPoint point : points) {
            point.save();
        }
    }

    public static Run get(Context context, long id) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = Uri.parse(RunProvider.CONTENT_URI.toString() + '/' + id);
        Run run = new Run();
        Cursor runCursor = contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        );
        while (runCursor.moveToNext()) {
            long date = runCursor.getLong(Arrays.asList(RunTable.COLUMNS).indexOf(RunTable.COLUMN_DATE));
            double distance = runCursor.getDouble(Arrays.asList(RunTable.COLUMNS).indexOf(RunTable.COLUMN_DISTANCE));
            run.context = context;
            run.uri = uri;
            run.date = DateTime.forInstant(date, TimeZone.getDefault());
            run.distance = distance;
        }

        List<GeoPoint> points = new ArrayList<>();
        Cursor pointsCursor =contentResolver.query(
            Uri.parse(RunProvider.CONTENT_URI.toString() + '/' + id + "/points"),
            null,
            null,
            null,
            null
        );

        while (pointsCursor.moveToNext()) {
            points.add(new GeoPoint(
                context,
                run,
                pointsCursor.getDouble(2),
                pointsCursor.getDouble(3)
            ));
        }

        run.points = points;

        return run;
    }

    public static double calculateDistance(List<GeoPoint> geoPoints) {
        double distance = 0.0;
        GeoPoint last = null;
        for(GeoPoint current : geoPoints) {
            if (last != null) {
                distance = distance + SphericalUtil.computeDistanceBetween(last.getLatLng(), current.getLatLng());
            }
            last = current;
        }

        return (distance / 1000) * KM_TO_MILES;
    }
}
