package de.uni_ulm.ismm.stolperpfad.map_activities.model;

import android.content.Context;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;

public class Stone {

    private static final int MAX_NEAREST = 5;

    private Context ctx;
    private double lat, lng;
    private GeoPoint location;
    private String first_name, last_name, short_desc;
    private ArrayList<Stone> nearest_stones;
    private Marker marker;


    public Stone(Context ctx, double lat, double lng, String first_name, String last_name, String short_desc) {
        this.lat = lat;
        this.lng = lng;
        this.first_name = first_name;
        this.last_name = last_name;
        this.short_desc = short_desc;
        this.location = new GeoPoint(lat, lng);
        this.ctx = ctx;
    }

    public Marker getMarker(MapView map) {
        if(marker == null) {
            marker= new Marker(map);
            marker.setPosition(location);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(last_name + ", " + first_name);
            marker.setSubDescription(short_desc);
        }
        return marker;
    }

    public GeoPoint getLocation() {
        return location;
    }
    public boolean equals(Object o) {
        if(!(o instanceof Stone)) {
            return false;
        }
        Stone check = (Stone) o;
        return (this.lat == check.lat) && (this.lng == check.lng);
    }

}
