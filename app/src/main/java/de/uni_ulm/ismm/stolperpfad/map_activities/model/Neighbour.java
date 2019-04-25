package de.uni_ulm.ismm.stolperpfad.map_activities.model;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.maps.MapboxMap;

public class Neighbour {
    private Stone stone;
    private double dist_to;

    public Neighbour(Stone stone, double dist) {
        this.stone = stone;
        this.dist_to = dist;
    }

    public Marker getMarker(MapboxMap mapboxMap) {
        return stone.getMarker(mapboxMap);
    }

    public double getDist() {
        return dist_to;
    }

    public Stone getStone() {
        return stone;
    }
}
