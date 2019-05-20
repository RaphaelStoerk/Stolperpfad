package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneInfoMainActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneInfoViewModel;

/**
 * Holds only a simple representation of a map locked to
 * the position of the specific persons stone
 */
public class StoneInfoMapFragment extends Fragment {

    private static final int DEFAULT_ERROR = -1;

    private int current_person_index;

    public StoneInfoMapFragment() {

    }

    public static StoneInfoMapFragment newInstance(int person_page) {
        StoneInfoMapFragment fragment = new StoneInfoMapFragment();
        fragment.current_person_index = person_page;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle saved_state) {
        super.onCreate(saved_state);
        int buff;
        if(saved_state != null) {
            if((buff = saved_state.getInt("current_person")) != DEFAULT_ERROR) {
                current_person_index = buff;
            }
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle saved_state) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_stone_info_map, container, false);
        StoneInfoViewModel model = StoneInfoViewModel.getInstance((StoneInfoMainActivity) getActivity());
        model.showMapContent(root, current_person_index);
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out_state) {
        out_state.putInt("current_person", current_person_index);
        super.onSaveInstanceState(out_state);
    }
}
