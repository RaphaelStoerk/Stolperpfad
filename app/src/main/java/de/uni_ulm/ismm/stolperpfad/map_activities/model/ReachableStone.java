package de.uni_ulm.ismm.stolperpfad.map_activities.model;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.maps.MapboxMap;

/**
 * A simple neighbour class for stones not far away from one specific stone
 */
public class ReachableStone {

    private double dist_to;
    private StoneOnMap stone;

    ReachableStone(StoneOnMap stone, double dist) {
        this.stone = stone;
        this.dist_to = dist;
    }

    Marker getMarker(MapboxMap mapboxMap) {
        return stone.getMarker(mapboxMap);
    }

    double getDist() {
        return dist_to;
    }

    public StoneOnMap getStone() {
        return stone;
    }
}
