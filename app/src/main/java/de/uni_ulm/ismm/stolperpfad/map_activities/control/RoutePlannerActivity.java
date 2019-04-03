package de.uni_ulm.ismm.stolperpfad.map_activities.control;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapFragment;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;

public class RoutePlannerActivity extends AppCompatActivity {

    private AQuery aq;

    private static final String MAP_FRAGMENT_TAG = "MAPQUEST_MAP_FRAGMENT";
    private MapQuestFragment myMapFragment;
    private MyClickListener myListener;
    private String my_text;
    private int starting_choice;
    private String[] categories = new String[]{"Nein", "Jüdische Verfolgte", "Politisch Verfolgte", "Andere"};

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

                    starting_choice = 0;
                    my_text = "";

                    Spinner spin = myDialogView.findViewById(R.id.spinner);

                    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    ArrayAdapter aa = new ArrayAdapter(builder.getContext(), android.R.layout.simple_spinner_item, categories);

                    aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spin.setAdapter(aa);

                    spin.setSelection(0);


                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myMapFragment.createRoute();
                            Log.d("HERE I AM", my_text);
                            dialog.cancel();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                    break;

            }
        }
    }
}
