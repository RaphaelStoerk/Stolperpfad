package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.MyButtonClickListener;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

/**
 * The Fragment that is the highest up fragment, holds the whole information
 * for a specific person and keeps track of it's children
 */
public class StoneInfoPersonFragment extends Fragment {

    private PersonInfo curr_person;
    private AQuery aq;
    private MyButtonClickListener<? extends StolperpfadeAppActivity> myClickListener;
    private ViewPager infoPager;
    private int shown;
    private int DISPLAY_BIO = 0;
    private int DISPLAY_MAP = 1;


    public StoneInfoPersonFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(curr_person == null) {
            return null;
        }
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_stone_info, container, false);
        aq = new AQuery(root);
        aq.id(R.id.title_stone_info).text(curr_person.getVorname() + " " + curr_person.getNachname());
        String geb_nam = curr_person.getGeburtsname();
        aq.id(R.id.sub_title_stone_info).text(geb_nam.length() == 0 ? "" : ("geb. " + geb_nam));

        aq.id(R.id.stone_info_to_bio_button).clicked(view -> setInfoDisplay(0));
        aq.id(R.id.stone_info_to_map_button).clicked(view -> setInfoDisplay(1));

        infoPager = (ViewPager) aq.id(R.id.stone_info_pager).getView();
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        infoPager.setAdapter(pagerAdapter);
        infoPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                shown = position;
                updateButtons();
                // TODO: Update left and right buttons if persons are before or after
            }
        });
        infoPager.setOffscreenPageLimit(0);
        shown = 0;
        return root;
    }

    public void update(PersonInfo person) {
        this.curr_person = person;
        if(!this.isDetached()) {
            getChildFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    public static StoneInfoPersonFragment newInstance(PersonInfo curr_person, MyButtonClickListener<? extends StolperpfadeAppActivity> myClickListener) {
        StoneInfoPersonFragment ret = new StoneInfoPersonFragment();
        ret.curr_person = curr_person;
        ret.myClickListener = myClickListener;
        return ret;
    }

    public void setInfoDisplay(int i) {
        shown = i;
        if(shown == DISPLAY_BIO) {
            infoPager.setCurrentItem(DISPLAY_BIO);
        } else if(shown == DISPLAY_MAP) {
            infoPager.setCurrentItem(DISPLAY_MAP);
        }
        updateButtons();
    }

    public void updateButtons() {
        Button bio_button, map_button;
        bio_button = aq.id(R.id.stone_info_to_bio_button).getButton();
        map_button = aq.id(R.id.stone_info_to_map_button).getButton();
        if(shown == DISPLAY_BIO) {
            setButtonActive(bio_button, true);
            setButtonActive(map_button, false);
        } else if(shown == DISPLAY_MAP) {
            setButtonActive(bio_button, false);
            setButtonActive(map_button, true);
        }
    }

    @SuppressLint("ResourceType")
    public void setButtonActive(Button button, boolean active) {
        int[] attr = {R.attr.colorAppAccent, R.attr.colorAppTextButtonAccent, R.attr.colorAppPrimaryContrast, R.attr.colorAppTextButtonContrast};
        TypedArray ta = this.getActivity().obtainStyledAttributes(attr);

        int bg_color_active = ta.getResourceId(0, android.R.color.black);
        int text_color_active = ta.getResourceId(1, android.R.color.black);
        int bg_color_inactive = ta.getResourceId(2, android.R.color.black);
        int text_color_inactive = ta.getResourceId(3, android.R.color.black);
        if(active) {
            button.setBackgroundColor(getResources().getColor(bg_color_active, getActivity().getTheme()));
            button.setTextColor(getResources().getColor(text_color_active, getActivity().getTheme()));
        } else {
            button.setBackgroundColor(getResources().getColor(bg_color_inactive, getActivity().getTheme()));
            button.setTextColor(getResources().getColor(text_color_inactive, getActivity().getTheme()));
        }
        ta.recycle();
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
                f = StoneInfoBioFragment.newInstance(curr_person);
            } else {
                f = StoneInfoMapFragment.newInstance(curr_person);
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
