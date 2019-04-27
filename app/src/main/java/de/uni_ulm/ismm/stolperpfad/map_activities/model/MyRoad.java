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

import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.database.data_util.DataFromJSON;

public class MyRoad extends Road {

    private Marker start;
    private Marker end;
    private LatLng start_pos;
    private LatLng end_pos;
    private ArrayList<Stone> stones;
    private ArrayList<Integer> stoneIds;
    private String path_name;
    private boolean direct;
    private int requested_time;
    private int time_flag;

    public static final int NO_TIME_REQUESTED = -1;
    public static final int NO_ROAD_FOR_REQ_TIME = -1;

    private MyRoad(ArrayList<GeoPoint> points) {
        super(points);
    }

    private MyRoad(Road road) {
        addRoadInformation(road);
    }

    private MyRoad() {
        direct = false;
    }

    private MyRoad(Marker start, Marker end) {
        this.start = start;
        this.end = end;
        this.stones = new ArrayList<>();
        this.direct = true;
        this.time_flag = NO_TIME_REQUESTED;
    }
    public static MyRoad newInstance() {
        return new MyRoad();
    }

    public static MyRoad newDirectPathInstance(Marker start, Marker end) {
        return new MyRoad(start, end);
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

    public static MyRoad newFromJson(JSONObject path) {
        MyRoad buff = new MyRoad();
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
            } else {

            }
            buff.stoneIds = new ArrayList<>();
            for(int i = 0; i < stones.length(); i++) {
                buff.stoneIds.add(stones.getInt(i));
            }
        }catch(JSONException e) {
            return null;
        }
        return buff;
    }

    public void addRoadInformation(Road road) {
        mStatus = road.mStatus;
        mLength = road.mLength;
        mDuration = road.mDuration;
        mNodes = road.mNodes;
        mLegs = road.mLegs;
        mRouteHigh = road.mRouteHigh;
        mBoundingBox = road.mBoundingBox;
    }

    public ArrayList<GeoPoint> getWaypoints() {
        if(!isValid()) {
            return null;
        }
        ArrayList<GeoPoint> points = new ArrayList<>();
        points.add(new GeoPoint(start.getPosition().getLatitude(), start.getPosition().getLongitude()));
        for(Stone s : stones) {
            points.add(new GeoPoint(s.getLocation().getLatitude(), s.getLocation().getLongitude()));
        }
        if(end != null) {
            points.add(new GeoPoint(end.getPosition().getLatitude(), end.getPosition().getLongitude()));
        }
        return points;
    }

    public boolean saveRoad(String filename) {
        if(!isValid()) {
            return false;
        }
        try {
            JSONObject toSave = new JSONObject();
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
            for(Stone s : this.stones) {
                stones.put(s.getStoneId());
            }
            toSave.put("start", start);
            toSave.put("end", end);
            toSave.put("stones", stones);
            if(time_flag != NO_TIME_REQUESTED)
                toSave.put("time", requested_time);
            else {
                toSave.put("time", NO_TIME_REQUESTED);
            }

            DataFromJSON.saveJsonTo(toSave, filename);

        } catch (JSONException e) {
            return false;
        }
        return true;
    }

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

    @NonNull
    public ArrayList<LatLng> getWaypointsLatLng() {
        if(!isValid()) {
            return new ArrayList<>();
        }
        ArrayList<LatLng> points = new ArrayList<>();
        points.add(start.getPosition());
        for(Stone s : stones) {
            points.add(s.getLocation());
        }
        if(end != null) {
            points.add(end.getPosition());
        }
        return points;
    }

    public LatLng getStartPosition() {
        return start.getPosition();
    }

    public void setStart(Marker start_route_from) {
        this.start = start_route_from;
    }

    public void addStone(Stone next_stone) {
        if(stones == null) {
            stones = new ArrayList<>();
        }
        stones.add(next_stone);
    }
    public Stone getLastStone() {
        if(stones == null || stones.size() == 0) {
            return null;
        }
        return stones.get(stones.size()-1);
    }

    public void addEnd(Marker end_route_at) {
        this.end = end_route_at;
    }

    public void setRequestedTime(int time_in_seconds) {
        this.requested_time = time_in_seconds;
    }

    public void setNotPossible() {
        this.time_flag = NO_ROAD_FOR_REQ_TIME;
    }

    public String getName() {
        return path_name;
    }

    public void inflateFromBasic(StoneFactory handler, MapboxMap map, Icon icon) {
        stones = handler.getStonesFromIds(stoneIds);
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

    public String getTime() {
        if(time_flag == 0) {
            return "" + (requested_time / 60) + " min";
        }
        return "-";
    }

    public String getStoneCount() {
        if(stoneIds == null && stones == null) {
            return "" + 0;
        }
        return "" + (stoneIds == null ? stones.size() : stoneIds.size());
    }

    public String getBasicStart() {
        return start_pos.toString();
    }
}
