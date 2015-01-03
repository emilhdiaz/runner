package com.emildiaz.runner;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.emildiaz.runner.fragment.RunnerMapFragment;
import com.emildiaz.runner.task.GetAddressTask;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectFragment;
import roboguice.inject.InjectView;

@ContentView(R.layout.activity_runner_map)
public class RunnerMapActivity extends RoboActivity implements RunnerMapFragment.LocationUpdateListener, GetAddressTask.AddressUpdateListener {

    @InjectView(R.id.location_text) TextView locationText;
    @InjectView(R.id.address_text)  TextView addressText;
    @InjectFragment(R.id.map)       RunnerMapFragment runnerMapFragment;

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
        this.locationText.setText(String.format(
            "Latitude: %f \nLongitude: %f",
            location.getLatitude(),
            location.getLongitude()
        ));
    }

    @Override
    public void onAddressUpdated(Address address) {
        String addressText = String.format(
            "%s, %s, %s",
            address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
            address.getLocality(),
            address.getCountryName()
        );
        this.addressText.setText(String.format("Nearest Address: %s \n", addressText));
    }

    public void startTracking(View view) {
        this.runnerMapFragment.startTracking();
    }

    public void stopTracking(View view) {
        this.runnerMapFragment.stopTracking();
    }
}
