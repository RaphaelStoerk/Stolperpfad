package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.PersonInfo;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.NextStoneActivity;

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
        TextView address = root.findViewById(R.id.address);
        address.setText(person.getStolperstein().getAdress());
        Button to_map = root.findViewById(R.id.show_map_button);
        to_map.setOnClickListener(view -> startActivity(new Intent(getActivity(), NextStoneActivity.class)));
        return root;
    }
}
