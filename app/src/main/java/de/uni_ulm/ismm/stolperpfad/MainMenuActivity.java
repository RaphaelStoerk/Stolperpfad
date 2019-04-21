package de.uni_ulm.ismm.stolperpfad;

import android.os.Bundle;

import com.mapquest.mapping.MapQuest;

import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

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
        if(StolperpfadeApplication.getInstance().isFirstCall()) {
            StolperpfadeApplication.getInstance().setFirstCall(false);
            StolperpfadeApplication.getInstance().setupFileTree();
            MapQuest.start(getApplicationContext());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Initialize this view and display the right screen
        super.onCreate(savedInstanceState);

        initializeGeneralControls(R.layout.activity_main_menu);

        // add the listener to the buttons on screen and make them visible
        aq.id(R.id.info_button).visible().clicked(myClickListener);
        aq.id(R.id.menu_to_scan_button).visible().clicked(myClickListener);
        aq.id(R.id.menu_to_route_button).visible().clicked(myClickListener);
        aq.id(R.id.menu_to_next_stone_button).visible().clicked(myClickListener);
    }
}
