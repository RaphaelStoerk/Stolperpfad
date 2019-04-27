package de.uni_ulm.ismm.stolperpfad.map_activities.model;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.database.data.Stolperstein;

/**
 * This is a model class to represent a "Stolperstein" on the map activities of this application
 */

public class Stone {

    private LatLng location;
    private String first_name, last_name, short_desc;
    private int stoneId;
    private Marker marker;
    ArrayList<Neighbour> neighbours;


    public Stone(Stolperstein stein, String first_name, String last_name) {
        this.stoneId = stein.getStoneId();
        this.first_name = first_name;
        this.last_name = last_name;
        this.short_desc = stein.getAddress();
        this.location = new LatLng(stein.getLatitude(), stein.getLongitude());
        this.neighbours = new ArrayList<>();
    }

    /**
     * Creates a new marker for a given MapView with the current values of the Stone
     * or returns the earlier created Marker
     *
     * @return a Marker representing this Stone
     */
    public Marker getMarker(MapboxMap mapboxMap) {
        if(marker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(location);
            markerOptions.title(last_name + ", " + first_name);
            markerOptions.snippet(short_desc);
            marker = mapboxMap.addMarker(markerOptions);
        }
        return marker;
    }

    /**
     * Returns the geographical position of this Stone as a GeoPoint
     * @return the location of this Stone
     */
    public LatLng getLocation() {
        return location;
    }

    public int getStoneId() {
        return stoneId;
    }

    public String toString() {
        return last_name + ", " + first_name;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Stone)) {
            return false;
        }
        Stone check = (Stone) o;
        return (this.location.getLatitude() == check.getLocation().getLatitude()) &&
                (this.location.getLongitude() == check.getLocation().getLongitude());
    }

    public boolean hasNeighbour(Stone s_to) {
        for(Neighbour n : neighbours) {
            if(n.getStone().equals(s_to)) {
                return true;
            }
        }
        return false;
    }

    public void addNeighbour(double shorest_dist, Stone nearest) {
        neighbours.add(new Neighbour(nearest, shorest_dist));
    }

    public int countNeighbours() {
        return neighbours.size();
    }

    public ArrayList<Neighbour> getNeighbours() {
        return neighbours;
    }
}
