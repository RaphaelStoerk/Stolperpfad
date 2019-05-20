package de.uni_ulm.ismm.stolperpfad.map_activities.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import java.util.Objects;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;

/**
 * This class represents an extended Dialog that can be swiped
 */
public class RouteOptionsDialog extends DialogFragment {

    private static final float NO_ALPHA = 0;
    private static final float SOME_ALPHA = 0.7f;
    private ImageButton[] shortcut_index;
    private ImageButton left, right;

    public RouteOptionsDialog() {
        // required empty constructor
    }

    public static RouteOptionsDialog newInstance() {
        return new RouteOptionsDialog();
    }

    @Override
    public void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
    }

    @Nullable
    @Override
    @SuppressLint("InflateParams")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle saved_state) {
        SharedPreferences prefs = Objects.requireNonNull(getActivity()).getApplicationContext().getSharedPreferences("de.uni_ulm.ismm.stolperpfad", Context.MODE_PRIVATE);
        prefs.edit().putString("de.uni_ulm.ismm.stolperpfad.route_time", "#").apply();
        prefs.edit().putString("de.uni_ulm.ismm.stolperpfad.route_start", "#").apply();
        prefs.edit().putString("de.uni_ulm.ismm.stolperpfad.route_end", "#").apply();
        if(StolperpfadeApplication.getInstance().isDarkMode()) {
            setStyle(STYLE_NO_TITLE, R.style.DialogTheme_Dark);
        } else {
            setStyle(STYLE_NO_TITLE, R.style.DialogTheme_Light);
        }
        View route_options_dialog_view = inflater.inflate(R.layout.dialog_route_options, null);
        getDialog().setTitle("");
        ViewPager options_pager = route_options_dialog_view.findViewById(R.id.route_option_pager);
        options_pager.setMinimumHeight(options_pager.getWidth());
        PagerAdapter dialog_pager_adapter = new RouteOptionsPagerAdapter(getChildFragmentManager());
        options_pager.setAdapter(dialog_pager_adapter);
        left = route_options_dialog_view.findViewById(R.id.dialog_left);
        left.setOnClickListener(view -> options_pager.setCurrentItem(options_pager.getCurrentItem() - 1));
        right = route_options_dialog_view.findViewById(R.id.dialog_right);
        right.setOnClickListener(view -> options_pager.setCurrentItem(options_pager.getCurrentItem() + 1));
        options_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Objects.requireNonNull(options_pager.getAdapter()).notifyDataSetChanged();
                updateIndex(position);
            }
        });
        shortcut_index = new ImageButton[] {
                route_options_dialog_view.findViewById(R.id.dialog_one),
                route_options_dialog_view.findViewById(R.id.dialog_two),
                route_options_dialog_view.findViewById(R.id.dialog_three),
                route_options_dialog_view.findViewById(R.id.dialog_four)};
        shortcut_index[0].setOnClickListener(view -> options_pager.setCurrentItem(0));
        shortcut_index[1].setOnClickListener(view -> options_pager.setCurrentItem(1));
        shortcut_index[2].setOnClickListener(view -> options_pager.setCurrentItem(2));
        shortcut_index[3].setOnClickListener(view -> options_pager.setCurrentItem(3));
        options_pager.setOffscreenPageLimit(1);
        updateIndex(0);
        return route_options_dialog_view;
    }

    /**
     * Updates the navigation interface depending on what page the dialog is at
     *
     * @param page the dialog page that is currently displayed
     */
    private void updateIndex(int page) {
        for(ImageButton ib : shortcut_index) {
            if(StolperpfadeApplication.getInstance().isDarkMode()) {
                ib.setImageResource(R.drawable.ic_bio_off_dark);
            } else {
                ib.setImageResource(R.drawable.ic_bio_point_off);
            }
        }
        shortcut_index[page].setImageResource(R.drawable.ic_bio_point_on);
        if(page <= 0) {
            left.setAlpha(NO_ALPHA);
        } else {
            left.setAlpha(SOME_ALPHA);
        }
        if(page >= shortcut_index.length - 1) {
            right.setAlpha(NO_ALPHA);
        } else {
            right.setAlpha(SOME_ALPHA);
        }
    }

    /**
     * The pager adapter for the route optionas dialog
     */
    private class RouteOptionsPagerAdapter extends FragmentStatePagerAdapter {

        private Fragment myCurrentFragment;

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        Fragment getMyCurrentFragment() {
            return myCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int pos, Object prim_fragment) {
            if(getMyCurrentFragment() != prim_fragment) {
                myCurrentFragment = (Fragment) prim_fragment;
            }
            super.setPrimaryItem(container, pos, prim_fragment);
        }

        RouteOptionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return RouteOptionsFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
