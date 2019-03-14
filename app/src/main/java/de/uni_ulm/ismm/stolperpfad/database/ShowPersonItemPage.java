package de.uni_ulm.ismm.stolperpfad.database;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.androidquery.AQuery;

import de.uni_ulm.ismm.stolperpfad.R;

public class ShowPersonItemPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize this view and display the right screen
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_person_item_page);

        AQuery aq = new AQuery(this);

        // see ScrollingInfoActivity
        aq.id(R.id.floatingActionButton).visible().clicked(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // makes the current screen go away
                finish();
            }
        });
    }
}
