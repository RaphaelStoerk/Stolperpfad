package de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

public class ProjectAndArtistOverviewActivity extends StolperpfadeAppActivity {

    private final int DISPLAY_PROJECT = 0;
    private final int DISPLAY_ARTIST = 1;
    private static final int TOTAL_PAGES = 2;
    private int current_display;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeGeneralControls(R.layout.activity_project_and_artist_overview);

        // add the listener to the buttons on screen and make them visible
        aq.id(R.id.overview_to_project_info_button).visible().clicked(myClickListener);
        aq.id(R.id.overview_to_artist_info_button).visible().clicked(myClickListener);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.project_and_artist_info_pager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                current_display = position;
                updateButtons();
            }
        });
        current_display = DISPLAY_PROJECT;
        updateButtons();
    }

    public void setInfoDisplay(int i) {
        if(i == current_display) {
            return;
        }
        current_display = i;
        if(current_display == DISPLAY_PROJECT) {
            mPager.setCurrentItem(DISPLAY_PROJECT);
        } else if(current_display == DISPLAY_ARTIST) {
            mPager.setCurrentItem(DISPLAY_ARTIST);
        }
        updateButtons();
    }

    public void updateButtons() {
        Button project_button, artist_button;
        project_button = aq.id(R.id.overview_to_project_info_button).getButton();
        artist_button = aq.id(R.id.overview_to_artist_info_button).getButton();
        if(current_display == DISPLAY_PROJECT) {
            setButtonActive(project_button, true);
            setButtonActive(artist_button, false);
        } else if(current_display == DISPLAY_ARTIST) {
            setButtonActive(project_button, false);
            setButtonActive(artist_button, true);
        }
    }

    @SuppressLint("ResourceType")
    public void setButtonActive(Button button, boolean active) {
        int[] attr = {R.attr.colorAppAccent, R.attr.colorAppTextButtonAccent, R.attr.colorAppPrimaryContrast, R.attr.colorAppTextButtonContrast};
        TypedArray ta = this.obtainStyledAttributes(attr);

        int bg_color_active = ta.getResourceId(0, android.R.color.black);
        int text_color_active = ta.getResourceId(1, android.R.color.black);
        int bg_color_inactive = ta.getResourceId(2, android.R.color.black);
        int text_color_inactive = ta.getResourceId(3, android.R.color.black);
        if(active) {
            button.setBackgroundColor(getResources().getColor(bg_color_active, getTheme()));
            button.setTextColor(getResources().getColor(text_color_active, getTheme()));
        } else {
            button.setBackgroundColor(getResources().getColor(bg_color_inactive, getTheme()));
            button.setTextColor(getResources().getColor(text_color_inactive, getTheme()));
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
            return ProjectAndArtistInfoFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return TOTAL_PAGES;
        }
    }
}
