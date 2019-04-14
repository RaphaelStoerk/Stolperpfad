package de.uni_ulm.ismm.stolperpfad.map_activities.control;

import android.app.AlertDialog;
import android.support.v4.app.FragmentManager;
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
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.StolperpfadAppMapActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;

public class RoutePlannerActivity extends StolperpfadAppMapActivity {

    private int starting_choice;
    private int ending_choice;
    private String[] categories = new String[]{"Nein", "Jüdische Verfolgte", "Politisch Verfolgte", "Andere"};
    private String time_string;
    private String selected_category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeGeneralControls(R.layout.activity_route_planner);
        initializeMapQuestFragment();


        // Route Planner specific setups
        aq.id(R.id.header_route_planner).getView().setTranslationZ(HEADER_TRANSLATION_Z / 2);
        aq.id(R.id.route_option_button).visible().clicked(myClickListener);

    }

    @Override



    public void onResume(){
        super.onResume();
    }

    public void onPause(){
        super.onPause();
    }

    public void routeOptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(myMapFragment.getContext());

        // Get the layout inflater
        LayoutInflater inflater = myMapFragment.getLayoutInflater();

        View myDialogView = inflater.inflate(R.layout.route_option_layout, null);

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
        starting_choice = -1;
        RadioGroup start_choice = myDialogView.findViewById(R.id.start_of_route_choice);
        start_choice.setOnCheckedChangeListener((radioGroup, i) -> {
            starting_choice = i;
        });

        ending_choice = -1;
        RadioGroup end_choice = myDialogView.findViewById(R.id.end_of_route_choice);
        end_choice.setOnCheckedChangeListener((radioGroup, i) -> {
            ending_choice = i;
        });

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {

            int time_in_minutes;
            try {
                time_in_minutes = Integer.parseInt(time_string);
            }catch(NumberFormatException nfe) {
                time_in_minutes = -1;
            }

            myMapFragment.createRoute(selected_category, time_in_minutes, starting_choice, ending_choice);
            dialog.cancel();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
}
