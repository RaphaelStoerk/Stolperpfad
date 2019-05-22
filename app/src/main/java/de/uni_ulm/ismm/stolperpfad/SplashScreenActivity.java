package de.uni_ulm.ismm.stolperpfad;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import de.uni_ulm.ismm.stolperpfad.scanner.ScannerActivity;

/**
 * The activity that displays the splash screen at the start of the app
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final int TIME_ON_SPLASH_SCREEN = 3000;
    private static final int REQUEST_PERMISSION_CODE = 100;
    private boolean loaded = false;
    private LoadContentTask loadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        if(requestPermissions()) {
            loadContent();
        }
        // if the user taps on the splash screen the app will start if the background tasks are done
        findViewById(R.id.splash_background).setOnClickListener(view -> {
            if(loaded) {
                startApp();
            }
        });
    }

    /**
     * Checks which permissions have not been granted, and if there are any will prompt
     * a dialog informing the user to allow these permissions
     */
    private boolean requestPermissions() {
        ArrayList<String> permissions_to_request = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            permissions_to_request.add(Manifest.permission.CAMERA);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions_to_request.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions_to_request.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissions_to_request.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            permissions_to_request.add(Manifest.permission.INTERNET);
        }
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissions_to_request.add(Manifest.permission.ACCESS_NETWORK_STATE);
        }
        if(permissions_to_request.size() == 0) {
            return true;
        }
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this, R.style.DialogTheme_Dark);
        builder.setTitle(getResources().getString(R.string.permission_info_title));
        builder.setMessage(getResources().getString(R.string.permission_info));
        builder.setPositiveButton("Weiter", (dialogInterface, i) -> {
            dialogInterface.cancel();
            startRequest(permissions_to_request);
        });
        builder.setCancelable(false);
        AlertDialog request_dialog = builder.create();
        request_dialog.show();
        return false;
    }

    /**
     * Initializes the requesting for all relevant permissions
     *
     * @param permissions_to_request the permissions to request
     */
    private void startRequest(ArrayList<String> permissions_to_request) {
        ActivityCompat.requestPermissions(SplashScreenActivity.this, permissions_to_request.toArray(new String[]{}), REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for(int result : grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    finish();
                    System.exit(1);
                }
            }
            loadContent();
        }
    }

    /**
     * Sets up all necessary background file and data storages
     */
    @SuppressLint("StaticFieldLeak")
    private void loadContent() {
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
