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
 * The lowest Fragment of its group, placeholder fragment for one specific
 * timestep of the biography of one specific person
 */
public class StoneInfoBioContentFragment extends Fragment {

    private static final int DEFAULT_ERROR = -1;
    private int person_index_in_list;
    private int vita_point_index;

    public StoneInfoBioContentFragment() {
        // required empty constructor
    }

    public static StoneInfoBioContentFragment newInstance(int person_index_in_list, int vita_point_index) {
        StoneInfoBioContentFragment frag  = new StoneInfoBioContentFragment();
        frag.person_index_in_list = person_index_in_list;
        frag.vita_point_index = vita_point_index;
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle saved_state) {
        super.onCreate(saved_state);
        int buff;
        if(saved_state != null) {
            if((buff = saved_state.getInt("current_person")) != DEFAULT_ERROR) {
                person_index_in_list = buff;
            }
            if((buff = saved_state.getInt("current_vita_point")) != DEFAULT_ERROR) {
                vita_point_index = buff;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saved_state) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_bio_point, container, false);
        StoneInfoViewModel model = StoneInfoViewModel.getInstance((StoneInfoMainActivity) getActivity());
        model.showVitaContent(root, person_index_in_list, vita_point_index);
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out_state) {
        out_state.putInt("current_person", person_index_in_list);
        out_state.putInt("current_vita_point", vita_point_index);
        super.onSaveInstanceState(out_state);
    }
}
