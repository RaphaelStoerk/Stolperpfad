package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.BioPoint;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.PersonInfo;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.VerticalViewPager;

/**
 * The main fragment for the biography display of a specific person,
 * holds the information of which timestep in the bio of this person
 * is currently displayed
 */
public class StoneInfoBioFragment extends StoneInfoContentFragment {

    private PersonInfo person;

    private VerticalViewPager bio_pager;

    private int current_point;

    ArrayList<Button> bio_buttons;

    public StoneInfoBioFragment() {

    }

    public static StoneInfoBioFragment newInstance(PersonInfo person) {
        StoneInfoBioFragment fragment = new StoneInfoBioFragment();
        fragment.person = person;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            person = (PersonInfo) savedInstanceState.getSerializable("person");

        } catch(NullPointerException e) {

        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_stone_info_bio, container, false);

        ConstraintLayout bio_layout = (ConstraintLayout) root.findViewById(R.id.bio_layout);

        bio_pager = root.findViewById(R.id.bio_view_pager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        bio_pager.setAdapter(pagerAdapter);
        bio_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                current_point = position;
                updateButtons();
                // TODO: Update left and right buttons if persons are before or after
            }
        });
        current_point = 0;
        bio_pager.setCurrentItem(current_point);
        if(person == null) {
        } else {
            createBioButtons(inflater,inflater.getContext(), bio_layout);
        }
        updateButtons();
        return root;
    }

    private void updateButtons() {
        for(Button b : bio_buttons) {
            b.setBackgroundResource(R.drawable.ic_point);
        }
        bio_buttons.get(current_point).setBackgroundResource(R.drawable.ic_point_on);
    }

    private void createBioButtons(LayoutInflater inflater, Context ctx, ConstraintLayout bio_layout) {
        ArrayList<BioPoint> bio = person.getBio();
        int points = bio.size();
        if(points < 2) {
            return;
        }
        bio_buttons = new ArrayList<>();
        Button birth_button = makeButton(inflater, ctx, 0);
        Button death_button = makeButton(inflater,ctx, points - 1);
        bio_buttons.add(birth_button);
        Button buff;
        for(int i = 0; i < bio.size() - 2; i++) {
            bio_buttons.add(buff = makeButton(inflater,ctx, i + 1));
            bio_layout.addView(buff);
        }
        bio_buttons.add(death_button);
        bio_layout.addView(birth_button);
        bio_layout.addView(death_button);

        ConstraintSet cs = new ConstraintSet();
        cs.clone(bio_layout);
        cs.connect(birth_button.getId(),ConstraintSet.TOP, bio_layout.getId(),ConstraintSet.TOP,32 );
        cs.connect(birth_button.getId(),ConstraintSet.BOTTOM, bio_buttons.get(1).getId(),ConstraintSet.TOP );
        cs.connect(birth_button.getId(),ConstraintSet.START,bio_layout.getId(),ConstraintSet.START, 16 );
        cs.connect(birth_button.getId(),ConstraintSet.END, bio_pager.getId(),ConstraintSet.START, 16 );
        for(int i = 1; i <= points - 2; i++) {
            cs.connect(bio_buttons.get(i).getId(),ConstraintSet.TOP,bio_buttons.get(i-1).getId(),ConstraintSet.BOTTOM );
            cs.connect(bio_buttons.get(i).getId(),ConstraintSet.BOTTOM,bio_buttons.get(i+1).getId(),ConstraintSet.TOP );
            cs.connect(bio_buttons.get(i).getId(),ConstraintSet.START,bio_layout.getId(),ConstraintSet.START, 16 );
            cs.connect(bio_buttons.get(i).getId(),ConstraintSet.END, bio_pager.getId(),ConstraintSet.START, 16 );
        }
        cs.connect(death_button.getId(),ConstraintSet.TOP, bio_buttons.get(points-2).getId(),ConstraintSet.BOTTOM );
        cs.connect(death_button.getId(),ConstraintSet.BOTTOM, bio_layout.getId(),ConstraintSet.BOTTOM,32);
        cs.connect(death_button.getId(),ConstraintSet.START,bio_layout.getId(),ConstraintSet.START, 16 );
        cs.connect(death_button.getId(),ConstraintSet.END, bio_pager.getId(),ConstraintSet.START, 16 );
        cs.applyTo(bio_layout);
    }

    private Button makeButton(LayoutInflater inflater, Context ctx, int bio_index) {
        Button but = (Button) inflater.inflate(R.layout.bio_button_layout, null);
        but.setOnClickListener(view -> showInfo(bio_index));
        but.setBackgroundResource(R.drawable.ic_point);
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        float dp = 24f;
        float fpixels = dm.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(pixels,pixels);
        but.setLayoutParams(params);
        but.setId(bio_index+1000);
        return but;
    }

    private void showInfo(int index) {
        bio_pager.setCurrentItem(index);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //    outState.putSerializable("person", person);

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
            current_point = position;
            return StoneInfoBioContentFragment.newInstance(person.getBio().get(position));
        }

        @Override
        public int getCount() {
            return person.getBio().size();
        }
    }
}
