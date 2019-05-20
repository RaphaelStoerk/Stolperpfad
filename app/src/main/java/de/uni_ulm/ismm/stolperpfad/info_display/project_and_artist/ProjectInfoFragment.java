package de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.uni_ulm.ismm.stolperpfad.R;

/**
 * The Fragment displaying the project information
 */
public class ProjectInfoFragment extends ProjectAndArtistInfoFragment {

    public ProjectInfoFragment() {
        // required empty constructor
    }

    @Override
    public void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle saved_state) {
        return inflater.inflate(R.layout.content_project_info, container, false);
    }
}
