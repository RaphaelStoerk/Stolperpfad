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

public class ImpressumViewActivity extends StolperpfadeAppActivity {


    private final int DISPLAY_RIGHTS = 0;
    private final int DISPLAY_CONTACT = 1;
    private static final int TOTAL_PAGES = 2;
    private int current_display;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeGeneralControls(R.layout.activity_impressum_view);

        // add the listener to the buttons on screen and make them visible
        aq.id(R.id.impressum_to_rights_button).visible().clicked(myClickListener);
        aq.id(R.id.impressum_to_contact_button).visible().clicked(myClickListener);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.impressum_pager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                current_display = position;
                updateButtons();
            }
        });
        current_display = DISPLAY_RIGHTS;
        updateButtons();
    }

    public void setInfoDisplay(int i) {
        if(i == current_display) {
            return;
        }
        current_display = i;
        if(current_display == DISPLAY_RIGHTS) {
            mPager.setCurrentItem(DISPLAY_RIGHTS);
        } else if(current_display == DISPLAY_CONTACT) {
            mPager.setCurrentItem(DISPLAY_CONTACT);
        }
        updateButtons();
    }

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
            return ImpressumContentFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return TOTAL_PAGES;
        }
    }
}
