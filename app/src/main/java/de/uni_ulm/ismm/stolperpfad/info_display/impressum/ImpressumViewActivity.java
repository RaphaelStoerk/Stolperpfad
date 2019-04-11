package de.uni_ulm.ismm.stolperpfad.info_display.impressum;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.general.MyButtonClickListener;
import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

public class ImpressumViewActivity extends StolperpfadeAppActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_impressum_view);
        // Initialize important helper-Objects
        aq = new AQuery(this);

        // Initialize the Listener for the clickable items
        myClickListener = new MyButtonClickListener<>();
        myClickListener.setMyActivity(this);

        // add the listener to the items
        aq.id(R.id.quick_access_button).visible().clicked(myClickListener);

    }
}
