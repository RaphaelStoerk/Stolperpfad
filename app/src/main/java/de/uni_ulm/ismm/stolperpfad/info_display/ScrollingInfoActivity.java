package de.uni_ulm.ismm.stolperpfad.info_display;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.MyButtonClickListener;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

/**
 * This is a simple example activity that displays scrollable text,
 * this class is only important to actually get the app to show the right screen,
 * the content and interface things are in the corresponding xml layout file
 */
public class ScrollingInfoActivity extends StolperpfadeAppActivity implements View.OnClickListener {

    /**
     * This is what happens when this activity is first started
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize this view and display the right screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_info);

        // Initialize important helper-Objects
        aq = new AQuery(this);

        // Initialize the Listener for the clickable items
        myClickListener = new MyButtonClickListener<>();
        myClickListener.setMyActivity(this);

        // add the listener to the items
        aq.id(R.id.quick_access_button).visible().clicked(myClickListener);

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

        aq.id(R.id.info_test_button).clicked(this).visible();
    }

    @Override
    public void onClick(View view) {
        Intent myIntent = new Intent(ScrollingInfoActivity.this, BiographyExampleActivity.class);
        startActivity(myIntent);
    }
}
