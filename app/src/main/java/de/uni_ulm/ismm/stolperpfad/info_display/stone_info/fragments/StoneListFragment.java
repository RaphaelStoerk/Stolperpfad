package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneListActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneListViewModel;

public class StoneListFragment extends Fragment {

    private StoneListViewModel model;
    private char initial;

    public StoneListFragment() {

    }

    public static StoneListFragment newInstance(StoneListViewModel model, char initial){
        StoneListFragment frag = new StoneListFragment();
        frag.model = model;
        frag.initial = initial;
        return frag;
    }

    public char getInitial() {
        return initial;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_stone_list, container, false);
        model = StoneListActivity.getModelInstance();
        model.setUpList(this);
        return root;
    }
}
