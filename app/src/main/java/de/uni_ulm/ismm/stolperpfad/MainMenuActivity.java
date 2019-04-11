package de.uni_ulm.ismm.stolperpfad;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.database.list_of_historical_terms.HistoryActivity;
import de.uni_ulm.ismm.stolperpfad.database.list_of_persons.PersonsActivity;
import de.uni_ulm.ismm.stolperpfad.general.MyButtonClickListener;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.ScrollingInfoActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.impressum.ImpressumViewActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.impressum.PrivacyInfoActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist.ProjectAndArtistOverviewActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist.ProjectInfoActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.NextStoneActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.RoutePlannerActivity;
import de.uni_ulm.ismm.stolperpfad.scanner.ScannerActivity;

/**
 * This class is the current entry point to our activities, this might change with an added
 * splash screen. For now, this is the first activity a user will be able to interact with
 */
public class MainMenuActivity extends StolperpfadeAppActivity {

    /**
     * This is what happens when this activity is first started
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setTheme(R.style.AppTheme);
        // Initialize this view and display the right screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Initialize important helper-Objects
        aq = new AQuery(this);

        // Initialize the Listener for the clickable items
        myClickListener = new MyButtonClickListener<>();
        myClickListener.setMyActivity(this);

        // add the listener to the buttons on screen and make them visible
        aq.id(R.id.info_button).visible().clicked(myClickListener);
        aq.id(R.id.menu_to_scan_button).visible().clicked(myClickListener);
        aq.id(R.id.menu_to_route_button).visible().clicked(myClickListener);
        aq.id(R.id.menu_to_next_stone_button).visible().clicked(myClickListener);
        aq.id(R.id.project_and_artist_overview_button).visible().clicked(myClickListener);
        aq.id(R.id.impressum_button).visible().clicked(myClickListener);
        aq.id(R.id.privacy_button).visible().clicked(myClickListener);
        aq.id(R.id.quick_access_button).visible().clicked(myClickListener);
        aq.id(R.id.history_button).visible().clicked(myClickListener);
    }
}
