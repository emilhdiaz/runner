package com.emildiaz.runner.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;

public class Run {

    List<LatLng> latLngHistory;
    private static final double KM_TO_MILES = 0.621371;

    public Run(List<LatLng> latLngHistory) {
        this.latLngHistory = latLngHistory;
    }

    public Run(LatLng[] latLngHistory) {
        this.latLngHistory = new ArrayList(Arrays.asList(latLngHistory));
    }

    public void setLatLngHistory(List<LatLng> latLngHistory) {
        this.latLngHistory = latLngHistory;
    }

    public List<LatLng> getLatLngHistory() {
        return latLngHistory;
    }

    public double getTotalDistance() {
        double distance = 0.0;
        LatLng last = null;
        for(LatLng current : latLngHistory) {
            if (last != null) {
                distance = distance + SphericalUtil.computeDistanceBetween(last,current);
            }
            last = current;
        }

        return (distance / 1000) * KM_TO_MILES;
    }

    public double getAvgPace() {
        return 0.0;
    }

    public DateTime getDate() {
        return DateTime.now(TimeZone.getDefault());
    }
}
