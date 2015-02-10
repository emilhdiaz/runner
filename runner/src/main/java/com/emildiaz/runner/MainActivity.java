package com.emildiaz.runner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.emildiaz.runner.model.Run;
import com.google.android.gms.maps.model.LatLng;

import java.util.TimeZone;

import hirondelle.date4j.DateTime;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_main)
public class MainActivity extends RoboActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        populateDummyData();
    }

    private void populateDummyData() {
        Run run1 = new Run(DateTime.now(TimeZone.getDefault()).minus(0, 0, 0, 0, 27, 0, 0, DateTime.DayOverflow.Spillover));
        run1.save();
        run1.addPoint(new LatLng(40.7532238, -73.9775961));
        run1.addPoint(new LatLng(40.7365646, -73.989071));
        run1.addPoint(new LatLng(40.737098, -73.9903523));
        run1.addPoint(new LatLng(40.7359162, -73.9912157));

        Run run2 = new Run(DateTime.now(TimeZone.getDefault()).minus(0, 0, 0, 0, 36, 0, 0, DateTime.DayOverflow.Spillover));
        run2.save();
        run2.addPoint(new LatLng(40.7532238, -73.9775961));
        run2.addPoint(new LatLng(40.7508586, -73.9786452));
        run2.addPoint(new LatLng(40.7494838, -73.9753684));
        run2.addPoint(new LatLng(40.7615555, -73.9665923));
        run2.addPoint(new LatLng(40.7606092, -73.96432779999999));

        run2.addPoint(new LatLng(40.7591407, -73.9598489));
        run2.addPoint(new LatLng(40.7505916, -73.9401946));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    public void newRun(View view) {
        Intent intent = new Intent(this, RunActivity.class);
        startActivity(intent);
    }

    public void viewRunHistory(View view) {
        Intent intent = new Intent(this, RunHistoryActivity.class);
        startActivity(intent);
    }
}
