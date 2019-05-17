package de.uni_ulm.ismm.stolperpfad.map_activities.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.StolperpfadeApplication;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.control.RoutePlannerActivity;

public class RouteOptionsDialog extends DialogFragment {
    public RouteOptionsDialog() {

    }

    public static RouteOptionsDialog newInstance(RoutePlannerActivity parent) {
        RouteOptionsDialog frag = new RouteOptionsDialog();
        frag.parent = parent;
        return frag;
    }

    private SharedPreferences prefs;
    private ImageButton[] index;
    private ImageButton left, right;
    private RoutePlannerActivity parent;

    private String time_in_min;
    private int option_start;
    private int option_end;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        prefs = getActivity().getApplicationContext().getSharedPreferences(
                "de.uni_ulm.ismm.stolperpfad", Context.MODE_PRIVATE);
        prefs.edit().putString("de.uni_ulm.ismm.stolperpfad.route_time", "#").apply();
        prefs.edit().putString("de.uni_ulm.ismm.stolperpfad.route_start", "#").apply();
        prefs.edit().putString("de.uni_ulm.ismm.stolperpfad.route_end", "#").apply();

        if(StolperpfadeApplication.getInstance().isDarkMode()) {
            setStyle(STYLE_NO_TITLE, R.style.DialogTheme_Dark);
        } else {
            setStyle(STYLE_NO_TITLE, R.style.DialogTheme_Light);
        }

        View myDialogView = inflater.inflate(R.layout.dialog_route_options, null);

        Log.i("MY_DIALOG_TAG", "View inflated");

        getDialog().setTitle("");


        ViewPager options_pager = myDialogView.findViewById(R.id.route_option_pager);
        options_pager.setMinimumHeight(options_pager.getWidth());
        PagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        options_pager.setAdapter(pagerAdapter);
        left = myDialogView.findViewById(R.id.dialog_left);
        left.setOnClickListener(view -> options_pager.setCurrentItem(options_pager.getCurrentItem() - 1));
        right = myDialogView.findViewById(R.id.dialog_right);
        right.setOnClickListener(view -> options_pager.setCurrentItem(options_pager.getCurrentItem() + 1));
        options_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                options_pager.getAdapter().notifyDataSetChanged();
                updateIndex(position);
            }
        });
        index = new ImageButton[] {
                myDialogView.findViewById(R.id.dialog_one),
                myDialogView.findViewById(R.id.dialog_two),
                myDialogView.findViewById(R.id.dialog_three),
                myDialogView.findViewById(R.id.dialog_four)};
        index[0].setOnClickListener(view -> options_pager.setCurrentItem(0));
        index[1].setOnClickListener(view -> options_pager.setCurrentItem(1));
        index[2].setOnClickListener(view -> options_pager.setCurrentItem(2));
        index[3].setOnClickListener(view -> options_pager.setCurrentItem(3));

        Log.i("MY_DIALOG_TAG", "Dialog set up");

        options_pager.setOffscreenPageLimit(1);

        updateIndex(0);

        return myDialogView;
    }

    private void updateIndex(int pos) {
        for(ImageButton ib : index) {
            if(StolperpfadeApplication.getInstance().isDarkMode()) {
                ib.setImageResource(R.drawable.ic_bio_off_dark);
            } else {
                ib.setImageResource(R.drawable.ic_bio_point_off);
            }
        }
        index[pos].setImageResource(R.drawable.ic_bio_point_on);
        if(pos <= 0) {
            left.setAlpha(0f);
        } else {
            left.setAlpha(0.7f);
        }
        if(pos >= index.length - 1) {
            right.setAlpha(0f);
        } else {
            right.setAlpha(0.7f);
        }
    }

    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private Fragment myCurrentFragment;

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        public Fragment getMyCurrentFragment() {
            return myCurrentFragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup containter, int pos, Object obj) {
            if(getMyCurrentFragment() != obj) {
                myCurrentFragment = (Fragment) obj;
            }
            super.setPrimaryItem(containter,pos,obj);
        }

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            Log.i("MY_DIALOG_TAG", "First call to get Item");
            return RouteOptionsFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }





    /*
    public void dummy_method() {

        // build the category choices
        Spinner spin = myDialogView.findViewById(R.id.spinner);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_category = categories[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selected_category = "";
            }
        });

        ArrayAdapter<String> aa = new ArrayAdapter<>(builder.getContext(), android.R.layout.simple_spinner_item, categories);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(aa);
        spin.setSelection(0);

        // build the time input test field
        time_string = "";
        EditText time_input = myDialogView.findViewById(R.id.time_input);

        time_input.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                time_string = editable.toString();
            }
        });



        ending_choice = END_CHOICE_STN;
        RadioGroup end_choice = myDialogView.findViewById(R.id.end_of_route_choice);
        end_choice.setOnCheckedChangeListener((radioGroup, i) -> {
            ending_choice = getPosFromId(i, false);
        });

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {

            int time_in_minutes;
            try {
                time_in_minutes = Integer.parseInt(time_string);
            } catch (NumberFormatException nfe) {
                time_in_minutes = -1;
            }

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            map_quest.createRoute(selected_category, time_in_minutes, starting_choice, ending_choice);
            dialog.cancel();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
    }
    */
}
