package de.uni_ulm.ismm.stolperpfad;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.database.DbActivity;
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
public class MainMenuActivity extends AppCompatActivity {

    // TODO: add splash screen

    // The AQuery framework lets us write short understandable code, see further down
    private AQuery aq;

    // Buttons and similar Components need Listeners to do stuff when they are pressed
    private MyClickListener myListener;

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
        myListener = new MyClickListener();

        // add the listener to the buttons on screen and make them visible
        aq.id(R.id.info_button).visible().clicked(myListener);
        aq.id(R.id.db_button).visible().clicked(myListener);
        aq.id(R.id.menu_to_scan_button).visible().clicked(myListener);
        aq.id(R.id.menu_to_route_button).visible().clicked(myListener);
        aq.id(R.id.menu_to_next_stone_button).visible().clicked(myListener);
        aq.id(R.id.project_and_artist_overview_button).visible().clicked(myListener);
        aq.id(R.id.impressum_button).visible().clicked(myListener);
        aq.id(R.id.privacy_button).visible().clicked(myListener);
    }

    /**
     * This is an internal class that handles the Clicks of buttons on the main menu
     */
    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent;
            Log.i("MY_ACTION", "Something was pressed: " + v.getId());
            // a simple switch case statement that checks which button was pressed
            switch (v.getId()) {
                case R.id.info_button:
                    intent = new Intent(MainMenuActivity.this, ScrollingInfoActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainMenuActivity.this).toBundle());
                    break;
                case R.id.db_button: //TODO: update to "geschichtliches"
                    intent = new Intent(MainMenuActivity.this, DbActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainMenuActivity.this).toBundle());
                    break;
                case R.id.menu_to_scan_button:
                    intent = new Intent(MainMenuActivity.this, ScannerActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainMenuActivity.this).toBundle());
                    break;
                case R.id.menu_to_route_button:
                    intent = new Intent(MainMenuActivity.this, RoutePlannerActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainMenuActivity.this).toBundle());
                    break;
                case R.id.menu_to_next_stone_button:
                    intent = new Intent(MainMenuActivity.this, NextStoneActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainMenuActivity.this).toBundle());
                    break;
                case R.id.project_and_artist_overview_button:
                    intent = new Intent(MainMenuActivity.this, ProjectAndArtistOverviewActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainMenuActivity.this).toBundle());
                    break;
                case R.id.impressum_button:
                    Log.i("MY_ACTION", "Go to Impressum");
                    intent = new Intent(MainMenuActivity.this, ImpressumViewActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainMenuActivity.this).toBundle());
                    break;
                case R.id.privacy_button:
                    intent = new Intent(MainMenuActivity.this, PrivacyInfoActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainMenuActivity.this).toBundle());
                    break;
            }
        }
    }
}
