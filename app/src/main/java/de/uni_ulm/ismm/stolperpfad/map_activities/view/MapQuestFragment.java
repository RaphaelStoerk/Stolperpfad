package de.uni_ulm.ismm.stolperpfad.map_activities.view;

import java.util.Objects;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.general.MyMapActionsListener;
import de.uni_ulm.ismm.stolperpfad.map_activities.RoutingUtil;
import de.uni_ulm.ismm.stolperpfad.map_activities.StolperpfadAppMapActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.RoutePlannerActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.MyRoad;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.Stone;
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

    private static final double FOLLOW_MODE_TILT_VALUE_DEGREES = 50;
    private static final double CENTER_ON_USER_ZOOM_LEVEL = 18;

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
    // the next persons id
    private int next_id;

    // App specific values storing preferences for the routing
    private StoneFactory stone_handler;
    private Marker chosen_marker_start;
    private Marker chosen_marker_end;
    private LatLng ulm_center;
    private Marker ulm_center_marker;
    private Marker user_position_marker;
    private MyRoad current_path;
    private Polyline current_path_polyline;
    private MyLocationPresenter locationPresenter;
    private LocationEngine locationEngine;
    private Location lastLocation;
    private Marker nearest_stone_marker;
    private MyMapActionsListener myActionListener;
    private Icon icon_start_end_low, icon_user_low, icon_stone_low, icon_default_low;
    private AlertDialog info_dialog;
    private AQuery aq;

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
        fragment.NEXT = next;
        fragment.next_id = next_person_id;
        fragment.aq = aq;
        return fragment;
    }

    @Override
    public void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saved_state) {

        ctx = inflater.getContext();
        // Important Mapquest Initialization
        MapQuest.start(ctx);

        map = new MapView(ctx, null, 0, API_KEY);

        // Initialize the map visuals
        map.onCreate(saved_state);
        map.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;
            if (StolperpfadeApplication.getInstance().isDarkMode()) {
                map.setNightMode();
            } else {
                map.setStreetMode();
            }
            loadIconsLow();
            stone_handler = StoneFactory.initialize(this, mMapboxMap);
            ulm_center = new LatLng(48.39855, 9.99123);
            MarkerOptions ulm_center_options = new MarkerOptions();
            ulm_center_options.setPosition(ulm_center);
            ulm_center_options.setIcon(icon_default_low);
            ulm_center_options.setTitle("Münsterplatz");
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


        return map;
    }

    private void loadIconsLow() {
        IconFactory icon_factory = IconFactory.getInstance(ctx);
        icon_default_low = icon_factory.fromResource(R.drawable.marker_icon_default_round);
        icon_start_end_low = icon_factory.fromResource(R.drawable.marker_icon_start_end);
        icon_stone_low = icon_factory.fromResource(R.drawable.marker_icon_stone);
        icon_user_low = icon_factory.fromResource(R.drawable.marker_icon_default);
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
        Log.i("MY_DEBUG_TAG","setStones");
        if (!stone_handler.isReady() || map == null || mMapboxMap == null) {
            return;
        }
        if (icon_stone_low == null) {
            loadIconsLow();
        }
        for (Marker m : stone_handler.getMarkers()) {
            m.setIcon(icon_stone_low);
        }
        if (NEXT) {
            Log.i("MY_DEBUG_TAG","next");
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
            } else {
                // nearest_stone_marker.setSnippet("Bring mich zu diesem Stein");
            }
            moveCameraTo(nearest_stone_marker.getPosition(), 15, 45);
            mMapboxMap.selectMarker(nearest_stone_marker);
        }
        Log.i("MY_DEBUG_TAG","set stones done");
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
                if(user_position_marker == null) {
                    errorDialog("Keinen Standort gefunden");
                    return;
                }
                start_route_from = user_position_marker;
                break;
            case RoutePlannerActivity.START_CHOICE_MAP:
                if(chosen_marker_start == null) {
                    errorDialog("Keinen Start-Marker gesetzt");
                    return;
                }
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
                if(chosen_marker_end == null) {
                    errorDialog("Keinen End-Marker gesetzt");
                    return;
                }
                end_route_at = chosen_marker_end;
                break;
            case RoutePlannerActivity.END_CHOICE_STN:
                break;
        }
        MyRoad road = addStonesToRoute(start_route_from, end_route_at, time * 60);
        if(!road.isValid()) {
            errorDialog("Kein Erfolg", "Mit den gegebenen Einstellungen konnte kein Pfad erstellt werden");
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
                    mMapboxMap.removePolyline(current_path_polyline);
                }
                current_path_polyline = current_path.addPathToMap(mMapboxMap);
                ((RoutePlannerActivity)getActivity()).activatePathPlanner(true);
                moveCameraTo(road.getStartPosition(), 15, 45);
                aq.id(R.id.start_guide_button).visible();
                // dismissAlert();
            }
        }.execute();
    }

    private void alertUser(String s) {
        if(info_dialog != null) {
            // dismissAlert();
            return;
        }
        AlertDialog.Builder builder;
        if (StolperpfadeApplication.getInstance().isDarkMode()) {
            builder = new AlertDialog.Builder(this.getContext(), R.style.DialogTheme_Dark);
        } else {
            builder = new AlertDialog.Builder(this.getContext(), R.style.DialogTheme_Light);
        }
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setMessage(s);
        builder.setCancelable(false);

        info_dialog = builder.create();
        info_dialog.show();
    }

    private void dismissAlert() {
        if(info_dialog != null) {
            info_dialog.dismiss();
            info_dialog = null;
        }
    }
    private void errorDialog(String s) {
        errorDialog(s, "");
    }

    private AlertDialog error_dialog;

    private void errorDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setNegativeButton("Okay", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });
        if(error_dialog != null) {
            error_dialog.cancel();
            error_dialog = null;
        }
        error_dialog = builder.create();
        error_dialog.show();
    }

    private MyRoad addStonesToRoute(Marker start_route_from, Marker end_route_at, int time_in_seconds) {
        if(time_in_seconds < 60) {
            time_in_seconds = 60 * (getRandomtPathTime());
        } else if(time_in_seconds > 24 * 60 * 60) {
            time_in_seconds = 24 * 60 * 60;
        }
        return stone_handler.createPathWith(start_route_from, end_route_at, time_in_seconds);
    }

    private int getRandomtPathTime() {
        return (int) (30 + Math.random() * 90);
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

        MyRoad direct_path = MyRoad.newDirectPathInstance(user_position_marker, nearest_stone_marker);
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
                    mMapboxMap.removePolyline(current_path_polyline);
                }
                current_path_polyline = current_path.addPathToMap(mMapboxMap);
                ((RoutePlannerActivity)getActivity()).activatePathPlanner(true);
                moveCameraTo(current_path.getStartPosition(), 15, 45);
                aq.id(R.id.start_guide_button).visible();
                dismissAlert();
            }
        }.execute();
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
        if(locationPresenter != null) {
            ((RoutePlannerActivity)getActivity()).activatePathPlanner(true);
        } else {
            locationPresenter = new MyLocationPresenter(map, mMapboxMap, locationEngine);
        }
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
        locationEngine.setFastestInterval(800);
        locationEngine.setSmallestDisplacement(2);
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        if (myActionListener == null) {
            myActionListener = new MyMapActionsListener(this);
        }
        locationEngine.addLocationEngineListener(myActionListener);
        locationEngine.activate();
        locationEngine.requestLocationUpdates();
        lastLocation = locationEngine.getLastLocation();
        setUserMarker();
    }

    public void setUserMarker() {
        Log.i("MY_DEBUG_TAG","set user marker");
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

            user_position_marker_options.setIcon(icon_user_low);

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
            setStartOrEndMarker(point, chosen_marker_start, true);
        } else {
            setStartOrEndMarker(point, chosen_marker_end, false);
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
                chosen_marker_options.setIcon(icon_start_end_low);
                chosen_marker_start = mMapboxMap.addMarker(chosen_marker_options);
            } else {
                chosen_marker_options.setIcon(icon_start_end_low);
                chosen_marker_end = mMapboxMap.addMarker(chosen_marker_options);
            }
        }
    }

    public void startGuide() {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if(lastLocation == null) {
            return;
        }
        if(current_path_polyline != null && (locationPresenter == null || !locationPresenter.isFollowing())) {
            enterFollowMode();
        } else {
            if(locationPresenter == null) {
                return;
            }
            locationPresenter.setFollow(false);
            locationPresenter.onStop();
            StolperpfadAppMapActivity a = (StolperpfadAppMapActivity) getActivity();
            if(a instanceof RoutePlannerActivity) {
                ((RoutePlannerActivity) a).deactivateGuide();
            }
        }
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

    public void activatePathPlanner(boolean bool) {
        StolperpfadAppMapActivity a = (StolperpfadAppMapActivity) getActivity();
        if(a instanceof RoutePlannerActivity) {
            ((RoutePlannerActivity) a).activatePathPlanner(bool);
        } else {

        }
    }

    public void saveRoute(String current_file_name) {
        if(current_path != null && current_path.isValid()) {
            current_path.saveRoad(current_file_name);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void loadRoute(MyRoad road) {
        current_path = road;
        current_path.inflateFromBasic(stone_handler, mMapboxMap, icon_start_end_low);
        new CreateRouteTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                if (current_path == null || !current_path.isValid()) {
                    return;
                }
                if(current_path_polyline != null) {
                    mMapboxMap.removePolyline(current_path_polyline);
                }
                current_path_polyline = current_path.addPathToMap(mMapboxMap);
                ((RoutePlannerActivity)getActivity()).activatePathPlanner(true);
                moveCameraTo(current_path.getStartPosition(), 15, 45);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void createRouteTo(Stone stone) {
        if(user_position_marker == null){
            return;
        }
        MyRoad direct_path = MyRoad.newDirectPathInstance(user_position_marker, stone.getMarker(mMapboxMap));
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
                    mMapboxMap.removePolyline(current_path_polyline);
                }
                current_path_polyline = current_path.addPathToMap(mMapboxMap);
                ((RoutePlannerActivity)getActivity()).activatePathPlanner(true);
                moveCameraTo(current_path.getStartPosition(), 15, 45);
                aq.id(R.id.start_guide_button).visible();
                dismissAlert();
            }
        }.execute();
    }

    /**
     * This class is responsible for creating a network call to get a route
     * for a collection of - at least - two stones
     * A MapQuest Key is needed for the call to be succesful
     */
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
                errorDialog( "Fehler", "Bei der Pfad Generierung ist ein Fehler aufgetreten");
                return null;
            }
            current_path.addRoadInformation(path);
            return null;
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
