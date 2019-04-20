package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.database.data_util.DataFromJSON;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

/**
 * The main activity that stores an alphabetical List of all the persons
 * and keeps track which persons stone is displayed
 */

public class StoneInfoMainActivity extends StolperpfadeAppActivity {

    private ArrayList<Person> persons;
    private ViewPager mPager;
    private int current_person_index;
    private int MAX_PERSONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeGeneralControls(R.layout.activity_stone_info_main);
        aq.id(R.id.left_button).clicked(myClickListener);
        aq.id(R.id.right_button).clicked(myClickListener);
        loadPersons(); // TODO: das am Anfang machen und hier dafür aus Datenbak lesen

        // TODO: Personen sortieren
        // TODO: erst die Liste Anzeigen und danach erst die Person anzeigen

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.stone_pager);
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(pagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                current_person_index = position;
                // TODO: Update left and right buttons if persons are before or after
            }
        });
        // current_person_index = getIntent().getAction().charAt(0);
        current_person_index = 0;
    }

    public void left_click() {
        if(current_person_index > 0) {
            current_person_index--;
            setPersonDisplay(current_person_index, true, false);
        }
    }

    public void right_click() {
        if(current_person_index < MAX_PERSONS - 1) {
            current_person_index++;
            setPersonDisplay(current_person_index, true, false);
        }
    }

    public void setPersonDisplay(int index, boolean update, boolean smooth) {
        if(!update && index == current_person_index) {
            return;
        }
        current_person_index = index;
        mPager.setCurrentItem(current_person_index, smooth);
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
            return StoneInfoPersonFragment.newInstance(persons.get(current_person_index = position), myClickListener);
        }

        @Override
        public int getCount() {
            return MAX_PERSONS;
        }
    }
    private void loadPersons() {
        persons = new ArrayList<>();
        ArrayList<JSONObject> personen = DataFromJSON.loadAllJSONFromDirectory(this, "personen_daten");
        Person next;
        int id;
        String vorname;
        String nachname;
        int stostein;
        for(JSONObject json : personen) {
            try {
                id = json.getInt("id");
                vorname = json.getString("vorname");
                nachname = json.getString("nachname");
                stostein = json.getJSONObject("stein").getInt("id");
                next = new Person(id,vorname, nachname, stostein);
                persons.add(next);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        MAX_PERSONS = persons.size();
    }
}
