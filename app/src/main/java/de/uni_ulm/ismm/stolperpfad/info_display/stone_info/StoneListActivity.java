package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.StoneListFragment;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.StoneListViewModel;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.VerticalViewPager;

public class StoneListActivity extends StolperpfadeAppActivity {

    VerticalViewPager list_pager;
    private StoneListViewModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeGeneralControls(R.layout.activity_stone_list);

        list_pager = findViewById(R.id.stone_list_pager);
        PagerAdapter lpa = new ListPagerAdapter(getSupportFragmentManager());
        list_pager.setAdapter(lpa);
        list_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                model.updateIndex(position);
            }
        });
        model = StoneListViewModel.getInstance(this);
        model.setUpIndex(list_pager);

    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ListPagerAdapter extends FragmentStatePagerAdapter {
        ListPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return StoneListFragment.newInstance(model, model.getInitial(position));
        }

        @Override
        public int getCount() {
            return model.getInitialCount();
        }
    }
}
