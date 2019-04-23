package de.uni_ulm.ismm.stolperpfad.database.list_of_historical_terms;

import android.arch.lifecycle.ViewModelProviders;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;

public class HistoInfoPage extends AppCompatActivity {

    private HistoViewModel mHistoViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histo_info_page);

        //button to go back
        FloatingActionButton fabBack = findViewById(R.id.fab_back);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //txtHistoDescript
        mHistoViewModel = ViewModelProviders.of(this).get(HistoViewModel.class);

        TextView textView = findViewById(R.id.txtHistoDescript);
        int current = getIntent().getExtras().getInt("termId");
        //textView.setText(mHistoViewModel.getExplanation(current));

    }


}
