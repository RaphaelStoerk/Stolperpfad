package de.uni_ulm.ismm.stolperpfad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * The activity that displays the splash screen at the start of the app
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final int TIME_ON_SPLASH_SCREEN = 3000;
    private boolean loaded = false;
    private LoadContentTask loadTask;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        // if the user taps on the splash screen the app will start if the background tasks are done
        findViewById(R.id.splash_background).setOnClickListener(view -> {
            if(loaded) {
                startApp();
            }
        });
        loadTask = new LoadContentTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                startApp();
            }
        };
        loadTask.execute();
    }

    /**
     * This method calls the activity displaying the main menu of the application
     */
    private void startApp() {
        if(loadTask != null && loaded) {
            loadTask.cancel(true);
        }
        startActivity(new Intent(SplashScreenActivity.this, MainMenuActivity.class));
    }

    /**
     * An AsyncTask that loads all necessary content at the start of the app
     */
    @SuppressLint("StaticFieldLeak")
    private class LoadContentTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            StolperpfadeApplication.getInstance().setupFileTree();
            StolperpfadeApplication.getInstance().setUpDb();
            loaded = true;
            try {
                Thread.sleep(TIME_ON_SPLASH_SCREEN);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
