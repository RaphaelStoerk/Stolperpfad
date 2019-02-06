package de.uni_ulm.ismm.stolperpfad;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

public class DbActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private PersViewModel mPersViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final PersListAdapter adapter = new PersListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        /** try to make list items clickable**/
        recyclerView.setOnClickListener((View.OnClickListener) this);

        //ViewModel
        mPersViewModel = ViewModelProviders.of(this).get(PersViewModel.class);

        mPersViewModel.getAllPersons().observe(this, new Observer<List<Person>>() {
            @Override
            public void onChanged(@Nullable final List<Person> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setPersons(words);
            }
        });

    }

/** show list item in detail test**/
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Log.i("TestListView", "You clicked Item: " + id + " at position:" + position);
        // start new activity
        Intent intent = new Intent();
        intent.setClass(this, ShowListItem.class);
        intent.putExtra("position", position);
        intent.putExtra("id", id);
        startActivity(intent);

    }
}
