package de.uni_ulm.ismm.stolperpfad.database.list_of_historical_terms;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data.HistoricalTerm;

public class HistoryActivity extends AppCompatActivity implements HistoListAdapter.OnHistoItemListener {

    private static final String TAG = "HistoryActivity";

    private HistoViewModel mHistoViewModel;
    private List<HistoricalTerm> mTermList; //cached copy of terms


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_historical_terms);

        //button to go back
        FloatingActionButton fabBack = findViewById(R.id.fab_back);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //RecyclerView to get data
        RecyclerView recyclerView = findViewById(R.id.recyclerview_history);
        final HistoListAdapter adapter = new HistoListAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // maybe we don't need this because our database won't be changed by the user
        //TODO: if this project is published and continued we have to think about a solution
        //TODO: of how we can insert new Stolpersteine and update the data the user has downloaded
        //TODO: maybe this is possible with playstore updates
        //ViewModel
        mHistoViewModel = ViewModelProviders.of(this).get(HistoViewModel.class);

        mHistoViewModel.getAllTerms().observe(this, new Observer<List<HistoricalTerm>>() {
            @Override
            public void onChanged(@Nullable final List<HistoricalTerm> histoTerms) {
                // Update the cached copy of the words in the adapter.
                adapter.setTerms(histoTerms);
            }
        });

    }


    /**
     * this method leads to a new activity, the into page of a person
     * @param position
     */
    @Override
    public void onHistoClick(int position) {
        //mTermList.get(position);
        Log.d(TAG, "onHistoClick: onHistoItemClick: clicked");
        /*TextView textView = findViewById(R.id.txtHistoDescript);
        textView.setText(mHistoViewModel.getExplanation(position));*/
        Intent intent = new Intent(this, HistoInfoPage.class);
        startActivity(intent);
    }
}
