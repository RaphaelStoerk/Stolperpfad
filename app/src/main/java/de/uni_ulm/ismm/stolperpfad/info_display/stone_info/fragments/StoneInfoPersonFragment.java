package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneInfoMainActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneInfoViewModel;

/**
 * The Fragment that is the highest up fragment, holds the whole information
 * for a specific person and keeps track of it's children
 */
public class StoneInfoPersonFragment extends Fragment {

    private static final int PAGE_COUNT = 2;
    private int current_person; // the current person as the position in the list of all persons
    // TODO: maybe work with person id instead

    public StoneInfoPersonFragment() {
        // required empty constructor
    }

    public static StoneInfoPersonFragment newInstance(int pos) {
        StoneInfoPersonFragment ret = new StoneInfoPersonFragment();
        ret.current_person = pos;
        return ret;
    }

    @Override
    public void onCreate(Bundle saved_state) { super.onCreate(saved_state);
        if(saved_state != null) {
            int buff;
            if((buff = saved_state.getInt("current_person")) != -1) {
                current_person = buff;
            }
        }
    }

    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, ViewGroup container, Bundle saved_state) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_stone_info, container, false);
        StoneInfoViewModel model = StoneInfoViewModel.getInstance((StoneInfoMainActivity) getActivity());
        AQuery aq = new AQuery(root);
        ViewPager bio_and_map_pager = (ViewPager) aq.id(R.id.bio_and_map_pager).getView();
        PagerAdapter info_pager_adapter = new PersonInfoContentPagerAdapter(getChildFragmentManager());
        bio_and_map_pager.setAdapter(info_pager_adapter);
        bio_and_map_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                model.updateContentButtons(root, position);
            }
        });
        model.showBasicPersonInfo(root, current_person);
        return root;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out_state) {
        out_state.putInt("current_person", current_person);
        super.onSaveInstanceState(out_state);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class PersonInfoContentPagerAdapter extends FragmentStatePagerAdapter {
        PersonInfoContentPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            // position 0 = bio, 1 = map
            if(position == 0) {
                f = StoneInfoBioFragment.newInstance(current_person);
            } else {
                f = StoneInfoMapFragment.newInstance(current_person);
            }
            f.setRetainInstance(true);
            return f;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }
}
