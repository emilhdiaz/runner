package com.emildiaz.runner.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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

public class RunnerMapFragment extends MapFragment implements
    LocationListener,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener
{
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
    private LocationRequest locationRequest;
    private LocationUpdateListener locationUpdateListener;
    private GoogleApiClient apiClient;


    public interface LocationUpdateListener {
        public void onLocationUpdated(Location location);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        apiClient = new GoogleApiClient.Builder(getActivity())
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(NORMAL_UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            locationUpdateListener = (LocationUpdateListener) activity;
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
        Location location = LocationServices.FusedLocationApi.getLastLocation(apiClient);

        if (location != null) {
            onLocationChanged(location);
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
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
    public void onLocationChanged(Location location) {
        lastLocation = (currentLocation != null) ? currentLocation : location;
        currentLocation = location;
        locationUpdateListener.onLocationUpdated(location);
        animateUpdate();
    }

    public void startTracking() {
        GoogleMap map = getMap();
        map.setMyLocationEnabled(true);
        apiClient.connect();
    }

    public void stopTracking() {
        getMap().setMyLocationEnabled(false);
        if (apiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(apiClient, this);
        }
        apiClient.disconnect();
    }

    protected void animateUpdate() {
        GoogleMap map = getMap();
        LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
        LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        // Create the polyline
        if (polyline == null) {
            polyline = map.addPolyline(new PolylineOptions().geodesic(true)
                .color(Color.BLUE)
                .width(15));
        }

        // Create the first marker
        if (firstMarker == null) {
            firstMarker = map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title("Start")
                .position(currentLatLng));
        }

        // Create the current marker
        if (currentMarker == null) {
            currentMarker = map.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Me!")
                .position(currentLatLng));
        }

        // Animate the camera update
        CameraPosition cameraPosition = CameraPosition.builder()
            .target(currentLatLng)
//            .bearing(getLocationFromLatLng(lastLatLng).bearingTo(getLocationFromLatLng(currentLatLng)))
//            .tilt(90)
            .zoom(DEFAULT_MAP_ZOOM)
            .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), DEFAULT_CAMERA_UPDATE_INTERVAL, null);

        // Animate the marker and polyline update
        double t = 0;
        long start = SystemClock.uptimeMillis();
        LinearInterpolator interpolator = new LinearInterpolator();
        List<LatLng> points = polyline.getPoints() != null ?
            polyline.getPoints() :
            new ArrayList<LatLng>();

        while (t < 1) {
            double lat = t * currentLatLng.latitude + (1 - t) * lastLatLng.latitude;
            double lng = t * currentLatLng.longitude + (1 - t) * lastLatLng.longitude;
            LatLng intermediateLatLng = new LatLng(lat, lng);
            points.add(intermediateLatLng);
            polyline.setPoints(points);
            currentMarker.setPosition(intermediateLatLng);
            float elapsed = (float) SystemClock.uptimeMillis() - start;
            t = interpolator.getInterpolation(elapsed / DEFAULT_CAMERA_UPDATE_INTERVAL);
        }

        points.add(currentLatLng);
        polyline.setPoints(points);
        currentMarker.setPosition(currentLatLng);
    }

    protected Location getLocationFromLatLng(LatLng latLng) {
        Location location = new Location("location");
        location.setLatitude(latLng.latitude);
        location.setLongitude(latLng.longitude);
        location.setTime(new Date().getTime());
        return location;
    }
}
