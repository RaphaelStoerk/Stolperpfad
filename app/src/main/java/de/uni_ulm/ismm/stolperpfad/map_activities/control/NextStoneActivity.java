package de.uni_ulm.ismm.stolperpfad.map_activities.control;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.map_activities.view.MapFragment;

public class NextStoneActivity extends AppCompatActivity {

    AQuery aq;

    private static final String MAP_FRAGMENT_TAG = "org.osmdroid.MAP_FRAGMENT_TAG";
    private MapFragment myMapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aq = new AQuery(this);

        setContentView(R.layout.activity_next_stone);

        // The actual map view is now a fragment, for easier reuse and readability
        FragmentManager fm = this.getSupportFragmentManager();

        if (fm.findFragmentById(R.id.map_container) == null) {
            myMapFragment = MapFragment.newInstance(true);
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
