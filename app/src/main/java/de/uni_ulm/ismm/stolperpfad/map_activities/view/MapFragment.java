package de.uni_ulm.ismm.stolperpfad.map_activities.view;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.ScrollingInfoActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.Stone;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.StoneFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements Marker.OnMarkerClickListener {

    private RotationGestureOverlay mRotationGestureOverlay;
    private MapView map;
    private boolean next;
    private StoneFactory stone_handler;
    private LocationManager loc_man;

    private Polyline store_current_drawn_path = new Polyline();
    private Marker curr_user_location;

    private final GeoPoint ULM_LOCATION = new GeoPoint(48.4011, 9.9876);
    private final double START_ZOOM = 14.;
    private final double MIN_ZOOM = 5.;
    private final double MAX_ZOOM = 20.;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance(boolean next) {
        MapFragment fragment = new MapFragment();
        fragment.next = next;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: Delete these policies and bugfix afterwards
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        map = new MapView(inflater.getContext());

        stone_handler = StoneFactory.initialize(this);

        curr_user_location = new Marker(map);
        curr_user_location.setPosition(new GeoPoint(48.3973, 9.99124));

        // set up the GPS Tracking of the user
        loc_man = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions
        } else {
            loc_man.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        }
        return map;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        //load/initialize the osmdroid configuration
        final Context ctx = this.getActivity();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        // final DisplayMetrics dm = ctx.getResources().getDisplayMetrics();

        // MAPNIK is standard "style" for open street maps, other styles and overlays possible
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        IMapController mapController = map.getController();

        mapController.setCenter(ULM_LOCATION);
        mapController.setZoom(START_ZOOM);

        //support for map rotation
        mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(this.mRotationGestureOverlay);

        //needed for pinch zooms
        map.setMultiTouchControls(true);

        //scales tiles to the current screen's DPI, helps with readability of labels
        map.setTilesScaledToDpi(true);

        // lock the possible Zoom levels
        map.setMinZoomLevel(MIN_ZOOM);
        map.setMaxZoomLevel(MAX_ZOOM);

        // stop the display of zoom controls
        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        map.invalidate();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        map.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    /**
     * Displays the Stone markers on the map, that have been stored in the stone factory
     */
    public void setStones() {
        if(!stone_handler.isReady() || map == null) {
            return;
        }
        for(Marker m : stone_handler.getMarkers()) {
            m.setIcon(getResources().getDrawable(R.drawable.marker_default, null));
            m.setOnMarkerClickListener(this);
            if(next) {
                m.setAlpha(0.5f);
            }
            map.getOverlays().add(m);
        }
        if(next) {
            stone_handler.getNearestTo(curr_user_location).setAlpha(1f);
        }
        map.invalidate();
    }

    public MapView getView() {
        return map;
    }

    public void updateUserPosition(GeoPoint pos) {
        curr_user_location.setPosition(pos);
        map.invalidate();
    }

    @Override
    public boolean onMarkerClick(final Marker marker, MapView mapView) {
        final String[] options = {"Show Info","Route To Nearest Stone", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(map.getContext());
        builder.setTitle(marker.getTitle());
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch(options[which]) {
                    case "Show Info":
                        Intent intent = new Intent(map.getContext(), ScrollingInfoActivity.class);
                        startActivity(intent);
                        break;
                    case "Route To Nearest Stone":
                        routeToNext(marker);
                        break;
                    case "Nothing":
                    default:
                        return;
                }
            }
        });
        builder.show();
        return false;
    }

    /**
     * This method calculates and displays the (pedestrian) route from a marker to its
     * closest neighbour
     * @param marker the start of the route
     */
    @SuppressLint("StaticFieldLeak")
    public void routeToNext(Marker marker) {
        Stone rel_stone = stone_handler.getStoneFromMarker(marker);
        Stone goal = stone_handler.getNearestTo(rel_stone);

        Log.i("HELPME", "This is what you get: " + rel_stone.getLocation().getLongitude());

        new CreateRouteTask(){
            @Override
            public void onPostExecute(Road road) {

                map.getOverlays().remove(store_current_drawn_path);

                store_current_drawn_path = RoadManager.buildRoadOverlay(road);

                store_current_drawn_path.setColor(Color.RED);

                store_current_drawn_path.setWidth(10f);

                map.getOverlays().add(store_current_drawn_path);


                // this is for displaying the steps of the route one by one
                // TODO: maybe add in later
                /*
                for (int i=0; i<road.mNodes.size(); i++){
                    RoadNode node = road.mNodes.get(i);
                    Marker nodeMarker = new Marker(map);
                    nodeMarker.setPosition(node.mLocation);
                    nodeMarker.setVisible(false);
                    nodeMarker.setTitle("Step "+ i);
                    nodeMarker.setSnippet(node.mInstructions);
                    nodeMarker.setSubDescription(Road.getLengthDurationText(getActivity(), node.mLength, node.mDuration));
                    nodeMarker.setImage(nodeIcon);
                    map.getOverlays().add(nodeMarker);
                }
                */
                map.invalidate();
            }
        }.execute(new Stone[]{rel_stone, goal});


        Log.i("HELPME", "This is what you get: " + rel_stone.getLocation().getLongitude());
    }

    @SuppressLint("StaticFieldLeak")
    public void createRoute(String duration, int start) {
        Marker start_marker = new Marker(map);
        switch(start) {
            case 0:
            case 1:
            case 2:
                start_marker.setPosition(new GeoPoint(48.3993, 9.98724));
        }
        int time = Integer.parseInt(duration);

        Log.i("ROUTE", "STARTED PLANNING");


        // TODO: create a good route through ulm

        ArrayList<Stone> route_points = new ArrayList<>();
        route_points.addAll(stone_handler.getStones());
        for(int i = 0; i < 5; i++) {
            int one = (int) (Math.random() * route_points.size());
            route_points.add(route_points.remove(one));
            if(Math.random() < 0.2) {
                route_points.remove(0);
            }
        }

        Log.i("ROUTE", "FINISHED ALL WONKY BUSINESS");

        new CreateRouteTask(){
            @Override
            public void onPostExecute(Road road) {

                map.getOverlays().remove(store_current_drawn_path);

                store_current_drawn_path = RoadManager.buildRoadOverlay(road);

                store_current_drawn_path.setColor(Color.RED);

                store_current_drawn_path.setWidth(10f);
                
                map.getOverlays().add(store_current_drawn_path);

                map.invalidate();
            }
        }.execute(route_points.toArray(new Stone[]{}));

        Log.i("ROUTE", "ALL DONE");

    }

    /**
     * This class is responsible for creating a network call to get a route
     * for a collection of - at least - two stones
     * A MapQuest Key is needed for the call to be succesful
     */
    private class CreateRouteTask extends AsyncTask<Stone[], Void, Road> {

        @Override
        protected Road doInBackground(Stone[]... stones) {

            Log.i("HELPME", "This is what you get: " + stones.length);

            if(stones == null || stones.length < 1) {
                Log.i("ERROR", "NO STONE FOUND");
                return null;
            }

            RoadManager roadManager = new MapQuestRoadManager(getResources().getString(R.string.mapquest_api_key));

            roadManager.addRequestOption("routeType=pedestrian");

            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            for(Stone s : stones[0]) {
                waypoints.add(s.getLocation());
            }

            Log.i("HELPME", "This is what you get: " + waypoints.get(0).getLongitude());

            Road road = roadManager.getRoad(waypoints);

            return road;
        }
    }


    // TODO: Add functionality to the GPS Tracking
    /*---------- Listener class to get coordinates ------------- */
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            double longitude = loc.getLongitude();
            double latitude = loc.getLatitude();

            updateUserPosition(new GeoPoint(latitude, longitude));

        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
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
