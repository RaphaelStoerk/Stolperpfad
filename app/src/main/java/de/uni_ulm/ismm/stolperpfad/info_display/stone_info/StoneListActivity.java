package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.os.Bundle;
import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneListViewModel;

public class StoneListActivity extends StolperpfadeAppActivity {

    private static volatile StoneListViewModel model;

    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        initializeGeneralControls(R.layout.activity_stone_list);
        model = StoneListViewModel.getInstance(this);
        model.setUpIndex(this);
    }

    public static StoneListViewModel getModelInstance() {
        if(model == null) {
            return null;
        }
        return model;
    }
}
