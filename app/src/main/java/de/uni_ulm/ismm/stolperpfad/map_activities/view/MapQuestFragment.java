package de.uni_ulm.ismm.stolperpfad.map_activities.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import com.mapquest.mapping.MapQuest;
import com.mapquest.mapping.maps.MapView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapQuestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapQuestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapQuestFragment extends Fragment {

    private MapView map;
    private MapboxMap mMapboxMap;
    private Context ctx;
    private boolean next;

    private Polyline store_current_drawn_path;

    public MapQuestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment MapFragment.
     */
    public static MapQuestFragment newInstance(boolean next) {
        MapQuestFragment fragment = new MapQuestFragment();
        fragment.next = next;
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

        MapQuest.start(ctx);

        map = new MapView(ctx,null, 0, "@string/mapquest_api_key");

        map.onCreate(savedInstanceState);
        map.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;
            map.setStreetMode();
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(48.4011, 9.9876));
            markerOptions.title("Test1");
            markerOptions.snippet("Test Text 1");
            mapboxMap.addMarker(markerOptions);
            markerOptions.position(new LatLng(48.39855, 9.99123));
            markerOptions.title("Test2");
            markerOptions.snippet("Test Text 2");
            mapboxMap.addMarker(markerOptions);
        });


        CameraPosition position = new CameraPosition.Builder()
                .target(new LatLng(48.4011, 9.9876)) // Sets the new camera position
                .zoom(20) // Sets the zoom to level 10
                .tilt(20) // Set the camera tilt to 20 degrees
                .build(); // Builds the CameraPosition object from the builder



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
