package de.uni_ulm.ismm.stolperpfad.general;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import de.uni_ulm.ismm.stolperpfad.MainMenuActivity;
import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.history.HistoListActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.impressum.ImpressumActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.impressum.PrivacyInfoActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist.ProjectAndArtistOverviewActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneListActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.RoutePlannerActivity;
import de.uni_ulm.ismm.stolperpfad.scanner.ScannerActivity;

/**
 * This is the global Listener for all the button interactions with the Stolperpfade App.
 * To use this Listener, your Activity should extend the StolperpfadAppActivity class
 * then you can initialize this Listener with your Activity as follows:
 * my_click_listener = new AppClickListener<>();
 * my_click_listener.setParent_activity(this);
 * and then proceed to add this listener to any of your interactive items. After that you
 * can simply add a new case to the switch statement below and handle the events as you like.
 *
 * @param <T> A StolperpfAppActivity that acts as the context for the interactive item.
 */
public class AppClickListener<T extends StolperpfadeAppActivity> implements View.OnClickListener {

    private T parent_activity;

    void setParent_activity(T parent_activity) {
        this.parent_activity = parent_activity;
    }

    /**
     * The interactive powerhouse of this application, handles all click-inputs from the user
     *
     * @param clicked_element the view element on which the user clicked
     */
    @Override
    public void onClick(View clicked_element) {
        Intent intent = null;
        Bundle transition_options = ActivityOptions.makeSceneTransitionAnimation(parent_activity).toBundle();
        // a "simple" switch case statement that checks which button was pressed
        switch (clicked_element.getId()) {

            // ######################## //
            // buttons on the main menu
            // ######################## //
            case R.id.scan_button:
                ((ScannerActivity) parent_activity).takePicture();
                break;
            case R.id.info_button:
                intent = new Intent(parent_activity, StoneListActivity.class);
                break;
            case R.id.menu_to_scan_button:
                intent = new Intent(parent_activity, ScannerActivity.class);
                break;
            case R.id.menu_to_route_button:
                intent = new Intent(parent_activity, RoutePlannerActivity.class);
                intent.putExtra("id", RoutePlannerActivity.NO_NEXT_STONE_FLAG);
                intent.putExtra("next", false);
                break;
            case R.id.menu_to_next_stone_button:
                intent = new Intent(parent_activity, RoutePlannerActivity.class);
                intent.putExtra("id", RoutePlannerActivity.NO_NEXT_STONE_FLAG);
                intent.putExtra("next", true);
                break;

            // ######################## //
            // header buttons
            // ######################## //
            case R.id.header_image:
                if (parent_activity instanceof MainMenuActivity) {
                    return;
                }
                intent = new Intent(parent_activity, MainMenuActivity.class);
                break;
            case R.id.quick_access_button:
                parent_activity.showQuickAccesMenu();
                break;

            // ######################## //
            // buttons in the quick acces dialog
            // ######################## //
            case R.id.quick_access_scanner_button:
                intent = intentFromQuickAccess(ScannerActivity.class);
                break;
            case R.id.quick_access_stone_info:
                intent = intentFromQuickAccess(StoneListActivity.class);
                intent.setAction("1");
                break;
            case R.id.quick_access_next_stone_button:
                intent = intentFromQuickAccess(RoutePlannerActivity.class);
                intent.putExtra("id", RoutePlannerActivity.NO_NEXT_STONE_FLAG);
                intent.putExtra("next", true);
                break;
            case R.id.quick_access_route_planner:
                intent = intentFromQuickAccess(RoutePlannerActivity.class);
                intent.putExtra("id", RoutePlannerActivity.NO_NEXT_STONE_FLAG);
                intent.putExtra("next", false);
                break;
            case R.id.quick_access_historical_info:
                intent = intentFromQuickAccess(HistoListActivity.class);
                break;
            case R.id.quick_access_project_artist:
                intent = intentFromQuickAccess(ProjectAndArtistOverviewActivity.class);
                break;
            case R.id.quick_access_impressum:
                intent = intentFromQuickAccess(ImpressumActivity.class);
                break;
            case R.id.quick_access_privacy:
                intent = intentFromQuickAccess(PrivacyInfoActivity.class);
                break;
            case R.id.dark_mode_text:
                clicked_element = null;
            case R.id.dark_mode_switch:
                parent_activity.toggleDarkMode(clicked_element == null ? null : (Switch) clicked_element, true);
            case R.id.header_quick_access_cancel_button:
                parent_activity.endQuickAccesDialog();
                break;

            // ######################## //
            // buttons on the map activity
            // ######################## //
            case R.id.route_option_button:
                if (parent_activity instanceof RoutePlannerActivity)
                    ((RoutePlannerActivity) parent_activity).routeOptionDialog();
                break;
            case R.id.save_route_button:
                if (parent_activity instanceof RoutePlannerActivity)
                    ((RoutePlannerActivity) parent_activity).saveOrLoadRouteDialog();
                break;
            case R.id.info_map_options_button:
                if (parent_activity instanceof RoutePlannerActivity)
                    ((RoutePlannerActivity) parent_activity).informationDialog();
                break;
            case R.id.start_guide_button:
                if (parent_activity instanceof RoutePlannerActivity)
                    ((RoutePlannerActivity) parent_activity).startGuide();
                break;
            case R.id.menu_open_button:
                if (parent_activity instanceof RoutePlannerActivity) {
                    ((RoutePlannerActivity) parent_activity).toggleMenu();
                }
                break;

            // ######################## //
            // buttons on information screens
            // ######################## //
            case R.id.overview_to_project_info_button:
                if (parent_activity instanceof ProjectAndArtistOverviewActivity) {
                    ((ProjectAndArtistOverviewActivity) parent_activity).setInfoDisplay(ProjectAndArtistOverviewActivity.DISPLAY_PROJECT);
                }
                break;
            case R.id.overview_to_artist_info_button:
                if (parent_activity instanceof ProjectAndArtistOverviewActivity) {
                    ((ProjectAndArtistOverviewActivity) parent_activity).setInfoDisplay(ProjectAndArtistOverviewActivity.DISPLAY_ARTIST);
                }
                break;
            case R.id.impressum_to_rights_button:
                if (parent_activity instanceof ImpressumActivity) {
                    ((ImpressumActivity) parent_activity).setInfoDisplay(ImpressumActivity.DISPLAY_RIGHTS);
                }
                break;
            case R.id.impressum_to_contact_button:
                if (parent_activity instanceof ImpressumActivity) {
                    ((ImpressumActivity) parent_activity).setInfoDisplay(ImpressumActivity.DISPLAY_CONTACT);
                }
                break;
            case R.id.button_back:
                parent_activity.onBackPressed();
                break;
        }
        if (intent != null) {
            parent_activity.startActivity(intent, transition_options);
        }
    }

    /**
     * Sets up an intent from a button click in the quick access menu
     *
     * @param activity_to_start the activity that should be started
     * @return the created intent
     */
    private Intent intentFromQuickAccess(Class activity_to_start) {
        parent_activity.endQuickAccesDialog();
        return new Intent(parent_activity.getApplicationContext(), activity_to_start);
    }
}
