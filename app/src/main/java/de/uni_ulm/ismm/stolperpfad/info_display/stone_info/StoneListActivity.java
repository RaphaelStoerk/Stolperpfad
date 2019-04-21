package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data_util.DataFromJSON;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.IndexFragment;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.StoneListFragment;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.BioPoint;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.PersonInfo;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.Stolperstein;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.VerticalViewPager;

public class StoneListActivity extends StolperpfadeAppActivity {

    VerticalViewPager index_pager, list_pager;
    ArrayList<PersonInfo> persons;
    ArrayList<Character> initials;
    private int MAX_PERSONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeGeneralControls(R.layout.activity_stone_list);

        loadPersons();

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


    private void loadPersons() {
        persons = new ArrayList<>();
        initials = new ArrayList<>();
        ArrayList<JSONObject> personen = DataFromJSON.loadAllJSONFromDirectory(this, "personen_daten");
        PersonInfo next;
        int id;
        String vorname;
        String nachname;
        JSONObject stostein;
        Stolperstein stolperstein;
        for(JSONObject json : personen) {
            try {
                next = createPersonFromJson(json);

                persons.add(next);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
       // initials.sort();
        MAX_PERSONS = persons.size();
    }

    private PersonInfo createPersonFromJson(JSONObject json) throws JSONException {
        Stolperstein stolperstein;
        int id = json.getInt("id");
        String vorname = json.getString("vorname");
        String nachname = json.getString("nachname");
        String geburtsname = json.getString("geburtsname");
        JSONObject stostein = json.getJSONObject("stein");
        stolperstein = new Stolperstein(stostein.getInt("id"),stostein.getString("addresse"), stostein.getDouble("latitude"), stostein.getDouble("longitude"));
        JSONArray bio = json.getJSONArray("bio");
        ArrayList<BioPoint> biography = new ArrayList<>();
        for(int i = 0; i < bio.length(); i++) {
            JSONObject bio_point = bio.getJSONObject(i);
            BioPoint next = new BioPoint(vorname + " " + nachname, bio_point);
            biography.add(next);
        }
        if(!initials.contains(nachname.charAt(0))) {
            initials.add(nachname.charAt(0));
        }
        return new PersonInfo(id,vorname, nachname, geburtsname, stolperstein, biography);
    }
}
