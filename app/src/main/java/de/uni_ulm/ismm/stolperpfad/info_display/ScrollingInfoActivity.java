package de.uni_ulm.ismm.stolperpfad.info_display;

import android.os.Bundle;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

/**
 * This is a simple example activity that displays scrollable text,
 * this class is only important to actually get the app to show the right screen,
 * the content and interface things are in the corresponding xml layout file
 */
public class ScrollingInfoActivity extends StolperpfadeAppActivity {

    /**
     * This is what happens when this activity is first started
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize this view and display the right screen
        super.onCreate(savedInstanceState);

        initializeGeneralControls(R.layout.activity_scrolling_info);

        // Lets the button do something, this time with an anonymous class (kinda)
        aq.id(R.id.floatingActionButton).visible().clicked(v -> {
            // makes the current screen go away
            finish();
        });

        String test = getIntent().getAction();

        if(test != null && test.length() > 0) {
            aq.id(R.id.info_title).text(test);

            // TODO: add Info
            aq.id(R.id.info_text).text("--- Hier bitte Info einfuegen ---");
        }

        aq.id(R.id.info_test_button).clicked(myClickListener).visible();
    }
}
