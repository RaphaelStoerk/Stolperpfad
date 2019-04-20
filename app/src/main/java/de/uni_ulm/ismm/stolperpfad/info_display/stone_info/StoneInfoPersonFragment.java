package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.general.MyButtonClickListener;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

/**
 * The Fragment that is the highest up fragment, holds the whole information
 * for a specific person and keeps track of it's children
 */
public class StoneInfoPersonFragment extends Fragment {

    private Person curr_person;
    private AQuery aq;
    private MyButtonClickListener<? extends StolperpfadeAppActivity> myClickListener;

    public StoneInfoPersonFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(this.getActivity());
        aq.id(R.id.sub_title_stone_info).text("This is tile # " + curr_person.getPersId());

        // add the listener to the buttons on screen and make them visible
        aq.id(R.id.left_button).visible().clicked(myClickListener);
        aq.id(R.id.right_button).visible().clicked(myClickListener);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stone_info, container, false);
    }

    public static Fragment newInstance(Person curr_person, MyButtonClickListener<? extends StolperpfadeAppActivity> myClickListener, AQuery aq) {
        Fragment ret = new StoneInfoPersonFragment();
        ((StoneInfoPersonFragment)ret).curr_person = curr_person;
        ((StoneInfoPersonFragment)ret).aq = aq;
        ((StoneInfoPersonFragment)ret).myClickListener = myClickListener;
        return ret;
    }
}
