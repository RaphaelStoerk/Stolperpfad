package de.uni_ulm.ismm.stolperpfad.map_activities.control;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapFragment;

public class RoutePlannerActivity extends AppCompatActivity {

    private AQuery aq;

    private static final String MAP_FRAGMENT_TAG = "org.osmdroid.MAP_FRAGMENT_TAG";
    private MapFragment myMapFragment;
    private MyClickListener myListener;
    private String my_text;
    private int starting_choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(this);
        myListener = new MyClickListener();

        setContentView(R.layout.activity_route_planner);


        // The actual map view is now a fragment, for easier reuse and readability
        FragmentManager fm = this.getSupportFragmentManager();

        if (fm.findFragmentById(R.id.map_container) == null) {
            myMapFragment = MapFragment.newInstance(false);
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
                    builder.setTitle("Erstelle eine Route");
                    //builder.setMessage("Von wo aus soll die Route starten?");

                    starting_choice = 0;
                    my_text = "";

                    builder.setSingleChoiceItems(new String[]{"Meine Position", "Punkt auf Karte", "Bahnhof"}, 1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            starting_choice = which;
                        }
                    });

                    // Set up the input
                    final EditText input = new EditText(myMapFragment.getContext());
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    input.setText("30");
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            my_text = input.getText().toString();
                            myMapFragment.createRoute(my_text, starting_choice);
                            Log.d("HERE I AM", my_text);
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
