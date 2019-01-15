package de.uni_ulm.ismm.stolperpfad;

/*
This is a test comment from Ulrike :)
 */


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.androidquery.AQuery;

/**
 * This class the current entry point to our activities, this might change with an added
 * splash screen. For now, this is the first activity a user will be able to interact with
 */
public class MainMenuActivity extends AppCompatActivity {

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
        // Initialize this view and display the right screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Initialize important helper-Objects
        aq = new AQuery(this);
        myListener = new MyClickListener();

        // add the listener to the buttons on screen and make them visible
        aq.id(R.id.exit_button).visible().clicked(myListener);
        aq.id(R.id.info_button).visible().clicked(myListener);
    }

    /**
     * This is an internal class that handles the Clicks of buttons on the main menu
     */
    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent;

            // a simple switch case statement that checks which button was pressed
            switch (v.getId()) {
                case R.id.exit_button:
                    finish();
                    System.exit(0);
                    break;
                case R.id.info_button:
                    intent = new Intent(MainMenuActivity.this, ScrollingInfoActivity.class);
                    startActivity(intent);
            }
        }
    }
}
