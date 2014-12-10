package com.emildiaz.runner.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RunnerMapFragment extends MapFragment implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
    private final static String LOG_TAG = "Location Updates";
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static int DEFAULT_CAMERA_UPDATE_INTERVAL = 1000 * 5;
    private final static int FASTEST_UPDATE_INTERVAL = 1000 * 5;
    private final static int NORMAL_UPDATE_INTERVAL = 1000 * 10;
    private final static int DEFAULT_MAP_ZOOM = 20;

    private Polyline polyline;
    private Marker firstMarker;
    private Marker currentMarker;
    private Location lastLocation;
    private Location currentLocation;
    private LocationClient locationClient;
    private LocationRequest locationRequest;
    private LocationUpdateListener locationUpdateListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.locationClient = new LocationClient(this.getActivity(), this, this);
        this.locationRequest = LocationRequest.create();
        this.locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        this.locationRequest.setInterval(NORMAL_UPDATE_INTERVAL);
        this.locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            this.locationUpdateListener = (LocationUpdateListener) activity;
        }
        catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement LocationUpdateListener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        break;
                }
                break;
        }
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        this.locationClient.requestLocationUpdates(this.locationRequest, this);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this.getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }
            catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.d(LOG_TAG, "Connection Failed");
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location location) {
        //        if (!isBetterLocation(location)) return;
        this.lastLocation = (this.currentLocation != null) ? this.currentLocation : location;
        this.currentLocation = location;
        this.locationUpdateListener.onLocationUpdated(location);
        this.animateUpdate();
    }

    public void startTracking() {
        GoogleMap map = this.getMap();
        map.setMyLocationEnabled(true);
        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setAllGesturesEnabled(false);
        map.getUiSettings().setCompassEnabled(true);
        this.locationClient.connect();
    }

    public void stopTracking() {
        this.getMap().setMyLocationEnabled(false);
        if (this.locationClient.isConnected()) {
            this.locationClient.removeLocationUpdates(this);
        }
        locationClient.disconnect();
    }

    protected void animateUpdate() {
        GoogleMap map = this.getMap();
        LatLng lastLatLng = new LatLng(this.lastLocation.getLatitude(), this.lastLocation.getLongitude());
        LatLng currentLatLng = new LatLng(this.currentLocation.getLatitude(), this.currentLocation.getLongitude());

        // Create the polyline
        if (this.polyline == null) {
            this.polyline = map.addPolyline(new PolylineOptions().geodesic(true)
                .color(Color.BLUE)
                .width(15));
        }

        // Create the first marker
        if (this.firstMarker == null) {
            this.firstMarker = map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("Start")
                .position(currentLatLng));
        }

        // Create the current marker
        if (this.currentMarker == null) {
            this.currentMarker = map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Me!")
                .position(currentLatLng));
        }

        // Animate the camera update
        CameraPosition cameraPosition = CameraPosition.builder()
            .target(currentLatLng)
            .bearing(getLocationFromLatLng(lastLatLng).bearingTo(getLocationFromLatLng(currentLatLng)))
            .tilt(90)
            .zoom(DEFAULT_MAP_ZOOM)
            .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), DEFAULT_CAMERA_UPDATE_INTERVAL, null);

        // Animate the marker and polyline update
        double t = 0;
        long start = SystemClock.uptimeMillis();
        LinearInterpolator interpolator = new LinearInterpolator();
        List<LatLng> points = this.polyline.getPoints() != null ?
            this.polyline.getPoints() :
            new ArrayList<LatLng>();

        while (t < 1) {
            double lat = t * currentLatLng.latitude + (1 - t) * lastLatLng.latitude;
            double lng = t * currentLatLng.longitude + (1 - t) * lastLatLng.longitude;
            LatLng intermediateLatLng = new LatLng(lat, lng);
            points.add(intermediateLatLng);
            this.polyline.setPoints(points);
            this.currentMarker.setPosition(intermediateLatLng);
            float elapsed = (float) SystemClock.uptimeMillis() - start;
            t = interpolator.getInterpolation(elapsed / DEFAULT_CAMERA_UPDATE_INTERVAL);
        }

        points.add(currentLatLng);
        this.polyline.setPoints(points);
        this.currentMarker.setPosition(currentLatLng);
    }

    protected Location getLocationFromLatLng(LatLng latLng) {
        Location location = new Location("location");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        location.setTime(new Date().getTime());
        return location;
    }

    protected boolean isBetterLocation(Location location) {
        // New location is always better
        if (this.currentLocation == null) return true;

        long timeDelta = location.getTime() - this.currentLocation.getTime();
        int accuracyDelta = (int) (location.getAccuracy() - this.currentLocation.getAccuracy());

        // Is significantly newer
        if (timeDelta < NORMAL_UPDATE_INTERVAL) return true;

        // Is significantly older
        if (timeDelta > -NORMAL_UPDATE_INTERVAL) return false;

        // Is more accurate
        return accuracyDelta < 0;

    }

    protected boolean servicesConnected() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.getActivity());

        // Google Play Services is available
        if (resultCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this.getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);

            if (errorDialog != null) {
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getFragmentManager(), "Location Updates");
            }

            return false;
        }

        return true;
    }

    public interface LocationUpdateListener {
        public void onLocationUpdated(Location location);
    }
}
