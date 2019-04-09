package de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist;

import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;

public class ProjectAndArtistOverviewActivity extends AppCompatActivity {

    AQuery aq;
    MyClickListener myListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_and_artist_overview);
        aq = new AQuery(this);
        myListener = new MyClickListener();

        // add the listener to the buttons on screen and make them visible
        aq.id(R.id.overview_to_project_info_button).visible().clicked(myListener);
        aq.id(R.id.overview_to_artist_info_button).visible().clicked(myListener);
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
                case R.id.overview_to_project_info_button:
                    intent = new Intent(ProjectAndArtistOverviewActivity.this, ProjectInfoActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ProjectAndArtistOverviewActivity.this).toBundle());
                    break;
                case R.id.overview_to_artist_info_button:
                    intent = new Intent(ProjectAndArtistOverviewActivity.this, ArtistInfoActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ProjectAndArtistOverviewActivity.this).toBundle());
                    break;
            }
        }
    }
}
