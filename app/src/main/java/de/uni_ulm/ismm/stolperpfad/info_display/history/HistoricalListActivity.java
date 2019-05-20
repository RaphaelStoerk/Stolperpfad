package de.uni_ulm.ismm.stolperpfad.info_display.history;

import android.os.Bundle;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.history.model.HistoricalListViewModel;

/**
 * This Activity contains the list of all the historical terms sorted by name and grouped by the
 * first letter of the term names
 */
public class HistoricalListActivity extends StolperpfadeAppActivity {

    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        initializeGeneralControls(R.layout.activity_histo_list);
        HistoricalListViewModel model = HistoricalListViewModel.getInstance(this);
        model.setUpIndex();
    }
}
