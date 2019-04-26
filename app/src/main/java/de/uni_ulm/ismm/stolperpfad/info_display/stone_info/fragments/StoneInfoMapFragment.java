package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneInfoMainActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneInfoViewModel;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.NextStoneActivity;

/**
 * Holds only a simple representation of a map locked to
 * the position of the specific persons stone
 */
public class StoneInfoMapFragment extends StoneInfoContentFragment {

    private StoneInfoViewModel model;
    private int index;

    public StoneInfoMapFragment() {

    }

    public static StoneInfoMapFragment newInstance(StoneInfoViewModel model, int person_page) {
        StoneInfoMapFragment fragment = new StoneInfoMapFragment();
        fragment.model = model;
        fragment.index = person_page;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int buff;
        if(savedInstanceState != null) {
            if((buff = savedInstanceState.getInt("current_person")) != -1) {
                index = buff;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_stone_info_map, container, false);
        model = StoneInfoMainActivity.getModelInstance();
        model.showMapContent(root, index);
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("current_person", index);
        super.onSaveInstanceState(outState);
    }
}
