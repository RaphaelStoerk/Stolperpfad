package de.uni_ulm.ismm.stolperpfad.map_activities.view;

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
        View myDialogView = inflater.inflate(R.layout.dialog_route_options, null);
        getDialog().setTitle("");
        ViewPager options_pager = myDialogView.findViewById(R.id.route_option_pager);
        options_pager.setMinimumHeight(options_pager.getWidth());
        PagerAdapter pagerAdapter = new RouteOptionsPagerAdapter(getChildFragmentManager());
        options_pager.setAdapter(pagerAdapter);
        left = myDialogView.findViewById(R.id.dialog_left);
        left.setOnClickListener(view -> options_pager.setCurrentItem(options_pager.getCurrentItem() - 1));
        right = myDialogView.findViewById(R.id.dialog_right);
        right.setOnClickListener(view -> options_pager.setCurrentItem(options_pager.getCurrentItem() + 1));
        options_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Objects.requireNonNull(options_pager.getAdapter()).notifyDataSetChanged();
                updateIndex(position);
            }
        });
        shortcut_index = new ImageButton[] {
                myDialogView.findViewById(R.id.dialog_one),
                myDialogView.findViewById(R.id.dialog_two),
                myDialogView.findViewById(R.id.dialog_three),
                myDialogView.findViewById(R.id.dialog_four)};
        shortcut_index[0].setOnClickListener(view -> options_pager.setCurrentItem(0));
        shortcut_index[1].setOnClickListener(view -> options_pager.setCurrentItem(1));
        shortcut_index[2].setOnClickListener(view -> options_pager.setCurrentItem(2));
        shortcut_index[3].setOnClickListener(view -> options_pager.setCurrentItem(3));
        options_pager.setOffscreenPageLimit(1);
        updateIndex(0);
        return myDialogView;
    }

    private void updateIndex(int pos) {
        for(ImageButton ib : shortcut_index) {
            if(StolperpfadeApplication.getInstance().isDarkMode()) {
                ib.setImageResource(R.drawable.ic_bio_off_dark);
            } else {
                ib.setImageResource(R.drawable.ic_bio_point_off);
            }
        }
        shortcut_index[pos].setImageResource(R.drawable.ic_bio_point_on);
        if(pos <= 0) {
            left.setAlpha(0f);
        } else {
            left.setAlpha(0.7f);
        }
        if(pos >= shortcut_index.length - 1) {
            right.setAlpha(0f);
        } else {
            right.setAlpha(0.7f);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
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
        public void setPrimaryItem(ViewGroup containter, int pos, Object obj) {
            if(getMyCurrentFragment() != obj) {
                myCurrentFragment = (Fragment) obj;
            }
            super.setPrimaryItem(containter,pos,obj);
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
