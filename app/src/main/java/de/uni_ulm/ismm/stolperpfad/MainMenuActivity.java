package de.uni_ulm.ismm.stolperpfad;

import android.annotation.SuppressLint;
import android.os.Bundle;

import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

/**
 * This activity represents the home menu for the application, the user will be redirected to
 * this activity after the splash screen disappears or the app header is clicked on
 */
public class MainMenuActivity extends StolperpfadeAppActivity {

    /**
     * This is what happens when this activity is first started
     * @param saved_state the saved instance state
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        initializeGeneralControls(R.layout.activity_main_menu);

        // add the listener to the buttons on screen and make them visible
        aq.id(R.id.info_button).visible().clicked(my_click_listener);
        aq.id(R.id.menu_to_scan_button).visible().clicked(my_click_listener);
        aq.id(R.id.menu_to_route_button).visible().clicked(my_click_listener);
        aq.id(R.id.menu_to_next_stone_button).visible().clicked(my_click_listener);
    }
}
