package de.uni_ulm.ismm.stolperpfad.map_activities.model;

/**
 * This is a model class to represent a "Stolperstein" on the map activities of this application
 */

public class Stone {
/*
    private GeoPoint location;
    private String first_name, last_name, short_desc;
    private Marker marker;


    public Stone(double lat, double lng, String first_name, String last_name, String short_desc) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.short_desc = short_desc;
        this.location = new GeoPoint(lat, lng);
    }

    /**
     * Creates a new marker for a given MapView with the current values of the Stone
     * or returns the earlier created Marker
     *
     * @param map the Mapview, that will later contain the Marker
     *
     * @return a Marker representing this Stone
     *
    public Marker getMarker(MapView map) {
        if(marker == null) {
            marker = new Marker(map);
            marker.setPosition(location);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(last_name + ", " + first_name);
            marker.setSubDescription(short_desc);
            marker.setSnippet("A Snippet");
        }
        return marker;
    }

    /**
     * Returns the geographical position of this Stone as a GeoPoint
     * @return the location of this Stone
     *
    public GeoPoint getLocation() {
        return location;
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


*/
}
