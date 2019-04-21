package de.uni_ulm.ismm.stolperpfad.info_display.stone_info.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.info_display.stone_info.StoneListActivity;

public class IndexFragment extends Fragment {

    private ArrayList<Character> initials;
    private ArrayList<Button> index_buttons;

    public IndexFragment() {

    }

    public static IndexFragment newInstance(ArrayList<Character> initials) {
        IndexFragment frag = new IndexFragment();
        frag.initials = initials;
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.content_list_index, container, false);

        ConstraintLayout index_layout = (ConstraintLayout) root.findViewById(R.id.index_layout);

        if(initials == null) {
        } else {
            createIndexButtons(inflater.getContext(), index_layout);
        }
        return root;
    }

    private void createIndexButtons(Context ctx, ConstraintLayout index_layout) {
        int points = initials.size();
        if(points < 2) {
            return;
        }
        index_buttons = new ArrayList<>();
        Button first_button = makeButton( ctx, 0);
        Button last_button = makeButton(ctx, points - 1);
        index_buttons.add(first_button);
        Button buff;
        for(int i = 0; i < points - 2; i++) {
            index_buttons.add(buff = makeButton(ctx, i + 1));
            index_layout.addView(buff);
        }
        index_buttons.add(last_button);
        index_layout.addView(first_button);
        index_layout.addView(last_button);

        ConstraintSet cs = new ConstraintSet();
        cs.clone(index_layout);
        cs.connect(first_button.getId(),ConstraintSet.TOP, index_layout.getId(),ConstraintSet.TOP,32 );
        cs.connect(first_button.getId(),ConstraintSet.BOTTOM, index_buttons.get(1).getId(),ConstraintSet.TOP );
        cs.connect(first_button.getId(),ConstraintSet.START,index_layout.getId(),ConstraintSet.START, 16 );
        cs.connect(first_button.getId(),ConstraintSet.END, index_layout.getId(),ConstraintSet.END, 16 );
        for(int i = 1; i <= points - 2; i++) {
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.TOP,index_buttons.get(i-1).getId(),ConstraintSet.BOTTOM );
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.BOTTOM,index_buttons.get(i+1).getId(),ConstraintSet.TOP );
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.START,index_layout.getId(),ConstraintSet.START, 16 );
            cs.connect(index_buttons.get(i).getId(),ConstraintSet.END, index_layout.getId(),ConstraintSet.END, 16 );
        }
        cs.connect(last_button.getId(),ConstraintSet.TOP, index_buttons.get(points-2).getId(),ConstraintSet.BOTTOM );
        cs.connect(last_button.getId(),ConstraintSet.BOTTOM, index_layout.getId(),ConstraintSet.BOTTOM,32);
        cs.connect(last_button.getId(),ConstraintSet.START,index_layout.getId(),ConstraintSet.START, 16 );
        cs.connect(last_button.getId(),ConstraintSet.END, index_layout.getId(),ConstraintSet.END, 16 );
        cs.applyTo(index_layout);
    }

    private Button makeButton(Context ctx, int index) {
        Button but = new Button(ctx);
        but.setOnClickListener(view -> {
            ((StoneListActivity)getActivity()).updateList(index);
        });
        but.setBackgroundColor(Color.argb(100,250,160,70));
        but.setText(initials.get(index).toString());
        DisplayMetrics dm = ctx.getResources().getDisplayMetrics();
        float dp = 32f;
        float fpixels = dm.density * dp;
        int pixels = (int) (fpixels + 0.5f);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(pixels,pixels);
        but.setLayoutParams(params);
        but.setId(View.generateViewId());
        return but;
    }


}