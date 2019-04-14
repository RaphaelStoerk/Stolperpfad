package de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist;

import android.os.Bundle;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

public class ProjectInfoActivity extends StolperpfadeAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeGeneralControls(R.layout.activity_project_info);
    }
}
