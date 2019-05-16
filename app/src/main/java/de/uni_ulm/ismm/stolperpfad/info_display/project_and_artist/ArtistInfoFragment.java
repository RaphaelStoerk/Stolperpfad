package de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.uni_ulm.ismm.stolperpfad.R;

/**
 * The Fragment displaying the artist information
 */
public class ArtistInfoFragment extends ProjectAndArtistInfoFragment {

    public ArtistInfoFragment() {
        // required empty constructor
    }

    @Override
    public void onCreate(Bundle saved_state) { super.onCreate(saved_state); }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle saved_state) {
        return inflater.inflate(R.layout.content_artist_info, container, false);
    }
}
