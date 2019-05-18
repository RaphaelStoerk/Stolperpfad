package de.uni_ulm.ismm.stolperpfad.info_display.history.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.history.HistoricalListActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.history.model.HistoricalListViewModel;

/**
 * This Fragment represents a page in the list of all terms corresponding to one specified
 * initial for the term names that will be displayed
 */
public class HistoricalListFragment extends Fragment {

    private char initial;

    public HistoricalListFragment() { /*---*/ }

    /**
     * Creates a new HistoricalListFragment instance with the corresponding initial
     *
     * @param initial the initial of the term names to display
     * @return a new HistoricalListFragment
     */
    public static HistoricalListFragment newInstance(char initial) {
        HistoricalListFragment frag = new HistoricalListFragment();
        frag.initial = initial;
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_histo_list, container, false);
        HistoricalListViewModel model = HistoricalListViewModel.getInstance((HistoricalListActivity) getActivity());
        model.setUpList(root, initial);
        return root;
    }
}
