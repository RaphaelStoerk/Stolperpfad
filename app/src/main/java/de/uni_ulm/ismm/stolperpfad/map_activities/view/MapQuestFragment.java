package de.uni_ulm.ismm.stolperpfad.map_activities.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import com.mapquest.mapping.MapQuest;
import com.mapquest.mapping.maps.MapView;
import com.mapquest.navigation.NavigationManager;
import com.mapquest.navigation.dataclient.RouteService;

import de.uni_ulm.ismm.stolperpfad.MainMenuActivity;
import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.ScrollingInfoActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.model.StoneFactory;

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
    private RouteService mRouteService;
    private NavigationManager mNavigationManager;
    private Context ctx;
    private boolean next;
    private StoneFactory stone_handler;

    private final String API_KEY = "@string/mapquest_api_key";

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

        map = new MapView(ctx,null, 0, API_KEY);

        mRouteService = new RouteService.Builder().build(this.getContext(),API_KEY);

        map.onCreate(savedInstanceState);
        map.getMapAsync(mapboxMap -> {
            mMapboxMap = mapboxMap;
            map.setStreetMode();
            stone_handler = StoneFactory.initialize(this, mMapboxMap);
            mMapboxMap.setOnMarkerClickListener(marker -> {
                Intent intent = new Intent(getActivity(), ScrollingInfoActivity.class);
                intent.setAction(stone_handler.getStoneFromMarker(marker).toString());
                startActivity(intent);
                return false;
            });
            CameraPosition position = new CameraPosition.Builder()
                    .target(new LatLng(48.4011, 9.9876)) // Sets the new camera position
                    .zoom(16.) // Sets the zoom to level 10
                    .tilt(45) // Set the camera tilt to 20 degrees
                    .build(); // Builds the CameraPosition object from the builder

            mMapboxMap.easeCamera(mapboxMap1 -> position, 1000);
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
            if(next) {
                m.setIcon(IconFactory.getInstance(getContext()).fromFile("drawable/ic_menu_share.xml"));
            }
        }
        if(next) {
         //   stone_handler.getNearestTo(curr_user_location).setAlpha(1f);
        }
        map.invalidate();
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
