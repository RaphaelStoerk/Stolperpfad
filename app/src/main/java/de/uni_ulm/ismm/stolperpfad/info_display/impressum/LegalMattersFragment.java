package de.uni_ulm.ismm.stolperpfad.info_display.impressum;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.uni_ulm.ismm.stolperpfad.R;

/**
 * This Fragment represents the content for the impressum screen that contains the
 * legal matters information
 */
public class LegalMattersFragment extends ImpressumContentFragment {

    public LegalMattersFragment() {
        // required empty constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_impressum_rights, container, false);
    }
}
