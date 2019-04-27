package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data_util.DataFromJSON;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.StoneInfoPersonFragment;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.NoSwipeViewPager;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneInfoViewModel;

/**
 * The main activity that stores an alphabetical List of all the persons
 * and keeps track which persons stone is displayed
 */

public class StoneInfoMainActivity extends StolperpfadeAppActivity {

    private StoneInfoPersonFragment fragment;
    private NoSwipeViewPager infoPager;
    private int current_person_index;
    private static volatile StoneInfoViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeGeneralControls(R.layout.activity_stone_info_main);
        aq.id(R.id.left_button).clicked(view -> left_click());
        aq.id(R.id.right_button).clicked(view -> right_click());

        // Instantiate a ViewPager and a PagerAdapter.
        infoPager = (NoSwipeViewPager) findViewById(R.id.stone_pager);
        infoPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setPerson(position);
            }
        });
        model = StoneInfoViewModel.getInstance(this);
        Log.i("MY_DATA_TAG", "" + getIntent().getAction());
        model.setUpPersonPage(infoPager, getIntent().getAction());
    }

    public void setPerson(int position) {
        current_person_index = position;
    }


    public void left_click() {
        if (current_person_index == 0) {
            return;
        }
        current_person_index--;
        infoPager.setCurrentItem(current_person_index);
    }

    public void right_click() {
        if(infoPager == null || infoPager.getAdapter() == null) {
            return;
        }
        if (current_person_index == infoPager.getAdapter().getCount() - 1) {
            return;
        }
        current_person_index++;
        infoPager.setCurrentItem(current_person_index);
    }

    public static StoneInfoViewModel getModelInstance() {
        if(model == null) {
            return null;
        }
        return model;
    }
}
