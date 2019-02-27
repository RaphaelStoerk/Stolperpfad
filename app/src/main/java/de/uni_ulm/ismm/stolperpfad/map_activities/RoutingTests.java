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

        RoadManager roadManager = new MapQuestRoadManager(a.getResources().getString(R.string.mapquest_api_key));

        roadManager.addRequestOption("routeType=pedestrian");

        //RoadManager roadManager = new OSRMRoadManager(this.getActivity());

        ArrayList<GeoPoint> waypoints = new ArrayList<>();
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(48.398638, 9.993720);
        waypoints.add(endPoint);

        Road road = roadManager.getRoad(waypoints);

        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

        map.getOverlays().add(roadOverlay);

        Drawable nodeIcon = a.getResources().getDrawable(R.drawable.marker_cluster);
        for (int i=0; i<road.mNodes.size(); i++){
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setVisible(false);
            nodeMarker.setTitle("Step "+ i);
            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(a, node.mLength, node.mDuration));
            Drawable icon = a.getResources().getDrawable(R.drawable.ic_menu_compass);
            nodeMarker.setImage(icon);
            map.getOverlays().add(nodeMarker);
        }
    }

}
