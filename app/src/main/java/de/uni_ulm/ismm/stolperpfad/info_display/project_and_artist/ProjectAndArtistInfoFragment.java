package de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist;

import android.support.v4.app.Fragment;

public class ProjectAndArtistInfoFragment extends Fragment {

    private static final int PROJECT_PAGE = 0;
    private static final int ARTIST_PAGE = 1;

    public ProjectAndArtistInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BiographyTestFragment.
     */
    public static Fragment newInstance(int position) {
        ProjectAndArtistInfoFragment fragment;
        if(position == PROJECT_PAGE) {
            fragment = new ProjectInfoFragment();
        } else if(position == ARTIST_PAGE){
            fragment = new ArtistInfoFragment();
        } else {
            return null;
        }
        return fragment;
    }
}
