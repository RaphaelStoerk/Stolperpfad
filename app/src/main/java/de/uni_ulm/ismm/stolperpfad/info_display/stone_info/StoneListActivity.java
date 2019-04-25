package de.uni_ulm.ismm.stolperpfad.info_display.stone_info;

import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

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

    VerticalViewPager list_pager;
    ScrollView index_scroll_view;
    ArrayList<PersonInfo> persons;
    ArrayList<Character> initials;
    private Button last_pressed;
    ArrayList<Button> index_buttons;
    private int MAX_PERSONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeGeneralControls(R.layout.activity_stone_list);

        loadPersons();

        index_scroll_view = findViewById(R.id.index_scroll_view);
        setUpScrollView(index_scroll_view);

        list_pager = findViewById(R.id.stone_list_pager);
        PagerAdapter lpa = new ListPagerAdapter(getSupportFragmentManager());
        list_pager.setAdapter(lpa);
        list_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateList(position);
            }
        });

    }

    private void setUpScrollView(ScrollView index_scroll_view) {
        ConstraintLayout index = index_scroll_view.findViewById(R.id.index_layout);
        index_buttons = new ArrayList<>();
        int count = initials.size();
        Button buff;
        for(int i = 0; i < count; i++) {
            index_buttons.add(buff = makeButton(this, i));
            index.addView(buff);
        }
        index_buttons.get(0).setBackgroundResource(R.drawable.ic_bio_point_on);
        Button first = index_buttons.get(0);
        DisplayMetrics dm = this.getResources().getDisplayMetrics();
        float dp = 16f;
        float fpixels = dm.density * dp;
        int margin = (int) (fpixels + 0.5f);
        ConstraintSet cs = new ConstraintSet();
        cs.clone(index);
        cs.connect(first.getId(),ConstraintSet.TOP, index.getId(),ConstraintSet.TOP,margin );
        cs.connect(first.getId(),ConstraintSet.START,index.getId(),ConstraintSet.START );
        cs.connect(first.getId(),ConstraintSet.END, index.getId(),ConstraintSet.END);
        for(int i = 1; i < count; i++) {
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.TOP,index_buttons.get(i-1).getId(),ConstraintSet.BOTTOM,margin);
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.START,index.getId(),ConstraintSet.START );
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.END, index.getId(),ConstraintSet.END );
        }
        cs.applyTo(index);
        last_pressed = first;
    }

    private Button makeButton(Context ctx, int index) {
        Button but = (Button) LayoutInflater.from(ctx).inflate(R.layout.button_index, null);
        but.setOnClickListener(view -> {
            updateList(index);
            last_pressed.setBackground(null);
            but.setBackgroundResource(R.drawable.ic_point_on);
            last_pressed = but;
        });
        but.setText(initials.get(index).toString());
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        float dp = 50f;
        float fpixels = dm.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(pixels,pixels);
        but.setLayoutParams(params);
        but.setId(index+1000);
        return but;


    }

    public void updateList(int index) {
        list_pager.setCurrentItem(index);
        last_pressed.setBackground(null);
        index_buttons.get(index).setBackgroundResource(R.drawable.ic_bio_point_on);
        last_pressed = index_buttons.get(index);
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
        ArrayList<JSONObject> personen = DataFromJSON.loadAllJSONFromDirectory(this, "person_data");
        PersonInfo next;
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
        try {
            int id = json.getInt("id");
            String vorname = json.getString("vorname");
            String nachname = json.getString("nachname");
            String geburtsname = json.getString("geburtsname");
            if (!initials.contains(nachname.charAt(0))) {
                initials.add(nachname.charAt(0));
            }
            JSONObject stein = json.getJSONObject("stein");
            int id1 = stein.getInt("id");
            String ad = stein.getString("addresse");
            double lat = stein.getDouble("latitude");
            double lon = stein.getDouble("longitude");
            return new PersonInfo(id, vorname, nachname, geburtsname, new Stolperstein(id1, ad, lat, lon));
        } catch(NullPointerException e) {

        }
        return new PersonInfo(-1, "Fehler", "Fehler", "", null);
    }
}
