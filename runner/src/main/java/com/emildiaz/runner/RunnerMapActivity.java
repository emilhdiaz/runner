package com.emildiaz.runner;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.emildiaz.runner.fragment.RunnerMapFragment;
import com.emildiaz.runner.task.GetAddressTask;

public class RunnerMapActivity extends ActionBarActivity implements RunnerMapFragment.LocationUpdateListener, GetAddressTask.AddressUpdateListener {

    private TextView locationText;
    private TextView addressText;
    private RunnerMapFragment runnerMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runner_map);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.locationText = (TextView) findViewById(R.id.location_text);
        this.addressText = (TextView) findViewById(R.id.address_text);
        this.runnerMapFragment = (RunnerMapFragment) getFragmentManager().findFragmentById(R.id.map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationUpdated(Location location) {
        // Update location on screen
        (new GetAddressTask(this)).execute(location);
        this.locationText.setText(String.format("Latitude: %f \nLongitude: %f", location.getLatitude(), location
            .getLongitude()));
    }

    @Override
    public void onAddressUpdated(Address address) {
        String addressText = String.format("%s, %s, %s", address.getMaxAddressLineIndex() > 0 ?
            address.getAddressLine(0) :
            "", address.getLocality(), address.getCountryName());
        this.addressText.setText(String.format("Nearest Address: %s \n", addressText));
    }

    public void startTracking(View view) {
        this.runnerMapFragment.startTracking(this);
    }

    public void stopTracking(View view) {
        this.runnerMapFragment.stopTracking();
    }
}
