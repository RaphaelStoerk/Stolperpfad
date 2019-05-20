package de.uni_ulm.ismm.stolperpfad.info_display.impressum;

import android.os.Bundle;
import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

/**
 * This activity displays the privacy information disclaimer
 */
public class PrivacyInfoActivity extends StolperpfadeAppActivity {

    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        initializeGeneralControls(R.layout.activity_privacy_info);
    }
}
