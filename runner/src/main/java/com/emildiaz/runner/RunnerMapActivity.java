package com.emildiaz.runner;

import android.location.Address;
import android.location.Location;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.emildiaz.runner.fragment.MapFragment;
import com.emildiaz.runner.fragment.RunHistoryFragment;
import com.emildiaz.runner.model.Run;
import com.emildiaz.runner.task.GetAddressTask;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectFragment;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_runner_map)
public class RunnerMapActivity extends RoboActivity implements
    MapFragment.LocationUpdateListener,
    RunHistoryFragment.RunSelectedListener,
    GetAddressTask.AddressUpdateListener
{

    @InjectView(R.id.location_text) TextView locationText;
    @InjectView(R.id.address_text)  TextView addressText;
    @InjectFragment(R.id.map)       MapFragment mapFragment;

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

    @Override
    public void onLocationUpdated(Location location) {
        // Update location on screen
        (new GetAddressTask(this)).execute(location);
        locationText.setText(String.format(
            "Latitude: %f \nLongitude: %f",
            location.getLatitude(),
            location.getLongitude()
        ));
    }

    @Override
    public void onRunSelected(Run run) {
        mapFragment.drawPath(run);
    }

    @Override
    public void onAddressUpdated(Address address) {
        String text = String.format(
            "%s, %s, %s",
            address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
            address.getLocality(),
            address.getCountryName()
        );
        addressText.setText(String.format("Nearest Address: %s \n", text));
    }

    public void startTracking(View view) {
        mapFragment.startTracking();
    }

    public void stopTracking(View view) {
        mapFragment.stopTracking();
    }
}
