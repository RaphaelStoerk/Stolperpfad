package de.uni_ulm.ismm.stolperpfad.map_activities.control;

import android.os.Bundle;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.map_activities.StolperpfadAppMapActivity;

public class NextStoneActivity extends StolperpfadAppMapActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeGeneralControls(R.layout.activity_next_stone);
        initializeMapQuestFragment(true);
    }
}
