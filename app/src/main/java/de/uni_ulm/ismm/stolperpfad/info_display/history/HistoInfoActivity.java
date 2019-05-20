package de.uni_ulm.ismm.stolperpfad.info_display.history;

import android.os.Bundle;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.history.model.HistoInfoViewModel;

/**
 * This activity displays the information for one specific historical term
 */
public class HistoInfoActivity extends StolperpfadeAppActivity {
    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        initializeGeneralControls(R.layout.activity_histo_info_page);
        HistoInfoViewModel.getInstance(this).setUpInfoPage();
    }
}
