package de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.MyButtonClickListener;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

public class ProjectAndArtistOverviewActivity extends StolperpfadeAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_and_artist_overview);

        // Initialize important helper-Objects
        aq = new AQuery(this);

        // Initialize the Listener for the clickable items
        myClickListener = new MyButtonClickListener<>();
        myClickListener.setMyActivity(this);

        // add the listener to the items
        aq.id(R.id.quick_access_button).visible().clicked(myClickListener);

        // add the listener to the buttons on screen and make them visible
        aq.id(R.id.overview_to_project_info_button).visible().clicked(myClickListener);
        aq.id(R.id.overview_to_artist_info_button).visible().clicked(myClickListener);
    }
}
