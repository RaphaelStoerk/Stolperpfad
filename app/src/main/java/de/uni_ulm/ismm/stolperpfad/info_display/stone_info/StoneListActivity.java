package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.os.Bundle;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.IndexFragment;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.StoneListFragment;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.VerticalViewPager;

public class StoneListActivity extends StolperpfadeAppActivity {

    VerticalViewPager index_pager, list_pager;
    ArrayList<Person> persons;
    ArrayList<Character> initials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeGeneralControls(R.layout.activity_stone_list);

        index_pager = findViewById(R.id.index_pager);
        PagerAdapter ipa = new IndexPagerAdapter(getSupportFragmentManager());
        index_pager.setAdapter(ipa);

        list_pager = findViewById(R.id.stone_list_pager);
        PagerAdapter lpa = new ListPagerAdapter(getSupportFragmentManager());
        list_pager.setAdapter(lpa);

    }

    public void updateList(int index) {
        list_pager.setCurrentItem(index);
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class IndexPagerAdapter extends FragmentStatePagerAdapter {
        IndexPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return IndexFragment.newInstance(initials);
        }

        @Override
        public int getCount() {
            return 1;
        }
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
            return StoneListFragment.newInstance(persons, initials.get(position));
        }

        @Override
        public int getCount() {
            return initials.size();
        }
    }

}
