package de.uni_ulm.ismm.stolperpfad.map_activities.control;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;

public class RoutePlannerActivity extends AppCompatActivity {

    private AQuery aq;

    private static final String MAP_FRAGMENT_TAG = "MAPQUEST_MAP_FRAGMENT";
    private MapQuestFragment myMapFragment;
    private MyClickListener myListener;
    private String my_text;
    private int starting_choice;
    private int ending_choice;
    private int end_choice;
    private String[] categories = new String[]{"Nein", "Jüdische Verfolgte", "Politisch Verfolgte", "Andere"};
    private String time_string;
    private String selected_category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(this);
        myListener = new MyClickListener();

        setContentView(R.layout.activity_route_planner);


        // The actual map view is now a fragment, for easier reuse and readability
        FragmentManager fm = this.getSupportFragmentManager();

        if (fm.findFragmentById(R.id.map_container) == null) {
            myMapFragment = MapQuestFragment.newInstance(false);
            fm.beginTransaction().add(R.id.map_container, myMapFragment, MAP_FRAGMENT_TAG).commit();
        }

        aq.id(R.id.route_option_button).visible().clicked(myListener);

    }

    public void onResume(){
        super.onResume();
    }

    public void onPause(){
        super.onPause();
    }


    /**
     * This is an internal class that handles the Clicks of buttons on the main menu
     */
    class MyClickListener implements View.OnClickListener {

        // TODO: Create an extra xml file to add more input possibilities for all the things
        // a users could want to input for their route
        @Override
        public void onClick(View v) {

            Log.i("BUTTON", "THE BUTTON HAS BEEN PRESSED");

            // a simple switch case statement that checks which button was pressed
            switch (v.getId()) {
                case R.id.route_option_button:
                    AlertDialog.Builder builder = new AlertDialog.Builder(myMapFragment.getContext());

                    // Get the layout inflater
                    LayoutInflater inflater = myMapFragment.getLayoutInflater();

                    View myDialogView = inflater.inflate(R.layout.route_option_layout, null);

                    // Inflate and set the layout for the dialog
                    // Pass null as the parent view because its going in the dialog layout
                    builder.setView(myDialogView);

                    builder.setTitle("Erstelle eine Route");

                    my_text = "";

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

                    ArrayAdapter aa = new ArrayAdapter(builder.getContext(), android.R.layout.simple_spinner_item, categories);

                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spin.setAdapter(aa);

                    spin.setSelection(0);


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
                        Log.d("HERE I AM", time_string);
                        dialog.cancel();
                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

                    builder.show();
                    break;
            }
        }
    }
}
