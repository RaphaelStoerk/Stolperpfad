package de.uni_ulm.ismm.stolperpfad.map_activities.view;

import java.util.Objects;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.general.MyMapActionsListener;
import de.uni_ulm.ismm.stolperpfad.map_activities.RoutingUtil;
import de.uni_ulm.ismm.stolperpfad.map_activities.StolperpfadAppMapActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.RoutePlannerActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.Stolperpfad;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.StoneOnMap;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.StoneFactory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import com.mapquest.mapping.MapQuest;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MyLocationPresenter;

import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;

import static de.uni_ulm.ismm.stolperpfad.map_activities.model.StoneFactory.SECONDS_PER_MINUTE;

/**
 * A Fragment class representing all our map_view visualizations.
 * We are using the mapquest mapping and navigation sdk together with pieces
 * of the osmdroid bonus pack to create routes and show important places on the map_view
 *
 * @author Raphael
 */
public class MapQuestFragment extends Fragment {

    private static final int DEFAULT_TILT = 45;
    private static final float DEFAULT_ZOOM = 13.5f;
    private static final float NEAR_ZOOM = 15;
    private static final double FOLLOW_MODE_TILT_VALUE_DEGREES = 50;
    private static final double CENTER_ON_USER_ZOOM_LEVEL = 18;
    private static final LatLng ULM_CTR = new LatLng(48.39855, 9.99123);

    // the map_view visual
    private MapView map_view;
    //the map_view controller
    private MapboxMap map_object;
    // activity context
    private Context context;

    // The mapquest api key needed for transactions
    private final String API_KEY;

    // flag, for wether the calling Activity is the next_stone_activity
    private boolean next;
    // the next persons id
    private int next_id;

    // App specific values storing preferences for the routing
    private AQuery aq;
    private RoutePlannerActivity parent_activity;
    private StoneFactory stone_handler;
    private Marker chosen_marker_start;
    private Marker chosen_marker_end;
    private Marker ulm_center_marker;
    private Marker user_position_marker;
    private Stolperpfad current_path;
    private Polyline current_path_polyline;
    private MyLocationPresenter location_presenter;
    private LocationEngine location_engine;
    private Location last_location;
    private MyMapActionsListener map_action_listener;
    private Icon icon_start_end_low, icon_user_low, icon_stone_low, icon_default_low;

    public MapQuestFragment() {
        API_KEY = String.valueOf(R.string.mapquest_api_key);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MapQuestFragment.
     */
    public static MapQuestFragment newInstance(int next_person_id, boolean next, AQuery aq) {
        MapQuestFragment fragment = new MapQuestFragment();
        fragment.next = next;
        fragment.next_id = next_person_id;
        fragment.aq = aq;
        return fragment;
    }

    @Override
    public void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        parent_activity = (RoutePlannerActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saved_state) {
        context = inflater.getContext();
        // Important Mapquest Initialization
        MapQuest.start(context);
        map_view = new MapView(context, null, 0, API_KEY);
        // Initialize the map_view visuals
        map_view.onCreate(saved_state);
        map_view.getMapAsync(mapboxMap -> {
            map_object = mapboxMap;
            if (StolperpfadeApplication.getInstance().isDarkMode()) {
                map_view.setNightMode();
            } else {
                map_view.setStreetMode();
            }
            loadIconsLow();
            stone_handler = StoneFactory.initialize(this, map_object);
            MarkerOptions ulm_center_options = new MarkerOptions();
            ulm_center_options.setPosition(ULM_CTR);
            ulm_center_options.setIcon(icon_default_low);
            ulm_center_options.setTitle("Münsterplatz");
            ulm_center_marker = map_object.addMarker(ulm_center_options);
            if (map_action_listener == null) {
                map_action_listener = new MyMapActionsListener(this);
            }
            map_object.setOnInfoWindowClickListener(map_action_listener);
            mapboxMap.addOnMapLongClickListener(map_action_listener);
            moveCameraTo(ULM_CTR, DEFAULT_ZOOM, DEFAULT_TILT);
            if (location_engine != null && location_engine.isConnected()) {
                setUserMarker();
            }
        });
        return map_view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        map_view.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        map_view.onPause();
    }

    /**
     * Load the drawables for the marker icons, TODO: maybe get scalable images
     */
    private void loadIconsLow() {
        IconFactory icon_factory = IconFactory.getInstance(context);
        icon_default_low = icon_factory.fromResource(R.drawable.marker_icon_default_round);
        icon_start_end_low = icon_factory.fromResource(R.drawable.marker_icon_start_end);
        icon_stone_low = icon_factory.fromResource(R.drawable.marker_icon_stone);
        icon_user_low = icon_factory.fromResource(R.drawable.marker_icon_default);
    }

    /**
     * Displays the stone markers on the map_view, that have been stored in the stone factory
     */
    public void setStones() {
        if (icon_stone_low == null) {
            loadIconsLow();
        }
        for (Marker m : stone_handler.getMarkers()) {
            m.setIcon(icon_stone_low);
        }
        if (next) {
            Marker nearest_stone_marker;
            if(next_id == -1) {
                nearest_stone_marker = stone_handler.getNearestTo(user_position_marker);
                if(nearest_stone_marker != null) {
                    StolperpfadeApplication.getInstance().addStoneToMemory(stone_handler.getStoneFromMarker(nearest_stone_marker).getStoneId());
                }
            } else {
                nearest_stone_marker = stone_handler.getMarkerFromId(next_id);
            }
            if (nearest_stone_marker == null) {
                nearest_stone_marker = ulm_center_marker;
                nearest_stone_marker.setTitle("Information");
                nearest_stone_marker.setSnippet("Sie haben bereits alle Steine gesehen");
            }
            moveCameraTo(nearest_stone_marker.getPosition(), 15, 45);
            map_object.selectMarker(nearest_stone_marker);
        }
        setUserMarker();
        map_view.invalidate();
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
        Marker start_route_from;
        Marker end_route_at = null;
        int start = Integer.parseInt(start_choice);
        int end = Integer.parseInt(end_choice);
        int time = Integer.parseInt(time_in_minutes);

        switch (start) {
            case RoutePlannerActivity.START_CHOICE_CTR:
                start_route_from = ulm_center_marker;
                break;
            case RoutePlannerActivity.START_CHOICE_GPS:
                if(user_position_marker == null) {
                    parent_activity.errorDialog("Keinen Standort gefunden");
                    return;
                }
                start_route_from = user_position_marker;
                break;
            case RoutePlannerActivity.START_CHOICE_MAP:
                if(chosen_marker_start == null) {
                    parent_activity.errorDialog("Keinen Start-Marker gesetzt");
                    return;
                }
                start_route_from = chosen_marker_start;
                break;
            case RoutePlannerActivity.CHOICE_NAN:
            default:
                start_route_from = ulm_center_marker;
        }

        switch (end) {
            case RoutePlannerActivity.END_CHOICE_CTR:
                end_route_at = ulm_center_marker;
                break;
            case RoutePlannerActivity.END_CHOICE_MAP:
                if(chosen_marker_end == null) {
                    parent_activity.errorDialog("Keinen End-Marker gesetzt");
                    return;
                }
                end_route_at = chosen_marker_end;
                break;
            case RoutePlannerActivity.END_CHOICE_STN:
                break;
        }
        Stolperpfad road = addStonesToRoute(start_route_from, end_route_at, time * SECONDS_PER_MINUTE);
        if(!road.isValid()) {
            parent_activity.errorDialog("Kein Erfolg", "Mit den gegebenen Einstellungen konnte kein Pfad erstellt werden");
            return;
        }
        current_path = road;
        new CreateRouteTask() {
            @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
            @Override
            public void onPostExecute(Void voids) {
                if (current_path == null || !current_path.isValid()) {
                    return;
                }
                if(current_path_polyline != null) {
                    map_object.removePolyline(current_path_polyline);
                }
                current_path_polyline = current_path.addPathToMap(map_object);
                parent_activity.activatePathGuide();
                moveCameraTo(road.getStartPosition(), NEAR_ZOOM, DEFAULT_TILT);
                aq.id(R.id.start_guide_button).visible();
            }
        }.execute();
    }

    /**
     * Prepares the route creation for the stone handler
     *
     * @param start_route_from the start of the next route
     * @param end_route_at the end of the next route
     * @param time_in_seconds the requested time for the next route
     * @return a new route for the specified parameters
     */
    private Stolperpfad addStonesToRoute(Marker start_route_from, Marker end_route_at, int time_in_seconds) {
        if(time_in_seconds < 60) {
            time_in_seconds = 60 * (getRandomtPathTime());
        } else if(time_in_seconds > 24 * 60 * 60) {
            time_in_seconds = 24 * 60 * 60;
        }
        return stone_handler.createPathWith(start_route_from, end_route_at, time_in_seconds);
    }

    /**
     * Calculates a random path time if no time was specified
     *
     * @return a random time in minutes
     */
    private int getRandomtPathTime() {
        return (int) (30 + Math.random() * 90);
    }

    /**
     * Moves the camera on the map to a requested position
     *
     * @param new_position the new camera position
     * @param zoom the new zoom for the camera
     * @param tilt the new tilt
     */
    public void moveCameraTo(LatLng new_position, float zoom, float tilt) {
        CameraPosition position = new CameraPosition.Builder()
                .target(new_position) // Sets the new camera position
                .zoom(zoom) // Sets the zoom to level 14
                .tilt(tilt) // Set the camera tilt to 45 degrees
                .build(); // Builds the CameraPosition object from the builder
        map_object.easeCamera(mapboxMap -> position, 1000);
    }

    /**
     * Initializes the location engine that checks the users position in set intervals
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void initializeLocationEngine() {
        location_engine = (new LocationEngineProvider(Objects.requireNonNull(getActivity()).getApplicationContext())).obtainBestLocationEngineAvailable();
        location_engine.setInterval(1000);
        location_engine.setFastestInterval(800);
        location_engine.setSmallestDisplacement(2);
        location_engine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        if (map_action_listener == null) {
            map_action_listener = new MyMapActionsListener(this);
        }
        location_engine.addLocationEngineListener(map_action_listener);
        location_engine.activate();
        location_engine.requestLocationUpdates();
        last_location = location_engine.getLastLocation();
        setUserMarker();
    }

    /**
     * Starts the guide for the route planner and follows the user position while walking
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected synchronized void enterFollowMode() {
        if (user_position_marker != null) {
            map_object.removeMarker(user_position_marker);
        }
        if(location_presenter != null) {
            parent_activity.activatePathGuide();
        } else {
            location_presenter = new MyLocationPresenter(map_view, map_object, location_engine);
        }
        location_presenter.setInitialZoomLevel(CENTER_ON_USER_ZOOM_LEVEL);
        location_presenter.setFollowCameraAngle(FOLLOW_MODE_TILT_VALUE_DEGREES);
        location_presenter.setLockNorthUp(false);
        location_presenter.setFollow(true);
        location_presenter.forceLocationChange(last_location);
        location_presenter.onStart();
    }

    /**
     * Set the user marker if the location engine is ready
     */
    public void setUserMarker() {
        if(last_location == null) {
            return;
        }
        if (user_position_marker != null) {
            user_position_marker.setPosition(RoutingUtil.convertLocationToLatLng(last_location));
        } else {
            MarkerOptions user_position_marker_options = new MarkerOptions();
            user_position_marker_options.setPosition(RoutingUtil.convertLocationToLatLng(last_location));
            user_position_marker_options.setTitle("Sie sind hier");
            user_position_marker_options.setSnippet("");
            if (icon_user_low == null) {
                loadIconsLow();
            }

            user_position_marker_options.setIcon(icon_user_low);

            if (map_object == null) {
                return;
            }
            user_position_marker = map_object.addMarker(user_position_marker_options);
        }
    }

    /**
     * Forces the location engine to check for a new user location
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void forceUserLocationUpdate() {
        location_engine.requestLocationUpdates();
        last_location = location_engine.getLastLocation();
        setUserLocation(last_location);
    }

    /**
     * Sets the Marker for the start or end position of e new route
     *
     * @param position the requested position
     * @param as_start if the point corresponds to a start position
     */
    public void setStartOrEndMarker(LatLng position, boolean as_start) {
        if (as_start) {
            setStartOrEndMarker(position, chosen_marker_start, true);
        } else {
            setStartOrEndMarker(position, chosen_marker_end, false);
        }
    }

    /**
     * Sets the Marker for the start or end position of e new route
     *
     * @param position the requested position
     * @param marker the marker for that position
     * @param as_start if the point corresponds to a start position
     */
    private void setStartOrEndMarker(LatLng position, Marker marker, boolean as_start) {
        if (marker != null) {
            marker.setPosition(position);
        } else {
            MarkerOptions chosen_marker_options = new MarkerOptions();
            chosen_marker_options.setPosition(position);
            chosen_marker_options.setTitle((as_start ? "Beginn" : "Ende") + " der nächsten Route");
            if (icon_start_end_low == null) {
                loadIconsLow();
            }
            if (as_start) {
                chosen_marker_options.setIcon(icon_start_end_low);
                chosen_marker_start = map_object.addMarker(chosen_marker_options);
            } else {
                chosen_marker_options.setIcon(icon_start_end_low);
                chosen_marker_end = map_object.addMarker(chosen_marker_options);
            }
        }
    }

    /**
     * starts the route guide
     */
    public void startGuide() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(last_location == null) {
            return;
        }
        if(current_path_polyline != null && (location_presenter == null || !location_presenter.isFollowing())) {
            enterFollowMode();
        } else {
            if(location_presenter == null) {
                return;
            }
            location_presenter.setFollow(false);
            location_presenter.onStop();
            StolperpfadAppMapActivity a = (StolperpfadAppMapActivity) getActivity();
            if(a instanceof RoutePlannerActivity) {
                ((RoutePlannerActivity) a).deactivateGuide();
            }
        }
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
        map_object.removeMarker(chosen_marker_start);
        chosen_marker_start = null;
    }

    public void removeEndMarker() {
        map_object.removeMarker(chosen_marker_end);
        chosen_marker_end = null;
    }

    /**
     * Sets the user location if the location engine has an update
     *
     * @param location the new user location
     */
    public void setUserLocation(Location location) {
        last_location = location;
        if (location_presenter == null || !location_presenter.isFollowing()) {
            setUserMarker();
        }
    }

    /**
     * Save the current route as a json file in the external storage
     *
     * @param current_file_name the current route name
     * @return if the route could be saved
     */
    public boolean saveRoute(String current_file_name) {
        if(current_path != null && current_path.isValid()) {
           return current_path.saveRoad(current_file_name); // TODO: check if route has been saved
        }
        return false;
    }

    /**
     *
     * @param road
     */
    @SuppressLint("StaticFieldLeak")
    public void loadRoute(Stolperpfad road) {
        current_path = road;
        current_path.inflateFromBasic(stone_handler, map_object, icon_start_end_low);
        new CreateRouteTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (current_path == null || !current_path.isValid()) {
                    return;
                }
                if(current_path_polyline != null) {
                    map_object.removePolyline(current_path_polyline);
                }
                current_path_polyline = current_path.addPathToMap(map_object);
                parent_activity.activatePathGuide();
                moveCameraTo(current_path.getStartPosition(), 15, 45);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void createRouteTo(StoneOnMap stone) {
        if(user_position_marker == null){
            return;
        }
        Stolperpfad direct_path = Stolperpfad.newDirectPathInstance(user_position_marker, stone.getMarker(map_object));
        if(direct_path.isValid()) {
            current_path = direct_path;
        }
        new CreateRouteTask() {
            @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
            @Override
            public void onPostExecute(Void voids) {
                if (current_path == null || !current_path.isValid()) {
                    return;
                }
                if(current_path_polyline != null) {
                    map_object.removePolyline(current_path_polyline);
                }
                current_path_polyline = current_path.addPathToMap(map_object);
                parent_activity.activatePathGuide();
                moveCameraTo(current_path.getStartPosition(), 15, 45);
                aq.id(R.id.start_guide_button).visible();
            }
        }.execute();
    }

    /**
     * This class is responsible for creating a network call to get a route
     * for a collection of - at least - two stones
     * A MapQuest Key is needed for the call to be succesful
     */
    @SuppressLint("StaticFieldLeak")
    private class CreateRouteTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if(current_path == null || !current_path.isValid()) {
                return null;
            }
            RoadManager roadManager = new MapQuestRoadManager(getResources().getString(R.string.mapquest_api_key));
            roadManager.addRequestOption("routeType=pedestrian");
            Road path;
            try {
                path = roadManager.getRoad(current_path.getWaypoints());
            }catch(IndexOutOfBoundsException ioobe) {
                parent_activity.errorDialog( "Fehler", "Bei der Pfad Generierung ist ein Fehler aufgetreten");
                return null;
            }
            current_path.addRoadInformation(path);
            return null;
        }
    }
}
