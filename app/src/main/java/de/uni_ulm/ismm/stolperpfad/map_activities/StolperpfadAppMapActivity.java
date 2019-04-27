package de.uni_ulm.ismm.stolperpfad.map_activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;
import timber.log.Timber;

public class StolperpfadAppMapActivity extends StolperpfadeAppActivity {

    protected static final String MAP_FRAGMENT_TAG_ROUTE = "MAPQUEST_MAP_FRAGMENT_ROUTE";
    protected static final String MAP_FRAGMENT_TAG_NEXT = "MAPQUEST_MAP_FRAGMENT_NEXT";
    public MapQuestFragment myMapFragment;
    protected PermissionsManager permissionsManager;
    protected boolean is_first_call;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        is_first_call = true;
    }

    public void onResume(){
        super.onResume();
        if(is_first_call) {
            requestPermissions();
            is_first_call = false;
        }
    }

    public void onPause(){
        super.onPause();
    }

    protected void initializeMapQuestFragment(int id, boolean next) {
        // The actual map view is now a fragment, for easier reuse and readability
        FragmentManager fm = this.getSupportFragmentManager();

        if (fm.findFragmentById(R.id.map_container) == null) {
            myMapFragment = MapQuestFragment.newInstance(id, next, aq);
            fm.beginTransaction().add(R.id.map_container, myMapFragment, MAP_FRAGMENT_TAG_ROUTE).commit();
        } else {
            myMapFragment = (MapQuestFragment) fm.findFragmentByTag(MAP_FRAGMENT_TAG_ROUTE);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    protected void requestPermissions() {

        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create a location engine instance
            myMapFragment.initializeLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(new PermissionsListener() {

                @Override
                public void onExplanationNeeded(List permissionsToExplain) {
                    // Left blank on purpose
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted) {
                        myMapFragment.initializeLocationEngine();
                    }
                }

            });
            permissionsManager.requestLocationPermissions(this);
        }
    }
}
