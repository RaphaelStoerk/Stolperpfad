package de.uni_ulm.ismm.stolperpfad.info_display.history;

import android.os.Bundle;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.history.model.HistoInfoViewModel;

public class HistoInfoActivity extends StolperpfadeAppActivity {

    HistoInfoViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeGeneralControls(R.layout.activity_histo_info_page);
        model = HistoInfoViewModel.getInstance(this);
        model.requestContent(this);
    }

}
