package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneInfoMainActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneInfoViewModel;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.RotatedViewPager;

/**
 * The main fragment for the biography display of a specific person,
 * holds the information of which timestep in the vita of this person
 * is currently displayed
 */
public class StoneInfoBioFragment extends Fragment {

    private static final int DEFAULT_ERROR = -1;

    private int index;
    ArrayList<Button> vita_buttons;

    public StoneInfoBioFragment() {

    }

    public static StoneInfoBioFragment newInstance(int person_index) {
        StoneInfoBioFragment fragment = new StoneInfoBioFragment();
        fragment.index = person_index;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle saved_state) {
        super.onCreate(saved_state);
        int buff;
        if(saved_state != null) {
            if((buff = saved_state.getInt("current_person")) != DEFAULT_ERROR) {
                index = buff;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle svaed_state) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_stone_info_bio, container, false);
        StoneInfoViewModel model = StoneInfoViewModel.getInstance((StoneInfoMainActivity) getActivity());
        RotatedViewPager bio_pager = root.findViewById(R.id.bio_view_pager);
        bio_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // current_point = position;
                model.updateVitaButtons(StoneInfoBioFragment.this, position);
                // TODO: Update left and right buttons if persons are before or after
            }
        });
        vita_buttons = new ArrayList<>();
        model.buildPersonVita(this, getChildFragmentManager(), root, index);
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.setRetainInstance(true);
    }

    public ArrayList<Button> getVitaButtons() {
        return vita_buttons;
    }

    public void setVitaButtons(ArrayList<Button> vita_buttons) {
        this.vita_buttons = vita_buttons;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("current_person", index);
        super.onSaveInstanceState(outState);
    }
}
