package de.uni_ulm.ismm.stolperpfad.info_display.project_and_artist;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Button;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

/**
 * This Activity represents the information screen for the artist and the project itself
 */
public class ProjectAndArtistOverviewActivity extends StolperpfadeAppActivity {

    private static final int DISPLAY_PROJECT = 0;
    private static final int DISPLAY_ARTIST = 1;
    private static final int TOTAL_PAGES = 2;
    private int current_display;
    private ViewPager project_and_artist_pager;

    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        initializeGeneralControls(R.layout.activity_project_and_artist_overview);
        aq.id(R.id.overview_to_project_info_button).visible().clicked(my_click_listener);
        aq.id(R.id.overview_to_artist_info_button).visible().clicked(my_click_listener);
        project_and_artist_pager = findViewById(R.id.project_and_artist_info_pager);
        PagerAdapter pagerAdapter = new OverviewPagerAdapter(getSupportFragmentManager());
        project_and_artist_pager.setAdapter(pagerAdapter);
        project_and_artist_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                current_display = position;
                updateButtons();
            }
        });
        current_display = DISPLAY_PROJECT;
        updateButtons();
    }

    /**
     * Sets the content that should be displayed
     *
     * @param content_index the type of content
     */
    public void setInfoDisplay(int content_index) {
        if(content_index == current_display) return;
        current_display = content_index;
        project_and_artist_pager.setCurrentItem(current_display);
        updateButtons();
    }

    /**
     * Update the styling of the pager buttons corresponding to the current display
     */
    public void updateButtons() {
        setButtonActive(aq.id(R.id.overview_to_project_info_button).getButton(), current_display == DISPLAY_PROJECT);
        setButtonActive(aq.id(R.id.overview_to_artist_info_button).getButton(), current_display == DISPLAY_ARTIST);
    }

    /**
     * Set the styling of a specific button according to whether its corresponding content is displayed
     *
     * @param button the button to style
     * @param active if the button is active, meaning if its content is displayed
     */
    @SuppressLint("ResourceType")
    public void setButtonActive(Button button, boolean active) {
        int[] attr;
        if(active) {
            attr = new int[]{R.attr.colorAppAccent, R.attr.colorAppTextButtonAccent};
        } else {
            attr = new int[]{ R.attr.colorAppPrimaryContrast, R.attr.colorAppTextButtonContrast};
        }
        TypedArray ta = this.obtainStyledAttributes(attr);
        int bg_color = ta.getResourceId(0, android.R.color.black);
        int text_color = ta.getResourceId(1, android.R.color.black);
        button.setBackgroundColor(getResources().getColor(bg_color, getTheme()));
        button.setTextColor(getResources().getColor(text_color, getTheme()));
        ta.recycle();
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class OverviewPagerAdapter extends FragmentStatePagerAdapter {
        OverviewPagerAdapter(FragmentManager fm) {
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
