package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneInfoMainActivity;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.model.PersonInfo;

public class StoneListFragment extends Fragment {

    private ArrayList<Person> persons;
    private char initial;

    public StoneListFragment() {

    }

    public static StoneListFragment newInstance(ArrayList<Person> persons, char initial){
        StoneListFragment frag = new StoneListFragment();
        frag.persons = persons;
        frag.initial = initial;
        return frag;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_stone_list, container, false);

        LinearLayout list_layout = (LinearLayout) root.findViewById(R.id.list_layout);

        if(persons == null) {
        } else {
            createList(inflater.getContext(), list_layout);
        }
        return root;
    }

    private void createList(Context ctx, LinearLayout list_layout) {
        Button buff;
        for(Person person : persons) {
            if(person.getFamName().startsWith(initial + "")) {
                buff = addButton(ctx, person);
                list_layout.addView(buff);
            }
        }
    }

    private Button addButton(Context ctx, Person person) {
        Button but = new Button(ctx);
        but.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), StoneInfoMainActivity.class);
            intent.setAction("" + person.getPersId());
            startActivity(intent);
        });
        but.setTextAppearance(R.style.AppTheme_Light);
        String display_name = person.getEntireName();
        but.setText(display_name);
        but.setBackgroundColor(Color.argb(0,0,0,0));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        but.setLayoutParams(params);
        return but;
    }
}
