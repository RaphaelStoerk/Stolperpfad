package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data_util.DataFromJSON;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments.StoneInfoPersonFragment;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.BioPoint;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.NoSwipeViewPager;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.PersonInfo;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.Stolperstein;

/**
 * The main activity that stores an alphabetical List of all the persons
 * and keeps track which persons stone is displayed
 */

public class StoneInfoMainActivity extends StolperpfadeAppActivity {

    private ArrayList<PersonInfo> persons;
    private int current_person_index;
    private int MAX_PERSONS;
    private StoneInfoPersonFragment fragment;
    private NoSwipeViewPager infoPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeGeneralControls(R.layout.activity_stone_info_main);
        aq.id(R.id.left_button).clicked(view -> left_click());
        aq.id(R.id.right_button).clicked(view -> right_click());
        loadPersons(); // TODO: das am Anfang machen und hier dafür aus Datenbak lesen

        // TODO: Personen sortieren
        // TODO: erst die Liste Anzeigen und danach erst die Person anzeigen

        // Instantiate a ViewPager and a PagerAdapter.
        infoPager = (NoSwipeViewPager)
                findViewById(R.id.stone_pager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        infoPager.setAdapter(pagerAdapter);
        infoPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                current_person_index = position;
            }
        });
        // current_person_index = getIntent().getAction().charAt(0);
        current_person_index = 0;

        String id = getIntent().getAction();
        current_person_index = getPerson(id);
        infoPager.setCurrentItem(current_person_index);
    }

    private int getPerson(String id) {
        for(PersonInfo person : persons) {
            if(id.equals(person.getId() + "")) {
                return persons.indexOf(person);
            }
        }
        return 0;
    }

    public void left_click() {
        if (current_person_index == 0) {
            return;
        }
        current_person_index--;
        infoPager.setCurrentItem(current_person_index);
        //fragment.update(persons.get(current_person_index));
    }

    public void right_click() {
        if (current_person_index == MAX_PERSONS - 1) {
            return;
        }
        current_person_index++;
        infoPager.setCurrentItem(current_person_index);
        //fragment.update(persons.get(current_person_index));
    }

    private void loadPersons() {
        persons = new ArrayList<>();
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
        return new PersonInfo(id,vorname, nachname, geburtsname, stolperstein, biography);
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
            StoneInfoPersonFragment fragment = StoneInfoPersonFragment.newInstance(persons.get(position), myClickListener);
            fragment.setRetainInstance(true);
            return fragment;
        }

        @Override
        public int getCount() {
            return MAX_PERSONS;
        }
    }

}
