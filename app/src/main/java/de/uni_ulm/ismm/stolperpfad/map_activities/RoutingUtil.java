package de.uni_ulm.ismm.stolperpfad.map_activities;

import org.osmdroid.views.overlay.Marker;

public class RoutingUtil {

    /**
     * calc the (direct) distance between two markers
     * @param m1 marker 1
     * @param m2 marker 2
     * @return the distance bewtween m1 and m2
     */
    public static double getDist(Marker m1, Marker m2) {
        //TODO: Do not use direct distance but rather path distance
        double lat_dif = m1.getPosition().getLatitude() - m2.getPosition().getLatitude();
        double lng_diff = m1.getPosition().getLongitude() - m2.getPosition().getLongitude();
        return lat_dif * lat_dif + lng_diff * lng_diff;
    }

}
