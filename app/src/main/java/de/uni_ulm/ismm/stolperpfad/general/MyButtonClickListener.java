package de.uni_ulm.ismm.stolperpfad.general;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Switch;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadApplication;
import de.uni_ulm.ismm.stolperpfad.database.list_of_historical_terms.HistoryActivity;
import de.uni_ulm.ismm.stolperpfad.database.list_of_persons.PersonsActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.BiographyExampleActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.ScrollingInfoActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.impressum.ImpressumViewActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.impressum.PrivacyInfoActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist.ArtistInfoActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist.ProjectAndArtistOverviewActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist.ProjectInfoActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.NextStoneActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.RoutePlannerActivity;
import de.uni_ulm.ismm.stolperpfad.scanner.ScannerActivity;

/**
 * This is the global Listener for all the button interactions with the Stolperpfade App.
 * To use this Listener, your Activity should extend the StolperpfadAppActivity class
 * then you can initialize this Listener with your Activity as follows:
 * myClickListener = new MyButtonClickListener<>();
 * myClickListener.setMyActivity(this);
 * and then proceed to add this listener to any of your interactive items. After that you
 * can simply add a new case to the switch statement below and handle the events as you like.
 *
 * @param <T> A StolperpfAppActivity that acts as the context for the interactive item.
 */
public class MyButtonClickListener<T extends StolperpfadeAppActivity> implements View.OnClickListener {

    private T myActivity;

    public void setMyActivity(T myActivity) {
        this.myActivity = myActivity;
    }

    /**
     * The interactive powerhouse of this application, handles all click-inputs from the user
     *
     * @param v the view on which the user clicked
     */
    @Override
    public void onClick(View v) {
        Intent intent = null;
        Bundle transitionOptions = ActivityOptions.makeSceneTransitionAnimation(myActivity).toBundle();
        Log.i("MY_CLICK_LISTENER","BUTTON CLICKED " + v.getId());
        // a "simple" switch case statement that checks which button was pressed
        switch (v.getId()) {
            case R.id.info_button:
                intent = new Intent(myActivity, PersonsActivity.class);
                break;
            case R.id.menu_to_scan_button:
                intent = new Intent(myActivity, ScannerActivity.class);
                break;
            case R.id.menu_to_route_button:
                intent = new Intent(myActivity, RoutePlannerActivity.class);
                break;
            case R.id.menu_to_next_stone_button:
                intent = new Intent(myActivity, NextStoneActivity.class);
                break;
            case R.id.quick_access_button:
                myActivity.showQuickAccesMenu();
                break;
            case R.id.quick_acces_scanner_button:
                intent = new Intent(myActivity, ScannerActivity.class);
                myActivity.endQuickAccesDialog();
                break;
            case R.id.quick_access_stone_info:
                intent = new Intent(myActivity, PersonsActivity.class);
                myActivity.endQuickAccesDialog();
                break;
            case R.id.quick_acces_next_stone_button:
                intent = new Intent(myActivity, NextStoneActivity.class);
                myActivity.endQuickAccesDialog();
                break;
            case R.id.quick_acces_route_planner:
                intent = new Intent(myActivity, RoutePlannerActivity.class);
                myActivity.endQuickAccesDialog();
                break;
            case R.id.quick_access_historical_info:
                intent = new Intent(myActivity, HistoryActivity.class);
                myActivity.endQuickAccesDialog();
                break;
            case R.id.quick_acces_project_artist:
                intent = new Intent(myActivity, ProjectAndArtistOverviewActivity.class);
                myActivity.endQuickAccesDialog();
                break;
            case R.id.quick_access_impresum:
                intent = new Intent(myActivity, ImpressumViewActivity.class);
                myActivity.endQuickAccesDialog();
                break;
            case R.id.quick_acces_privacy:
                intent = new Intent(myActivity, PrivacyInfoActivity.class);
                myActivity.endQuickAccesDialog();
                break;
            case R.id.header_quick_access_cancel_button:
                myActivity.endQuickAccesDialog();
                break;
            case R.id.dark_mode_switch:
                myActivity.toggleDarkMode((Switch) v, true);
                myActivity.endQuickAccesDialog();
                break;
            case R.id.route_option_button:
                if (myActivity instanceof RoutePlannerActivity)
                    ((RoutePlannerActivity) myActivity).routeOptionDialog();
                break;
            case R.id.save_route_button:
                if (myActivity instanceof RoutePlannerActivity)
                    if (ActivityCompat.checkSelfPermission(myActivity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(myActivity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    // TODO: Save the Route
                break;
            case R.id.info_map_options_button:
                // TODO: Some nice Dialog
                break;
            case R.id.overview_to_project_info_button:
                intent = new Intent(myActivity, ProjectInfoActivity.class);
                break;
            case R.id.overview_to_artist_info_button:
                intent = new Intent(myActivity, ArtistInfoActivity.class);
                break;
            case R.id.info_test_button:
                intent = new Intent(myActivity, BiographyExampleActivity.class);
                break;
        }
        if(intent != null) {
            myActivity.startActivity(intent, transitionOptions);
        }
    }
}