package de.uni_ulm.ismm.stolperpfad.database.list_of_persons;

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

import java.util.List;

import de.uni_ulm.ismm.stolperpfad.R;
import de.uni_ulm.ismm.stolperpfad.database.data.Person;

public class PersonsActivity extends AppCompatActivity implements PersListAdapter.OnPersItemListener {

    private static final String TAG = "PersonsActivity";

    private PersViewModel mPersViewModel;
    private List<Person> mPersList; //cached copy of persons


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_persons);

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
        final PersListAdapter adapter = new PersListAdapter(this, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        // maybe we don't need this because our database won't be changed by the user
        //TODO: if this project is published and continued we have to think about a solution
        //TODO: of how we can insert new Stolpersteine and update the data the user has downloaded
        //TODO: maybe this is possible with playstore updates
        //ViewModel
        mPersViewModel = ViewModelProviders.of(this).get(PersViewModel.class);

        mPersViewModel.getAllPersons().observe(this, new Observer<List<Person>>() {
            @Override
            public void onChanged(@Nullable final List<Person> persons) {
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
        //mPersList.get(position);
        Log.d(TAG, "onPersClick: onPersItemClick: clicked");
        Intent intent = new Intent(this, PersInfoPage.class);
        startActivity(intent);
    }
}
