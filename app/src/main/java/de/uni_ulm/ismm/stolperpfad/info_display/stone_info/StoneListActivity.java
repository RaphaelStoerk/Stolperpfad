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

    }

    private void setUpScrollView(ScrollView index_scroll_view) {
        ConstraintLayout index = index_scroll_view.findViewById(R.id.index_container);
        ArrayList<Button> index_buttons = new ArrayList<>();
        Button buff;
        for(int i = 0; i < initials.size(); i++) {
            index_buttons.add(buff = makeButton(this, i));
            index.addView(buff);
        }
        ConstraintSet cs = new ConstraintSet();
        cs.connect(index_buttons.get(0).getId(),ConstraintSet.TOP, index.getId(),ConstraintSet.TOP,32 );
        cs.connect(index_buttons.get(0).getId(),ConstraintSet.START, index.getId(),ConstraintSet.START, 16 );
        cs.connect(index_buttons.get(0).getId(),ConstraintSet.END, index.getId(),ConstraintSet.END, 16 );

        for(int i = 1; i < index_buttons.size(); i++) {
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.TOP,index_buttons.get(i-1).getId(),ConstraintSet.BOTTOM, 64 );
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.START,index.getId(),ConstraintSet.START, 16 );
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.END, index.getId(),ConstraintSet.END, 16 );
        }
        cs.applyTo(index);
    }

    private Button makeButton(Context ctx, int index) {
        Button but = new Button(ctx);
        but.setOnClickListener(view -> {
            updateList(index);
        });
        but.setBackgroundResource(R.drawable.ic_bio_point_on);
        but.setText(initials.get(index).toString());
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        float dp = 32f;
        float fpixels = dm.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(pixels, pixels);
        but.setLayoutParams(params);
        but.setId(View.generateViewId());
        return but;
    }

    public void updateList(int index) {
        list_pager.setCurrentItem(index);
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
