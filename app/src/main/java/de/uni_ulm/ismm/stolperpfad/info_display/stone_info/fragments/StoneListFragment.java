package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneListActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneListViewModel;

/**
 * This Fragment represents a page in the list of all stones corresponding to one specified
 * initial for the last names that will be displayed
 */
public class StoneListFragment extends Fragment {

    private char initial;
    private StoneListViewModel model;

    public StoneListFragment() {
        // required empty constructor
    }

    /**
     * Creates a new StoneListFragment instance with the corresponding initial
     *
     * @param model the ViewModel of the displayed list
     * @param initial the initial of the last names to display
     * @return a new StoneListFragment
     */
    public static StoneListFragment newInstance(StoneListViewModel model, char initial){
        StoneListFragment frag = new StoneListFragment();
        frag.model = model;
        frag.initial = initial;
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle saved_state) {
        super.onCreate(saved_state);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saved_state) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_stone_list, container, false);
        setupListDisplay(root);
        return root;
    }

    /**
     * Grabs the ViewModel for the StoneList from the calling activity and starts to
     * initialize the list
     */
    private void setupListDisplay(ViewGroup root) {
        model = StoneListViewModel.getInstance((StoneListActivity) getActivity());
        model.setUpList(root, initial);
    }

    public char getInitial() {
        return initial;
    }
}
