package de.uni_ulm.ismm.stolperpfad.map_activities;

import android.location.Location;
import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.geometry.LatLng;

import de.uni_ulm.ismm.stolperpfad.map_activities.model.Stone;

/**
 * A Utility class for common routing calculations
 */
public class RoutingUtil {


    /**
     * calculate the (direct) distance between two markers
     *
     * @param m1 the first marker
     * @param m2 the second marker
     *
     * @return the distance between m1 and m2
     */
    public static double getDist(@NonNull Marker m1,@NonNull  Marker m2) {
        return getDist(m1.getPosition(), m2.getPosition());
    }

    /**
     * calculate the (direct) distance between two stones
     *
     * @param s1 the first marker
     * @param s2 the second marker
     *
     * @return the distance between s1 and s2
     */
    public static double getDist(@NonNull Stone s1, @NonNull Stone s2) {
        return getDist(s1.getLocation(), s2.getLocation());
    }

    /**
     * calculate the (direct) distance between two GeoPoints
     *
     * @param g1 the first marker
     * @param g2 the second marker
     *
     * @return the distance between g1 and g2
     */
    public static double getDist(@NonNull LatLng g1, @NonNull LatLng g2) {
        double lat_dif = g1.getLatitude() - g2.getLatitude();
        double lng_diff = g1.getLongitude() - g2.getLongitude();
        return lat_dif * lat_dif + lng_diff * lng_diff;
    }

    public static LatLng convertLocationToLatLng(Location loc) {
        if(loc == null) {
            return null;
        }
        return new LatLng(loc.getLatitude(), loc.getLongitude());
    }
}
