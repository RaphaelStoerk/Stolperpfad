package de.uni_ulm.ismm.stolperpfad.general;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.list_of_historical_terms.HistoryActivity;
import de.uni_ulm.ismm.stolperpfad.database.list_of_persons.PersonsActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.impressum.ImpressumViewActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.impressum.PrivacyInfoActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist.ArtistInfoActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist.ProjectAndArtistOverviewActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist.ProjectInfoActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.NextStoneActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.RoutePlannerActivity;
import de.uni_ulm.ismm.stolperpfad.scanner.ScannerActivity;

public class MyButtonClickListener<T extends StolperpfadeAppActivity> implements View.OnClickListener {
    
    private T myActivity;
    
    public void setMyActivity(T myActivity) {
        this.myActivity = myActivity;
    }
    
    @Override
    public void onClick(View v) {
        Intent intent;
        Bundle transitionOptions = ActivityOptions.makeSceneTransitionAnimation(myActivity).toBundle();
        // a simple switch case statement that checks which button was pressed
        switch (v.getId()) {
            case R.id.info_button:
                intent = new Intent(myActivity, PersonsActivity.class);
                myActivity.startActivity(intent, transitionOptions);
                break;
            case R.id.menu_to_scan_button:
                intent = new Intent(myActivity, ScannerActivity.class);
                myActivity.startActivity(intent, transitionOptions);
                break;
            case R.id.menu_to_route_button:
                intent = new Intent(myActivity, RoutePlannerActivity.class);
                myActivity.startActivity(intent, transitionOptions);
                break;
            case R.id.menu_to_next_stone_button:
                intent = new Intent(myActivity, NextStoneActivity.class);
                myActivity.startActivity(intent, transitionOptions);
                break;
            case R.id.project_and_artist_overview_button:
                intent = new Intent(myActivity, ProjectAndArtistOverviewActivity.class);
                myActivity.startActivity(intent, transitionOptions);
                break;
            case R.id.impressum_button:
                intent = new Intent(myActivity, ImpressumViewActivity.class);
                myActivity.startActivity(intent, transitionOptions);
                break;
            case R.id.privacy_button:
                intent = new Intent(myActivity, PrivacyInfoActivity.class);
                myActivity.startActivity(intent, transitionOptions);
                break;
            case R.id.quick_access_button:
                myActivity.showQuickAccesMenu();
                break;
            case R.id.history_button:
                intent = new Intent(myActivity, HistoryActivity.class);
                myActivity.startActivity(intent);
                break;
            case R.id.route_option_button:
                if(myActivity instanceof RoutePlannerActivity)
                    ((RoutePlannerActivity) myActivity).routeOptionDialog();
            case R.id.overview_to_project_info_button:
                intent = new Intent(myActivity, ProjectInfoActivity.class);
                myActivity.startActivity(intent, transitionOptions);
                break;
            case R.id.overview_to_artist_info_button:
                intent = new Intent(myActivity, ArtistInfoActivity.class);
                myActivity.startActivity(intent, transitionOptions);
                break;
        }
    }
}
