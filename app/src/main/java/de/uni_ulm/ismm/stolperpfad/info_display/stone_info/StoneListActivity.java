package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.os.Bundle;
import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneListViewModel;

/**
 * This Activity contains the list of all the stones sorted by name and grouped by the first letter
 * of the last names
 */
public class StoneListActivity extends StolperpfadeAppActivity {

    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        initializeGeneralControls(R.layout.activity_stone_list);
        StoneListViewModel.getInstance(this).setUpIndex(this);
    }
}
