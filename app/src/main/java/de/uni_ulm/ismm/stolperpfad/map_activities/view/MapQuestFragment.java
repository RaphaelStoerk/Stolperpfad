package de.uni_ulm.ismm.stolperpfad.map_activities.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadApplication;
import de.uni_ulm.ismm.stolperpfad.info_display.ScrollingInfoActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.RoutingUtil;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.Stone;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.StoneFactory;
import timber.log.Timber;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import com.mapquest.mapping.MapQuest;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.mapping.maps.MyLocationPresenter;
import com.mapquest.navigation.NavigationManager;
import com.mapquest.navigation.dataclient.RouteService;

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
public class MapQuestFragment extends Fragment implements MapboxMap.OnInfoWindowClickListener, MapboxMap.OnMapLongClickListener, LocationEngineListener {

    // the map visual
    private MapView map;
    //the map controller
    private MapboxMap mMapboxMap;
    // activity context
    private Context ctx;

    // The mapquest api key needed for transactions
    private final String API_KEY;

    // routing and navigation services
    private RouteService mRouteService;
    private NavigationManager mNavigationManager;

    // flag, for wether the calling Activity is the next_stone_activity
    private boolean NEXT;

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

    public MapQuestFragment() {
        // Required empty public constructor
        API_KEY = String.valueOf(R.string.mapquest_api_key);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MapQuestFragment.
     */
    public static MapQuestFragment newInstance(boolean next) {
        MapQuestFragment fragment = new MapQuestFragment();
        fragment.NEXT = next;
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

        // TODO: Leave if necessary, check if disposable
        mRouteService = new RouteService.Builder().build(this.getContext(), API_KEY);

        // Initialize the map visuals
        map.onCreate(savedInstanceState);
        map.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;
            map.setStreetMode();

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

            mMapboxMap.setOnInfoWindowClickListener(this);

            mapboxMap.addOnMapLongClickListener(this);

            moveCameraTo(ulm_center, 13.5f, 60);

            if (locationEngine != null && locationEngine.isConnected()) {
                setUserMarker();
            }
        });

        if (StolperpfadApplication.getInstance().isDarkMode()) {
            map.setNightMode();
        } else {
            map.setStreetMode();
        }

        return map;
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
        if (!stone_handler.isReady() || map == null) {
            return;
        }
        for (Marker m : stone_handler.getMarkers()) {
            m.setIcon(IconFactory.getInstance(Objects.requireNonNull(getActivity()).getApplicationContext()).defaultMarker());
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
            moveCameraTo(nearest_stone_marker.getPosition(), 15,45);
            mMapboxMap.selectMarker(nearest_stone_marker);
        }
        map.invalidate();
    }

    /**
     * Creates a route from the user specified values for category, travel length, start and end positions
     *
     * @param category_selected A Category for the Route
     * @param time_in_minutes The length the user has time for walking a route
     * @param start_choice The place the user wants to start at
     * @param end_choice The place the user wants to end at
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @SuppressLint("StaticFieldLeak")
    public void createRoute(String category_selected, int time_in_minutes, int start_choice, int end_choice) {

        // TODO: create a good route through ulm

        ArrayList<Marker> route_points = new ArrayList<>();
        route_points.add(stone_handler.getMarkers().get(1));
        route_points.add(stone_handler.getMarkers().get(2));
        route_points.add(stone_handler.getMarkers().get(3));

        new CreateRouteTask() {
            @Override
            public void onPostExecute(Road road) {
                if(road == null) {
                    Log.i("MY_ROUTE_TAG","Something went wrong, no Road created");
                    return;
                }
                ArrayList<GeoPoint> coords = road.mRouteHigh;
                Log.i("MY_ROUTE_TAG","The current Road is this long: " + coords.size());
                List<LatLng> coordinates = new ArrayList<>();

                PolylineOptions polyline = new PolylineOptions();

                for (GeoPoint g : coords) {
                    coordinates.add(new LatLng(g.getLatitude(), g.getLongitude()));
                }
                polyline.addAll(coordinates);
                polyline.width(3);
                polyline.color(Color.BLUE);
                mMapboxMap.addPolyline(polyline);
                Log.i("MY_ROUTE_TAG","The Polyline has been added with length: " + polyline.getPoints().size());

                moveCameraTo(coordinates.get(0), 15,45);

                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                // enterFollowMode();
            }
        }.execute(route_points.toArray(new Marker[]{}));
    }


    /**
     * Creates a route from the user location to the nearest stone
     */
    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @SuppressLint("StaticFieldLeak")
    public void createRouteToNext() {

        // TODO: create a good route through ulm
        if(user_position_marker == null) {
            Log.i("MY_ROUTE_TAG","Something went wrong, trying to set user marker: " + user_position_marker);
            setUserMarker();
        }
        if(nearest_stone_marker == null) {
            Log.i("MY_ROUTE_TAG","Something went wrong, no nearest Stone, no road creation " + nearest_stone_marker);
            return;
        }

        new CreateRouteTask() {
            @Override
            public void onPostExecute(Road road) {
                if(road == null) {
                    Log.i("MY_ROUTE_TAG","Something went wrong, no Road created");
                    return;
                }
                ArrayList<GeoPoint> coords = road.mRouteHigh;
                Log.i("MY_ROUTE_TAG","The current Road is this long: " + coords.size());
                List<LatLng> coordinates = new ArrayList<>();

                PolylineOptions polyline = new PolylineOptions();

                for (GeoPoint g : coords) {
                    coordinates.add(new LatLng(g.getLatitude(), g.getLongitude()));
                }
                polyline.addAll(coordinates);
                polyline.width(3);
                polyline.color(Color.RED);
                mMapboxMap.addPolyline(polyline);
                Log.i("MY_ROUTE_TAG","Polyline added");

                moveCameraTo(user_position_marker.getPosition(), 15, 45);

                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                // enterFollowMode();
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


    private static final double FOLLOW_MODE_TILT_VALUE_DEGREES = 50;
    private static final double CENTER_ON_USER_ZOOM_LEVEL = 18;

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    protected synchronized void enterFollowMode() {
        mMapboxMap.removeMarker(user_position_marker);
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
        locationEngine.setInterval(500);
        locationEngine.setFastestInterval(100);
        locationEngine.setSmallestDisplacement(0);
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.addLocationEngineListener(this);
        locationEngine.activate();
        locationEngine.requestLocationUpdates();
        lastLocation = locationEngine.getLastLocation();
    }

    /**
     * From OnInfoWindowClickListener Interface, handles click on the info window
     * of the markers
     * @param marker The marker from which the info window has been clicked on
     * @return true, if the marker represents a stone, else return false
     */
    @Override
    public boolean onInfoWindowClick(@NonNull Marker marker) {
        if (NEXT && marker.equals(nearest_stone_marker) && !marker.equals(ulm_center_marker)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

            builder.setTitle("Zu diesem Stein führen lassen?");

            builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                if (ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                createRouteToNext();
                dialogInterface.cancel();
            });
            builder.setNegativeButton("Nein", (dialogInterface, i) -> {
                dialogInterface.cancel();
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getContext());

                builder2.setTitle("Informationen zu diesem Stein anzeigen?");

                builder2.setPositiveButton("Ja", (dialogInterface1, i1) -> {
                    mMapboxMap.deselectMarker(marker);
                    dialogInterface1.cancel();
                    Intent intent = new Intent(getActivity(), ScrollingInfoActivity.class);
                    intent.setAction(stone_handler.getStoneFromMarker(marker).toString());
                    startActivity(intent);
                });
                builder2.setNegativeButton("Nein", (dialogInterface1, i1) -> {
                    dialogInterface1.cancel();
                });

                // Create the AlertDialog
                AlertDialog dialog = builder2.create();
                dialog.show();
            });

            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
        Stone check = stone_handler.getStoneFromMarker(marker);
        if (check == null) {
            if (marker.equals(chosen_marker_start)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


                builder.setTitle("Start Markierung löschen?");

                builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                    mMapboxMap.removeMarker(chosen_marker_start);
                    chosen_marker_start = null;
                });
                builder.setNegativeButton("Nein", (dialogInterface, i) -> {
                });

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if (marker.equals(chosen_marker_end)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("End Markierung löschen?");

                builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                    mMapboxMap.removeMarker(chosen_marker_end);
                    chosen_marker_end = null;
                });
                builder.setNegativeButton("Nein", (dialogInterface, i) -> {
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            return false;
        }
        Intent intent = new Intent(getActivity(), ScrollingInfoActivity.class);
        intent.setAction(check.toString());
        startActivity(intent);
        return true;
    }

    /**
     * From OnMapLongClick Interface, if the user clicks long on the map
     * a dialog will be presented letting the user chose if a new marker should be placed
     * at this position
     *
     * @param point the position where the user can place a marker
     */
    @Override
    public void onMapLongClick(@NonNull LatLng point) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


        String[] choice = new String[]{"Route von hier", "Route nach hier", "Zurück"};

        builder.setTitle("Auswahl festlegen als:")
                .setItems(choice, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            if (chosen_marker_start != null) {
                                chosen_marker_start.setPosition(point);
                            } else {
                                chosen_position_start = point;
                                MarkerOptions chosen_marker_options = new MarkerOptions();
                                chosen_marker_options.setPosition(point);
                                chosen_marker_options.setTitle("Gewählte Start-Position");
                                chosen_marker_options.setSnippet("< Zum löschen hier drücken >");
                                chosen_marker_start = mMapboxMap.addMarker(chosen_marker_options);
                                mMapboxMap.selectMarker(chosen_marker_start);
                            }
                            break;
                        case 1:
                            if (chosen_marker_end != null) {
                                chosen_marker_end.setPosition(point);
                            } else {
                                chosen_position_end = point;
                                MarkerOptions chosen_marker_options = new MarkerOptions();
                                chosen_marker_options.setPosition(point);
                                chosen_marker_options.setTitle("Gewählte End-Position");
                                chosen_marker_options.setSnippet("< Zum löschen hier drücken >");
                                chosen_marker_end = mMapboxMap.addMarker(chosen_marker_options);
                                mMapboxMap.selectMarker(chosen_marker_end);
                            }
                            break;
                        default:
                    }
                });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onConnected() {
        lastLocation = locationEngine.getLastLocation();

        if(mMapboxMap == null) {
            return;
        }
        setUserMarker();
    }

    public void setUserMarker() {
        if (user_position_marker != null) {
            user_position_marker.setPosition(RoutingUtil.convertLocationToLatLng(lastLocation));
        } else {
            MarkerOptions user_position_marker_options = new MarkerOptions();
            user_position_marker_options.setPosition(RoutingUtil.convertLocationToLatLng(lastLocation));
            user_position_marker_options.setTitle("Sie sind hier");
            user_position_marker_options.setSnippet("");
            user_position_marker = mMapboxMap.addMarker(user_position_marker_options);
        }
    }

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    @Override
    public void onLocationChanged(Location location) {
        lastLocation = locationEngine.getLastLocation();
        if(mMapboxMap == null) {
            return;
        }
        setUserMarker();
    }

    @RequiresPermission(anyOf = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    public void test() {

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
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
