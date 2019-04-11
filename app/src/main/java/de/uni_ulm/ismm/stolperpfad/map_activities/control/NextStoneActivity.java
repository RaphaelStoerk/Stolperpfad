package de.uni_ulm.ismm.stolperpfad.map_activities.control;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.MyButtonClickListener;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapFragment;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapQuestFragment;

public class NextStoneActivity extends StolperpfadeAppActivity {

    AQuery aq;

    private static final String MAP_FRAGMENT_TAG = "org.osmdroid.MAP_FRAGMENT_TAG";
    private MapQuestFragment myMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize important helper-Objects
        aq = new AQuery(this);

        // Initialize the Listener for the clickable items
        myClickListener = new MyButtonClickListener<>();
        myClickListener.setMyActivity(this);

        // add the listener to the items
        aq.id(R.id.quick_access_button).visible().clicked(myClickListener);

        setContentView(R.layout.activity_next_stone);

        // The actual map view is now a fragment, for easier reuse and readability
        FragmentManager fm = this.getSupportFragmentManager();

        if (fm.findFragmentById(R.id.map_container) == null) {
            myMapFragment = MapQuestFragment.newInstance(true);
            fm.beginTransaction().add(R.id.map_container, myMapFragment, MAP_FRAGMENT_TAG).commit();
        }

    }

    public void onResume(){
        super.onResume();
    }

    public void onPause(){
        super.onPause();
    }
}
