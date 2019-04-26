package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data_util.StringCreator;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneInfoMainActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneInfoViewModel;

/**
 * The lowest Fragment of its group, placeholder fragment for one specific
 * timestep of the biography of one specific person
 */
public class StoneInfoBioContentFragment extends Fragment {

    private int position;
    private int point;
    private StoneInfoViewModel model;

    public StoneInfoBioContentFragment() {

    }

    public static StoneInfoBioContentFragment newInstance(StoneInfoViewModel model, int position, int point) {
        StoneInfoBioContentFragment frag  = new StoneInfoBioContentFragment();
        frag.position = position;
        frag.point = point;
        frag.model = model;
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int buff;
        if(savedInstanceState != null) {
            if((buff = savedInstanceState.getInt("current_person")) != -1) {
                position = buff;
            }
            if((buff = savedInstanceState.getInt("current_vita_point")) != -1) {
                point = buff;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_bio_point, container, false);
        model = StoneInfoMainActivity.getModelInstance();
        model.showVitaContent(root, position, point);
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("current_person", position);
        outState.putInt("current_vita_point", point);
        super.onSaveInstanceState(outState);
    }
}
