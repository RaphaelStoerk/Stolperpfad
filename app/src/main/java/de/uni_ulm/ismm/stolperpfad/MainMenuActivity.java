package de.uni_ulm.ismm.stolperpfad;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import com.mapquest.mapping.MapQuest;

import de.uni_ulm.ismm.stolperpfad.general.StolperpfadeAppActivity;

/**
 * This class is the current entry point to our activities, this might change with an added
 * splash screen. For now, this is the first activity a user will be able to interact with
 */
public class MainMenuActivity extends StolperpfadeAppActivity {

    private boolean created = false;

    /**
     * This is what happens when this activity is first started
     * @param savedInstanceState
     */
    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(StolperpfadeApplication.getInstance().isFirstCall()) {
            StolperpfadeApplication.getInstance().setFirstCall(false);
            new LoadDataTask(this) {
                @Override
                protected void onPostExecute(Void aVoid) {
                    if(!created)
                        createMainMenu();
                }
            }.execute();
            setContentView(R.layout.splash_screen);
            findViewById(R.id.splash_background).setOnClickListener(view -> createMainMenu());
        } else {
            createMainMenu();
            StolperpfadeApplication.getInstance().setUpDb();
        }
    }

    private void createMainMenu() {
        created = true;
        // Initialize this view and display the right screen
        initializeGeneralControls(R.layout.activity_main_menu);

        // add the listener to the buttons on screen and make them visible
        aq.id(R.id.info_button).visible().clicked(myClickListener);
        aq.id(R.id.menu_to_scan_button).visible().clicked(myClickListener);
        aq.id(R.id.menu_to_route_button).visible().clicked(myClickListener);
        aq.id(R.id.menu_to_next_stone_button).visible().clicked(myClickListener);
    }

    private static class LoadDataTask extends AsyncTask<Void,Void,Void> {

        @SuppressLint("StaticFieldLeak")
        private Activity a;

        LoadDataTask(Activity a) {
            this.a = a;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            StolperpfadeApplication.getInstance().setupFileTree();
            StolperpfadeApplication.getInstance().setUpDatabase();
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
