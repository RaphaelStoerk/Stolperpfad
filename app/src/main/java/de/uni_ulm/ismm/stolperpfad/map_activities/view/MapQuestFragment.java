package de.uni_ulm.ismm.stolperpfad.map_activities.view;

import java.util.ArrayList;
import java.util.List;

import de.uni_ulm.ismm.stolperpfad.BuildConfig;
import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.ScrollingInfoActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.Stone;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.StoneFactory;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class MapQuestFragment extends Fragment implements MapboxMap.OnInfoWindowClickListener, MapboxMap.OnMapLongClickListener {

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
    private Polyline store_current_drawn_path;

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

        map = new MapView(ctx,null, 0, API_KEY);

        // TODO: Leave if necessary, check if disposable
        mRouteService = new RouteService.Builder().build(this.getContext(),API_KEY);

        // Initialize the map visuals
        map.onCreate(savedInstanceState);
        map.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;
            map.setStreetMode();

            stone_handler = StoneFactory.initialize(this, mMapboxMap);

            chosen_position_start = new LatLng(0,0);
            chosen_marker_start = null;
            chosen_position_end = new LatLng(0,0);
            chosen_marker_end = null;

            mMapboxMap.setOnInfoWindowClickListener(this);

            mapboxMap.addOnMapLongClickListener(this);

            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(48.398638, 9.993720)) // Sets the new camera position
                    .zoom(13.5) // Sets the zoom to level 14
                    .tilt(60) // Set the camera tilt to 45 degrees
                    .build(); // Builds the CameraPosition object from the builder

            mMapboxMap.easeCamera(mapboxMap1 -> position, 2000);
        });

        return map;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onResume()
    { super.onResume(); map.onResume(); }

    @Override
    public void onPause()
    { super.onPause(); map.onPause(); }

    /**
     * Displays the Stone markers on the map, that have been stored in the stone factory
     */
    public void setStones() {
        if(!stone_handler.isReady() || map == null) {
            return;
        }
        for(Marker m : stone_handler.getMarkers()) {
            m.setIcon(IconFactory.getInstance(getContext()).defaultMarker());
            if(NEXT) {
                // TODO: use a individual marker to define the stones on the map
                //   m.setIcon(IconFactory.getInstance(getContext()).fromFile("drawable/ic_menu_share.xml"));
            }
        }
        if(NEXT) {
            // TODO: use another marker to stylize the nearest Stone on the map
            //   stone_handler.getNearestTo(curr_user_location).setAlpha(1f);
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
    @SuppressLint("StaticFieldLeak")
    public void createRoute(String category_selected, int time_in_minutes, int start_choice, int end_choice) {

        // TODO: create a good route through ulm

        ArrayList<Stone> route_points = new ArrayList<>();

        new CreateRouteTask(){
            @Override
            public void onPostExecute(Road road) {
                ArrayList<GeoPoint> coords = road.mRouteHigh;
                List<LatLng> coordinates = new ArrayList<>();

                PolylineOptions polyline = new PolylineOptions();

                for(GeoPoint g : coords) {
                    coordinates.add(new LatLng(g.getLatitude(),g.getLongitude()));
                }
                polyline.addAll(coordinates);
                polyline.width(3);
                polyline.color(Color.BLUE);
                mMapboxMap.addPolyline(polyline);
            }
        }.execute(route_points.toArray(new Stone[]{}));

    }

    /**
     * From OnInfoWindowClickListener Interface, handles click on the info window
     * of the markers
     * @param marker The marker from which the info window has been clicked on
     * @return true, if the marker represents a stone, else return false
     */
    @Override
    public boolean onInfoWindowClick(@NonNull Marker marker) {
        Stone check = stone_handler.getStoneFromMarker(marker);
        if(check == null) {
            if(marker.equals(chosen_marker_start)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());


                builder.setTitle("Start Markierung löschen?");

                builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                    mMapboxMap.removeMarker(chosen_marker_start);
                    chosen_marker_start = null;
                });
                builder.setNegativeButton("Nein", (dialogInterface, i) -> {});

                // Create the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            } else if(marker.equals(chosen_marker_end)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("End Markierung löschen?");

                builder.setPositiveButton("Ja", (dialogInterface, i) -> {
                    mMapboxMap.removeMarker(chosen_marker_end);
                    chosen_marker_end = null;
                });
                builder.setNegativeButton("Nein", (dialogInterface, i) -> {});

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


        String[] choice = new String[] {"Route von hier", "Route nach hier", "Zurück"};

        builder.setTitle("Auswahl festlegen als:")
                .setItems(choice, (dialog, which) -> {
                    switch(which) {
                        case 0:
                            if(chosen_marker_start != null) {
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
                            if(chosen_marker_end != null) {
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

    /**
     * This class is responsible for creating a network call to get a route
     * for a collection of - at least - two stones
     * A MapQuest Key is needed for the call to be succesful
     */
    private class CreateRouteTask extends AsyncTask<Stone[], Void, Road> {

        @Override
        protected Road doInBackground(Stone[]... stones) {

            RoadManager roadManager = new MapQuestRoadManager(getResources().getString(R.string.mapquest_api_key));

            roadManager.addRequestOption("routeType=pedestrian");

            ArrayList<GeoPoint> waypoints = new ArrayList<>();

            waypoints.add(new GeoPoint(48.4011, 9.9876));
            waypoints.add(new GeoPoint(48.40002, 9.99721));

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
