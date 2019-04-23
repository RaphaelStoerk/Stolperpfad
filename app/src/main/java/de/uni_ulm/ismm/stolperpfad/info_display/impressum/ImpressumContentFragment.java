package de.uni_ulm.ismm.stolperpfad.info_display.impressum;

import android.support.v4.app.Fragment;

public class ImpressumContentFragment extends Fragment {

    private static final int RIGHTS_PAGE = 0;
    private static final int CONTACT_PAGE = 1;

    public ImpressumContentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImpressumContentFragment.
     */
    public static Fragment newInstance(int position) {
        ImpressumContentFragment fragment;
        if(position == RIGHTS_PAGE) {
            fragment = new RightsFragment();
        } else if(position == CONTACT_PAGE){
            fragment = new ContactFragment();
        } else {
            return null;
        }
        return fragment;
    }
}
