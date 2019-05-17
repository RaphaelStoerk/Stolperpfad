package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.PersonInfoPager;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneInfoViewModel;

/**
 * The main activity that stores an alphabetical List of all the persons
 * and keeps track which persons stone is displayed
 */
public class StoneInfoMainActivity extends StolperpfadeAppActivity {

    private int current_person_index = -1;
    private PersonInfoPager infoPager;

    @Override
    protected void onCreate(Bundle saved_state) {
        super.onCreate(saved_state);
        initializeGeneralControls(R.layout.activity_stone_info_main);
        setLeftRightButtons();
        infoPager = findViewById(R.id.stone_pager);
        infoPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                setPerson(position);
            }
        });
        StoneInfoViewModel model = StoneInfoViewModel.getInstance(this);
        model.setUpInformationPage(infoPager, getIntent().getAction());
    }

    /**
     * Create the buttons that switch the displayed persons to the persons on the left or right
     * when viewed as a horizontal list that's ordered alphabetically
     */
    private void setLeftRightButtons() {
        Button left = (Button) aq.id(R.id.left_button).clicked(view -> leftClick()).getView();
        Button right = (Button) aq.id(R.id.right_button).clicked(view -> rightClick()).getView();
        if(StolperpfadeApplication.getInstance().isDarkMode()) {
            left.setBackgroundResource(R.drawable.ic_left_dark);
            right.setBackgroundResource(R.drawable.ic_right_dark);
        } else {
            left.setBackgroundResource(R.drawable.ic_arrow_left);
            right.setBackgroundResource(R.drawable.ic_arrow_right);
        }
    }

    /**
     * Called when the left button is pressed, will show the next left person if possible
     */
    public void leftClick() {
        if(infoPager == null || infoPager.getAdapter() == null) {
            return;
        }
        if (current_person_index <= 0) {
            return;
        }
        infoPager.setCurrentItem(--current_person_index);
    }

    /**
     * Called when the right button is pressed, will show the next right person if possible
     */
    public void rightClick() {
        if(infoPager == null || infoPager.getAdapter() == null) {
            return;
        }
        if (current_person_index == -1 || current_person_index == infoPager.getAdapter().getCount() - 1) {
            return;
        }
        infoPager.setCurrentItem(++current_person_index);
    }

    public void setPerson(int position) {
        current_person_index = position;
    }
}
