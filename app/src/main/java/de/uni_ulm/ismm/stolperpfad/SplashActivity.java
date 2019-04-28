package de.uni_ulm.ismm.stolperpfad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private boolean loaded = false;
    private LoadContentTask loadTask;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
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

    private void startApp() {
        if(loadTask != null && loaded) {
            loadTask.cancel(true);
        }
        startActivity(new Intent(SplashActivity.this, MainMenuActivity.class));
    }

    private class LoadContentTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            StolperpfadeApplication.getInstance().setupFileTree();
            //StolperpfadeApplication.getInstance().set();
            loaded = true;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
