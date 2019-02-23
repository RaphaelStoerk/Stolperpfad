package de.uni_ulm.ismm.stolperpfad.map_activities;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.R;

public class RoutingTests {

    public static void test_routing(Activity a, MapView map, GeoPoint startPoint, boolean next) {
        // The following should be automated with database coordinates
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("Ulm");
        startMarker.setSubDescription("Das ist Ulm");

        Marker test = new Marker(map);
        test.setPosition(new GeoPoint(48.398638, 9.993720));
        test.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        test.setTitle("Polatschek");
        test.setSubDescription("Das ist nicht Ulm");


        Marker test2 = new Marker(map);
        test2.setPosition(new GeoPoint(48.39855, 9.99123));
        test2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        test2.setTitle("Test");
        test2.setSubDescription("Noch mal");


        Marker test3 = new Marker(map);
        test3.setPosition(new GeoPoint(48.3893, 9.98924));
        test3.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        test3.setTitle("Test");
        test3.setSubDescription("Noch mal");


        Marker test4 = new Marker(map);
        test4.setPosition(new GeoPoint(48.40002, 9.99721));
        test4.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        test4.setTitle("Test");
        test4.setSubDescription("Noch mal");

        ArrayList<Marker> myMarkers = new ArrayList<>();
        myMarkers.add(test);
        myMarkers.add(test2);
        myMarkers.add(test3);
        myMarkers.add(test4);
        myMarkers.add(startMarker);

        // setting the markers and looking for the nearest stone
        Marker best = null;
        double shortest = -1;
        double buff;
        for(Marker m : myMarkers) {
            m.setIcon(a.getResources().getDrawable(R.drawable.marker_default, null));
            map.getOverlays().add(m);
            buff = RoutingUtil.getDist(startMarker, m);
            if (next && m != startMarker && (buff < shortest || shortest == -1)) {
                shortest = buff;
                best = m;
            }
            if(next) {
                m.setAlpha(0.5f);
            }
            Log.d("TEST", "Set new marker" + m.getPosition().getLongitude());

        }
        if(next) {
            best.setAlpha(1f);
        }

        RoadManager roadManager = new MapQuestRoadManager(a.getResources().getString(R.string.mapquest_api_key));

        roadManager.addRequestOption("routeType=pedestrian");

        //RoadManager roadManager = new OSRMRoadManager(this.getActivity());

        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(48.398638, 9.993720);
        waypoints.add(endPoint);
        waypoints.add(new GeoPoint(48.40002, 9.99721));

        Road road = roadManager.getRoad(waypoints);

        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

        map.getOverlays().add(roadOverlay);

        Drawable nodeIcon = a.getResources().getDrawable(R.drawable.marker_cluster);
        for (int i=0; i<road.mNodes.size(); i++){
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setVisible(false);
            nodeMarker.setTitle("Step "+i);
            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(a, node.mLength, node.mDuration));
            Drawable icon = a.getResources().getDrawable(R.drawable.ic_menu_compass);
            nodeMarker.setImage(icon);
            map.getOverlays().add(nodeMarker);
        }
    }

}
