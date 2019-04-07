package de.uni_ulm.ismm.stolperpfad.info_display;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;

/**
 * This is a simple example activity that displays scrollable text,
 * this class is only important to actually get the app to show the right screen,
 * the content and interface things are in the corresponding xml layout file
 */
public class ScrollingInfoActivity extends AppCompatActivity {

    /**
     * This is what happens when this activity is first started
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize this view and display the right screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_info);

        AQuery aq = new AQuery(this);

        // Lets the button do something, this time with an anonymous class (kinda)
        aq.id(R.id.floatingActionButton).visible().clicked(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // makes the current screen go away
                finish();
            }
        });

        String test = getIntent().getAction();

        if(test != null && test.length() > 0) {
            aq.id(R.id.info_title).text(test);

            // TODO: add Info
            aq.id(R.id.info_text).text("--- Hier bitte Info einfuegen ---");
        }
    }
}
