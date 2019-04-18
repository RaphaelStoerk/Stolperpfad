package de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.uni_ulm.ismm.stolperpfad.R;

public class ArtistInfoFragment extends ProjectAndArtistInfoFragment {

    public ArtistInfoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_artist_info, container, false);
    }
}
