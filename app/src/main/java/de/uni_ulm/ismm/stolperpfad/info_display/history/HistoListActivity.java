package de.uni_ulm.ismm.stolperpfad.info_display.history;

import android.os.Bundle;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.history.model.HistoListViewModel;

public class HistoListActivity extends StolperpfadeAppActivity {

    private static volatile HistoListViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeGeneralControls(R.layout.activity_histo_list);
        model = HistoListViewModel.getInstance(this);
        model.setUpIndex(this);
    }

    public static HistoListViewModel getModelInstance(){
        if(model == null){
            return null;
        }
        return model;
    }
}
