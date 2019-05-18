package de.uni_ulm.ismm.stolperpfad.map_activities.model;

import android.graphics.Color;
import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.database.data_util.DataFromJSON;

/**
 * Extends the OSM class Road to allow for JSON compatability and to help save routes
 * in the external storage
 */
public class Stolperpfad extends Road {

    private static final int NO_TIME_REQUESTED = -1;
    private static final int NO_ROAD_FOR_REQ_TIME = -2;

    private int requested_time;
    private int time_flag;
    private String path_name;
    private Marker start;
    private Marker end;
    private LatLng start_pos;
    private LatLng end_pos;
    private ArrayList<StoneOnMap> stones;
    private ArrayList<Integer> stone_ids;

    /* CONSTRUCTORS AND STATIC INSTANCE METHODS */

    private Stolperpfad(){ /*---*/ }

    private Stolperpfad(Road road) {
        addRoadInformation(road);
    }

    private Stolperpfad(Marker start, Marker end) {
        this.start = start;
        this.end = end;
        this.stones = new ArrayList<>();
        this.time_flag = NO_TIME_REQUESTED;
    }

    public static Stolperpfad newInstance() {
        return new Stolperpfad();
    }

    public static Stolperpfad newDirectPathInstance(Marker start, Marker end) {
        return new Stolperpfad(start, end);
    }

    public static Stolperpfad from(Road road) {
        return new Stolperpfad(road);
    }

    public static Stolperpfad newFromJson(JSONObject path) {
        Stolperpfad buff = new Stolperpfad();
        try {
            JSONObject start = path.getJSONObject("start");
            JSONObject end = path.getJSONObject("end");
            buff.path_name = path.getString("name");
            int time = path.getInt("time");
            if(time != NO_TIME_REQUESTED) {
                buff.requested_time = time;
            }
            JSONArray stones = path.getJSONArray("stones");
            buff.start_pos = new LatLng(start.getDouble("lat"), start.getDouble("lon"));
            String end_status = end.getString("status");
            if(end_status.equals("okay")) {
                buff.end_pos = new LatLng(end.getDouble("lat"), end.getDouble("lon"));
            }
            buff.stone_ids = new ArrayList<>();
            for(int i = 0; i < stones.length(); i++) {
                buff.stone_ids.add(stones.getInt(i));
            }
        }catch(JSONException e) {
            return null;
        }
        return buff;
    }

    /**
     * Creates the minimum information necessary for this object to be a valid road
     *
     * @param handler the stone factory containing all stone informations
     * @param map the map object
     * @param icon the marker icon for the start and end marker
     */
    public void inflateFromBasic(StoneFactory handler, MapboxMap map, Icon icon) {
        stones = handler.getStonesFromIds(stone_ids);
        if(start_pos != null) {
            MarkerOptions start_options = new MarkerOptions();
            start_options.setTitle("Start des Pfades");
            start_options.setPosition(start_pos);
            start_options.setIcon(icon);
            start = map.addMarker(start_options);
        }
        if(end_pos != null) {
            MarkerOptions end_options = new MarkerOptions();
            end_options.setTitle("Ende des Pfades");
            end_options.setPosition(end_pos);
            end_options.setIcon(icon);
            end = map.addMarker(end_options);
        }
    }

    /**
     * Adds the road values from an already created route, TODO: maybe save as Road
     *
     * @param road the created route
     */
    public void addRoadInformation(Road road) {
        mStatus = road.mStatus;
        mLength = road.mLength;
        mDuration = road.mDuration;
        mNodes = road.mNodes;
        mLegs = road.mLegs;
        mRouteHigh = road.mRouteHigh;
        mBoundingBox = road.mBoundingBox;
    }

    /**
     * Checks if the current route is a valid path, meaning it contains all information
     * to start a path creation
     *
     * @return true, if this road is valid
     */
    public boolean isValid() {
        if(start == null) {
            return false;
        }
        if(stones == null){
            return false;
        }
        if(stones.size() == 0 && end == null){
            return false;
        }
        return time_flag != NO_ROAD_FOR_REQ_TIME;
    }

    /**
     * Creates a list of all important waypoints with the stones, the start and the end position
     *
     * @return a list of waypoints
     */
    public ArrayList<GeoPoint> getWaypoints() {
        if(!isValid()) {
            return new ArrayList<>();
        }
        ArrayList<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(start.getPosition().getLatitude(), start.getPosition().getLongitude()));
        for(StoneOnMap s : stones) {
            points.add(new GeoPoint(s.getLocation().getLatitude(), s.getLocation().getLongitude()));
        }
        if(end != null) {
            points.add(new GeoPoint(end.getPosition().getLatitude(), end.getPosition().getLongitude()));
        }
        return points;
    }

    /**
     * Returns thewaypoints of this road as LatLng Objects
     *
     * @return the LatLng waypoints as a list
     */
    @NonNull
    ArrayList<LatLng> getWaypointsLatLng() {
        if(!isValid()) {
            return new ArrayList<>();
        }
        ArrayList<LatLng> points = new ArrayList<>();
        points.add(start.getPosition());
        for(StoneOnMap s : stones) {
            points.add(s.getLocation());
        }
        if(end != null) {
            points.add(end.getPosition());
        }
        return points;
    }

    /* GETTERS AND SETTERS */

    public String getName() {
        return path_name;
    }

    public String getBasicStart() {
        return start_pos.toString();
    }

    public LatLng getStartPosition() {
        return start.getPosition();
    }

    public void setStart(Marker start_route_from) {
        this.start = start_route_from;
    }

    void addEnd(Marker end_route_at) {
        this.end = end_route_at;
    }

    void addStone(StoneOnMap next_stone) {
        if(stones == null) {
            stones = new ArrayList<>();
        }
        stones.add(next_stone);
    }

    StoneOnMap getLastStone() {
        if(stones == null || stones.size() == 0) {
            return null;
        }
        return stones.get(stones.size()-1);
    }

    public String getStoneCount() {
        if(stone_ids == null && stones == null) {
            return "" + 0;
        }
        return "" + (stone_ids == null ? stones.size() : stone_ids.size());
    }

    public String getTime() {
        if(time_flag == 0) {
            return "" + (requested_time / 60) + " min";
        }
        return "-";
    }

    void setRequestedTime(int time_in_seconds) {
        this.requested_time = time_in_seconds;
    }

    void setTimeNotPossible() {
        this.time_flag = NO_ROAD_FOR_REQ_TIME;
    }

    /**
     * Saves this object as a json file in the external storage
     *
     * @param filename the new filename for this road
     * @return true, if the road could be saved
     */
    public boolean saveRoad(String filename) {
        if(!isValid()) {
            return false;
        }
        try {
            JSONObject to_save = new JSONObject();
            JSONObject start = new JSONObject();
            JSONObject end = new JSONObject();
            JSONArray stones = new JSONArray();
            if(this.start == null) {
                return false;
            }
            start.put("lat", this.start.getPosition().getLatitude());
            start.put("lon", this.start.getPosition().getLongitude());
            if(this.end != null) {
                end.put("lat", this.end.getPosition().getLatitude());
                end.put("lon", this.end.getPosition().getLongitude());
                end.put("status", "okay");
            } else {
                end.put("status", "no");
            }
            for(StoneOnMap s : this.stones) {
                stones.put(s.getStoneId());
            }
            to_save.put("start", start);
            to_save.put("end", end);
            to_save.put("stones", stones);
            if(time_flag != NO_TIME_REQUESTED)
                to_save.put("time", requested_time);
            else {
                to_save.put("time", NO_TIME_REQUESTED);
            }
            DataFromJSON.saveJsonTo(to_save, filename);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    /**
     * Adds this road as a polyline to the map
     *
     * @param mMapboxMap the map object
     * @return the added polyline
     */
    public Polyline addPathToMap(MapboxMap mMapboxMap) {
        PolylineOptions polyline = new PolylineOptions();
        ArrayList<LatLng> points = new ArrayList<>();
        if(mRouteHigh != null) {
            for (GeoPoint gp : mRouteHigh) {
                points.add(new LatLng(gp.getLatitude(), gp.getLongitude()));
            }
        }
        polyline.addAll(points);
        polyline.width(3);
        polyline.color(Color.argb(255, 251,178,75));
        return mMapboxMap.addPolyline(polyline);
    }
}
