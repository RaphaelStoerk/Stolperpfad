package de.uni_ulm.ismm.stolperpfad.map_activities.model;

import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class MyRoad extends Road {

    private MyRoad(ArrayList<GeoPoint> points) {
        super(points);
    }

    private MyRoad(Road road) {
        mStatus = road.mStatus;
        mLength = road.mLength;
        mDuration = road.mDuration;
        mNodes = road.mNodes;
        mLegs = road.mLegs;
        mRouteHigh = road.mRouteHigh;
        mBoundingBox = road.mBoundingBox;
    }

    public static MyRoad getRoadFrom(JSONObject json) {
        ArrayList<GeoPoint> points = new ArrayList<>();

        // TODO: fill waypoints

        return new MyRoad(points);
    }

    public static MyRoad from(Road road) {
        MyRoad ret = new MyRoad(road);
        return ret;
    }

}
