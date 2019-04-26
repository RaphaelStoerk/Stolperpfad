package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.support.v4.app.Fragment;

/**
 * Only a placeholder class that gives form to the two specific
 * classes for the Bio-Content and the Map-Content for a specific
 * person, quasi a middler fragment for the whole activity
 */
public class StoneInfoContentFragment extends Fragment {

    public StoneInfoContentFragment() {

    }

    public static Fragment newInstance(StoneInfoPersonFragment parent, int shown, PersonInfo person) {
        if(shown == 0) {
            return StoneInfoMapFragment.newInstance(person);
        } else if(shown == 1){
            return StoneInfoBioFragment.newInstance(person);
        } else {
            return null;
        }
    }
}
