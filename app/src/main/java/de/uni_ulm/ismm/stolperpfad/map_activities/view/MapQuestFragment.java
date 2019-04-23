package de.uni_ulm.ismm.stolperpfad.map_activities.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.general.MyMapActionsListener;
import de.uni_ulm.ismm.stolperpfad.map_activities.RoutingUtil;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.RoutePlannerActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.StoneFactory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import com.mapquest.mapping.MapQuest;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MyLocationPresenter;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.util.GeoPoint;

/**
 * A Fragment class representing all our map visualizations.
 * We are using the mapquest mapping and navigation sdk together with pieces
 * of the osmdroid bonus pack to create routes and show imortant places on the map
 *
 * @author Raphael
 */
public class MapQuestFragment extends Fragment {

    // the map visual
    private MapView map;
    //the map controller
    private MapboxMap mMapboxMap;
    // activity context
    private Context ctx;

    // The mapquest api key needed for transactions
    private final String API_KEY;

    // flag, for wether the calling Activity is the next_stone_activity
    private boolean NEXT;
    private boolean icons_loaded = false;

    AQuery aq;

    private static final double FOLLOW_MODE_TILT_VALUE_DEGREES = 50;
    private static final double CENTER_ON_USER_ZOOM_LEVEL = 18;

    // App specific values storing preferences for the routing
    private StoneFactory stone_handler;
    private LatLng chosen_position_start;
    private Marker chosen_marker_start;
    private LatLng chosen_position_end;
    private Marker chosen_marker_end;
    private LatLng ulm_center;
    private Marker ulm_center_marker;

    private Marker user_position_marker;

    private Polyline store_current_drawn_path;
    private MyLocationPresenter locationPresenter;
    protected LocationEngine locationEngine;
    private Location lastLocation;
    private Marker nearest_stone_marker;
    private MyMapActionsListener myActionListener;
    private IconFactory icon_factory;
    private Icon icon_start_end, icon_user, icon_stone, icon_default;
    private Icon icon_start_end_low, icon_user_low, icon_stone_low, icon_default_low, icon_spec_red_low, icon_spec_blue_low, icon_spec_green_low, icon_spec_white_low;

    public MapQuestFragment() {
        // Required empty public constructor
        API_KEY = String.valueOf(R.string.mapquest_api_key);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MapQuestFragment.
     */
    public static MapQuestFragment newInstance(boolean next, AQuery aq) {
        MapQuestFragment fragment = new MapQuestFragment();
        fragment.NEXT = next;
        fragment.aq = aq;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ctx = inflater.getContext();

        // Important Mapquest Initialization
        MapQuest.start(ctx);

        map = new MapView(ctx, null, 0, API_KEY);

        // Initialize the map visuals
        map.onCreate(savedInstanceState);
        map.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;
            map.setStreetMode();
            // Thread load = new Thread(this::loadIcons);
            // load.start();

            stone_handler = StoneFactory.initialize(this, mMapboxMap);

            chosen_position_start = new LatLng(0, 0);
            chosen_marker_start = null;
            chosen_position_end = new LatLng(0, 0);
            chosen_marker_end = null;
            ulm_center = new LatLng(48.398638, 9.993720);
            MarkerOptions ulm_center_options = new MarkerOptions();
            ulm_center_options.setPosition(ulm_center);
            ulm_center_options.setTitle("Stadt Ulm");
            ulm_center_options.setSnippet("Dies ist die Stadt Ulm");
            ulm_center_marker = mMapboxMap.addMarker(ulm_center_options);

            if (myActionListener == null) {
                myActionListener = new MyMapActionsListener(this);
            }

            mMapboxMap.setOnInfoWindowClickListener(myActionListener);

            mapboxMap.addOnMapLongClickListener(myActionListener);

            moveCameraTo(ulm_center, 13.5f, 60);

            if (locationEngine != null && locationEngine.isConnected()) {
                setUserMarker();
            }
        });

        if (StolperpfadeApplication.getInstance().isDarkMode()) {
            map.setNightMode();
        } else {
            map.setStreetMode();
        }

        return map;
    }

    private void loadIconsLow() {
        icon_factory = IconFactory.getInstance(ctx);
        icon_default_low = icon_factory.fromResource(R.drawable.marker_icon_default_round);
        icon_start_end_low = icon_factory.fromResource(R.drawable.marker_icon_start_end);
        icon_stone_low = icon_factory.fromResource(R.drawable.marker_icon_stone);
        icon_user_low = icon_factory.fromResource(R.drawable.marker_icon_user);
        icon_spec_blue_low = icon_factory.fromResource(R.drawable.marker_icon_special_blue);
        icon_spec_red_low = icon_factory.fromResource(R.drawable.marker_icon_special_red);
        icon_spec_green_low = icon_factory.fromResource(R.drawable.marker_icon_special_green);
        icon_spec_white_low = icon_factory.fromResource(R.drawable.marker_icon_default);
    }

    private void loadIcons() {
        if (icon_factory == null) {
            icon_factory = IconFactory.getInstance(ctx);
        }
        icon_default = icon_factory.defaultMarker();
        icon_user = icon_factory.fromAsset("icon_marker_user.json");
        icon_start_end = icon_factory.fromAsset("icon_marker_start_end.json");
        icon_stone = icon_factory.fromAsset("icon_marker_stone.json");
        icons_loaded = true;
        attemptRedraw();
    }

    private void attemptRedraw() {
        if (user_position_marker != null) {
            user_position_marker.setIcon(icon_user);
        }
        if (chosen_marker_start != null) {
            chosen_marker_start.setIcon(icon_start_end);
        }
        if (chosen_marker_end != null) {
            chosen_marker_end.setIcon(icon_start_end);
        }
        if (stone_handler != null) {
            if (stone_handler.isReady()) {
                for (Marker m : stone_handler.getMarkers()) {
                    if (m != null) {
                        m.setIcon(icon_stone);
                    }
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    /**
     * Displays the Stone markers on the map, that have been stored in the stone factory
     */
    public void setStones() {
        if (!stone_handler.isReady() || map == null || mMapboxMap == null) {
            return;
        }
        if (icon_stone_low == null) {
            loadIconsLow();
        }
        for (Marker m : stone_handler.getMarkers()) {

            if (icons_loaded) {
                m.setIcon(icon_stone);
            } else {
                m.setIcon(icon_stone_low);
            }
        }
        if (NEXT) {
            nearest_stone_marker = stone_handler.getNearestTo(user_position_marker);
            if (nearest_stone_marker == null) {
                nearest_stone_marker = ulm_center_marker;
                nearest_stone_marker.setTitle("Fehler");
                nearest_stone_marker.setSnippet("Es konnte kein Stein gefunden werden");
            } else {
                nearest_stone_marker.setSnippet("Bring mich zu diesem Stein");
            }
            moveCameraTo(nearest_stone_marker.getPosition(), 15, 45);
            mMapboxMap.selectMarker(nearest_stone_marker);
        }
        setUserMarker();
        map.invalidate();
    }

    /**
     * Creates a route from the user specified values for category, travel length, start and end positions
     *
     * @param time_in_minutes The length the user has time for walking a route
     * @param start_choice The place the user wants to start at
     * @param end_choice The place the user wants to end at
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @SuppressLint("StaticFieldLeak")
    public void createRoute(String start_choice, String end_choice, String time_in_minutes) {

        // TODO: create a good route through ulm
        Marker start_route_from;
        Marker end_route_at = null;

        int start = Integer.parseInt(start_choice);
        int end = Integer.parseInt(end_choice);
        int time = Integer.parseInt(time_in_minutes);

        switch (start) {
            case RoutePlannerActivity
                    .START_CHOICE_CTR:
                start_route_from = ulm_center_marker;
                break;
            case RoutePlannerActivity.START_CHOICE_GPS:
                start_route_from = user_position_marker;
                break;
            case RoutePlannerActivity.START_CHOICE_MAP:
                start_route_from = chosen_marker_start;
                break;
            case RoutePlannerActivity.START_CHOICE_NAN:
            default:
                start_route_from = ulm_center_marker;
        }

        switch (end) {
            case RoutePlannerActivity.END_CHOICE_CTR:
                end_route_at = ulm_center_marker;
                break;
            case RoutePlannerActivity.END_CHOICE_MAP:
                end_route_at = chosen_marker_end;
                break;
            case RoutePlannerActivity.END_CHOICE_STN:
                break;
        }

        ArrayList<Marker> route_points = new ArrayList<>();

        addStonesToRoute(route_points, start_route_from, end_route_at, time * 60);

        new CreateRouteTask() {
            @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
            @Override
            public void onPostExecute(Road road) {
                if (road == null) {
                    return;
                }
                ArrayList<GeoPoint> coords = road.mRouteHigh;
                List<LatLng> coordinates = new ArrayList<>();

                PolylineOptions polyline = new PolylineOptions();

                for (GeoPoint g : coords) {
                    coordinates.add(new LatLng(g.getLatitude(), g.getLongitude()));
                }
                polyline.addAll(coordinates);
                polyline.width(3);
                if (time > 0 && time * 60 > road.mDuration) {
                    polyline.color(Color.argb(150, 70, 255, 50));
                } else {
                    polyline.color(Color.argb(150, 255, 50, 50));
                }
                mMapboxMap.addPolyline(polyline);
                moveCameraTo(coordinates.get(0), 15, 45);
                aq.id(R.id.start_guide_button).visible();
                // enterFollowMode();
            }
        }.execute(route_points.toArray(new Marker[]{}));
    }

    private void addStonesToRoute(ArrayList<Marker> route_points, Marker start_route_from, Marker end_route_at, int time_in_seconds) {
        route_points.add(start_route_from);

        // TODO: chose some good stones!!!
        route_points.add(stone_handler.getMarkers().get(0));
        route_points.add(stone_handler.getMarkers().get(1));
        route_points.add(stone_handler.getMarkers().get(4));

        if (!(end_route_at == null)) {
            route_points.add(end_route_at);
        }
    }

    /**
     * Creates a route from the user location to the nearest stone
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @SuppressLint("StaticFieldLeak")
    public void createRouteToNext() {

        if (user_position_marker == null) {
            setUserMarker();
        }
        if (nearest_stone_marker == null) {
            return;
        }

        new CreateRouteTask() {
            @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
            @Override
            public void onPostExecute(Road road) {
                if (road == null) {
                    return;
                }
                ArrayList<GeoPoint> coords = road.mRouteHigh;
                List<LatLng> coordinates = new ArrayList<>();

                PolylineOptions polyline = new PolylineOptions();

                for (GeoPoint g : coords) {
                    coordinates.add(new LatLng(g.getLatitude(), g.getLongitude()));
                }
                polyline.addAll(coordinates);
                polyline.width(3);
                polyline.color(Color.argb(150, 250, 190, 50));
                mMapboxMap.addPolyline(polyline);

                moveCameraTo(user_position_marker.getPosition(), 15, 45);
                enterFollowMode();
            }
        }.execute(new Marker[]{user_position_marker, nearest_stone_marker});
    }

    public void moveCameraTo(LatLng newPosition, float zoom, float tilt) {
        CameraPosition position = new CameraPosition.Builder()
                .target(newPosition) // Sets the new camera position
                .zoom(zoom) // Sets the zoom to level 14
                .tilt(tilt) // Set the camera tilt to 45 degrees
                .build(); // Builds the CameraPosition object from the builder
        mMapboxMap.easeCamera(mapboxMap -> position, 1000);
    }

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected synchronized void enterFollowMode() {
        if (user_position_marker != null) {
            mMapboxMap.removeMarker(user_position_marker);
        }
        locationPresenter = new MyLocationPresenter(map, mMapboxMap, locationEngine);
        locationPresenter.setInitialZoomLevel(CENTER_ON_USER_ZOOM_LEVEL);
        locationPresenter.setFollowCameraAngle(FOLLOW_MODE_TILT_VALUE_DEGREES);
        locationPresenter.setLockNorthUp(false);
        locationPresenter.setFollow(true);
        locationPresenter.forceLocationChange(lastLocation);
        locationPresenter.onStart();
    }

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void initializeLocationEngine() {
        locationEngine = (new LocationEngineProvider(Objects.requireNonNull(getActivity()).getApplicationContext())).obtainBestLocationEngineAvailable();
        locationEngine.setInterval(1000);
        locationEngine.setFastestInterval(500);
        locationEngine.setSmallestDisplacement(5);
        locationEngine.setPriority(LocationEnginePriority.BALANCED_POWER_ACCURACY);
        if (myActionListener == null) {
            myActionListener = new MyMapActionsListener(this);
        }
        locationEngine.addLocationEngineListener(myActionListener);
        locationEngine.activate();
        locationEngine.requestLocationUpdates();
        lastLocation = locationEngine.getLastLocation();
    }

    public void setUserMarker() {
        if(lastLocation == null) {
            return;
        }
        if (user_position_marker != null) {
            user_position_marker.setPosition(RoutingUtil.convertLocationToLatLng(lastLocation));
        } else {
            MarkerOptions user_position_marker_options = new MarkerOptions();
            user_position_marker_options.setPosition(RoutingUtil.convertLocationToLatLng(lastLocation));
            user_position_marker_options.setTitle("Sie sind hier");
            user_position_marker_options.setSnippet("");
            if (icon_user_low == null) {
                loadIconsLow();
            }
            if (icons_loaded) {
                user_position_marker_options.setIcon(icon_user);
            } else {
                user_position_marker_options.setIcon(icon_spec_blue_low);
            }
            if (mMapboxMap == null) {
                return;
            }
            user_position_marker = mMapboxMap.addMarker(user_position_marker_options);
        }
    }

    public void setUserLocation(Location location) {
        lastLocation = location;
        if (locationPresenter == null || !locationPresenter.isFollowing()) {
            setUserMarker();
        }
    }

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void forceUserLocationUpdate() {
        locationEngine.requestLocationUpdates();
        lastLocation = locationEngine.getLastLocation();
        setUserLocation(lastLocation);
    }

    public void setStartOrEndMarker(LatLng point, boolean asStart) {
        if (asStart) {
            setStartOrEndMarker(chosen_position_start = point, chosen_marker_start, true);
        } else {
            setStartOrEndMarker(chosen_position_end = point, chosen_marker_end, false);
        }
    }

    private void setStartOrEndMarker(LatLng point, Marker marker, boolean asStart) {
        if (marker != null) {
            marker.setPosition(point);
        } else {
            MarkerOptions chosen_marker_options = new MarkerOptions();
            chosen_marker_options.setPosition(point);
            chosen_marker_options.setTitle((asStart ? "Beginn" : "Ende") + " der nächsten Route");
            if (icon_start_end_low == null) {
                loadIconsLow();
            }
            if (asStart) {
                if (icons_loaded) {
                    chosen_marker_options.setIcon(icon_start_end);
                } else {
                    chosen_marker_options.setIcon(icon_spec_green_low);
                }
                chosen_marker_start = mMapboxMap.addMarker(chosen_marker_options);
            } else {
                if (icons_loaded) {
                    chosen_marker_options.setIcon(icon_start_end);
                } else {
                    chosen_marker_options.setIcon(icon_spec_red_low);
                }
                chosen_marker_end = mMapboxMap.addMarker(chosen_marker_options);
            }
        }
    }

    public void startGuide() {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        enterFollowMode();
    }

    public boolean isNearestMarkerToUser(Marker marker) {
        return NEXT && marker.equals(nearest_stone_marker) && !marker.equals(ulm_center_marker);
    }

    public MapboxMap getMapboxMap() {
        return mMapboxMap;
    }

    public StoneFactory getStoneHandler() {
        return stone_handler;
    }

    public boolean isStartMarker(Marker marker) {
        return marker.equals(chosen_marker_start);
    }

    public boolean isEndMarker(Marker marker) {
        return marker.equals(chosen_marker_end);
    }

    public void removeStartMarker() {
        mMapboxMap.removeMarker(chosen_marker_start);
        chosen_marker_start = null;
    }

    public void removeEndMarker() {
        mMapboxMap.removeMarker(chosen_marker_end);
        chosen_marker_end = null;
    }

    /**
     * This class is responsible for creating a network call to get a route
     * for a collection of - at least - two stones
     * A MapQuest Key is needed for the call to be succesful
     */
    private class CreateRouteTask extends AsyncTask<Marker[], Void, Road> {

        @Override
        protected Road doInBackground(Marker[]... markers) {

            if(markers == null || markers.length < 1 || markers[0].length < 2) {
                return null;
            }

            RoadManager roadManager = new MapQuestRoadManager(getResources().getString(R.string.mapquest_api_key));

            roadManager.addRequestOption("routeType=pedestrian");

            ArrayList<GeoPoint> waypoints = new ArrayList<>();

            LatLng marker_position;
            for(Marker m : markers[0]) {
                Log.i("MY_ROUTE_TAG", "This is a new marker in the array: " + m);
                if(m == null) {
                    return null;
                }
                marker_position = m.getPosition();
                waypoints.add(new GeoPoint(marker_position.getLatitude(), marker_position.getLongitude()));
            }

            Road road = roadManager.getRoad(waypoints);

            return road;
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
