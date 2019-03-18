package de.uni_ulm.ismm.stolperpfad.database;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;

public class DbActivity extends AppCompatActivity implements PersListAdapter.OnPersItemListener {

    private static final String TAG = "DbActivity";

    private PersViewModel mPersViewModel;
    private ArrayList<Person> mPersList = new ArrayList<>(); //cached copy of persons


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        //button to go back
        FloatingActionButton fabBack = findViewById(R.id.fab_back);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //RecyclerView to get data
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final PersListAdapter adapter = new PersListAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //ViewModel
        mPersViewModel = ViewModelProviders.of(this).get(PersViewModel.class);

        mPersViewModel.getAllPersons().observe(this, new Observer<ArrayList<Person>>() {
            @Override
            public void onChanged(@Nullable final ArrayList<Person> persons) {
                // Update the cached copy of the words in the adapter.
                adapter.setPersons(persons);
            }
        });

    }


    /**
     * this method leads to a new activity, the into page of a person
     * @param position
     */
    @Override
    public void onPersClick(int position) {
        mPersList.get(position);
        Log.d(TAG, "onPersClick: onPersItemClick: clicked");
        Intent intent = new Intent(this, PersInfoPage.class);
        startActivity(intent);
    }
}
