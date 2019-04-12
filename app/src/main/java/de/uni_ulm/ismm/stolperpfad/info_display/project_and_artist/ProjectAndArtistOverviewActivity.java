package de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist;

import android.os.Bundle;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

public class ProjectAndArtistOverviewActivity extends StolperpfadeAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeGeneralControls(R.layout.activity_project_and_artist_overview);

        // add the listener to the buttons on screen and make them visible
        aq.id(R.id.overview_to_project_info_button).visible().clicked(myClickListener);
        aq.id(R.id.overview_to_artist_info_button).visible().clicked(myClickListener);
    }
}
