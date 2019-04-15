package de.uni_ulm.ismm.stolperpfad.map_activities.control;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.map_activities.StolperpfadAppMapActivity;

public class RoutePlannerActivity extends StolperpfadAppMapActivity {

    private int starting_choice;
    private int ending_choice;
    private String[] categories = new String[]{"Nein", "Jüdische Verfolgte", "Politisch Verfolgte", "Andere"};
    private String time_string;
    private String selected_category;

    public static final int START_CHOICE_GPS = 0;
    public static final int START_CHOICE_MAP = 1;
    public static final int START_CHOICE_CTR = 2;
    public static final int START_CHOICE_NAN = -1;

    public static final int END_CHOICE_STN = 0;
    public static final int END_CHOICE_MAP = 1;
    public static final int END_CHOICE_CTR = 2;
    public static final int END_CHOICE_NAN = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeGeneralControls(R.layout.activity_route_planner);
        initializeMapQuestFragment(false);

        // Route Planner specific setups
        aq.id(R.id.header_route_planner).getView().setTranslationZ(HEADER_TRANSLATION_Z / 2);
        aq.id(R.id.route_option_button).visible().clicked(myClickListener);
        aq.id(R.id.save_route_button).visible().clicked(myClickListener);
        aq.id(R.id.info_map_options_button).visible().clicked(myClickListener);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void routeOptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myMapFragment.getContext());

        // Get the layout inflater
        LayoutInflater inflater = myMapFragment.getLayoutInflater();

        View myDialogView = inflater.inflate(R.layout.dialog_route_options, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(myDialogView);
        builder.setTitle("Erstelle eine Route");


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

        // build the start and end position options
        starting_choice = START_CHOICE_GPS;
        RadioGroup start_choice = myDialogView.findViewById(R.id.start_of_route_choice);
        start_choice.setOnCheckedChangeListener((RadioGroup radioGroup, int i) -> {
            starting_choice = getPosFromId(i, true);
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
            myMapFragment.createRoute(selected_category, time_in_minutes, starting_choice, ending_choice);
            dialog.cancel();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private int getPosFromId(int i, boolean start) {
        switch(i) {
            case R.id.start_at_center:
                return START_CHOICE_CTR;
            case R.id.start_at_marker:
                return START_CHOICE_MAP;
            case R.id.start_at_position:
                return START_CHOICE_GPS;
            case R.id.end_at_marker:
                return END_CHOICE_MAP;
            case R.id.end_at_center:
                return END_CHOICE_CTR;
            case R.id.end_at_stone:
                return END_CHOICE_STN;
            default:
                return start ? START_CHOICE_NAN : END_CHOICE_NAN;
        }
    }

    public void informationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myMapFragment.getContext());

        // Get the layout inflater
        LayoutInflater inflater = myMapFragment.getLayoutInflater();

        View myDialogView = inflater.inflate(R.layout.dialog_map_info, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(myDialogView);

        builder.setNegativeButton("Verstanden", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void saveOrLoadRouteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myMapFragment.getContext());

        // Get the layout inflater
        LayoutInflater inflater = myMapFragment.getLayoutInflater();

        View myDialogView = inflater.inflate(R.layout.dialog_save_or_load_route, null);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(myDialogView);

        builder.setNegativeButton("Speichern", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
