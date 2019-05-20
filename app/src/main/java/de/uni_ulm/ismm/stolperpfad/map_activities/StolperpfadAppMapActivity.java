package de.uni_ulm.ismm.stolperpfad.map_activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;

/**
 * Blueprint class for map activities, super class to the Route Planner activity
 */
public class StolperpfadAppMapActivity extends StolperpfadeAppActivity {

    protected static final String MAP_FRAGMENT_TAG = "MAPQUEST_MAP_FRAGMENT_ROUTE";

    public MapQuestFragment map_quest;
    protected boolean is_first_call;
    protected PermissionsManager permissionsManager;


    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
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

    /**
     * Initialize the MapQuest map fragment
     *
     * @param next_person_id the next persons id
     * @param next if the route planner activity should act like a next stone activity
     */
    protected void initializeMapQuestFragment(int next_person_id, boolean next) {
        // The actual map view is now a fragment, for easier reuse and readability
        FragmentManager fm = this.getSupportFragmentManager();
        if (fm.findFragmentById(R.id.map_container) == null) {
            map_quest = MapQuestFragment.newInstance(next_person_id, next, aq);
            fm.beginTransaction().add(R.id.map_container, map_quest, MAP_FRAGMENT_TAG).commit();
        } else {
            map_quest = (MapQuestFragment) fm.findFragmentByTag(MAP_FRAGMENT_TAG);
        }
    }

    /**
     * Check if the user has allowed to use the location and if not ask to do so
     */
    @SuppressWarnings( {"MissingPermission"})
    protected void requestPermissions() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create a location engine instance
            map_quest.initializeLocationEngine();
        } else {
            permissionsManager = new PermissionsManager(new PermissionsListener() {

                @Override
                public void onExplanationNeeded(List permissionsToExplain) {
                    // Left blank on purpose
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted) {
                        map_quest.initializeLocationEngine();
                    }
                    recreate();
                }
            });
            permissionsManager.requestLocationPermissions(this);
        }
    }
}
