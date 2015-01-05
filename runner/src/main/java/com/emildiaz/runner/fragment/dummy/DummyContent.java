package com.emildiaz.runner.fragment.dummy;

import com.emildiaz.runner.model.Run;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyContent {

    public static List<Run> ITEMS = new ArrayList<Run>();

    static {
        addItem(new Run(new LatLng[] {
            new LatLng(40.7532238, -73.9775961),
            new LatLng(40.7365646, -73.989071),
            new LatLng(40.737098, -73.9903523),
            new LatLng(40.7359162, -73.9912157)
        }));
        addItem(new Run(new LatLng[] {
            new LatLng(40.7532238, -73.9775961),
            new LatLng(40.7508586, -73.9786452),
            new LatLng(40.7494838, -73.9753684),
            new LatLng(40.7615555, -73.9665923),
            new LatLng(40.7606092, -73.96432779999999),
            new LatLng(40.7591407, -73.9598489),
            new LatLng(40.7505916, -73.9401946),
        }));
    }

    private static void addItem(Run run) {
        ITEMS.add(run);
    }
}
