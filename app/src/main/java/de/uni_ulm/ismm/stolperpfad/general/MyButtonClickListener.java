package de.uni_ulm.ismm.stolperpfad.general;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import de.uni_ulm.ismm.stolperpfad.MainMenuActivity;
import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.impressum.ImpressumViewActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.impressum.PrivacyInfoActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist.ProjectAndArtistOverviewActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneListActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.NextStoneActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.RoutePlannerActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.RouteOptionsDialog;
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
        // a "simple" switch case statement that checks which button was pressed
        switch (v.getId()) {
            case R.id.scan_button:
                ((ScannerActivity) myActivity).takePicture();
                break;
            case R.id.info_button:
                intent = new Intent(myActivity, StoneListActivity.class);
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
            case R.id.header_image:
                if(myActivity instanceof MainMenuActivity) {
                    return;
                }
                intent = new Intent(myActivity, MainMenuActivity.class);
                break;
            case R.id.quick_access_button:
                myActivity.showQuickAccesMenu();
                break;
            case R.id.quick_access_scanner_button:
                intent = intentFromQuickAccess(ScannerActivity.class);
                break;
            case R.id.quick_access_stone_info:
                intent = intentFromQuickAccess(StoneListActivity.class);
                intent.setAction("1");
                break;
            case R.id.quick_access_next_stone_button:
                intent = intentFromQuickAccess(NextStoneActivity.class);
                break;
            case R.id.quick_access_route_planner:
                intent = intentFromQuickAccess(RoutePlannerActivity.class);
                break;
            /*case R.id.quick_access_historical_info:
                intent = intentFromQuickAccess(HistoryActivity.class);
                break;*/
            case R.id.quick_access_project_artist:
                intent = intentFromQuickAccess(ProjectAndArtistOverviewActivity.class);
                break;
            case R.id.quick_access_impressum:
                intent = intentFromQuickAccess(ImpressumViewActivity.class);
                break;
            case R.id.quick_access_privacy:
                intent = intentFromQuickAccess(PrivacyInfoActivity.class);
                break;
            case R.id.header_quick_access_cancel_button:
                myActivity.endQuickAccesDialog();
                break;
            case R.id.dark_mode_text:
                // v = myActivity.findViewById(R.id.dark_mode_switch);
                return;
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
                    ((RoutePlannerActivity) myActivity).saveOrLoadRouteDialog();
                break;
            case R.id.info_map_options_button:
                if (myActivity instanceof RoutePlannerActivity)
                    ((RoutePlannerActivity) myActivity).informationDialog();
                break;
            case R.id.start_guide_button:
                if (myActivity instanceof RoutePlannerActivity)
                    ((RoutePlannerActivity) myActivity).startGuide();
                break;
            case R.id.menu_close_button:
            case R.id.menu_open_button:
                if(myActivity instanceof RoutePlannerActivity) {
                    ((RoutePlannerActivity) myActivity).toggleMenu();
                }
                break;
            case R.id.overview_to_project_info_button:
                if(myActivity instanceof ProjectAndArtistOverviewActivity) {
                    ((ProjectAndArtistOverviewActivity) myActivity).setInfoDisplay(0);
                }
                break;
            case R.id.overview_to_artist_info_button:
                if(myActivity instanceof ProjectAndArtistOverviewActivity) {
                    ((ProjectAndArtistOverviewActivity) myActivity).setInfoDisplay(1);
                }
                break;
            case R.id.impressum_to_rights_button:
                if(myActivity instanceof ImpressumViewActivity) {
                    ((ImpressumViewActivity) myActivity).setInfoDisplay(0);
                }
                break;
            case R.id.impressum_to_contact_button:
                if(myActivity instanceof ImpressumViewActivity) {
                    ((ImpressumViewActivity) myActivity).setInfoDisplay(1);
                }
                break;
            case R.id.button_back:
                myActivity.onBackPressed();
                break;
        }
        if(intent != null) {
            myActivity.startActivity(intent, transitionOptions);
        }
    }

    private Intent intentFromQuickAccess(Class toOpen) {
        myActivity.endQuickAccesDialog();
        return new Intent(myActivity.getApplicationContext(), toOpen);
    }
}
