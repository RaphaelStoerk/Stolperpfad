package de.uni_ulm.ismm.stolperpfad.info_display.impressum;

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
 * This is the activity containing the impressum of this application
 */
public class ImpressumActivity extends StolperpfadeAppActivity {

    private final int DISPLAY_RIGHTS = 0;
    private final int DISPLAY_CONTACT = 1;
    private static final int TOTAL_PAGES = 2;
    private int current_display;
    private ViewPager impressum_pager;

    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        initializeGeneralControls(R.layout.activity_impressum_view);
        aq.id(R.id.impressum_to_rights_button).visible().clicked(my_click_listener);
        aq.id(R.id.impressum_to_contact_button).visible().clicked(my_click_listener);
        impressum_pager = findViewById(R.id.impressum_pager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        impressum_pager.setAdapter(pagerAdapter);
        impressum_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                current_display = position;
                updateButtons();
        }
        });
        current_display = DISPLAY_RIGHTS;
        updateButtons();
    }

    /**
     * Change the content of the impressum information pager
     *
     * @param page_index the index of the display that should be displayed
     */
    public void setInfoDisplay(int page_index) {
        if(page_index == current_display) {
            return;
        }
        current_display = page_index;
        if(current_display == DISPLAY_RIGHTS) {
            impressum_pager.setCurrentItem(DISPLAY_RIGHTS);
        } else if(current_display == DISPLAY_CONTACT) {
            impressum_pager.setCurrentItem(DISPLAY_CONTACT);
        }
        updateButtons();
    }

    /**
     * Updated the design of the pager buttons if something has changed, this will highlight
     * the active page's button
     */
    public void updateButtons() {
        Button rights_button, contact_button;
        rights_button = aq.id(R.id.impressum_to_rights_button).getButton();
        contact_button = aq.id(R.id.impressum_to_contact_button).getButton();
        if(current_display == DISPLAY_RIGHTS) {
            setButtonActive(rights_button, true);
            setButtonActive(contact_button, false);
        } else if(current_display == DISPLAY_CONTACT) {
            setButtonActive(rights_button, false);
            setButtonActive(contact_button, true);
        }
    }

    /**
     * This method sets the correct design to the buttons that switch between the
     * info pages of the impressum, so that the button corresponding to the displayed page
     * is highlighted
     *
     * @param button the button to change the look of
     * @param active if this button's page is displayed
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
        int bg_color_active = ta.getResourceId(0, android.R.color.black);
        int text_color_active = ta.getResourceId(1, android.R.color.black);
        button.setBackgroundColor(getResources().getColor(bg_color_active, getTheme()));
        button.setTextColor(getResources().getColor(text_color_active, getTheme()));
        ta.recycle();
    }

    /**
     * The PagerAdapter for switching the content of the two impressum information pages
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ImpressumContentFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return TOTAL_PAGES;
        }
    }
}
