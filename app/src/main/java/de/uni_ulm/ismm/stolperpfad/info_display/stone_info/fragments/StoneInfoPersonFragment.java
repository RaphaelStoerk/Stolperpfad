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
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneInfoViewModel;

/**
 * The Fragment that is the highest up fragment, holds the whole information
 * for a specific person and keeps track of it's children
 */
public class StoneInfoPersonFragment extends Fragment {

    private AQuery aq;
    private ViewPager infoPager;
    private StoneInfoViewModel model;
    private int index;

    public StoneInfoPersonFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState);
        int buff;
        if(savedInstanceState != null) {
            if((buff = savedInstanceState.getInt("current_person")) != -1) {
                index = buff;
            }
        }
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_stone_info, container, false);
        aq = new AQuery(root);
        infoPager = (ViewPager) aq.id(R.id.stone_info_pager).getView();
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        infoPager.setAdapter(pagerAdapter);
        infoPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                model.updatePersonInfoContent(root, position);
            }
        });
        model.showBasicPersonInfo(root, infoPager, index);
        return root;
    }

    public static StoneInfoPersonFragment newInstance(StoneInfoViewModel model, int pos) {
        StoneInfoPersonFragment ret = new StoneInfoPersonFragment();
        ret.model = model;
        ret.index = pos;
        return ret;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("current_person", index);
        super.onSaveInstanceState(outState);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment f;
            if(position == 0) {
                f = StoneInfoBioFragment.newInstance(model, index);
            } else {
                f = StoneInfoMapFragment.newInstance(model, index);
            }
            f.setRetainInstance(true);
            return f;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
