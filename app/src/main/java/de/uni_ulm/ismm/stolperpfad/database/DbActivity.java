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

public class DbActivity extends AppCompatActivity {

    private List<Person> persList = new ArrayList<>();
    private PersViewModel mPersViewModel;

    private static final String TAG = "test item click";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);


        FloatingActionButton fabBack = findViewById(R.id.fab_back);
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final PersListAdapter adapter = new PersListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //ViewModel
        mPersViewModel = ViewModelProviders.of(this).get(PersViewModel.class);

        mPersViewModel.getAllPersons().observe(this, new Observer<List<Person>>() {
            @Override
            public void onChanged(@Nullable final List<Person> words) {
                // Update the cached copy of the words in the adapter.
                adapter.setPersons(words);
            }
        });


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //TODO fixen
                //(hier soll beim Klick eine neue Page aufgehen mit den Infos zu der angeklickten Person)

                /*Intent intent;
                Person person = persList.get(position);
                Log.i(TAG,person.getFstName() + person.getFamName() + " is selected!");
                intent = new Intent(DbActivity.this, ***.class);
                startActivity(intent);*/

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


}
