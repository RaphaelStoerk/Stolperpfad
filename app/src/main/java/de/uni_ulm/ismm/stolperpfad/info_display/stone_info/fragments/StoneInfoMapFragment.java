package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.PersonInfo;

/**
 * Holds only a simple representation of a map locked to
 * the position of the specific persons stone
 */
public class StoneInfoMapFragment extends StoneInfoContentFragment {

    private PersonInfo person;

    public StoneInfoMapFragment() {

    }

    public static StoneInfoMapFragment newInstance(PersonInfo person) {
        StoneInfoMapFragment fragment = new StoneInfoMapFragment();
        fragment.setPerson(person);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setPerson(PersonInfo person) {
        this.person = person;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_stone_info_map, container, false);
        AQuery aq = new AQuery(root);
        return root;
    }
}
