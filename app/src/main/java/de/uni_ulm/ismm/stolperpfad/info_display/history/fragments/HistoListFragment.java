package de.uni_ulm.ismm.stolperpfad.info_display.history.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.history.HistoListActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.history.model.HistoListViewModel;

public class HistoListFragment extends Fragment {
    private HistoListViewModel model;
    private char initial;

    public HistoListFragment() {

    }

    public static HistoListFragment newInstance(HistoListViewModel model, char initial) {
        HistoListFragment frag = new HistoListFragment();
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
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_histo_list, container, false);
        model = HistoListActivity.getModelInstance();
        model.setUpList(this);
        return root;
    }
}
