package de.uni_ulm.ismm.stolperpfad.map_activities.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.MapQuestRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.bonuspack.routing.RoadNode;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.MinimapOverlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.MainMenuActivity;
import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.ScrollingInfoActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.RoutingTests;
import de.uni_ulm.ismm.stolperpfad.map_activities.RoutingUtil;
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
    private MinimapOverlay mMinimapOverlay;
    private boolean next;
    private StoneFactory stone_handler;

    private Polyline store_current_drawn_path = new Polyline();

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(boolean next) {
        MapFragment fragment = new MapFragment();
        fragment.next = next;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        map = new MapView(inflater.getContext());
        stone_handler = StoneFactory.initialize(this);
        return map;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);


        //load/initialize the osmdroid configuration, this can be done
        final Context ctx = this.getActivity();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        final DisplayMetrics dm = ctx.getResources().getDisplayMetrics();

        // MAPNIK is standard "style" for open street maps, other styles and overlays possible
        map.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

        IMapController mapController = map.getController();

        GeoPoint startPoint = new GeoPoint(48.4011, 9.9876);
        mapController.setCenter(startPoint);
        mapController.setZoom(14.);

        //support for map rotation
        mRotationGestureOverlay = new RotationGestureOverlay(map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(this.mRotationGestureOverlay);

        //needed for pinch zooms
        map.setMultiTouchControls(true);

        //scales tiles to the current screen's DPI, helps with readability of labels
        map.setTilesScaledToDpi(true);

        map.setMinZoomLevel(5.);
        map.setMaxZoomLevel(20.);

        map.getZoomController().setVisibility(CustomZoomButtonsController.Visibility.NEVER);

        // RoutingTests.test_routing(this.getActivity(), map,startPoint, next);

        map.invalidate();

    }

    public void setStones() {
        if(!stone_handler.isReady()) {
            return;
        }
        for(Marker m : stone_handler.getMarkers()) {
            m.setIcon(getResources().getDrawable(R.drawable.marker_default, null));
            m.setOnMarkerClickListener(this);
            map.getOverlays().add(m);
        }
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
        map.onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //this part terminates all of the overlays and background threads for osmdroid
        //only needed when you programmatically create the map
        map.onDetach();

    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    public MapView getView() {
        return map;
    }

    public void routeToNext(Marker marker) {
        Stone rel_stone = stone_handler.getStoneFromMarker(marker);
        Stone goal = stone_handler.getNearestTo(rel_stone);
        if(goal == null) {
            Log.i("ERROR", "NO STONE FOUND");
            return;
        }

        new CreateRouteTask(){
            @Override
            public void onPostExecute(Road road) {

                map.getOverlays().remove(store_current_drawn_path);

                store_current_drawn_path = RoadManager.buildRoadOverlay(road);

                map.getOverlays().add(store_current_drawn_path);

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
        }.execute(rel_stone, goal);
    }


    private class CreateRouteTask extends AsyncTask<Stone, Void, Road> {

        @Override
        protected Road doInBackground(Stone... stones) {

            if(stones == null || stones.length < 2) {
                Log.i("ERROR", "NO STONE FOUND");
                return null;
            }

            RoadManager roadManager = new MapQuestRoadManager(getResources().getString(R.string.mapquest_api_key));

            roadManager.addRequestOption("routeType=pedestrian");

            ArrayList<GeoPoint> waypoints = new ArrayList<>();
            for(Stone s : stones) {
                waypoints.add(s.getLocation());
            }
            Log.i("LOGGED INFO", "THIS IS LOCATION 1: " + waypoints.get(0).getLongitude());
            Log.i("LOGGED INFO", "THIS IS LOCATION 2: " + waypoints.get(1).getLongitude());

            Road road = roadManager.getRoad(waypoints);

            return road;
        }
    }


    @Override
    public boolean onMarkerClick(final Marker marker, MapView mapView) {
        final String[] options = {"Show Info","Route To Next Stone", "Nothing"};

        AlertDialog.Builder builder = new AlertDialog.Builder(map.getContext());
        builder.setTitle("What would you like to do?");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch(options[which]) {
                    case "Show Info":
                        Intent intent = new Intent(map.getContext(), ScrollingInfoActivity.class);
                        startActivity(intent);
                        break;
                    case "Route To Next Stone":
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
