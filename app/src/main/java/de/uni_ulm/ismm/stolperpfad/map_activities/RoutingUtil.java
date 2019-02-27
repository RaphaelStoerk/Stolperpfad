package de.uni_ulm.ismm.stolperpfad.map_activities;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Marker;

import de.uni_ulm.ismm.stolperpfad.map_activities.model.Stone;

public class RoutingUtil {

    /**
     * calc the (direct) distance between two markers
     * @param m1 marker 1
     * @param m2 marker 2
     * @return the distance bewtween m1 and m2
     */
    public static double getDist(Marker m1, Marker m2) {
        return getDist(m1.getPosition(), m2.getPosition());
    }

    public static double getDist(Stone s1, Stone s2) {
        return getDist(s1.getLocation(), s2.getLocation());
    }

    public static double getDist(GeoPoint g1, GeoPoint g2) {
        //TODO: Do not use direct distance but rather path distance
        double lat_dif = g1.getLatitude() - g2.getLatitude();
        double lng_diff = g1.getLongitude() - g2.getLongitude();
        return lat_dif * lat_dif + lng_diff * lng_diff;
    }

}
