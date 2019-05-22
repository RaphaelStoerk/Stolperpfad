package de.uni_ulm.ismm.stolperpfad;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

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

    /**
     * If the user is on the main screen pressing back will ask the user to close
     * the app
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, R.style.DialogTheme_Light);
        builder.setTitle("Stolperpfade beenden?");
        builder.setPositiveButton("Ja", (dialogInterface, i) -> {
            dialogInterface.cancel();
            finish();
            System.exit(0);
        });
        builder.setNegativeButton("Nein", (dialogInterface, i) -> dialogInterface.cancel());
        AlertDialog request_dialog = builder.create();
    }
}
