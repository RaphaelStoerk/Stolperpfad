package de.uni_ulm.ismm.stolperpfad.map_activities.fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
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

import de.uni_ulm.ismm.stolperpfad.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {

    private RotationGestureOverlay mRotationGestureOverlay;
    private MapView map;
    private MinimapOverlay mMinimapOverlay;
    private boolean next;

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

        //Mini map
        mMinimapOverlay = new MinimapOverlay(ctx, map.getTileRequestCompleteHandler());
        mMinimapOverlay.setWidth(dm.widthPixels / 5);
        mMinimapOverlay.setHeight(dm.heightPixels / 5);
        map.getOverlays().add(this.mMinimapOverlay);

        markerTests(startPoint);

        map.invalidate();

    }

    /**
     * Testing placing of markers and calculating nearest stone
     * @param startPoint ulm
     */
    private void markerTests(GeoPoint startPoint) {

        // The following should be automated with database coordinates
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setTitle("Ulm");
        startMarker.setSubDescription("Das ist Ulm");

        Marker test = new Marker(map);
        test.setPosition(new GeoPoint(48.398638, 9.993720));
        test.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        test.setTitle("Polatschek");
        test.setSubDescription("Das ist nicht Ulm");


        Marker test2 = new Marker(map);
        test2.setPosition(new GeoPoint(48.39855, 9.99123));
        test2.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        test2.setTitle("Test");
        test2.setSubDescription("Noch mal");


        Marker test3 = new Marker(map);
        test3.setPosition(new GeoPoint(48.3893, 9.98924));
        test3.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        test3.setTitle("Test");
        test3.setSubDescription("Noch mal");


        Marker test4 = new Marker(map);
        test4.setPosition(new GeoPoint(48.40002, 9.99721));
        test4.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        test4.setTitle("Test");
        test4.setSubDescription("Noch mal");

        ArrayList<Marker> myMarkers = new ArrayList<>();
        myMarkers.add(test);
        myMarkers.add(test2);
        myMarkers.add(test3);
        myMarkers.add(test4);
        myMarkers.add(startMarker);

        // setting the markers and looking for the nearest stone
        Marker best = null;
        double shortest = -1;
        double buff;
        for(Marker m : myMarkers) {
            m.setIcon(getResources().getDrawable(R.drawable.marker_default, null));
            map.getOverlays().add(m);
            buff = getDist(startMarker, m);
            if (next && m != startMarker && (buff < shortest || shortest == -1)) {
                shortest = buff;
                best = m;
            }
            if(next) {
                m.setAlpha(0.5f);
            }
            Log.d("TEST", "Set new marker" + m.getPosition().getLongitude());

        }
        if(next) {
            best.setAlpha(1f);
        }

        RoadManager roadManager = new OSRMRoadManager(this.getActivity());

        ArrayList<GeoPoint> waypoints = new ArrayList<GeoPoint>();
        waypoints.add(startPoint);
        GeoPoint endPoint = new GeoPoint(48.398638, 9.993720);
        waypoints.add(endPoint);

        Road road = roadManager.getRoad(waypoints);

        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);

        map.getOverlays().add(roadOverlay);

        Drawable nodeIcon = getResources().getDrawable(R.drawable.marker_cluster);
        for (int i=0; i<road.mNodes.size(); i++){
            RoadNode node = road.mNodes.get(i);
            Marker nodeMarker = new Marker(map);
            nodeMarker.setPosition(node.mLocation);
            nodeMarker.setIcon(nodeIcon);
            nodeMarker.setTitle("Step "+i);
            nodeMarker.setSnippet(node.mInstructions);
            nodeMarker.setSubDescription(Road.getLengthDurationText(this.getActivity(), node.mLength, node.mDuration));
            Drawable icon = getResources().getDrawable(R.drawable.ic_menu_compass);
            nodeMarker.setImage(icon);
            map.getOverlays().add(nodeMarker);
        }
    }

    /**
     * calc the (direct) distance between two markers
     * @param m1 marker 1
     * @param m2 marker 2
     * @return the distance bewtween m1 and m2
     */
    private static double getDist(Marker m1, Marker m2) {
        //TODO: Do not use direct distance but rather path distance
        double lat_dif = m1.getPosition().getLatitude() - m2.getPosition().getLatitude();
        double lng_diff = m1.getPosition().getLongitude() - m2.getPosition().getLongitude();
        return lat_dif * lat_dif + lng_diff * lng_diff;
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
