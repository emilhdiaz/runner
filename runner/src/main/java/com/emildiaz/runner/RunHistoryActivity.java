package com.emildiaz.runner;

import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.emildiaz.runner.fragment.MapFragment;
import com.emildiaz.runner.fragment.RunDetailFragment;
import com.emildiaz.runner.fragment.RunHistoryFragment;
import com.emildiaz.runner.model.Run;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;

@ContentView(R.layout.activity_run_history)
public class RunHistoryActivity extends RoboFragmentActivity implements
    MapFragment.LocationUpdateListener,
    RunHistoryFragment.RunSelectedListener
{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((findViewById(R.id.fragment_container) != null) && (savedInstanceState == null)) {
            RunHistoryFragment runHistoryFragment = new RunHistoryFragment();
            runHistoryFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, runHistoryFragment)
                .commit();
        }
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

    @Override
    public void onRunSelected(Run run) {
        RunDetailFragment runDetailFragment = RunDetailFragment.newInstance(run);
        getFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, runDetailFragment)
            .addToBackStack(null)
            .commit();
    }

    @Override
    public void onLocationUpdated(Location location) {

    }
}
